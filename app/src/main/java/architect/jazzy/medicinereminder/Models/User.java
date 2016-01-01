package architect.jazzy.medicinereminder.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Jibin_ism on 18-Dec-15.
 */
public class User implements Parcelable {

    enum Sex {
        MALE, FEMALE
    }

    private String id = "";
    private String name = "User";
//    private int age = 0;
    private Sex sex = Sex.MALE;
    private String email = "";
    private String mobile = "";
    private Stats stats;
    private ArrayList<String> medicineIds, commentIds, remedyIds;
    private DOB dob;

    public User() {
        stats = new Stats();
        dob=new DOB();
        medicineIds=new ArrayList<>();
        commentIds=new ArrayList<>();
        remedyIds=new ArrayList<>();
    }

    protected User(Parcel in) {
        this.setName(in.readString());
        this.setId(in.readString());
        this.setEmail(in.readString());
        this.setMobile(in.readString());
        this.setDob(in.readInt(), in.readInt(), in.readInt());
        Sex sex=Sex.FEMALE;
        if(in.readString().equalsIgnoreCase("m")){
            sex=Sex.MALE;
        }
        this.setSex(sex);
        this.setRemedyUpvotes(in.readInt());
        this.setRemedyDownvotes(in.readInt());
        this.setDeletedCommentsCount(in.readInt());
        this.setDeletedMedicinesCount(in.readInt());
        this.setDeletedRemediesCount(in.readInt());
        this.setRemediesCount(in.readInt());
        this.setCommentsCount(in.readInt());
        this.setMedicinesCount(in.readInt());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.getName());
        parcel.writeString(this.getId());
        parcel.writeString(this.getEmail());
        parcel.writeString(this.getMobile());
        DOB dob=this.getDob();
        parcel.writeInt(dob.getDd());
        parcel.writeInt(dob.getMm());
        parcel.writeInt(dob.getYyyy());
        String sex="F";
        if(this.getSex()==Sex.MALE){
            sex="M";
        }
        parcel.writeString(sex);
        parcel.writeInt(this.getRemedyUpvotes());
        parcel.writeInt(this.getRemedyDownvotes());
        parcel.writeInt(this.getDeletedCommentsCount());
        parcel.writeInt(this.getDeletedMedicinesCount());
        parcel.writeInt(this.getDeletedRemediesCount());
        parcel.writeInt(this.getRemediesCount());
        parcel.writeInt(this.getCommentsCount());
        parcel.writeInt(this.getMedicinesCount());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static User parseUserResponse(JSONObject jsonObject){
        User user=new User();

        try {
            user.setName(jsonObject.getString("name"));
            JSONObject dob=jsonObject.getJSONObject("dob");
            user.setDob(dob.getInt("dd"), dob.getInt("mm"), dob.getInt("yyyy"));
            switch (jsonObject.getString("sex").toUpperCase()){
                case "F":
                    user.setSex(Sex.FEMALE);
                    break;
                default:
                    user.setSex(Sex.MALE);
                    break;
            }
            user.setEmail(jsonObject.getString("email"));
            user.setMobile(jsonObject.getString("mobile"));
            user.setId(jsonObject.getString("_id"));

            JSONObject stats=jsonObject.getJSONObject("stats");

            JSONObject remedyObject=stats.getJSONObject("remedyVotes");
            user.setRemedyUpvotes(remedyObject.getInt("upvotes"));
            user.setRemedyDownvotes(remedyObject.getInt("downvotes"));

            JSONObject deleted=stats.getJSONObject("deleted");
            user.setDeletedCommentsCount(deleted.getInt("comments"));
            user.setDeletedMedicinesCount(deleted.getInt("medicines"));
            user.setDeletedRemediesCount(deleted.getInt("remedies"));

            user.setMedicinesCount(stats.getInt("medicines"));
            user.setCommentsCount(stats.getInt("comments"));
            user.setRemediesCount(stats.getInt("remedies"));
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return user;
    }

    public static void saveUser(Context context, User user){
        SharedPreferences sharedPreferences=context.getSharedPreferences("UserPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString("name",user.getName());
        editor.putString("email",user.getEmail());
        editor.putString("id", user.getId());

        String sex;
        if(user.getSex()==Sex.MALE){
            sex="M";
        }else{
            sex="F";
        }
        editor.putString("sex", sex);

        editor.putString("mobile",user.getMobile());
        DOB dob=user.getDob();
        editor.putInt("dob.dd", dob.getDd());
        editor.putInt("dob.mm", dob.getMm());
        editor.putInt("dob.yyyy", dob.getYyyy());

        editor.putInt("remedyVotes.upvotes",user.getRemedyUpvotes());
        editor.putInt("remedyVotes.downvotes", user.getRemedyDownvotes());

        editor.putInt("deleted.remedies",user.getDeletedRemediesCount());
        editor.putInt("deleted.comments", user.getDeletedCommentsCount());
        editor.putInt("deleted.medicines", user.getDeletedMedicinesCount());

        editor.putInt("medicineCount", user.getMedicinesCount());
        editor.putInt("remedyCount", user.getRemediesCount());
        editor.putInt("commentCount", user.getCommentsCount());

        editor.putBoolean("loggedIn", true);
        //TODO: save user medicine list, remedyList and commentList

        editor.apply();
    }

    public static User getUser(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("UserPrefs",Context.MODE_PRIVATE);
        User user=new User();
        user.setName(sharedPreferences.getString("name",""));
        user.setEmail(sharedPreferences.getString("email",""));
        user.setId(sharedPreferences.getString("id",""));
        user.setMobile(sharedPreferences.getString("mobile",""));
        Sex sex=Sex.FEMALE;
        if(sharedPreferences.getString("sex","").equalsIgnoreCase("m")) {
            sex = Sex.MALE;
        }
        user.setDob(sharedPreferences.getInt("dob.dd",1), sharedPreferences.getInt("dob.mm",1), sharedPreferences.getInt("dob.yyyy", 1900));
        user.setSex(sex);
        user.setRemediesCount(sharedPreferences.getInt("remedyCount",0));
        user.setCommentsCount(sharedPreferences.getInt("commentCount",0));
        user.setMedicinesCount(sharedPreferences.getInt("medicineCount",0));
        user.setDeletedCommentsCount(sharedPreferences.getInt("deleted.comments",0));
        user.setDeletedRemediesCount(sharedPreferences.getInt("deleted.remedies",0));
        user.setDeletedMedicinesCount(sharedPreferences.getInt("deleted.medicines",0));
        user.setRemedyUpvotes(sharedPreferences.getInt("remedyVotes.upvotes",0));
        user.setRemedyDownvotes(sharedPreferences.getInt("remedyVotes.downvotes",0));

        //TODO: retrieve medicineList, commentList, remedyList from sharedpreferences
        return user;
    }

    public static boolean isUserLoggedIn(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("loggedIn", false);
    }



    public void save(Context context){
        User.saveUser(context, this);
    }





    /* -------------------- Getter Setter Methods -------------------- */
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

//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setRemedyUpvotes(int remedyUpvotes) {
        this.stats.remedyVotes.setUpvotes(remedyUpvotes);
    }

    public int getRemedyUpvotes() {
        return this.stats.remedyVotes.getUpvotes();
    }

    public void setRemedyDownvotes(int remedyDownvotes) {
        this.stats.remedyVotes.setDownvotes(remedyDownvotes);
    }

    public int getRemedyDownvotes() {
        return this.stats.remedyVotes.getDownvotes();
    }

    public void setDeletedRemediesCount(int deletedRemedies) {
        this.stats.deleted.setRemedies(deletedRemedies);
    }

    public int getDeletedRemediesCount() {
        return this.stats.deleted.getRemedies();
    }

    public void setDeletedCommentsCount(int deletedComments) {
        this.stats.deleted.setComments(deletedComments);
    }

    public int getDeletedCommentsCount() {
        return this.stats.deleted.getComments();
    }

    public void setDeletedMedicinesCount(int deletedMedicines) {
        this.stats.deleted.setMedicines(deletedMedicines);
    }

    public int getDeletedMedicinesCount() {
        return this.stats.deleted.getMedicines();
    }


    public int getMedicinesCount() {
        return this.stats.getMedicines();
    }

    public void setMedicinesCount(int medicines) {
        this.stats.setMedicines(medicines);
    }

    public int getCommentsCount() {
        return this.stats.getComments();
    }

    public void setCommentsCount(int comments) {
        this.stats.setComments(comments);
    }

    public int getRemediesCount() {
        return this.stats.getRemedies();
    }

    public void setRemediesCount(int remedies) {
        this.stats.setRemedies(remedies);
    }

    public DOB getDob() {
        return dob;
    }

    public void setDob(DOB dob) {
        this.dob = dob;
    }

    public void setDob(int dd, int mm, int yyyy){
        setDob(new DOB(dd, mm, yyyy));
    }

    public class Stats {
        RemedyVotes remedyVotes;
        Deleted deleted;

        public Stats() {
            remedyVotes = new RemedyVotes();
            deleted = new Deleted();
        }

        private class RemedyVotes {
            private int upvotes = 0;
            private int downvotes = 0;

            public int getUpvotes() {
                return upvotes;
            }

            public void setUpvotes(int upvotes) {
                this.upvotes = upvotes;
            }

            public int getDownvotes() {
                return downvotes;
            }

            public void setDownvotes(int downvotes) {
                this.downvotes = downvotes;
            }
        }

        private class Deleted {
            private int remedies = 0;
            private int medicines = 0;
            private int comments = 0;

            public int getRemedies() {
                return remedies;
            }

            public void setRemedies(int remedies) {
                this.remedies = remedies;
            }

            public int getMedicines() {
                return medicines;
            }

            public void setMedicines(int medicines) {
                this.medicines = medicines;
            }

            public int getComments() {
                return comments;
            }

            public void setComments(int comments) {
                this.comments = comments;
            }
        }

        private int medicines = 0;
        private int comments = 0;
        private int remedies = 0;

        public int getMedicines() {
            return medicines;
        }

        public void setMedicines(int medicines) {
            this.medicines = medicines;
        }

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public int getRemedies() {
            return remedies;
        }

        public void setRemedies(int remedies) {
            this.remedies = remedies;
        }
    }

    public class DOB{
        int dd, mm, yyyy;

        public DOB(int dd, int mm, int yyyy) {
            this.dd = dd;
            this.mm = mm;
            this.yyyy = yyyy;
        }

        public DOB(String dd, String mm, String yyyy) {
            try {
                this.dd = Integer.parseInt(dd);
                this.mm = Integer.parseInt(mm);
                this.yyyy = Integer.parseInt(yyyy);
            }catch (Exception e){
                this.dd=1;
                this.mm=1;
                this.yyyy=1900;
            }
        }

        public DOB() {
        }

        public int getDd() {
            return dd;
        }

        public void setDd(int dd) {
            this.dd = dd;
        }

        public int getMm() {
            return mm;
        }

        public void setMm(int mm) {
            this.mm = mm;
        }

        public int getYyyy() {
            return yyyy;
        }

        public void setYyyy(int yyyy) {
            this.yyyy = yyyy;
        }

        @Override
        public String toString() {
            return this.dd+"-"+this.mm+"-"+this.yyyy;
        }

        /**
         * Get age of user
         * @return pair<month,year>
         */
        public Pair<Integer, Integer> getAge(){
            Calendar calendar=Calendar.getInstance();
            int currentYear=calendar.get(Calendar.YEAR);
            int currentMonth=calendar.get(Calendar.MONTH)+1;

            int diffYear=currentYear-this.yyyy;
            int diffMonth=currentMonth<this.mm?12-currentMonth+this.mm:currentMonth-this.mm;

            return Pair.create(diffMonth, diffYear);

        }
    }

}
