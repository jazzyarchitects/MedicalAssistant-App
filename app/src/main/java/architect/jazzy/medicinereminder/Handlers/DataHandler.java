package architect.jazzy.medicinereminder.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.Models.MedTime;
import architect.jazzy.medicinereminder.Models.Medicine;

/**
 * Created by Jibin_ism on 04-Oct-14.
 */
public class DataHandler {

    private static final String TAG = "DataHandler";
    public static final String MEDICINE_NAME = "medName";
    public static final String DATABASE_NAME = "MedicineList";
    public static final String TABLE_NAME = "Reminders";

    public static class MedicineTable {
        public static final int TIME_BEFORE = -1;
        public static final int TIME_AFTER = 1;
        public static final int TIME_NONE = 0;

        public static final String TABLE_NAME = "medicines";
        public static final String COL_ID = "id";
        public static final String COL_NAME = "name";
        public static final String COL_DOCTOR = "doctor";
        public static final String COL_BREAKFAST = "breakfast";
        public static final String COL_LUNCH = "lunch";
        public static final String COL_DINNER = "dinner";
        public static final String COL_SUNDAY = "sunday";
        public static final String COL_MONDAY = "monday";
        public static final String COL_TUESDAY = "tuesday";
        public static final String COL_WEDNESDAY = "wednesday";
        public static final String COL_THURSDAY = "thursday";
        public static final String COL_FRIDAY = "friday";
        public static final String COL_SATURDAY = "saturday";
        public static final String COL_END_DATE = "endDate";
        public static final String COL_ICON = "icon";
        public static final String COL_NOTES = "notes";
        public static final String COL_CUSTOM_TIME = "customTime";
    }

    public static final int DATABASE_VERSION = 3;
    public static final String TABLE_ISSUED_CREATE = "CREATE TABLE IF NOT EXISTS Reminders(medName varchar(255)," +
            " mornTime varchar(255), noonTime varchar(255), nightTime varchar(255), Sunday varchar(255)," +
            " Monday varchar(255), Tuesday varchar(255), Wednesday varchar(255), Thursday varchar(255)," +
            " Friday varchar(255), Saturday varchar(255), startDate varchar(255), endDate varchar(255)," +
            " breakfast varchar(255), lunch varchar(255), dinner varchar(255), icon varchar(255), " +
            "customTimeHour varchar(255), customTimeMinute varchar(255),note varchar(2550))";

    public static final String CREATE_NEW_MEDICINE_TABLE = "CREATE TABLE IF NOT EXISTS " + MedicineTable.TABLE_NAME + "(" +
            MedicineTable.COL_ID + " TEXT, " +
            MedicineTable.COL_NAME + " TEXT, " +
            MedicineTable.COL_DOCTOR + " TEXT, " +
            MedicineTable.COL_BREAKFAST + " TEXT," +
            MedicineTable.COL_LUNCH + " TEXT, " +
            MedicineTable.COL_DINNER + " TEXT, " +
            MedicineTable.COL_SUNDAY + " TEXT, " +
            MedicineTable.COL_MONDAY + " TEXT, " +
            MedicineTable.COL_TUESDAY + " TEXT, " +
            MedicineTable.COL_WEDNESDAY + " TEXT, " +
            MedicineTable.COL_THURSDAY + " TEXT, " +
            MedicineTable.COL_FRIDAY + " TEXT, " +
            MedicineTable.COL_SATURDAY + " TEXT, " +
            MedicineTable.COL_END_DATE + " TEXT, " +
            MedicineTable.COL_ICON + " TEXT, " +
            MedicineTable.COL_NOTES + " varchar(3000), " +
            MedicineTable.COL_CUSTOM_TIME + " TEXT)";

    public static class DoctorTable {
        public static final String TABLE_DOCTOR = "Doctors";
        public static final String COL_ID = "id";
        public static final String COL_NAME = "name";
        public static final String COL_PHONE_1 = "mobile1";
        public static final String COL_PHONE_2 = "mobile2";
        public static final String COL_HOSPITAL = "hospital";
        public static final String COL_ADDRESS = "address";
        public static final String COL_NOTES = "notes";
        public static final String COL_IMAGE_URI = "imageUri";
    }

    public static final String CREATE_TABLE_DOCTORS = "CREATE TABLE IF NOT EXISTS " + DoctorTable.TABLE_DOCTOR + " (" +
            DoctorTable.COL_ID + " text," +
            DoctorTable.COL_NAME + " text," +
            DoctorTable.COL_HOSPITAL + " text," +
            DoctorTable.COL_PHONE_1 + " text," +
            DoctorTable.COL_PHONE_2 + " text," +
            DoctorTable.COL_ADDRESS + " text," +
            DoctorTable.COL_NOTES + " text," +
            DoctorTable.COL_IMAGE_URI + " text)";


    DataBaseHelper dbhelper;
    Context ctx;
    SQLiteDatabase db;

    public DataHandler(Context ctx) {
        this.ctx = ctx;
        dbhelper = new DataBaseHelper(ctx);
        db = dbhelper.getWritableDatabase();
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {

        private Context mcontext;
        private SQLiteDatabase db;

        public DataBaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
            this.mcontext = ctx;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_NEW_MEDICINE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion == 2) {
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN note varchar(2550)");
            }
            if (newVersion != 3)
                return;
            this.db = db;
            db.execSQL(CREATE_NEW_MEDICINE_TABLE);
//            Cursor cursor = db.query(TABLE_NAME, new String[]{MEDICINE_NAME, "mornTime", "noonTime", "nightTime", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "startDate", "endDate", "breakfast", "lunch", "dinner", "icon", "customTimeHour", "customTimeMinute"}, null, null, null, null, null);
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (!cursor.moveToFirst()) {
                return;
            }

            do {
                insertToNewTable(cursor.getString(0)
                        , ""
                        , ""
                        , ""
                        , cursor.getString(4)
                        , cursor.getString(5)
                        , cursor.getString(6)
                        , cursor.getString(7)
                        , cursor.getString(8)
                        , cursor.getString(9)
                        , cursor.getString(10)
                        , cursor.getString(11)
                        , cursor.getString(12)
                        , cursor.getString(13)
                        , cursor.getString(14)
                        , cursor.getString(15)
                        , cursor.getString(16)
                        , cursor.getString(17)
                        , cursor.getString(18)
                        , cursor.getString(19)
                );

            } while (cursor.moveToNext());


            cursor.close();
        }

        private long insertToNewTable(String medName, String mornTime, String noonTime, String nightTime,
                                      String sun, String mon, String tue, String wed, String thu, String fri, String sat,
                                      String startDate, String endDate, String breakfast, String lunch, String dinner,
                                      String icon, String customTimeHour, String customTimeMinute, String note) {
            ContentValues content = new ContentValues();
            content.put(MedicineTable.COL_ID, getId());
            content.put(MedicineTable.COL_NAME, medName);
            content.put(MedicineTable.COL_DOCTOR, "0");
            content.put(MedicineTable.COL_BREAKFAST, breakfast);
            content.put(MedicineTable.COL_LUNCH, lunch);
            content.put(MedicineTable.COL_DINNER, dinner);
            content.put(MedicineTable.COL_SUNDAY, sun);
            content.put(MedicineTable.COL_MONDAY, mon);
            content.put(MedicineTable.COL_TUESDAY, tue);
            content.put(MedicineTable.COL_WEDNESDAY, wed);
            content.put(MedicineTable.COL_THURSDAY, thu);
            content.put(MedicineTable.COL_FRIDAY, fri);
            content.put(MedicineTable.COL_SATURDAY, sat);
            content.put(MedicineTable.COL_CUSTOM_TIME, customTimeHour + "," + customTimeMinute);
            content.put(MedicineTable.COL_NOTES, note);
            content.put(MedicineTable.COL_END_DATE, endDate);
            content.put(MedicineTable.COL_ICON, icon);
            return db.insertOrThrow(MedicineTable.TABLE_NAME, null, content);
        }

        private String getBeforeAfterNone(String s) {
            if (s.equals("before"))
                return String.valueOf(MedicineTable.TIME_BEFORE);
            if (s.equals("after"))
                return String.valueOf(MedicineTable.TIME_AFTER);
            return String.valueOf(MedicineTable.TIME_NONE);
        }

        private String getId() {
            long id = System.currentTimeMillis();
            Cursor c = db.query(MedicineTable.TABLE_NAME, null, MedicineTable.COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
            while (c.moveToFirst() || id == 0) {
                id += (new Random()).nextLong();
                c = db.query(MedicineTable.TABLE_NAME, null, MedicineTable.COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
            }
            c.close();
            return String.valueOf(id);
        }


    }


    @Deprecated
    public DataHandler open() {
        db = dbhelper.getWritableDatabase();
        db.execSQL(CREATE_NEW_MEDICINE_TABLE);
        db.execSQL(CREATE_TABLE_DOCTORS);
        return this;
    }

    public void close() {
        dbhelper.close();
    }

    @Deprecated
    public long insertData(String medName, String doctorId, String sun, String mon, String tue, String wed, String thu, String fri, String sat
            , String endDate, String breakfast, String lunch, String dinner,
                           String icon, String customTimeHour, String customTimeMinute, String note) {

        ContentValues content = new ContentValues();
        content.put(MedicineTable.COL_ID, getId());
        content.put(MedicineTable.COL_NAME, medName);
        content.put(MedicineTable.COL_DOCTOR, doctorId);
        content.put(MedicineTable.COL_BREAKFAST, breakfast);
        content.put(MedicineTable.COL_LUNCH, lunch);
        content.put(MedicineTable.COL_DINNER, dinner);
        content.put(MedicineTable.COL_SUNDAY, sun);
        content.put(MedicineTable.COL_MONDAY, mon);
        content.put(MedicineTable.COL_TUESDAY, tue);
        content.put(MedicineTable.COL_WEDNESDAY, wed);
        content.put(MedicineTable.COL_THURSDAY, thu);
        content.put(MedicineTable.COL_FRIDAY, fri);
        content.put(MedicineTable.COL_SATURDAY, sat);
        content.put(MedicineTable.COL_CUSTOM_TIME, customTimeHour + "," + customTimeMinute);
        content.put(MedicineTable.COL_NOTES, note);
        content.put(MedicineTable.COL_END_DATE, endDate);
        content.put(MedicineTable.COL_ICON, icon);
        return db.insertOrThrow(MedicineTable.TABLE_NAME, null, content);
    }

    public long insertData(Medicine medicine) {

        ContentValues content = new ContentValues();
        content.put(MedicineTable.COL_ID, getId());
        content.put(MedicineTable.COL_NAME, medicine.getMedName());
        content.put(MedicineTable.COL_DOCTOR, medicine.getDoctorId());
        content.put(MedicineTable.COL_BREAKFAST, medicine.getBreakfast());
        content.put(MedicineTable.COL_LUNCH, medicine.getLunch());
        content.put(MedicineTable.COL_DINNER, medicine.getDinner());
        content.put(MedicineTable.COL_SUNDAY, String.valueOf(medicine.getSun()));
        content.put(MedicineTable.COL_MONDAY, String.valueOf(medicine.getMon()));
        content.put(MedicineTable.COL_TUESDAY, String.valueOf(medicine.getTue()));
        content.put(MedicineTable.COL_WEDNESDAY, String.valueOf(medicine.getWed()));
        content.put(MedicineTable.COL_THURSDAY, String.valueOf(medicine.getThu()));
        content.put(MedicineTable.COL_FRIDAY, String.valueOf(medicine.getFri()));
        content.put(MedicineTable.COL_SATURDAY, String.valueOf(medicine.getSat()));
        content.put(MedicineTable.COL_CUSTOM_TIME, medicine.getCustomTime().getHour() + "," + medicine.getCustomTime().getMinute());
        content.put(MedicineTable.COL_NOTES, medicine.getNote());
        content.put(MedicineTable.COL_END_DATE, medicine.getEndDate());
        content.put(MedicineTable.COL_ICON, medicine.getIcon());
        return db.insertOrThrow(MedicineTable.TABLE_NAME, null, content);
    }


    private String getId() {
        long id = System.currentTimeMillis();
        Cursor c = db.query(MedicineTable.TABLE_NAME, null, MedicineTable.COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        while (c.moveToFirst() || id == 0) {
            id += (new Random()).nextLong();
            c = db.query(MedicineTable.TABLE_NAME, null, MedicineTable.COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        }
        c.close();
        return String.valueOf(id);
    }


//    public long insertData(String medName, String mornTime, String noonTime, String nightTime, String sun, String mon, String tue, String wed, String thu, String fri, String sat, String startDate, String endDate, String breakfast, String lunch, String dinner, String icon, String customTimeHour, String customTimeMinute, String note) {
//        ContentValues content = new ContentValues();
//        /*0*/
//        content.put(MEDICINE_NAME, medName);
//        /*1*/
//        content.put("mornTime", mornTime);
//        /*2*/
//        content.put("noonTime", noonTime);
//        /*3*/
//        content.put("nightTime", nightTime);
//        /*4*/
//        content.put("Sunday", sun);
//        /*5*/
//        content.put("Monday", mon);
//        /*6*/
//        content.put("Tuesday", tue);
//        /*7*/
//        content.put("Wednesday", wed);
//        /*8*/
//        content.put("Thursday", thu);
//        /*9*/
//        content.put("Friday", fri);
//        /*10*/
//        content.put("Saturday", sat);
//        /*11*/
//        content.put("startDate", startDate);
//        /*12*/
//        content.put("endDate", endDate);
//        /*13*/
//        content.put("breakfast", breakfast);
//        /*14*/
//        content.put("lunch", lunch);
//        /*15*/
//        content.put("dinner", dinner);
//        /*16*/
//        content.put("icon", icon);
//        /*17*/
//        content.put("customTimeHour", customTimeHour);
//        /*18*/
//        content.put("customTimeMinute", customTimeMinute);
//        /*19*/
//        content.put("note", note);
//
//
//        return db.insertOrThrow(TABLE_NAME, null, content);
//    }

    public ArrayList<Medicine> getMedicineList() {

        ArrayList<Medicine> medicines = new ArrayList<>();
        Cursor c = db.query(MedicineTable.TABLE_NAME, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                medicines.add(getMedicine(c));
            } while (c.moveToNext());
        }

        return medicines;
    }

    private Medicine getMedicine(Cursor c) {
        Medicine medicine = new Medicine();
        medicine.setMedName(c.getString(c.getColumnIndex(MedicineTable.COL_NAME)));
        medicine.setDoctorId(c.getString(c.getColumnIndex(MedicineTable.COL_DOCTOR)));
        medicine.setBreakfast(c.getString(c.getColumnIndex(MedicineTable.COL_BREAKFAST)));
        medicine.setLunch(c.getString(c.getColumnIndex(MedicineTable.COL_LUNCH)));
        medicine.setDinner(c.getString(c.getColumnIndex(MedicineTable.COL_DINNER)));

        medicine.setSun(Boolean.parseBoolean(c.getString(c.getColumnIndex(MedicineTable.COL_SUNDAY))));
        medicine.setMon(Boolean.parseBoolean(c.getString(c.getColumnIndex(MedicineTable.COL_MONDAY))));
        medicine.setTue(Boolean.parseBoolean(c.getString(c.getColumnIndex(MedicineTable.COL_TUESDAY))));
        medicine.setWed(Boolean.parseBoolean(c.getString(c.getColumnIndex(MedicineTable.COL_WEDNESDAY))));
        medicine.setThu(Boolean.parseBoolean(c.getString(c.getColumnIndex(MedicineTable.COL_THURSDAY))));
        medicine.setFri(Boolean.parseBoolean(c.getString(c.getColumnIndex(MedicineTable.COL_FRIDAY))));
        medicine.setSat(Boolean.parseBoolean(c.getString(c.getColumnIndex(MedicineTable.COL_SATURDAY))));

        String custom = c.getString(c.getColumnIndex(MedicineTable.COL_CUSTOM_TIME));
        String[] time = custom.split(",");
        try {
            medicine.setCustomTime(new MedTime(Integer.parseInt(time[0]), Integer.parseInt(time[1])));
        } catch (Exception e) {
            medicine.setCustomTime(new MedTime(-1,-1));
        }

        medicine.setIcon(Integer.parseInt(c.getString(c.getColumnIndex(MedicineTable.COL_ICON))));
        medicine.setNote(c.getString(c.getColumnIndex(MedicineTable.COL_NOTES)));
        medicine.setEndDate(c.getString(c.getColumnIndex(MedicineTable.COL_END_DATE)));

        return medicine;
    }

    public ArrayList<Medicine> getMedicineListByDoctor(Doctor doctor){
        ArrayList<Medicine> medicines=null;
        Cursor c=db.query(MedicineTable.TABLE_NAME,null,MedicineTable.COL_DOCTOR+"=?",new String[]{doctor.getId()},null,null,null);
        if(c.moveToFirst()){
            medicines=new ArrayList<>();
            do {
                medicines.add(getMedicine(c));
            }while (c.moveToNext());
        }
        c.close();
        return medicines;
    }

    public ArrayList<Medicine> getTodaysMedicine(){
        ArrayList<Medicine> medicines=new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        String colName=getTodayColoumName(calendar);
        Cursor c=db.query(MedicineTable.TABLE_NAME,null,colName+"=?",new String[]{"true"},null,null,null);
        if(c.moveToFirst()){
            do{
                medicines.add(getMedicine(c));
            }while (c.moveToNext());
        }
        c.close();
        return medicines;
    }

    private String getTodayColoumName(Calendar calendar){
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY:
                return MedicineTable.COL_SUNDAY;
            case Calendar.MONDAY:
                return MedicineTable.COL_MONDAY;
            case Calendar.TUESDAY:
                return MedicineTable.COL_TUESDAY;
            case Calendar.WEDNESDAY:
                return MedicineTable.COL_WEDNESDAY;
            case Calendar.THURSDAY:
                return MedicineTable.COL_THURSDAY;
            case Calendar.FRIDAY:
                return MedicineTable.COL_FRIDAY;
            case Calendar.SATURDAY:
                return MedicineTable.COL_SATURDAY;
        }
        return MedicineTable.COL_SATURDAY;
    }

    public Medicine findRow(String medName) {
        Cursor c = db.query(MedicineTable.TABLE_NAME, null, MedicineTable.COL_NAME + "=?", new String[]{medName}, null, null, null);
        if (c.moveToFirst()) {
            Medicine medicine = getMedicine(c);
            c.close();
            return medicine;
        }
        return null;
    }


    @Deprecated
    public Cursor returnData() {
        return db.query(TABLE_NAME, new String[]{MEDICINE_NAME, "mornTime", "noonTime", "nightTime", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "startDate", "endDate", "breakfast", "lunch", "dinner", "icon", "customTimeHour", "customTimeMinute"}, null, null, null, null, "medName ASC");
    }

//    @Deprecated
//    public void updateData(String originalName, String medName, String mornTime, String noonTime, String nightTime, String sun, String mon, String tue, String wed, String thu, String fri, String sat, String startDate, String endDate, String breakfast, String lunch, String dinner, String icon, String customTimeHour, String customTimeMinute, String note) {
//        ContentValues content = new ContentValues();
//        /*0*/
//        content.put(MEDICINE_NAME, medName);
//        /*1*/
//        content.put("mornTime", mornTime);
//        /*2*/
//        content.put("noonTime", noonTime);
//        /*3*/
//        content.put("nightTime", nightTime);
//        /*4*/
//        content.put("Sunday", sun);
//        /*5*/
//        content.put("Monday", mon);
//        /*6*/
//        content.put("Tuesday", tue);
//        /*7*/
//        content.put("Wednesday", wed);
//        /*8*/
//        content.put("Thursday", thu);
//        /*9*/
//        content.put("Friday", fri);
//        /*10*/
//        content.put("Saturday", sat);
//        /*11*/
//        content.put("startDate", startDate);
//        /*12*/
//        content.put("endDate", endDate);
//        /*13*/
//        content.put("breakfast", breakfast);
//        /*14*/
//        content.put("lunch", lunch);
//        /*15*/
//        content.put("dinner", dinner);
//        /*16*/
//        content.put("icon", icon);
//        /*17*/
//        content.put("customTimeHour", customTimeHour);
//        /*18*/
//        content.put("customTimeMinute", customTimeMinute);
//        /*19*/
//        content.put("note", note);
//
//        Log.e("Database", "Updated medicine with name " + originalName + " to " + medName);
//        db.update(TABLE_NAME, content, MEDICINE_NAME + " = ?", new String[]{originalName});
//    }

    public void updateData(String originalName, String medName, String doctorId, String sun, String mon, String tue,
                           String wed, String thu, String fri, String sat
            , String endDate, String breakfast, String lunch, String dinner,
                           String customTime, String note) {

        Log.e(TAG, "Times: " + breakfast + " " + lunch + " " + dinner);

        ContentValues content = new ContentValues();
        content.put(MedicineTable.COL_NAME, medName);
        content.put(MedicineTable.COL_DOCTOR, doctorId);
        content.put(MedicineTable.COL_BREAKFAST, breakfast);
        content.put(MedicineTable.COL_LUNCH, lunch);
        content.put(MedicineTable.COL_DINNER, dinner);
        content.put(MedicineTable.COL_SUNDAY, sun);
        content.put(MedicineTable.COL_MONDAY, mon);
        content.put(MedicineTable.COL_TUESDAY, tue);
        content.put(MedicineTable.COL_WEDNESDAY, wed);
        content.put(MedicineTable.COL_THURSDAY, thu);
        content.put(MedicineTable.COL_FRIDAY, fri);
        content.put(MedicineTable.COL_SATURDAY, sat);
        content.put(MedicineTable.COL_CUSTOM_TIME, customTime);
        content.put(MedicineTable.COL_NOTES, note);
        content.put(MedicineTable.COL_END_DATE, endDate);
        db.update(MedicineTable.TABLE_NAME, content, MedicineTable.COL_NAME + " = ?", new String[]{originalName});
    }

    public void clear_database() {
        deleteTable();
    }

    public void deleteTable() {
        db.delete(TABLE_NAME, null, null);
    }


    public boolean deleteRow(String medName) {
//        Log.e("DataHandler","To be deleted: "+key);
        db.execSQL("DELETE FROM " + MedicineTable.TABLE_NAME + " WHERE " + MedicineTable.COL_NAME + "='" + medName + "'");
        return true;
    }

//    @Deprecated
//    public Cursor findRow(String key) {
//        return db.rawQuery("SELECT * FROM Reminders WHERE medName='" + key + "'", null);
//    }

    public Cursor findColumn(String columnName) {
        return db.rawQuery("SELECT " + columnName + " FROM " + MedicineTable.TABLE_NAME, null);
    }

    public Cursor searchDynamic(String pattern) {
        return db.rawQuery("SELECT * FROM " + MedicineTable.TABLE_NAME + " WHERE " + MedicineTable.COL_NAME + " LIKE '%" + pattern + "%'", null);
    }


    /**
     * CRUD operations for Doctor
     **/

    public long insertDoctor(Doctor doctor) {

        return db.insertOrThrow(DoctorTable.TABLE_DOCTOR, null, getContentValues(doctor));
    }

    public long updateDoctor(Doctor doctor) {

        return db.update(DoctorTable.TABLE_DOCTOR, getContentValues(doctor), DoctorTable.COL_ID + "=?", new String[]{doctor.getId()});
    }

    private ContentValues getContentValues(Doctor doctor) {

        ContentValues contentValues = new ContentValues();
        if (doctor.getId().isEmpty())
            contentValues.put(DoctorTable.COL_ID, getDoctorId());
        else
            contentValues.put(DoctorTable.COL_ID, doctor.getId());

        contentValues.put(DoctorTable.COL_NAME, doctor.getName());
        contentValues.put(DoctorTable.COL_PHONE_1, doctor.getPhone_1());
        contentValues.put(DoctorTable.COL_PHONE_2, doctor.getPhone_2());
        contentValues.put(DoctorTable.COL_HOSPITAL, doctor.getHospital());
        contentValues.put(DoctorTable.COL_ADDRESS, doctor.getAddress());
        contentValues.put(DoctorTable.COL_NOTES, doctor.getNotes());
        contentValues.put(DoctorTable.COL_IMAGE_URI, String.valueOf(doctor.getPhotoUri()));
        return contentValues;
    }

    public void deleteDoctor(Doctor doctor) {
        db.delete(DoctorTable.TABLE_DOCTOR, DoctorTable.COL_ID + "=?", new String[]{doctor.getId()});
    }

    public Doctor getDoctor(String id) {
        Doctor doctor = new Doctor();
        Cursor c = db.query(DoctorTable.TABLE_DOCTOR, null, DoctorTable.COL_ID + "=?", new String[]{id}, null, null, null);
        if (!c.moveToFirst()) {
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

    public ArrayList<Doctor> getDoctorList() {
        ArrayList<Doctor> doctors = new ArrayList<>();
        Cursor c = db.query(DoctorTable.TABLE_DOCTOR, null, null, null, null, null, DoctorTable.COL_NAME + " ASC");
        if (c.moveToFirst()) {
            do {
                doctors.add(getDoctor(c.getString(c.getColumnIndex(DoctorTable.COL_ID))));
            } while (c.moveToNext());
        }
        c.close();
        return doctors;
    }

    private String getDoctorId() {
        Random random = new Random();
        int id;
        Cursor c;
        do {
            id = random.nextInt();
            if (id == 0)
                id++;
            c = db.rawQuery("SELECT * FROM " + DoctorTable.TABLE_DOCTOR + " WHERE " + DoctorTable.COL_ID + "=" + id, null);
        } while (c.moveToFirst());
        c.close();
        return String.valueOf(id);
    }

}

