package architect.jazzy.medicinereminder.MedicalAssistant.Handlers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import architect.jazzy.medicinereminder.MedicalAssistant.Models.Doctor;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;

/**
 * Created by Jibin_ism on 26-Sep-15.
 */
public class ImportBackup {

  public ImportBackup() {
    super();
  }

  public static void ImportData(Activity activity) {
    File inputFile = new File(ExportToJSON.PATH, ExportToJSON.FILE_NAME);

    try {
      FileInputStream fileInputStream = new FileInputStream(inputFile);
      BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
      String line;
      String fileContent = "";
      while ((line = reader.readLine()) != null) {
        fileContent += line;
      }
      JSONObject jsonObject = new JSONObject(fileContent);
      activity.deleteDatabase(DataHandler.DATABASE_NAME);
      DataHandler handler = new DataHandler(activity);

      try {
        JSONArray medicineArray = jsonObject.optJSONArray("medicines");
        for (int i = 0; i < medicineArray.length(); i++) {
          handler.insertMedicine(Medicine.parseJSON(medicineArray.getJSONObject(i)));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        JSONArray doctorArray = jsonObject.optJSONArray("doctors");
        for (int i = 0; i < doctorArray.length(); i++) {
          handler.insertDoctor(Doctor.parseJSON(doctorArray.getJSONObject(i)));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      handler.close();

      try {
        JSONObject settingsObject = jsonObject.optJSONObject("settings");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        preferences.edit()
            .putBoolean("show_notification", settingsObject.optBoolean("show_notification"))
            .putBoolean("show_popup", settingsObject.optBoolean("show_popup"))
            .apply();
      } catch (Exception e) {
        e.printStackTrace();
      }

      Toast.makeText(activity, "Imported Successfully", Toast.LENGTH_LONG).show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
