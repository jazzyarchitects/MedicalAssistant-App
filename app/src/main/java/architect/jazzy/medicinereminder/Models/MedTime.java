package architect.jazzy.medicinereminder.Models;

import org.json.JSONException;
import org.json.JSONObject;

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
}
