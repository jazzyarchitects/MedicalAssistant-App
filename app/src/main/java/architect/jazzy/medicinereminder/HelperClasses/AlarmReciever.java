package architect.jazzy.medicinereminder.HelperClasses;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Activities.AppWidget;
import architect.jazzy.medicinereminder.Activities.NotificationOpen;
import architect.jazzy.medicinereminder.Activities.PopupWindow;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.R;

public class AlarmReciever extends BroadcastReceiver {
    public AlarmReciever() {
    }

    Bundle bundle;

    DataHandler dataHandler;
    Context mcontext;
    SharedPreferences sharedPreferences;
    Boolean showNoti = true, showPopup = true;
    ArrayList<String> medicineList = new ArrayList<>();
    int id = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        mcontext = context;
        intent.getData();
        //Toast.makeText(context,"Broadcast Service Started",Toast.LENGTH_LONG).show();
        bundle = intent.getExtras();
        medicineList = bundle.getStringArrayList("MedicineList");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        showNoti = sharedPreferences.getBoolean("show_notification", true);
        showPopup = sharedPreferences.getBoolean("show_popup", true);

        boolean all_present = true;
        ArrayList<String> toDeleteString = new ArrayList<>();
        dataHandler = new DataHandler(mcontext);
        dataHandler.open();
        Cursor c;
        for (int i = 0; i < medicineList.size(); i++) {
            c = dataHandler.findRow(medicineList.get(i));
            if (!c.moveToFirst()) {
                toDeleteString.add(medicineList.get(i));
            }
        }
        for (int i = 0; i < toDeleteString.size(); i++) {
            medicineList.remove(toDeleteString.get(i));
        }
        if (medicineList.isEmpty()) {
            all_present = false;
        }
        Log.v("Medicine Found", String.valueOf(all_present));
        //Toast.makeText(context, "Show notification?"+showNoti,Toast.LENGTH_LONG).show();
        if (showNoti && all_present) {
            generateNotification(context, medicineList);
        }

        if (showPopup && all_present) {
            generatePopup();
        }

        try {
            if (all_present) {
                String list = "";
                for (int i = 0; i < medicineList.size(); i++) {
                    list += (i + 1) + ". " + medicineList.get(i) + "\n";
                }
                SharedPreferences appUpdate = mcontext.getSharedPreferences("Widget", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = appUpdate.edit();
                editor.putString("medicineList", list);
                editor.apply();
                Intent intent1 = new Intent(mcontext.getApplicationContext(), AppWidget.class);
                mcontext.sendBroadcast(intent1);
                Log.v("AppWidget", "Update Signal Passed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) mcontext.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    @SuppressWarnings("depreciation")
    private void generateNotification(Context context, ArrayList<String> medicineList) {
        Bundle bundle1;
        bundle1 = new Bundle();
        if (!bundle1.isEmpty()) {
            bundle1.clear();
        }
        bundle1.putStringArrayList("medicineList", medicineList);
        int icon = R.drawable.capsule_icon_notification;
        long when = System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String notificationText = "";
        for (int i = 0; i < medicineList.size(); i++) {
            notificationText += " " + medicineList.get(i);
        }
        Notification notification = new Notification(icon, notificationText, when);
        String title = "Take your Medicines";
        String subtitle = "Medicines:" + notificationText;
        Intent i = new Intent(context, NotificationOpen.class);
        i.putExtras(bundle1);

        //TODO: Changed Pending Flag from FLAG_CANCEL_CURRENT
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) (System.currentTimeMillis() / 100000), i, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setLatestEventInfo(context, title, subtitle, pendingIntent);


        notification.priority = Notification.PRIORITY_MAX;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.sound = Uri.parse(sharedPreferences.getString("notification_ringtone", String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))));

        Boolean vibrate = sharedPreferences.getBoolean("notification_vibrate", true);
        if (vibrate) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        } else {
            notification.vibrate = null;
        }
        notificationManager.notify(id, notification);


    }

    public void generatePopup() {
        Intent i = new Intent(mcontext, PopupWindow.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("MedicineList", medicineList);
        bundle.putInt("NotificationId", id);
        i.putExtras(bundle);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mcontext.startActivity(i);
    }
}
