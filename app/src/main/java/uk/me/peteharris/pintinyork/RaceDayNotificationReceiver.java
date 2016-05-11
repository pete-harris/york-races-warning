package uk.me.peteharris.pintinyork;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uk.me.peteharris.pintinyork.model.BadTime;

/**
 * Created by pharris on 11/05/16.
 */
public class RaceDayNotificationReceiver extends BroadcastReceiver {
    private static final String ACTION_RACEDAY = "uk.me.peteharris.pintinyork.action.RACEDAY_ALERT";
    private static final String EXTRA_BADTIME = "uk.me.peteharris.pintinyork.EXTRA_BAD_TIME";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RaceDayNotification", String.format("Received intent: %s", intent.getAction()));
        switch (intent.getAction()){
            case ACTION_RACEDAY:
                BadTime race = intent.getParcelableExtra(EXTRA_BADTIME);
                Date date = new Date();
                if(race.isItNow(date)) {
                    showNotification(context, race);
                }
                // schedule next week's alarm
                new Helper(context).scheduleAlarm();
                break;
        }
    }

    /**
     * Simple receiver to restore alarm when the device is reset
     */
    public static class OnBootReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case "android.intent.action.BOOT_COMPLETED":
                    // Set the alarm here.
                    new Helper(context).scheduleAlarm();
                    break;
            }
        }
    }

    static class Helper {

        private final Context mContext;
        private final AlarmManager mAlarmManager;

        public Helper(Context context) {
            mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            mContext = context;
        }

        public void scheduleAlarm() {

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
            intent.putExtra(EXTRA_BADTIME, nextBadTime);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

            // cancel any existing alarm
            mAlarmManager.cancel(alarmIntent);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(nextBadTime.start.getTime());
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Log.d("RaceDayNotification", String.format("alarm scheduled for %s", calendar.getTime().toString()));

            mAlarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmIntent);
        }
    }

    public static void showNotification(Context context, BadTime race) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        String text = context.getString(R.string.raceday, race.label);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_raceday_notification)
                        .setContentTitle(context.getString(R.string.raceday_notification_title))
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            mBuilder.setCategory(Notification.CATEGORY_REMINDER);
        }

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(R.id.notification_raceday, mBuilder.build());
    }

}
