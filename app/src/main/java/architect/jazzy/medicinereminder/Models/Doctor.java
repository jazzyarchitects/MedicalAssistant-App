package architect.jazzy.medicinereminder.Models;

import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import architect.jazzy.medicinereminder.Handlers.DataHandler;

/**
 * Created by Jibin_ism on 14-Aug-15.
 */
public class Doctor implements Parcelable {
    private String id="",
            name="",phone_1="",phone_2="",address="",hospital="",notes="", photoPath="";
    private int textColor = -1;
    private int backgroundColor = -1;
    private Uri photoUri=null;

    private static final String TAG = "DoctorModel";

    public Doctor() {
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Doctor(String id, String name, String phone_1, String phone_2, String address, String hospital, String notes) {
        this.id = id;
        this.name = name;
        this.phone_1 = phone_1;
        this.phone_2 = phone_2;
        this.address = address;
        this.hospital = hospital;
        this.notes=notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phone_1);
        dest.writeString(this.phone_2);
        dest.writeString(this.address);
        dest.writeString(this.hospital);
        dest.writeString(this.notes);
        dest.writeInt(this.textColor);
        dest.writeInt(this.backgroundColor);
    }

    public Doctor(Parcel in){
        this.id=in.readString();
        this.name=in.readString();
        this.phone_1=in.readString();
        this.phone_2=in.readString();
        this.address=in.readString();
        this.hospital=in.readString();
        this.notes=in.readString();
        this.textColor = in.readInt();
        this.backgroundColor = in.readInt();
    }

    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel source) {
            return new Doctor(source);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public JSONObject getJSON(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(DataHandler.DoctorTable.COL_ID, id);
            jsonObject.put(DataHandler.DoctorTable.COL_NAME,name);
            jsonObject.put(DataHandler.DoctorTable.COL_PHONE_1,phone_1);
            jsonObject.put(DataHandler.DoctorTable.COL_PHONE_2,phone_2);
            jsonObject.put(DataHandler.DoctorTable.COL_ADDRESS,address);
            jsonObject.put(DataHandler.DoctorTable.COL_HOSPITAL,hospital);
            jsonObject.put(DataHandler.DoctorTable.COL_NOTES,notes);
            jsonObject.put(DataHandler.DoctorTable.COL_IMAGE_URI, getPhoto());
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static  Doctor parseJSON(JSONObject jsonObject){
        Doctor doctor=new Doctor();
        doctor.setId(jsonObject.optString(DataHandler.DoctorTable.COL_ID));
        doctor.setName(jsonObject.optString(DataHandler.DoctorTable.COL_NAME));
        doctor.setPhone_1(jsonObject.optString(DataHandler.DoctorTable.COL_PHONE_1));
        doctor.setPhone_2(jsonObject.optString(DataHandler.DoctorTable.COL_PHONE_2));
        doctor.setAddress(jsonObject.optString(DataHandler.DoctorTable.COL_ADDRESS));
        doctor.setHospital(jsonObject.optString(DataHandler.DoctorTable.COL_HOSPITAL));
        doctor.setNotes(jsonObject.optString(DataHandler.DoctorTable.COL_NOTES));
        doctor.setPhoto(jsonObject.optString(DataHandler.DoctorTable.COL_IMAGE_URI));
        return doctor;
    }


    public JSONObject toJSON(){
        return getJSON();
    }


    /**Getter Setter Methods**/

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_1() {
        return phone_1;
    }

    public void setPhone_1(String phone_1) {
        this.phone_1 = phone_1;
    }

    public String getPhone_2() {
        return phone_2;
    }

    public void setPhone_2(String phone_2) {
        this.phone_2 = phone_2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setPhoto(String string){
        try{
            Uri uri=Uri.parse(string);
            this.setPhotoUri(uri);
        }catch (Exception e){
            try {
                this.setPhotoPath(string);
            }catch (Exception e1){
                this.photoPath="";
            }
        }
    }

    public String getPhoto(){
        if(this.getPhotoUri()!=null){
            return this.getPhotoUri().toString();
        }else{
            return this.getPhotoPath();
        }
    }

    @Override
    public String toString() {
        return "Doctor: {id:"+id+",name:"+name+", hospital:"+hospital+", phone1:"+phone_1+"," +
                " phone2:"+phone_2+", address:"+address+", notes:"+notes+"}";
    }

}
