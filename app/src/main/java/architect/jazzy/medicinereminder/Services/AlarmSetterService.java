package architect.jazzy.medicinereminder.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;

import architect.jazzy.medicinereminder.BroadcastRecievers.AlarmReceiver;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.HelperClasses.DailyAlarmStarter;
import architect.jazzy.medicinereminder.Models.MedTime;
import architect.jazzy.medicinereminder.Models.Medicine;

public class AlarmSetterService extends IntentService {

    private static final String TAG = "AlarmSetterService";
    private static final int[][] ALARM_REQUEST_CODE = {{123, 124}, {133, 134}, {144, 144}};
    private static final int CUSTOM_ALARM_START_CODE = 150;
    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";
    private IntentFilter matcher;

    SharedPreferences inputPref;

    ArrayList<String>[] medicineList = new ArrayList[6];
    Boolean[] isPresent = {false, false, false, false, false, false};
    Bundle[] bundles = new Bundle[6];
    Intent[] intents = new Intent[6];

    MedTime[][] medTimes = new MedTime[3][2];


    public AlarmSetterService() {
        super("AlarmSetterService");
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        inputPref = getSharedPreferences("TimePrefs", MODE_PRIVATE);
        medTimes = MedTime.getDefaultTimes(this);

        /**
         * Cancel all pending alarms
         * **/
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        for (int i1 = 0; i1 < 3; i1++) {
            for (int j = 0; j < 2; j++) {
                PendingIntent pi = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE[i1][j], i, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(pi);
            }
        }
        int customCount = getSharedPreferences(Constants.INTERNAL_PREF, MODE_PRIVATE).getInt(Constants.CUSTOM_TIME__ALARM_ID_LAST, 1);
        for (int i1 = 1; i1 <= customCount; i1++) {
            PendingIntent pi = PendingIntent.getBroadcast(this, CUSTOM_ALARM_START_CODE + i1, i, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pi);
        }
        getSharedPreferences(Constants.INTERNAL_PREF, MODE_PRIVATE).edit().putInt(Constants.CUSTOM_TIME__ALARM_ID_LAST, 1);

        setAlarms();


        /**
         * Setting alarm to run this service at midnight next day
         * **/
        AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent start2 = new Intent(getApplicationContext(), DailyAlarmStarter.class);
        PendingIntent forNextDay = PendingIntent.getBroadcast(getApplicationContext(), 0, start2, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager2.cancel(forNextDay);
        Calendar cdND = Calendar.getInstance();
        cdND.set(Calendar.DATE, Calendar.getInstance().get(Calendar.DATE) + 1);
        cdND.set(Calendar.HOUR_OF_DAY, 0);
        cdND.set(Calendar.MINUTE, 0);
        cdND.set(Calendar.SECOND, 1);
        cdND.set(Calendar.MILLISECOND, 0);
        alarmManager2.setRepeating(AlarmManager.RTC, cdND.getTimeInMillis(), AlarmManager.INTERVAL_DAY, forNextDay);

    }

    private static final String BEFORE = "before";
    private static final String AFTER = "after";

    private void setAlarms() {

        DataHandler handler = new DataHandler(this);
        ArrayList<Medicine> medicines = handler.getTodaysMedicine();

        ArrayList<Medicine>[][] dailySlots = new ArrayList[3][2];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                dailySlots[i][j] = new ArrayList<>();
            }
        }
        handler.close();

        for (Medicine medicine : medicines) {
            if (medicine.getBreakfast().equalsIgnoreCase(BEFORE)) {
                dailySlots[0][0].add(medicine);
            } else if (medicine.getBreakfast().equalsIgnoreCase(AFTER)) {
                dailySlots[0][1].add(medicine);
            }
            if (medicine.getLunch().equalsIgnoreCase(BEFORE)) {
                dailySlots[1][0].add(medicine);
            } else if (medicine.getLunch().equalsIgnoreCase(AFTER)) {
                dailySlots[1][1].add(medicine);
            }
            if (medicine.getDinner().equalsIgnoreCase(BEFORE)) {
                dailySlots[2][0].add(medicine);
            } else if (medicine.getDinner().equalsIgnoreCase(AFTER)) {
                dailySlots[2][1].add(medicine);
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                if (dailySlots[i][j] == null) {
//                    Log.e(TAG,"DailySlot["+i+"]["+j+"] is null");
                    continue;
                }
                if (dailySlots[i][j].isEmpty()) {
//                    Log.e(TAG,"DailySlot["+i+"]["+j+"] is empty");
                    continue;
                }
                if (MedTime.hasPassed(medTimes[i][j])) {
//                    Log.e(TAG,"DailySlot["+i+"]["+j+"] is has passed time "+medTimes[i][j].getJSON());
                    continue;
                }
//
// Log.e(TAG, "Setting alarm: " + medicines.toString());
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST, dailySlots[i][j]);
                alarmIntent.putExtras(bundle);
                PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE[i][j], alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar alarmCalendar = getCalendar(medTimes[i][j]);
//                    Log.e(TAG, "Alarm Setting for: " + alarmCalendar.toString());
                Long timeMillis = (alarmCalendar.getTimeInMillis() - System.currentTimeMillis()) / 1000;
//                Log.e(TAG,"Time Left: "+timeMillis/3600 +" "+timeMillis/60+" ");
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), alarmPendingIntent);

            }
        }


        int count = 0;
        for (Medicine medicine : medicines) {
            if (medicine.getCustomTime() != null) {
                if (medicine.getCustomTime().hasPassed()) {
                    continue;
                }
                count++;
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent i = new Intent(this, AlarmReceiver.class);
                Bundle bundle = new Bundle();
                ArrayList<Medicine> tempMed = new ArrayList<>();
                tempMed.add(medicine);
                bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST, tempMed);
                i.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, CUSTOM_ALARM_START_CODE + count, i, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar alarmCalendar = getCalendar(medicine.getCustomTime());
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
            }
        }
        getSharedPreferences(Constants.INTERNAL_PREF, MODE_PRIVATE).edit().putInt(Constants.CUSTOM_TIME__ALARM_ID_LAST, count).apply();


    }

    private Calendar getCalendar(MedTime medTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, medTime.getHour());
        calendar.set(Calendar.HOUR, medTime.getHour());
        calendar.set(Calendar.MINUTE, medTime.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DATE, Calendar.getInstance().get(Calendar.DATE));
//        Log.e(TAG,"Returning calendar: "+calendar.toString());
        return calendar;
    }

}

