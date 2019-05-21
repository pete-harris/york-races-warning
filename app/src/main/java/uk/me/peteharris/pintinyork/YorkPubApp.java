package uk.me.peteharris.pintinyork;

import android.app.Application;

public class YorkPubApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // schedule next alarm
        new RaceDayNotificationReceiver.AlarmHelper(this).scheduleAlarm();
    }
}
