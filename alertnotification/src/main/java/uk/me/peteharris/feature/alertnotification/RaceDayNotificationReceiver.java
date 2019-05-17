package uk.me.peteharris.feature.alertnotification;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uk.me.peteharris.pintinyork.base.DataHelper;
import uk.me.peteharris.pintinyork.base.MainActivity;
import uk.me.peteharris.pintinyork.base.model.BadTime;
import uk.me.peteharris.pintinyork.feature.alertnotification.BuildConfig;
import uk.me.peteharris.pintinyork.feature.alertnotification.R;

public class RaceDayNotificationReceiver extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_RACEDAY_ALERT = "racedayAlert";
    private static final String ACTION_RACEDAY = "uk.me.peteharris.pintinyork.action.NOTIFICATION_CHANNEL_RACEDAY_ALERT";
    private static final String EXTRA_BADTIME = "uk.me.peteharris.pintinyork.EXTRA_BAD_TIME";
    private static final String EXTRA_BADTIME_HACK = "uk.me.peteharris.pintinyork.EXTRA_HACK";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RaceDayNotification", String.format("Received intent: %s", intent.getAction()));
        if (ACTION_RACEDAY.equals(intent.getAction())) {
            Bundle hack = intent.getBundleExtra(EXTRA_BADTIME_HACK);
            BadTime race = hack.getParcelable(EXTRA_BADTIME);
            Date date = new Date();
            if (race.isItNow(date)) {
                showNotification(context, race);
            } else {
                Log.d("RaceDayNotification", String.format("Race %s is not today (%s)", race.label, race.start.toString()));
            }
            // schedule next week's alarm
            new Helper(context).scheduleAlarm();
        }
    }

    /**
     * Simple receiver to restore alarm when the device is reset
     */
    public static class OnBootReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                Log.d("RaceDayNotification", "Boot detected, scheduling alarm");
                new Helper(context).scheduleAlarm();
            }
        }
    }

    static class Helper {

        private final Context mContext;
        private final AlarmManager mAlarmManager;

        Helper(Context context) {
            mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            mContext = context;

            if(Build.VERSION.SDK_INT >= 26){
                createNotificationChannel(context);
            }
        }

        void scheduleAlarm() {

            ArrayList<BadTime> badTimes = DataHelper.loadData(mContext);

            BadTime nextBadTime = null;

            Date now = new Date();
            for(BadTime b: badTimes){
                if(b.start.before(now)) continue;

                if(null == nextBadTime || nextBadTime.start.after(b.start)){
                    nextBadTime = b;
                }
            }
            if(null == nextBadTime) return;

            Intent intent = new Intent(mContext, RaceDayNotificationReceiver.class);
            intent.setAction(ACTION_RACEDAY);

            Bundle hackBundle = new Bundle();
            hackBundle.putParcelable(EXTRA_BADTIME, nextBadTime);
            intent.putExtra(EXTRA_BADTIME_HACK, hackBundle);

            intent.putExtra(EXTRA_BADTIME, hackBundle);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

            // cancel any existing alarm
            mAlarmManager.cancel(alarmIntent);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(nextBadTime.start.getTime());
            calendar.set(Calendar.HOUR_OF_DAY, BuildConfig.ALERT_HOUR);
            calendar.set(Calendar.MINUTE, BuildConfig.ALERT_MINUTE);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Log.d("RaceDayNotification", String.format("alarm scheduled for %s for %s", calendar.getTime().toString(), nextBadTime.start.toString()));

            mAlarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmIntent);
        }
    }

    private static void showNotification(Context context, BadTime race) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        String text = context.getString(uk.me.peteharris.pintinyork.R.string.raceday, race.label);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_RACEDAY_ALERT)
                        .setSmallIcon(R.drawable.ic_raceday_notification)
                        .setContentTitle(context.getString(R.string.raceday_notification_title))
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            builder.setCategory(Notification.CATEGORY_REMINDER);
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.id.notification_raceday, builder.build());
    }

    @TargetApi(26)
    private static void createNotificationChannel(Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence name = context.getString(R.string.channel_name);
        NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_RACEDAY_ALERT, name, NotificationManager.IMPORTANCE_LOW);
        mChannel.setDescription(context.getString(R.string.channel_description));
        mNotificationManager.createNotificationChannel(mChannel);
    }
}
