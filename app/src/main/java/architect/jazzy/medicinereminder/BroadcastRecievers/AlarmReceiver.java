package architect.jazzy.medicinereminder.BroadcastRecievers;

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
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import architect.jazzy.medicinereminder.Activities.AppWidget;
import architect.jazzy.medicinereminder.Activities.NotificationOpen;
import architect.jazzy.medicinereminder.Activities.PopupWindow;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.Handlers.ExportToJSON;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    private static final String TAG="AlarmReceiver";
    Bundle bundle;
    ArrayList<Medicine> medicines;

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
        bundle = intent.getExtras();
        medicines=bundle.getParcelableArrayList(Constants.MEDICINE_NAME_LIST);
//        medicineList = bundle.getStringArrayList("MedicineList");
        Log.e(TAG,"Alarm going off at: "+Calendar.getInstance().getTime().toString());
//        try{
//            logAlarm();
//        }catch (IOException e){
//            e.printStackTrace();
//        }catch (NullPointerException e){
//            e.printStackTrace();
//        }


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        showNoti = sharedPreferences.getBoolean("show_notification", true);
        showPopup = sharedPreferences.getBoolean("show_popup", true);

        dataHandler = new DataHandler(mcontext);

        for(Medicine medicine:medicines){
            if(!dataHandler.doesMedicineExists(medicine)){
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
                Log.v("AppWidget", "Update Signal Passed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateNotification(Context context, ArrayList<Medicine> medicines) {
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST,medicines);

        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String notificationText = "";
        for(Medicine medicine:medicines){
            notificationText+=medicine.getMedName()+" ";
        }

        Intent i = new Intent(context, NotificationOpen.class);
        i.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) (System.currentTimeMillis() / 100000), i, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = "Take your Medicines";
        String subtitle = "Medicines:" + notificationText;
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mcontext);
        builder.setContentTitle(title)
                .setContentText(subtitle)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setShowWhen(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher).build();
        Notification notification=builder.build();



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
        bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST,medicines);
        bundle.putInt("NotificationId", id);
        i.putExtras(bundle);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mcontext.startActivity(i);
    }

    private void logAlarm() throws IOException{
        Calendar calendar=Calendar.getInstance();
        File file=new File(ExportToJSON.PATH,"AlarmLog("+calendar.get(Calendar.HOUR_OF_DAY)+","+calendar.get(Calendar.MINUTE)+").txt");
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        String s="Alarm going off in: \n\n"+
                calendar.toString()+"\n\n\n\n\n";
        for (Medicine medicine:medicines){
            s+=medicine.toJSON()+"\n\n";
        }
        fileOutputStream.write(s.getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
    }

}
