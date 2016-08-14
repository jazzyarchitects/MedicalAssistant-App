package architect.jazzy.medicinereminder.MedicalAssistant.Models;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import architect.jazzy.medicinereminder.MedicalAssistant.CustomComponents.TimingClass;
import architect.jazzy.medicinereminder.HelperClasses.Constants;

/**
 * Created by Jibin_ism on 25-Aug-15.
 */
public class MedTime {
    private Integer hour=null, minute=null;

    public MedTime() {
    }

    public MedTime(Integer hour, Integer minute) {
        this.hour = hour;
        this.minute = minute;
    }
    public MedTime(String hour, String minute) {
        if(hour==null){
            hour="-1";
        }
        if(minute==null){
            minute="-1";
        }
        this.hour = Integer.parseInt(hour);
        this.minute = Integer.parseInt(minute);
    }

    public JSONObject getJSON(){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("hour",hour);
            jsonObject.put("minute",minute);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static MedTime parseJSON(JSONObject jsonObject){
        return new MedTime(jsonObject.optString("hour"),jsonObject.optString("minute"));
    }

    public boolean hasPassed(){
        Calendar calendar=Calendar.getInstance();
        return this.getHour()<calendar.get(Calendar.HOUR_OF_DAY) ||( this.getHour() == calendar.get(Calendar.HOUR_OF_DAY) && this.getMinute() <= calendar.get(Calendar.MINUTE));
    }

    public static boolean hasPassed(MedTime medTime){
        Calendar calendar=Calendar.getInstance();
        return medTime.getHour()<calendar.get(Calendar.HOUR_OF_DAY) ||( medTime.getHour() == calendar.get(Calendar.HOUR_OF_DAY) && medTime.getMinute() <= calendar.get(Calendar.MINUTE));
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public static MedTime parseTime(String customHour, String customMinute){
        return new MedTime(Integer.parseInt(customHour),Integer.parseInt(customMinute));
    }

    public static MedTime[][] getDefaultTimes(Context context) {
        SharedPreferences inputPref=context.getSharedPreferences("TimePrefs", Context.MODE_PRIVATE);
        int bbh, bbm, abh, abm, blh, blm, alh, alm, bdh, bdm, adh, adm;
        MedTime[][] medTimes=new MedTime[3][2];

        bbh = inputPref.getInt(Constants.BEFORE_BREAKFAST_HOUR, 8);
        bbm = inputPref.getInt(Constants.BEFORE_BREAKFAST_MINUTE, 0);
        medTimes[0][0]=new MedTime(bbh,bbm);

        abh = inputPref.getInt(Constants.AFTER_BREAKFAST_HOUR, 10);
        abm = inputPref.getInt(Constants.AFTER_BREAKFAST_MINUTE, 0);
        medTimes[0][1]=new MedTime(abh,abm);

        blh = inputPref.getInt(Constants.BEFORE_LUNCH_HOUR, 12);
        blm = inputPref.getInt(Constants.BEFORE_LUNCH_MINUTE, 0);
        medTimes[1][0]=new MedTime(blh, blm);

        alh = inputPref.getInt(Constants.AFTER_LUNCH_HOUR, 14);
        alm = inputPref.getInt(Constants.AFTER_LUNCH_MINUTE, 0);
        medTimes[1][1]=new MedTime(alh,alm);

        bdh = inputPref.getInt(Constants.BEFORE_DINNER_HOUR, 20);
        bdm = inputPref.getInt(Constants.BEFORE_DINNER_MINUTE, 0);
        medTimes[2][0]=new MedTime(bdh,bdm);

        adh = inputPref.getInt(Constants.AFTER_DINNER_HOUR, 22);
        adm = inputPref.getInt(Constants.AFTER_DINNER_MINUTE, 0);
        medTimes[2][1]=new MedTime(adh,adm);

        return medTimes;
    }

    public String toString(boolean is24Hrs){
        return TimingClass.getTime(hour,minute,is24Hrs);
    }

}
