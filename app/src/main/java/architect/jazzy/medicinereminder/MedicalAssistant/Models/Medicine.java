package architect.jazzy.medicinereminder.MedicalAssistant.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.DataHandler;

/**
 * Created by Jibin_ism on 25-Aug-15.
 */
public class Medicine implements Parcelable {

  public static final Creator<Medicine> CREATOR = new Creator<Medicine>() {

    @Override
    public Medicine createFromParcel(Parcel source) {
      return new Medicine(source);
    }

    @Override
    public Medicine[] newArray(int size) {
      return new Medicine[size];
    }
  };
  private static final String TAG = "Medicine";
  long id = (long) 0;
  String medName;
  String doctorId;
  String endDate;
  Integer icon;
  MedTime customTime;
  String note;
  String breakfast, lunch, dinner;
  Boolean sun, mon, tue, wed, thu, fri, sat;

  public Medicine() {
  }

  public Medicine(Parcel in) {
    this.id = in.readLong();
    this.medName = in.readString();
    this.doctorId = in.readString();
    this.sun = Boolean.parseBoolean(in.readString());
    this.mon = Boolean.parseBoolean(in.readString());
    this.tue = Boolean.parseBoolean(in.readString());
    this.wed = Boolean.parseBoolean(in.readString());
    this.thu = Boolean.parseBoolean(in.readString());
    this.fri = Boolean.parseBoolean(in.readString());
    this.sat = Boolean.parseBoolean(in.readString());
    this.endDate = in.readString();
    this.icon = Integer.parseInt(in.readString());
    this.customTime = MedTime.parseTime(in.readString(), in.readString());
    this.note = in.readString();
    this.breakfast = in.readString();
    this.lunch = in.readString();
    this.dinner = in.readString();
  }

  public static Medicine parseJSON(JSONObject jsonObject) {
    Medicine medicine = new Medicine();
    medicine.setId(jsonObject.optLong(DataHandler.MedicineTable.COL_ID));
    medicine.setMedName(jsonObject.optString(DataHandler.MedicineTable.COL_NAME));
    medicine.setIcon(jsonObject.optInt(DataHandler.MedicineTable.COL_ICON));
    medicine.setNote(jsonObject.optString(DataHandler.MedicineTable.COL_NOTES));
    medicine.setEndDate(jsonObject.optString(DataHandler.MedicineTable.COL_END_DATE));
    medicine.setDoctorId(jsonObject.optString(DataHandler.MedicineTable.COL_DOCTOR));
    medicine.setCustomTime(MedTime.parseJSON(jsonObject.optJSONObject(DataHandler.MedicineTable.COL_CUSTOM_TIME)));
    medicine.setBreakfast(jsonObject.optString(DataHandler.MedicineTable.COL_BREAKFAST));
    medicine.setLunch(jsonObject.optString(DataHandler.MedicineTable.COL_LUNCH));
    medicine.setDinner(jsonObject.optString(DataHandler.MedicineTable.COL_DINNER));
    medicine.setDays(Medicine.parseDaysFromJSON(jsonObject.optJSONObject(DataHandler.MedicineTable.DAYS)));
    return medicine;
  }

  private static boolean[] parseDaysFromJSON(JSONObject jsonObject) {
    boolean[] days = new boolean[7];
    days[0] = jsonObject.optBoolean("sunday");
    days[1] = jsonObject.optBoolean("monday");
    days[2] = jsonObject.optBoolean("tuesday");
    days[3] = jsonObject.optBoolean("wednesday");
    days[4] = jsonObject.optBoolean("thursday");
    days[5] = jsonObject.optBoolean("friday");
    days[6] = jsonObject.optBoolean("saturday");
    return days;
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
    dest.writeString(breakfast);
    dest.writeString(lunch);
    dest.writeString(dinner);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public JSONObject getJSON() {
    if (id == 0) {
      Log.e(TAG, "Invalid id: " + id);
      return null;
    }
    JSONObject jsonObject = new JSONObject();
    try {
//            Log.e(TAG,"Medicine "+getMedName()+" DoctorId: "+getDoctorId());
      jsonObject.put(DataHandler.MedicineTable.COL_ID, id);
      jsonObject.put(DataHandler.MedicineTable.COL_NAME, medName);
      jsonObject.put(DataHandler.MedicineTable.COL_ICON, icon);
      jsonObject.put(DataHandler.MedicineTable.COL_NOTES, note);
      jsonObject.put(DataHandler.MedicineTable.COL_END_DATE, endDate);
      jsonObject.put(DataHandler.MedicineTable.COL_DOCTOR, doctorId);
      jsonObject.put(DataHandler.MedicineTable.COL_CUSTOM_TIME, customTime.getJSON());
      jsonObject.put(DataHandler.MedicineTable.COL_BREAKFAST, breakfast);
      jsonObject.put(DataHandler.MedicineTable.COL_LUNCH, lunch);
      jsonObject.put(DataHandler.MedicineTable.COL_DINNER, dinner);
      jsonObject.put(DataHandler.MedicineTable.DAYS, getDaysJSON());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObject;
  }

  public JSONObject toJSON() {
    return getJSON();
  }

  private JSONObject getDaysJSON() {
    JSONObject object = new JSONObject();
    try {
      object.put("sunday", sun);
      object.put("monday", mon);
      object.put("tuesday", tue);
      object.put("wednesday", wed);
      object.put("thursday", thu);
      object.put("friday", fri);
      object.put("saturday", sat);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return object;
  }

  public void setDays(boolean[] day) {
    this.sun = day[0];
    this.mon = day[1];
    this.tue = day[2];
    this.wed = day[3];
    this.thu = day[4];
    this.fri = day[5];
    this.sat = day[6];
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
    return customTime.getHour() >= 0 ? customTime : null;
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
