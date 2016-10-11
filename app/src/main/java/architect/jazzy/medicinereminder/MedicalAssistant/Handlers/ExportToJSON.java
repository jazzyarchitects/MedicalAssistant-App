package architect.jazzy.medicinereminder.MedicalAssistant.Handlers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import architect.jazzy.medicinereminder.MedicalAssistant.Models.Doctor;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;
import architect.jazzy.medicinereminder.RemedySharing.HelperClasses.BackendUrls;
import architect.jazzy.medicinereminder.RemedySharing.Services.BackendInterfacer;

/**
 * Created by Jibin_ism on 23-Sep-15.
 */
public class ExportToJSON {

  public static final String FILE_NAME = "medicineBackup.medBackup";
  public static final String PATH = Environment.getExternalStorageDirectory().toString() + "/MedicineReminder/";
  private static final String TAG = "ExportToJSON";
  static Activity activity;

  public ExportToJSON(Activity activity2) {
    super();
    activity = activity2;
  }

  public static boolean ExportData(final Activity activity) {
    try {
      File folder = new File(PATH);
      if (!folder.exists())
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

      try {
        backupObject.put("settings", getSettings());
      } catch (Exception e) {
        e.printStackTrace();
      }

      File outputFile = new File(PATH, FILE_NAME);
      FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
      try {
        outputStreamWriter.write(backupObject.toString());
        outputStreamWriter.flush();
        outputStreamWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }


      FileInputStream fileInputStream = new FileInputStream(new File(PATH, FILE_NAME));
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
      String s = "";
      String line = null;
      while ((line = bufferedReader.readLine()) != null) {
        s += line;
      }
      bufferedReader.close();
      fileInputStream.close();

      JSONStringer jsonStringer = new JSONStringer();
      HashMap<String, String> dataSet = new HashMap<>();
      dataSet.put("data", jsonStringer.value(s).toString());
      BackendInterfacer interfacer = new BackendInterfacer(activity, BackendUrls.UPLOAD_APP_DATA, "POST", dataSet);
      interfacer.setBackendListener(new BackendInterfacer.BackendListener() {
        ProgressDialog dialog;

        @Override
        public void onPreExecute() {
          dialog = new ProgressDialog(activity);
          dialog.setIndeterminate(true);
          dialog.setMessage("Backing up data on server");
          dialog.setTitle("Backup");
          dialog.show();
        }

        @Override
        public void onError(Exception e) {

        }

        @Override
        public void onResult(String result) {
          if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
          }
          Toast.makeText(activity, "Exported Successfully", Toast.LENGTH_LONG).show();
        }
      });
      interfacer.execute();


      Log.e(TAG, "Finished exporting");

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }


  private static JSONObject getSettings() {
    JSONObject jsonObject = new JSONObject();
    try {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
      jsonObject.put("show_notification", sharedPreferences.getBoolean("show_notification", true));
      jsonObject.put("show_popup", sharedPreferences.getBoolean("show_popup", true));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObject;
  }


}
