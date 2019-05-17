package uk.me.peteharris.pintinyork

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.util.Log
import uk.me.peteharris.pintinyork.model.BadTime

import java.util.ArrayList
import java.util.Calendar
import java.util.Date

class RaceDayNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("RaceDayNotification", String.format("Received intent: %s", intent.action))
        if (ACTION_RACEDAY == intent.action) {
            val hack = intent.getBundleExtra(EXTRA_BADTIME_HACK)
            val race = hack.getParcelable<BadTime>(EXTRA_BADTIME)
            val date = Date()
            if (race!!.isItNow(date)) {
                showNotification(context, race)
            } else {
                Log.d(
                    "RaceDayNotification",
                    String.format("Race %s is not today (%s)", race.label, race.start.toString())
                )
            }
            // schedule next week's alarm
            Helper(context).scheduleAlarm()
        }
    }

    /**
     * Simple receiver to restore alarm when the device is reset
     */
    class OnBootReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
                Log.d("RaceDayNotification", "Boot detected, scheduling alarm")
                Helper(context).scheduleAlarm()
            }
        }
    }

    internal class Helper(private val mContext: Context) {
        private val mAlarmManager: AlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        init {
            if (Build.VERSION.SDK_INT >= 26) {
                createNotificationChannel(mContext)
            }
        }

        fun scheduleAlarm() {
            val dataHelper = DataHelper()
            val badTimes = dataHelper.loadData(mContext)

            var nextBadTime: BadTime? = null

            val now = Date()
            for (b in badTimes!!) {
                if (b.start.before(now)) continue

                if (null == nextBadTime || nextBadTime.start.after(b.start)) {
                    nextBadTime = b
                }
            }
            if (null == nextBadTime) return

            val intent = Intent(mContext, RaceDayNotificationReceiver::class.java)
            intent.action = ACTION_RACEDAY

            val hackBundle = Bundle()
            hackBundle.putParcelable(EXTRA_BADTIME, nextBadTime)
            intent.putExtra(EXTRA_BADTIME_HACK, hackBundle)

            intent.putExtra(EXTRA_BADTIME, hackBundle)
            val alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0)

            // cancel any existing alarm
            mAlarmManager.cancel(alarmIntent)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = nextBadTime.start.time
            calendar.set(Calendar.HOUR_OF_DAY, BuildConfig.ALERT_HOUR)
            calendar.set(Calendar.MINUTE, BuildConfig.ALERT_MINUTE)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            Log.d(
                "RaceDayNotification",
                String.format("alarm scheduled for %s for %s", calendar.time.toString(), nextBadTime.start.toString())
            )

            mAlarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )
        }
    }

    companion object {
        private val NOTIFICATION_CHANNEL_RACEDAY_ALERT = "racedayAlert"
        private val ACTION_RACEDAY = "uk.me.peteharris.pintinyork.action.NOTIFICATION_CHANNEL_RACEDAY_ALERT"
        private val EXTRA_BADTIME = "uk.me.peteharris.pintinyork.EXTRA_BAD_TIME"
        private val EXTRA_BADTIME_HACK = "uk.me.peteharris.pintinyork.EXTRA_HACK"

        private fun showNotification(context: Context, race: BadTime) {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val text = context.getString(uk.me.peteharris.pintinyork.R.string.raceday, race.label)
            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_RACEDAY_ALERT)
                .setSmallIcon(R.drawable.ic_raceday_notification)
                .setContentTitle(context.getString(R.string.raceday_notification_title))
                .setContentText(text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setVisibility(Notification.VISIBILITY_PUBLIC)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder.setCategory(Notification.CATEGORY_REMINDER)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(R.id.notification_raceday, builder.build())
        }

        @TargetApi(26)
        private fun createNotificationChannel(context: Context) {
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val name = context.getString(R.string.channel_name)
            val mChannel =
                NotificationChannel(NOTIFICATION_CHANNEL_RACEDAY_ALERT, name, NotificationManager.IMPORTANCE_LOW)
            mChannel.description = context.getString(R.string.channel_description)
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }
}
