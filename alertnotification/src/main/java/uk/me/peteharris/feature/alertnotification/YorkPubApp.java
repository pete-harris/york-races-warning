package uk.me.peteharris.feature.alertnotification;

import android.app.Application;


public class YorkPubApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // schedule next alarm
        new RaceDayNotificationReceiver.Helper(this).scheduleAlarm();
    }
}
