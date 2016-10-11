package architect.jazzy.medicinereminder.MedicalAssistant.BroadcastRecievers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.MedicalAssistant.Activities.AppWidget;
import architect.jazzy.medicinereminder.MedicalAssistant.Activities.NotificationOpen;
import architect.jazzy.medicinereminder.MedicalAssistant.Activities.PopupWindow;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.DataHandler;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.ExportToJSON;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;
import architect.jazzy.medicinereminder.R;

public class AlarmReceiver extends BroadcastReceiver {
  public static final String NOTIFICATION_TAG = "MedicineReminderAlarmNotification";
  public static final int NOTIFICATION_ID = 7844;
  private static final String TAG = "AlarmReceiver";
  Bundle bundle;
  ArrayList<Medicine> medicines;
  DataHandler dataHandler;
  Context mcontext;
  SharedPreferences sharedPreferences;
  Boolean showNoti = true, showPopup = true;
  ArrayList<String> medicineList = new ArrayList<>();

  public AlarmReceiver() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    mcontext = context;
    intent.getData();
    bundle = intent.getExtras();
    medicines = bundle.getParcelableArrayList(Constants.MEDICINE_NAME_LIST);
    assert medicines != null;
    assert medicines.get(0) != null;

    //Log.ee(TAG, "Alarm On Receive: "+medicines.get(0).toJSON());


    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    showNoti = sharedPreferences.getBoolean("show_notification", true);
    showPopup = sharedPreferences.getBoolean("show_popup", true);

    dataHandler = new DataHandler(mcontext);

    for (Medicine medicine : medicines) {
      if (!dataHandler.doesMedicineExists(medicine)) {
        medicines.remove(medicine);
      }
    }

    if (showNoti && !medicines.isEmpty()) {
      generateNotification(context, medicines);
    }

    if (showPopup && !medicines.isEmpty()) {
      generatePopup();
    }

    try {
      if (!medicines.isEmpty()) {
        String list = "";
        for (int i = 0; i < medicines.size(); i++) {
          list += (i + 1) + ". " + medicines.get(i).getMedName() + "\n";
        }
        SharedPreferences appUpdate = mcontext.getSharedPreferences("Widget", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appUpdate.edit();
        editor.putString("medicineList", list);
        editor.apply();
        Intent intent1 = new Intent(mcontext.getApplicationContext(), AppWidget.class);
        mcontext.sendBroadcast(intent1);
        //Log.ev("AppWidget", "Update Signal Passed");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void generateNotification(Context context, ArrayList<Medicine> medicines) {
    //Log.ee(TAG, "generate Notification");
    Bundle bundle = new Bundle();
    bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST, medicines);

    int icon = R.mipmap.ic_launcher;
    long when = System.currentTimeMillis();

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    String notificationText = "";
    for (Medicine medicine : medicines) {
      notificationText += medicine.getMedName() + " ";
    }

    Intent i = new Intent(context, NotificationOpen.class);
    i.putExtras(bundle);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) (System.currentTimeMillis() / 100000), i, PendingIntent.FLAG_UPDATE_CURRENT);

    Intent snoozeButtonIntent = new Intent(context, AlarmSnooze.class);
    snoozeButtonIntent.putExtra(Constants.MEDICINE_NAME_LIST, medicineList);
    PendingIntent snoozeButtonPendingIntent = PendingIntent.getBroadcast(context, 7894, snoozeButtonIntent, PendingIntent.FLAG_CANCEL_CURRENT);

    Intent dismissIntent = new Intent(context, DismissNotification.class);
    PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 7895, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);


    NotificationCompat.Action snoozeAction = new NotificationCompat.Action(R.drawable.ic_action_alarm, "Snooze", snoozeButtonPendingIntent);
    NotificationCompat.Action dismissAction = new NotificationCompat.Action(R.drawable.ic_action_cancel, "Dismiss", dismissPendingIntent);

    String title = "Take your Medicines";
    String subtitle = "Medicines:" + notificationText;
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext);
    builder.setContentTitle(title)
        .setContentText(subtitle)
        .setPriority(Notification.PRIORITY_DEFAULT)
        .setShowWhen(true)
        .addAction(snoozeAction)
        .addAction(dismissAction)
        .setContentIntent(pendingIntent)
        .setSmallIcon(R.mipmap.ic_launcher)
        .build();
    Notification notification = builder.build();

    notification.flags |= Notification.FLAG_AUTO_CANCEL;

    notification.ledARGB |= Notification.DEFAULT_LIGHTS;

    notification.sound = Uri.parse(sharedPreferences.getString("notification_ringtone", String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))));

    Boolean vibrate = sharedPreferences.getBoolean("notification_vibrate", true);
    if (vibrate) {
      notification.defaults |= Notification.DEFAULT_VIBRATE;
      notification.vibrate = new long[]{250, 0, 250, 0, 500, 0, 500, 0};
    } else {
      notification.vibrate = null;
    }
    notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notification);


  }

  public void generatePopup() {
    //Log.e(TAG, "Generate popup");
    Intent i = new Intent(mcontext, PopupWindow.class);
    Bundle bundle = new Bundle();
    bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST, medicines);
    bundle.putInt("NotificationId", NOTIFICATION_ID);
    i.putExtras(bundle);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    mcontext.startActivity(i);
  }

  private void logAlarm() throws IOException {
    Calendar calendar = Calendar.getInstance();
    File file = new File(ExportToJSON.PATH, "AlarmLog(" + calendar.get(Calendar.HOUR_OF_DAY) + "," + calendar.get(Calendar.MINUTE) + ").txt");
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    String s = "Alarm going off in: \n\n" +
        calendar.toString() + "\n\n\n\n\n";
    for (Medicine medicine : medicines) {
      s += medicine.toJSON() + "\n\n";
    }
    fileOutputStream.write(s.getBytes());
    fileOutputStream.flush();
    fileOutputStream.close();
  }

}
