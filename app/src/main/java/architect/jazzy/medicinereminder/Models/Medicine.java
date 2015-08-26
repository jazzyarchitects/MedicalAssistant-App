package architect.jazzy.medicinereminder.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jibin_ism on 25-Aug-15.
 */
public class Medicine implements Parcelable{

    String medName;
    Boolean sun, mon, tue, wed, thu, fri, sat;
    String doctorId;
    String endDate;
    String breakfast, lunch,dinner;
    Integer icon;
    MedTime customTime;
    String note;

    public Medicine() {
    }

    public Medicine(Parcel in){
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
