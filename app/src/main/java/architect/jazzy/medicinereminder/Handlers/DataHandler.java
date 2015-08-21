package architect.jazzy.medicinereminder.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import architect.jazzy.medicinereminder.Models.Doctor;

/**
 * Created by Jibin_ism on 04-Oct-14.
 */
public class DataHandler {
    public static final String MEDICINE_NAME = "medName";
    public static final String DATABASE_NAME = "MedicineList";
    public static final String TABLE_NAME = "Reminders";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_ISSUED_CREATE = "CREATE TABLE IF NOT EXISTS Reminders(medName varchar(255)," +
            " mornTime varchar(255), noonTime varchar(255), nightTime varchar(255), Sunday varchar(255)," +
            " Monday varchar(255), Tuesday varchar(255), Wednesday varchar(255), Thursday varchar(255)," +
            " Friday varchar(255), Saturday varchar(255), startDate varchar(255), endDate varchar(255)," +
            " breakfast varchar(255), lunch varchar(255), dinner varchar(255), icon varchar(255), " +
            "customTimeHour varchar(255), customTimeMinute varchar(255),note varchar(2550))";

    public static class DoctorTable{
        public static final String TABLE_DOCTOR="Doctors";
        public static final String COL_ID="id";
        public static final String COL_NAME="name";
        public static final String COL_PHONE_1="mobile1";
        public static final String COL_PHONE_2="mobile2";
        public static final String COL_HOSPITAL="hospital";
        public static final String COL_ADDRESS="address";
        public static final String COL_NOTES="notes";
        public static final String COL_IMAGE_URI="imageUri";
    }

    public static final String CREATE_TABLE_DOCTORS="CREATE TABLE IF NOT EXISTS "+DoctorTable.TABLE_DOCTOR+" ("+
            DoctorTable.COL_ID+" text,"+
            DoctorTable.COL_NAME+" text,"+
            DoctorTable.COL_HOSPITAL+" text,"+
            DoctorTable.COL_PHONE_1+" text,"+
            DoctorTable.COL_PHONE_2+" text,"+
            DoctorTable.COL_ADDRESS+" text," +
            DoctorTable.COL_NOTES+" text," +
            DoctorTable.COL_IMAGE_URI+" text)";


    DataBaseHelper dbhelper;
    Context ctx;
    SQLiteDatabase db;

    public DataHandler(Context ctx) {
        this.ctx = ctx;
        dbhelper = new DataBaseHelper(ctx);

    }

    private static class DataBaseHelper extends SQLiteOpenHelper {

        private Context mcontext;

        public DataBaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
            this.mcontext=ctx;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_ISSUED_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL("ALTER TABLE "+TABLE_NAME+" ADD COLUMN note varchar(2550)");
            }
        }


    }


    public DataHandler open() {
        db = dbhelper.getWritableDatabase();
        db.execSQL(TABLE_ISSUED_CREATE);
        db.execSQL(CREATE_TABLE_DOCTORS);
        return this;
    }

    public void close(){
        dbhelper.close();
    }

    public long insertData(String medName, String mornTime, String noonTime, String nightTime, String sun, String mon, String tue, String wed, String thu, String fri, String sat, String startDate, String endDate, String breakfast, String lunch, String dinner,String icon,String customTimeHour, String customTimeMinute, String note){
        ContentValues content=new ContentValues();
        /*0*/content.put(MEDICINE_NAME, medName);
        /*1*/content.put("mornTime",mornTime);
        /*2*/content.put("noonTime",noonTime);
        /*3*/content.put("nightTime",nightTime);
        /*4*/content.put("Sunday",sun);
        /*5*/content.put("Monday",mon);
        /*6*/content.put("Tuesday",tue);
        /*7*/content.put("Wednesday",wed);
        /*8*/content.put("Thursday",thu);
        /*9*/content.put("Friday",fri);
        /*10*/content.put("Saturday",sat);
        /*11*/content.put("startDate",startDate);
        /*12*/content.put("endDate",endDate);
        /*13*/content.put("breakfast",breakfast);
        /*14*/content.put("lunch",lunch);
        /*15*/content.put("dinner",dinner);
        /*16*/content.put("icon",icon);
        /*17*/content.put("customTimeHour",customTimeHour);
        /*18*/content.put("customTimeMinute",customTimeMinute);
        /*19*/content.put("note",note);


        return db.insertOrThrow(TABLE_NAME, null, content);
    }

    public Cursor returnData(){
        return db.query(TABLE_NAME, new String[] {MEDICINE_NAME, "mornTime", "noonTime", "nightTime", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday","Saturday","startDate","endDate","breakfast","lunch","dinner","icon","customTimeHour","customTimeMinute"}, null, null, null, null, "medName ASC");
    }

    public void updateData(String originalName, String medName, String mornTime, String noonTime, String nightTime, String sun, String mon, String tue, String wed, String thu, String fri, String sat, String startDate, String endDate, String breakfast, String lunch, String dinner,String icon,String customTimeHour, String customTimeMinute, String note)
    {
        ContentValues content=new ContentValues();
        /*0*/content.put(MEDICINE_NAME, medName);
        /*1*/content.put("mornTime",mornTime);
        /*2*/content.put("noonTime",noonTime);
        /*3*/content.put("nightTime",nightTime);
        /*4*/content.put("Sunday",sun);
        /*5*/content.put("Monday",mon);
        /*6*/content.put("Tuesday",tue);
        /*7*/content.put("Wednesday",wed);
        /*8*/content.put("Thursday",thu);
        /*9*/content.put("Friday",fri);
        /*10*/content.put("Saturday",sat);
        /*11*/content.put("startDate",startDate);
        /*12*/content.put("endDate",endDate);
        /*13*/content.put("breakfast",breakfast);
        /*14*/content.put("lunch",lunch);
        /*15*/content.put("dinner",dinner);
        /*16*/content.put("icon",icon);
        /*17*/content.put("customTimeHour",customTimeHour);
        /*18*/content.put("customTimeMinute",customTimeMinute);
        /*19*/content.put("note",note);

        Log.e("Database","Updated medicine with name "+originalName+" to "+medName);
        db.update(TABLE_NAME,content,MEDICINE_NAME+" = ?",new String[]{originalName});
    }


    public void clear_database(){
        deleteTable();
    }

    public void deleteTable(){
        db.delete(TABLE_NAME, null, null);
    }
    public boolean deleteRow(String key)
    {
//        Log.e("DataHandler","To be deleted: "+key);
       db.execSQL("DELETE FROM Reminders WHERE medName='" + key + "'");
        return true;
    }

    public Cursor findRow(String key)
    {
        return db.rawQuery("SELECT * FROM Reminders WHERE medName='"+key+"'",null);
    }

    public Cursor findColumn(String columnName)
    {
        return  db.rawQuery("SELECT "+columnName+" FROM Reminders",null);
    }

    public Cursor searchDynamic(String pattern)
    {
        return db.rawQuery("SELECT * FROM Reminders WHERE medName LIKE '%" + pattern + "%'", null);
    }




    /**
     * CRUD operations for Doctor
     * **/

    public long insertDoctor(Doctor doctor){
        return db.insertOrThrow(DoctorTable.TABLE_DOCTOR, null, getContentValues(doctor));
    }

    public long updateDoctor(Doctor doctor){
        return db.update(DoctorTable.TABLE_DOCTOR, getContentValues(doctor), DoctorTable.COL_ID + "=?", new String[]{doctor.getId()});
    }

    private ContentValues getContentValues(Doctor doctor){
        ContentValues contentValues=new ContentValues();
        if(doctor.getId().isEmpty())
            contentValues.put(DoctorTable.COL_ID,getDoctorId());
        else
            contentValues.put(DoctorTable.COL_ID,doctor.getId());
        contentValues.put(DoctorTable.COL_NAME,doctor.getName());
        contentValues.put(DoctorTable.COL_PHONE_1,doctor.getPhone_1());
        contentValues.put(DoctorTable.COL_PHONE_2,doctor.getPhone_2());
        contentValues.put(DoctorTable.COL_HOSPITAL,doctor.getHospital());
        contentValues.put(DoctorTable.COL_ADDRESS, doctor.getAddress());
        contentValues.put(DoctorTable.COL_NOTES,doctor.getNotes());
        contentValues.put(DoctorTable.COL_IMAGE_URI,String.valueOf(doctor.getPhotoUri()));
        return contentValues;
    }

    public void deleteDoctor(Doctor doctor){
        db.delete(DoctorTable.TABLE_DOCTOR, DoctorTable.COL_ID + "=?", new String[]{doctor.getId()});
    }

    public Doctor getDoctor(String id){
        Doctor doctor=new Doctor();
        Cursor c=db.query(DoctorTable.TABLE_DOCTOR,null,DoctorTable.COL_ID+"=?",new String[]{id},null,null,null);
        if(!c.moveToFirst()){
            return null;
        }
        doctor.setId(c.getString(0));
        doctor.setName(c.getString(1));
        doctor.setHospital(c.getString(2));
        doctor.setPhone_1(c.getColumnName(3));
        doctor.setPhone_2(c.getString(4));
        doctor.setAddress(c.getString(5));
        doctor.setNotes(c.getString(6));
        doctor.setPhotoUri(Uri.parse(c.getString(7)));
        c.close();
        return doctor;
    }

    public ArrayList<Doctor> getDoctorList(){
        ArrayList<Doctor> doctors=new ArrayList<>();
        Cursor c=db.query(DoctorTable.TABLE_DOCTOR,null,null,null,null,null,DoctorTable.COL_NAME+" ASC");
        if(c.moveToFirst()){
            do{
                doctors.add(getDoctor(c.getString(c.getColumnIndex(DoctorTable.COL_ID))));
            }while (c.moveToNext());
        }
        return doctors;
    }

    private String getDoctorId() {
        Random random=new Random();
        int id;
        Cursor c;
        do{
            id=random.nextInt();
            if(id==0)
                id++;
            c=db.rawQuery("SELECT * FROM " + DoctorTable.TABLE_DOCTOR + " WHERE " + DoctorTable.COL_ID + "=" + id, null);
        }while (c.moveToFirst());
        c.close();
        return String.valueOf(id);
    }

}

