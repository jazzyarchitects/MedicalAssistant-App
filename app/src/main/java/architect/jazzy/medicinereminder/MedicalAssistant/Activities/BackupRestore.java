package architect.jazzy.medicinereminder.MedicalAssistant.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.ExportFragment;
import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.ImportFragment;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.DataHandler;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.ExportToJSON;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Doctor;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;
import architect.jazzy.medicinereminder.R;

public class BackupRestore extends AppCompatActivity implements ImportFragment.ImportConfirmListener,
        ExportFragment.ExportConfirmListener {

    ArrayList<Medicine> medicines;
    ArrayList<Doctor> doctors;
    JSONArray medicineArray, doctorArray;
    JSONObject settingsObject;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Backup and Restore");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            Intent intent = getIntent();
            ContentResolver resolver = getContentResolver();
            Uri uri = intent.getData();
            InputStream inputStream = resolver.openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String fileContent = "";
            while ((line = reader.readLine()) != null) {
                fileContent += line;
            }
            JSONObject jsonObject = new JSONObject(fileContent);
            try {
                medicineArray = jsonObject.optJSONArray("medicines");
                medicines = new ArrayList<>();
                for (int i = 0; i < medicineArray.length(); i++) {
                    medicines.add(Medicine.parseJSON(medicineArray.getJSONObject(i)));
                }
                doctors = new ArrayList<>();
                doctorArray = jsonObject.optJSONArray("doctors");
                for (int i = 0; i < doctorArray.length(); i++) {
                    doctors.add(Doctor.parseJSON(doctorArray.getJSONObject(i)));
                }
                settingsObject = jsonObject.optJSONObject("settings");
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onImportConfirmed() {
        try {
            importBackup();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    DataHandler handler;

    void importBackup() throws JSONException {
        this.deleteDatabase(DataHandler.DATABASE_NAME);
        handler = new DataHandler(this);
        importMedicines();
        importDoctors();
        importSettings();
        handler.close();

        Toast.makeText(this, "Imported Successfully", Toast.LENGTH_LONG).show();
    }

    private void importMedicines() throws JSONException {
        for (int i = 0; i < medicines.size(); i++) {
            handler.insertMedicine(medicines.get(i));
        }
    }

    private void importDoctors() throws JSONException {
        for (int i = 0; i < doctors.size(); i++) {
            handler.insertDoctor(doctors.get(i));
        }
    }

    private void importSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit()
                .putBoolean("show_notification", settingsObject.optBoolean("show_notification"))
                .putBoolean("show_popup", settingsObject.optBoolean("show_popup"))
                .apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_backup_restore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onExportConfirmed() {
        ExportToJSON.ExportData(this);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ImportFragment.newInstance(medicines, doctors);
                case 1:
                    return ExportFragment.newInstance("","");
                default:
                    break;
            }
            return ImportFragment.newInstance(medicines, doctors);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Restore";
                case 1:
                    return "Backup";
                default:
                    return "";
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
