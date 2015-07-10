package uk.me.peteharris.pintinyork;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.ArrayList;

import uk.me.peteharris.pintinyork.model.BadTime;


/**
 * Implementation of App Widget functionality.
 */
public class GoOutWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        ArrayList<BadTime> data = DataHelper.loadData(context);
        int image;
        int color;
        if(null != DataHelper.isItBad(data)){
            image = R.drawable.raceday;
            color = R.color.bad;
        } else if(DataHelper.isWeekend()){
            image = R.drawable.weekend;
            color = R.color.careful;
        } else {
            image = R.drawable.haveapint;
            color = R.color.go;
        }
//        CharSequence widgetText = context.getString(R.string.appwidget_text);
//        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.go_out_widget);
        views.setImageViewResource(R.id.widget_state, image);


        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_state, pendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

