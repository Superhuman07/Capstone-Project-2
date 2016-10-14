package admin.com.UnSpammer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import admin.com.UnSpammer.CallService.CallTurningService;

public class Widget extends AppWidgetProvider {
    public static String TAG_SERVICE_STATE = "TAG_SERVICE_STATE";
    public static final String TAG_STOP_SERVICE = "STOP_CALL_BLOCKING_SERVICE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        // Check the service and then set it off
        if (Utility.isServiceRunning(CallTurningService.class, context)) {
            views.setImageViewResource(R.id.appwidget_button, R.drawable.widget_off);
            if (Build.VERSION.SDK_INT >= 15)
                views.setContentDescription(R.id.appwidget_button, context.getResources().getString(R.string.desc_widget_blocker_on));
        } else {
            views.setImageViewResource(R.id.appwidget_button, R.drawable.widget_on);
            if (Build.VERSION.SDK_INT >= 15)
                views.setContentDescription(R.id.appwidget_button, context.getResources().getString(R.string.desc_widget_blocker_off));
        }

        Intent intent = new Intent(context, Widget.class);
        intent.setAction(TAG_STOP_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_button, pendingIntent);

        // Updating the widget views
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

    @Override
    public void onReceive(Context context, Intent intent) {
        // Updating the app when the widget the enabled
        if (intent.getAction().equals(TAG_SERVICE_STATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
        else if (intent.getAction().equals(TAG_STOP_SERVICE)) {
            if (Utility.isServiceRunning(CallTurningService.class, context)) {
                Intent i = new Intent(context, CallTurningService.class);
                context.stopService(i);
            } else {
                Intent i = new Intent(context, CallTurningService.class);
                context.startService(i);
            }
        }
        super.onReceive(context, intent);
    }
}

