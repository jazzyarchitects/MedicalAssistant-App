package architect.jazzy.medicinereminder.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jibin_ism on 14-Aug-15.
 */
public class Doctor implements Parcelable {
    private String id="",
            name="",phone_1="",phone_2="",address="",hospital="",notes="";
    private Uri photoUri=null;

    public Doctor() {
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
    }

    public Doctor(Parcel in){
        this.id=in.readString();
        this.name=in.readString();
        this.phone_1=in.readString();
        this.phone_2=in.readString();
        this.address=in.readString();
        this.hospital=in.readString();
        this.notes=in.readString();
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

    @Override
    public String toString() {
        return "Doctor: {id:"+id+",name:"+name+", hospital:"+hospital+", phone1:"+phone_1+"," +
                " phone2:"+phone_2+", address:"+address+", notes:"+notes+"}";
    }

}
