package architect.jazzy.medicinereminder.MedicalAssistant.HelperClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.MedicalAssistant.Activities.AppWidget;
import architect.jazzy.medicinereminder.MedicalAssistant.Services.AlarmSetterService;

public class DailyAlarmStarter extends BroadcastReceiver {
  public DailyAlarmStarter() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {

    context.getSharedPreferences(Constants.INTERNAL_PREF, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(Constants.NEWS_LIST_LOADED, false)
        .apply();

    SharedPreferences appUpdate = context.getSharedPreferences("Widget", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = appUpdate.edit();
    editor.putString("medicineList", "No medicines to be taken.");
    editor.apply();
    Intent intent1 = new Intent(context.getApplicationContext(), AppWidget.class);
    context.sendBroadcast(intent1);


    Intent i = new Intent(context.getApplicationContext(), AlarmSetterService.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    i.setAction("CREATE");
    context.startService(i);


  }
}
