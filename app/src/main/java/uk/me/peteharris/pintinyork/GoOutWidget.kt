package uk.me.peteharris.pintinyork

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import uk.me.peteharris.pintinyork.model.BadTime
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class GoOutWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // There may be multiple widgets active, so update all of them
        val N = appWidgetIds.size
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }


    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val dataHelper = DataHelper()
        val data = dataHelper.loadData(context)
        val image: Int

        val now = Date()
        val itsARaceday = data?.any { it.isItNow(now) } == true

        image = when {
            itsARaceday -> R.drawable.widget_raceday
            dataHelper.isWeekend -> R.drawable.widget_weekend
            else -> R.drawable.widget_haveapint
        }

        val views = RemoteViews(context.packageName, R.layout.go_out_widget)
        views.setImageViewResource(R.id.widget_state, image)


        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        views.setOnClickPendingIntent(R.id.widget_state, pendingIntent)
        
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

