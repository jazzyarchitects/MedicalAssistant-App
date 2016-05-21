package architect.jazzy.medicinereminder.Activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import architect.jazzy.medicinereminder.R;


/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {


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
        SharedPreferences sharedPreferences=context.getSharedPreferences("Widget",Context.MODE_PRIVATE);
        String medicineList="";
        medicineList=sharedPreferences.getString("medicineList","No medicines to be taken till now");
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        views.setTextViewText(R.id.widgetMedicineList,medicineList);
        Intent i=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,i,0);


        // Instruct the widget manager to update the widget
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context.getApplicationContext());
        ComponentName thisWidget=new ComponentName(context.getApplicationContext(),AppWidget.class);
        int[] appWidgetIds=appWidgetManager.getAppWidgetIds(thisWidget);
        if(appWidgetIds!=null && appWidgetIds.length>0)
        {
            onUpdate(context,appWidgetManager,appWidgetIds);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        Log.v("AppWidget","update");
        SharedPreferences sharedPreferences=context.getSharedPreferences("Widget",Context.MODE_PRIVATE);
        String medicineList="";
        medicineList=sharedPreferences.getString("medicineList","No medicines to be taken till now");
        Log.v("AppWidget",medicineList);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        views.setTextViewText(R.id.widgetMedicineList,medicineList);

        Intent i=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,i,0);
        views.setOnClickPendingIntent(appWidgetId,pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


}


