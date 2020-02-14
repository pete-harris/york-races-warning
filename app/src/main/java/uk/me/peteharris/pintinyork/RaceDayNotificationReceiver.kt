package uk.me.peteharris.pintinyork

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import uk.me.peteharris.pintinyork.model.BadTime
import java.util.*

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
            AlarmHelper(context).scheduleAlarm()
        }
    }

    /**
     * Simple receiver to restore alarm when the device is reset
     */
    class OnBootReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
                Log.d("RaceDayNotification", "Boot detected, scheduling alarm")
                AlarmHelper(context).scheduleAlarm()
            }
        }
    }

    internal class AlarmHelper(private val context: Context) {
        private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        init {
            if (Build.VERSION.SDK_INT >= 26) {
                createNotificationChannel(context)
            }
        }

        fun scheduleAlarm() {
            val nextBadTime = getNextBadTime()
            nextBadTime?.let {
                scheduleAlarmFor(nextBadTime)
            } ?: disableAlarms()
        }

        private fun disableAlarms() {
            val receiver = ComponentName(context, OnBootReceiver::class.java)
            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
            Log.d(
                "RaceDayNotification",
                "No alarms set"
            )
        }

        private fun getNextBadTime(): BadTime? {
            if (BuildConfig.DEBUG) {
                val oneMinutesTime = Calendar.getInstance().apply { this.add(Calendar.MINUTE, 1) }.time
                return BadTime().apply {
                    label = "Test"
                    start = oneMinutesTime
                }
            }
            val dataHelper = DataHelper()
            val badTimes = dataHelper.loadData(context)

            var nextBadTime: BadTime? = null

            val now = Date()
            for (b in badTimes!!) {
                if (b.start.before(now)) continue

                if (nextBadTime == null || nextBadTime.start.after(b.start)) {
                    nextBadTime = b
                }
            }
            return nextBadTime
        }

        private fun scheduleAlarmFor(badTime: BadTime) {
            val intent = Intent(context, RaceDayNotificationReceiver::class.java)
            intent.action = ACTION_RACEDAY

            val hackBundle = Bundle()
            hackBundle.putParcelable(EXTRA_BADTIME, badTime)
            intent.putExtra(EXTRA_BADTIME_HACK, hackBundle)
            intent.putExtra(EXTRA_BADTIME, hackBundle)
            val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

            // cancel any existing alarm
            alarmManager.cancel(alarmIntent)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = badTime.start.time
            calendar.set(Calendar.HOUR_OF_DAY, BuildConfig.ALERT_HOUR)
            calendar.set(Calendar.MINUTE, BuildConfig.ALERT_MINUTE)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            Log.d(
                "RaceDayNotification",
                String.format("alarm scheduled for %s for %s", calendar.time.toString(), badTime.start.toString())
            )

            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )

            val receiver = ComponentName(context, OnBootReceiver::class.java)
            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

        }
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_RACEDAY_ALERT = "racedayAlert"
        private const val ACTION_RACEDAY = "uk.me.peteharris.pintinyork.action.NOTIFICATION_CHANNEL_RACEDAY_ALERT"
        private const val EXTRA_BADTIME = "uk.me.peteharris.pintinyork.EXTRA_BAD_TIME"
        private const val EXTRA_BADTIME_HACK = "uk.me.peteharris.pintinyork.EXTRA_HACK"

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
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
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
