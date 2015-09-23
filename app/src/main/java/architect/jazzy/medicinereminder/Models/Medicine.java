package architect.jazzy.medicinereminder.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import architect.jazzy.medicinereminder.Handlers.DataHandler;

/**
 * Created by Jibin_ism on 25-Aug-15.
 */
public class Medicine implements Parcelable{

    private static final String TAG="Medicine";
    long id=(long)0;
    String medName;
    String doctorId;
    String endDate;
    Integer icon;
    MedTime customTime;
    String note;
    String breakfast, lunch,dinner;
    Boolean sun, mon, tue, wed, thu, fri, sat;


    public Medicine() {
    }

    public Medicine(Parcel in){
        this.id=in.readLong();
        this.medName=in.readString();
        this.doctorId=in.readString();
        this.sun=Boolean.parseBoolean(in.readString());
        this.mon=Boolean.parseBoolean(in.readString());
        this.tue=Boolean.parseBoolean(in.readString());
        this.wed=Boolean.parseBoolean(in.readString());
        this.thu=Boolean.parseBoolean(in.readString());
        this.fri=Boolean.parseBoolean(in.readString());
        this.sat=Boolean.parseBoolean(in.readString());
        this.endDate=in.readString();
        this.icon=Integer.parseInt(in.readString());
        this.customTime=MedTime.parseTime(in.readString(), in.readString());
        this.note=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(medName);
        dest.writeString(doctorId);
        dest.writeString(String.valueOf(sun));
        dest.writeString(String.valueOf(mon));
        dest.writeString(String.valueOf(tue));
        dest.writeString(String.valueOf(wed));
        dest.writeString(String.valueOf(thu));
        dest.writeString(String.valueOf(fri));
        dest.writeString(String.valueOf(sat));
        dest.writeString(endDate);
        dest.writeString(String.valueOf(icon));
        dest.writeString(String.valueOf(customTime.getHour()));
        dest.writeString(String.valueOf(customTime.getMinute()));
        dest.writeString(note);
    }
    public static Parcelable.Creator<Medicine> CREATOR=new Parcelable.Creator<Medicine>(){

        @Override
        public Medicine createFromParcel(Parcel source) {
            return new Medicine(source);
        }

        @Override
        public Medicine[] newArray(int size) {
            return new Medicine[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    public JSONObject getJSON(){
        if(id==0){
            Log.e(TAG,"Invalid id: "+id);
            return null;
        }
        JSONObject jsonObject=new JSONObject();
        try {
            Log.e(TAG,"Medicine "+getMedName()+" DoctorId: "+getDoctorId());
            jsonObject.put(DataHandler.MedicineTable.COL_ID,id);
            jsonObject.put(DataHandler.MedicineTable.COL_NAME, medName);
            jsonObject.put(DataHandler.MedicineTable.COL_ICON,icon);
            jsonObject.put(DataHandler.MedicineTable.COL_NOTES,note);
            jsonObject.put(DataHandler.MedicineTable.COL_END_DATE,endDate);
            jsonObject.put(DataHandler.MedicineTable.COL_DOCTOR,doctorId);
            jsonObject.put(DataHandler.MedicineTable.COL_CUSTOM_TIME,customTime.getJSON());
            jsonObject.put(DataHandler.MedicineTable.COL_BREAKFAST,breakfast);
            jsonObject.put(DataHandler.MedicineTable.COL_LUNCH,lunch);
            jsonObject.put(DataHandler.MedicineTable.COL_DINNER,dinner);
            jsonObject.put(DataHandler.MedicineTable.DAYS,getDaysJSON());
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject getDaysJSON(){
        JSONObject object=new JSONObject();
        try{
            object.put("sunday",sun);
            object.put("monday",mon);
            object.put("tuesday",tue);
            object.put("wednesday",wed);
            object.put("thursday",thu);
            object.put("friday",fri);
            object.put("saturday",sat);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return object;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public Boolean getSun() {
        return sun;
    }

    public void setSun(Boolean sun) {
        this.sun = sun;
    }

    public Boolean getMon() {
        return mon;
    }

    public void setMon(Boolean mon) {
        this.mon = mon;
    }

    public Boolean getTue() {
        return tue;
    }

    public void setTue(Boolean tue) {
        this.tue = tue;
    }

    public Boolean getWed() {
        return wed;
    }

    public void setWed(Boolean wed) {
        this.wed = wed;
    }

    public Boolean getThu() {
        return thu;
    }

    public void setThu(Boolean thu) {
        this.thu = thu;
    }

    public Boolean getFri() {
        return fri;
    }

    public void setFri(Boolean fri) {
        this.fri = fri;
    }

    public Boolean getSat() {
        return sat;
    }

    public void setSat(Boolean sat) {
        this.sat = sat;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public MedTime getCustomTime() {
        return customTime;
    }

    public void setCustomTime(MedTime customTime) {
        this.customTime = customTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }
}
