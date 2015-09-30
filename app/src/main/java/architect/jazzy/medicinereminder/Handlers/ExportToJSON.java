package architect.jazzy.medicinereminder.Handlers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.Models.Medicine;

/**
 * Created by Jibin_ism on 23-Sep-15.
 */
public class ExportToJSON {

    private static final String TAG = "ExportToJSON";
    public static final String FILE_NAME = "medicineBackup.medBackup";
    public static final String PATH = Environment.getExternalStorageDirectory().toString() + "/MedicineReminder/";

    static Activity activity;

    public ExportToJSON(Activity activity2) {
        super();
        activity = activity2;
    }

    public static boolean ExportData(Activity activity) {
        try {
            File folder = new File(PATH);
            folder.mkdirs();

            DataHandler handler = new DataHandler(activity);
            ArrayList<Medicine> medicines = handler.getMedicineList();
            JSONObject backupObject = new JSONObject();
            if (medicines != null) {
                JSONArray medicineArray = new JSONArray();
                for (Medicine medicine : medicines) {
                    medicineArray.put(medicine.toJSON());
                }
                backupObject.put("medicines", medicineArray);
            }

            ArrayList<Doctor> doctors = handler.getDoctorList();
            if (doctors != null) {
                JSONArray doctorsArray = new JSONArray();
                for (Doctor doctor : doctors) {
                    doctorsArray.put(doctor.toJSON());
                }
                backupObject.put("doctors", doctorsArray);
            }

            backupObject.put("settings",getSettings());

            File outputFile = new File(PATH,FILE_NAME);
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            try {
                outputStreamWriter.write(backupObject.toString());
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(activity, "Exported Successfully", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Finished exporting");

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static JSONObject getSettings(){
        JSONObject jsonObject=new JSONObject();
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(activity);
        try {
            jsonObject.put("show_notification", sharedPreferences.getBoolean("show_notification", true));
            jsonObject.put("show_popup", sharedPreferences.getBoolean("show_popup", true));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }


}
