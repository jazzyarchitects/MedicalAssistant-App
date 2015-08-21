package architect.jazzy.medicinereminder.HelperClasses;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import architect.jazzy.medicinereminder.Handlers.DataHandler;

public class AlarmSetterService extends IntentService {

    public static final String CREATE="CREATE";
    public static final String CANCEL="CANCEL";
    private IntentFilter matcher;
    DataHandler dataHandler;
    Cursor ctime;

    SharedPreferences inputPref;
    int bbh,bbm,abh,abm,blh,blm,alh,alm,bdh,bdm,adh,adm;

    ArrayList<String>[] medicineList=new ArrayList[6];
    Boolean[] isPresent={false,false,false,false,false,false};
    Bundle[] bundles=new Bundle[6];
    Intent[] intents=new Intent[6];
    int[] hours;
    int[] minutes;
    //int[] hours={21,21,21,21,21,21};
    //int[] minutes={34,35,36,37,38,39};
    int[] seconds={0,0,0,0,0,0};
    int[] milliseconds={0,0,0,0,0,0};

    public AlarmSetterService() {
        super("AlarmSetterService");
        matcher=new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        inputPref = getSharedPreferences("TimePrefs",MODE_PRIVATE);

        getDefaultTime();
        hours=new int[]{bbh,abh,blh,alh,bdh,adh};
        minutes=new int[]{bbm,abm,blm,alm,bdm,adm};



        if (matcher.matchAction(action)) {
            try {
                execute(action);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        AlarmManager alarmManager2=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent start2=new Intent(getApplicationContext(),DailyAlarmStarter.class);
        PendingIntent forNextDay=PendingIntent.getBroadcast(getApplicationContext(),0,start2,PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cdND=Calendar.getInstance();
        cdND.set(Calendar.DATE,Calendar.getInstance().get(Calendar.DATE)+1);
        cdND.set(Calendar.HOUR_OF_DAY,0);
        cdND.set(Calendar.MINUTE,0);
        cdND.set(Calendar.SECOND,1);
        cdND.set(Calendar.MILLISECOND,0);
        alarmManager2.setRepeating(AlarmManager.RTC,cdND.getTimeInMillis(),AlarmManager.INTERVAL_DAY,forNextDay);
    }

    public void initialize() throws ParseException,NullPointerException
    {

        for(int i=0;i<6;i++)
        {
            medicineList[i]=new ArrayList<>();
        }
        dataHandler=new DataHandler(getBaseContext());
        dataHandler.open();
        ctime=dataHandler.returnData();

        int dayToday=Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-Calendar.SUNDAY;
            if(ctime.moveToFirst()) {

                do {
                    try {
                        Boolean dateok, dateok1;
                        Calendar today = Calendar.getInstance();

                        try{
                            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy",Locale.CANADA);
                            Date date = dateFormat.parse(ctime.getString(12));
                            Date currentDate=Calendar.getInstance().getTime();
                            dateok1=date.after(currentDate);
                        }
                        catch (ParseException e)
                        {
                            dateok1=true;
                        }

                        dateok = dateok1;
                        if (today.getTimeInMillis() < System.currentTimeMillis() && dateok1) {
                            dateok = true;
                        }

                        Boolean temp = Boolean.parseBoolean(ctime.getString(4 + dayToday));
                        if (ctime.getString(13).equals("before") && temp && dateok) {
                            isPresent[0] = true;
                            medicineList[0].add(ctime.getString(0));
                        } else if (ctime.getString(13).equals("after") && temp && dateok) {
                            isPresent[1] = true;
                            medicineList[1].add(ctime.getString(0));
                        }
                        if (ctime.getString(14).equals("before") && temp && dateok) {
                            isPresent[2] = true;
                            medicineList[2].add(ctime.getString(0));
                        } else if (ctime.getString(14).equals("after") && temp && dateok) {
                            isPresent[3] = true;
                            medicineList[3].add(ctime.getString(0));
                        }
                        if (ctime.getString(15).equals("before") && temp && dateok) {
                            isPresent[4] = true;
                            medicineList[4].add(ctime.getString(0));
                        } else if (ctime.getString(15).equals("after") && temp && dateok) {
                            isPresent[5] = true;
                            medicineList[5].add(ctime.getString(0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (ctime.moveToNext());
            }
    }


    private void execute(String action) throws ParseException,NullPointerException{
        initialize();
        //Setting the Alarms
        int id;
        for(id=0;id<6;id++)
        {
            if(isPresent[id])
            {
                Calendar calendar=Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,hours[id]);
                calendar.set(Calendar.MINUTE,minutes[id]);
                calendar.set(Calendar.SECOND,seconds[id]);
                calendar.set(Calendar.MILLISECOND,milliseconds[id]);
                AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
                intents[id]=new Intent(getApplicationContext(),AlarmReciever.class);
                intents[id].setData(Uri.parse("Alarm number"+id));
                bundles[id]=new Bundle();
                if(!bundles[id].isEmpty())
                {
                    bundles[id].clear();
                }
                bundles[id].putStringArrayList("MedicineList",medicineList[id]);
                intents[id].putExtras(bundles[id]);
                PendingIntent pendingIntent=PendingIntent.getBroadcast(getBaseContext(), id, intents[id], PendingIntent.FLAG_UPDATE_CURRENT);
                if(action.equals(CREATE) && calendar.getTimeInMillis()>=System.currentTimeMillis())
                {
                    alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                }
                else if(action.equals(CANCEL))
                {
                    alarmManager.cancel(pendingIntent);
                }
            }
        }

        //Set Custom Time
        Cursor custom=dataHandler.returnData();
        if(custom.moveToFirst())
        {
            int id2=100;
            do {
                if(!custom.isNull(17) && !custom.getString(17).equals("null") && !custom.getString(17).isEmpty()){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(custom.getString(17)));
                    calendar.set(Calendar.MINUTE, Integer.valueOf(custom.getString(18)));
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.DATE,Calendar.getInstance().get(Calendar.DATE));
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    Boolean dateok,dateok1;

                    Calendar today=Calendar.getInstance();
                    try{

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.CANADA);
                        Date date=simpleDateFormat.parse(custom.getString(12));
                        Date currentDate=Calendar.getInstance().getTime();
                        dateok1=date.after(currentDate);
                    }
                    catch (Exception e)
                    {
                        dateok1=true;
                    }
                    dateok=dateok1;
                    if(today.getTimeInMillis()<System.currentTimeMillis() && dateok1) {
                        dateok = true;
                    }
                    if(dateok) {
                        Intent customIntent = new Intent(getApplicationContext(), AlarmReciever.class);
                        customIntent.setData(Uri.parse("Alarm number" + id2));
                        Bundle bundle2;
                        bundle2 = new Bundle();
                        if (!bundle2.isEmpty()) {
                            bundle2.clear();
                        }
                        ArrayList<String> customName = new ArrayList<>();
                        customName.add(custom.getString(0));
                        bundle2.putStringArrayList("MedicineList", customName);
                        customIntent.putExtras(bundle2);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id2, customIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        if (action.equals(CREATE) && calendar.getTimeInMillis() >= System.currentTimeMillis()) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        } else if (action.equals(CANCEL)) {
                            alarmManager.cancel(pendingIntent);
                        }
                        customName.clear();
                        id2++;
                    }
                }
            }while(custom.moveToNext());
        }




        dataHandler.close();


    }

    public void getDefaultTime(){
        inputPref = getSharedPreferences(Constants.INPUT_SHARED_PREFS,MODE_PRIVATE);

        bbh=inputPref.getInt(Constants.BEFORE_BREAKFAST_HOUR,8);
        bbm=inputPref.getInt(Constants.BEFORE_BREAKFAST_MINUTE,0);
        abh=inputPref.getInt(Constants.AFTER_BREAKFAST_HOUR,10);
        abm=inputPref.getInt(Constants.AFTER_BREAKFAST_MINUTE,0);

        blh=inputPref.getInt(Constants.BEFORE_LUNCH_HOUR,12);
        blm=inputPref.getInt(Constants.BEFORE_LUNCH_MINUTE,0);
        alh=inputPref.getInt(Constants.AFTER_LUNCH_HOUR,14);
        alm=inputPref.getInt(Constants.AFTER_LUNCH_MINUTE,0);

        bdh=inputPref.getInt(Constants.BEFORE_DINNER_HOUR,20);
        bdm=inputPref.getInt(Constants.BEFORE_DINNER_MINUTE,0);
        adh=inputPref.getInt(Constants.AFTER_DINNER_HOUR,22);
        adm=inputPref.getInt(Constants.AFTER_DINNER_MINUTE,0);

    }



    }

