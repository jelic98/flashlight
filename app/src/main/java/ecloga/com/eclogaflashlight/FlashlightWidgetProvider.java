package ecloga.com.eclogaflashlight;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class FlashlightWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Intent receiver = new Intent(context, FlashlightWidgetReceiver.class);
        receiver.setAction("COM_FLASHLIGHT");
        receiver.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.app_widget);
        views.setOnClickPendingIntent(R.id.btnSwitch, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }
}
