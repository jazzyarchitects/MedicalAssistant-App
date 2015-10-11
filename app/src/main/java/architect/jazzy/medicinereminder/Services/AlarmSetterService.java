package architect.jazzy.medicinereminder.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
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

    private static final String TAG="AlarmSetterService";
    private static final int[][] ALARM_REQUEST_CODE ={{123,124},{133,134},{144,144}};
    private static final int CUSTOM_ALARM_START_CODE=150;
    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";
    private IntentFilter matcher;
    DataHandler dataHandler;
    Cursor ctime;

    SharedPreferences inputPref;
//    int bbh, bbm, abh, abm, blh, blm, alh, alm, bdh, bdm, adh, adm;

    ArrayList<String>[] medicineList = new ArrayList[6];
    Boolean[] isPresent = {false, false, false, false, false, false};
    Bundle[] bundles = new Bundle[6];
    Intent[] intents = new Intent[6];
//    int[] hours;
//    int[] minutes;
    //int[] hours={21,21,21,21,21,21};
    //int[] minutes={34,35,36,37,38,39};
//    int[] seconds = {0, 0, 0, 0, 0, 0};
//    int[] milliseconds = {0, 0, 0, 0, 0, 0};

    MedTime[][] medTimes=new MedTime[3][2];


    public AlarmSetterService() {
        super("AlarmSetterService");
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        inputPref = getSharedPreferences("TimePrefs", MODE_PRIVATE);
        medTimes=MedTime.getDefaultTimes(this);

        /**
         * Cancel all pending alarms
         * **/
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        for(int i1=0;i1<3;i1++) {
            for(int j=0;j<2;j++) {
                PendingIntent pi = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE[i1][j], i, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(pi);
            }
        }
        int customCount=getSharedPreferences(Constants.INTERNAL_PREF,MODE_PRIVATE).getInt(Constants.CUSTOM_TIME__ALARM_ID_LAST,1);
        for(int i1=1;i1<=customCount;i1++){
            PendingIntent pi=PendingIntent.getBroadcast(this, CUSTOM_ALARM_START_CODE+i1,i,PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pi);
        }
        getSharedPreferences(Constants.INTERNAL_PREF,MODE_PRIVATE).edit().putInt(Constants.CUSTOM_TIME__ALARM_ID_LAST,1);
//        Log.e("AlarmSetterService", "Cancelled Alarm");


//        hours = new int[]{bbh, abh, blh, alh, bdh, adh};
//        minutes = new int[]{bbm, abm, blm, alm, bdm, adm};

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

        ArrayList<Medicine>[][] dailySlots=new ArrayList[3][2];
        for(int i=0;i<3;i++){
            for(int j=0;j<2;j++){
                dailySlots[i][j]=new ArrayList<>();
            }
        }
        handler.close();

        for(Medicine medicine:medicines){
            if(medicine.getBreakfast().equalsIgnoreCase(BEFORE)){
                dailySlots[0][0].add(medicine);
            }else if(medicine.getBreakfast().equalsIgnoreCase(AFTER)){
                dailySlots[0][1].add(medicine);
            }
            if(medicine.getLunch().equalsIgnoreCase(BEFORE)){
                dailySlots[1][0].add(medicine);
            }else if (medicine.getLunch().equalsIgnoreCase(AFTER)){
                dailySlots[1][1].add(medicine);
            }
            if(medicine.getDinner().equalsIgnoreCase(BEFORE)){
                dailySlots[2][0].add(medicine);
            }else if(medicine.getDinner().equalsIgnoreCase(AFTER)){
                dailySlots[2][1].add(medicine);
            }
        }

        for(int i=0;i<3;i++){
            for(int j=0;j<2;j++){
                if(dailySlots[i][j]==null){
//                    Log.e(TAG,"DailySlot["+i+"]["+j+"] is null");
                    continue;
                }
                if(dailySlots[i][j].isEmpty()){
//                    Log.e(TAG,"DailySlot["+i+"]["+j+"] is empty");
                    continue;
                }
                if(MedTime.hasPassed(medTimes[i][j])){
//                    Log.e(TAG,"DailySlot["+i+"]["+j+"] is has passed time "+medTimes[i][j].getJSON());
                    continue;
                }else {
//                    Log.e(TAG, "Setting alarm: " + medicines.toString());
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
        }


        int count=0;
        for(Medicine medicine:medicines){
            if(medicine.getCustomTime()!=null){
                if(medicine.getCustomTime().hasPassed()){
                    continue;
                }
                count++;
                AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
                Intent i=new Intent(this, AlarmReceiver.class);
                Bundle bundle=new Bundle();
                ArrayList<Medicine> tempMed=new ArrayList<>();
                tempMed.add(medicine);
                bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST, tempMed);
                i.putExtras(bundle);
                PendingIntent pendingIntent=PendingIntent.getBroadcast(this, CUSTOM_ALARM_START_CODE+count,i,PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar alarmCalendar=getCalendar(medicine.getCustomTime());
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(),pendingIntent);
            }
        }
        getSharedPreferences(Constants.INTERNAL_PREF,MODE_PRIVATE).edit().putInt(Constants.CUSTOM_TIME__ALARM_ID_LAST,count).apply();




    }

    private Calendar getCalendar(MedTime medTime){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,medTime.getHour());
        calendar.set(Calendar.HOUR,medTime.getHour());
        calendar.set(Calendar.MINUTE, medTime.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DATE, Calendar.getInstance().get(Calendar.DATE));
//        Log.e(TAG,"Returning calendar: "+calendar.toString());
        return calendar;
    }

//    public void initialize() throws ParseException, NullPointerException {
//        for (int i = 0; i < 6; i++) {
//            medicineList[i] = new ArrayList<>();
//        }
//        dataHandler = new DataHandler(getBaseContext());
//        ctime = dataHandler.returnData();
//
//        int dayToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
//        if (ctime.moveToFirst()) {
//
//            do {
//                try {
//                    Boolean dateok, dateok1;
//                    Calendar today = Calendar.getInstance();
//
//                    try {
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.CANADA);
//                        Date date = dateFormat.parse(ctime.getString(12));
//                        Date currentDate = Calendar.getInstance().getTime();
//                        dateok1 = date.after(currentDate);
//                    } catch (ParseException e) {
//                        dateok1 = true;
//                    }
//
//                    dateok = dateok1;
//                    if (today.getTimeInMillis() < System.currentTimeMillis() && dateok1) {
//                        dateok = true;
//                    }
//
//                    Boolean temp = Boolean.parseBoolean(ctime.getString(4 + dayToday));
//                    if (ctime.getString(13).equals("before") && temp && dateok) {
//                        isPresent[0] = true;
//                        medicineList[0].add(ctime.getString(0));
//                    } else if (ctime.getString(13).equals("after") && temp && dateok) {
//                        isPresent[1] = true;
//                        medicineList[1].add(ctime.getString(0));
//                    }
//                    if (ctime.getString(14).equals("before") && temp && dateok) {
//                        isPresent[2] = true;
//                        medicineList[2].add(ctime.getString(0));
//                    } else if (ctime.getString(14).equals("after") && temp && dateok) {
//                        isPresent[3] = true;
//                        medicineList[3].add(ctime.getString(0));
//                    }
//                    if (ctime.getString(15).equals("before") && temp && dateok) {
//                        isPresent[4] = true;
//                        medicineList[4].add(ctime.getString(0));
//                    } else if (ctime.getString(15).equals("after") && temp && dateok) {
//                        isPresent[5] = true;
//                        medicineList[5].add(ctime.getString(0));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } while (ctime.moveToNext());
//        }
//    }


//    private void execute(String action) throws ParseException, NullPointerException {
//        initialize();
//        //Setting the Alarms
//        int id;
//        for (id = 0; id < 6; id++) {
//            if (isPresent[id]) {
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(Calendar.HOUR_OF_DAY, hours[id]);
//                calendar.set(Calendar.MINUTE, minutes[id]);
//                calendar.set(Calendar.SECOND, seconds[id]);
//                calendar.set(Calendar.MILLISECOND, milliseconds[id]);
//                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                intents[id] = new Intent(getApplicationContext(), AlarmReceiver.class);
//                intents[id].setData(Uri.parse("Alarm number" + id));
//                bundles[id] = new Bundle();
//                if (!bundles[id].isEmpty()) {
//                    bundles[id].clear();
//                }
//                bundles[id].putStringArrayList("MedicineList", medicineList[id]);
//                intents[id].putExtras(bundles[id]);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intents[id], PendingIntent.FLAG_UPDATE_CURRENT);
//                if (action.equals(CREATE) && calendar.getTimeInMillis() >= System.currentTimeMillis()) {
//                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                } else if (action.equals(CANCEL)) {
//                    alarmManager.cancel(pendingIntent);
//                }
//            }
//        }
//
//        //Set Custom Time
//        Cursor custom = dataHandler.returnData();
//        if (custom.moveToFirst()) {
//            int id2 = 100;
//            do {
//                if (!custom.isNull(17) && !custom.getString(17).equals("null") && !custom.getString(17).isEmpty()) {
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(custom.getString(17)));
//                    calendar.set(Calendar.MINUTE, Integer.valueOf(custom.getString(18)));
//                    calendar.set(Calendar.SECOND, 0);
//                    calendar.set(Calendar.MILLISECOND, 0);
//                    calendar.set(Calendar.DATE, Calendar.getInstance().get(Calendar.DATE));
//                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//                    Boolean dateok, dateok1;
//
//                    Calendar today = Calendar.getInstance();
//                    try {
//
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.CANADA);
//                        Date date = simpleDateFormat.parse(custom.getString(12));
//                        Date currentDate = Calendar.getInstance().getTime();
//                        dateok1 = date.after(currentDate);
//                    } catch (Exception e) {
//                        dateok1 = true;
//                    }
//                    dateok = dateok1;
//                    if (today.getTimeInMillis() < System.currentTimeMillis() && dateok1) {
//                        dateok = true;
//                    }
//                    if (dateok) {
//                        Intent customIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
//                        customIntent.setData(Uri.parse("Alarm number" + id2));
//                        Bundle bundle2;
//                        bundle2 = new Bundle();
//                        if (!bundle2.isEmpty()) {
//                            bundle2.clear();
//                        }
//                        ArrayList<String> customName = new ArrayList<>();
//                        customName.add(custom.getString(0));
//                        bundle2.putStringArrayList("MedicineList", customName);
//                        customIntent.putExtras(bundle2);
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id2, customIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//                        if (action.equals(CREATE) && calendar.getTimeInMillis() >= System.currentTimeMillis()) {
//                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                        } else if (action.equals(CANCEL)) {
//                            alarmManager.cancel(pendingIntent);
//                        }
//                        customName.clear();
//                        id2++;
//                    }
//                }
//            } while (custom.moveToNext());
//        }
//
//
//        dataHandler.close();
//
//
//    }


}

