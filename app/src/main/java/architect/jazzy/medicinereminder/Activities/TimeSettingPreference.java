package architect.jazzy.medicinereminder.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


import java.util.Calendar;

import architect.jazzy.medicinereminder.ThisApplication;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.CustomComponents.TimingClass;
import architect.jazzy.medicinereminder.R;


public class TimeSettingPreference extends AppCompatActivity {
    EditText bb,ab,bl,al,bd,ad;
    SharedPreferences inputPref;
    int bbh,bbm,abh,abm,blh,blm,alh,alm,bdh,bdm,adh,adm;
    Boolean is24hrs=false;
    RadioButton is12,is24;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_setting_preference);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Tracker t = ((ThisApplication) this.getApplication()).getTracker(
                ThisApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Time Change Preference");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.AppViewBuilder().build());

        bb=(EditText)findViewById(R.id.pref_beforeBreakfast);
        ab=(EditText)findViewById(R.id.pref_afterBreakfast);
        bl=(EditText)findViewById(R.id.pref_beforeLunch);
        al=(EditText)findViewById(R.id.pref_afterLunch);
        bd=(EditText)findViewById(R.id.pref_beforeNight);
        ad=(EditText)findViewById(R.id.pref_afterNight);

        EditText[] array=new EditText[]{bb,ab,bl,al,bd,ad};
        for(int i=0;i<array.length;i++){
            array[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimeDialog(v);
                }
            });
        }

        is12=(RadioButton)findViewById(R.id.hr12);
        is24=(RadioButton)findViewById(R.id.hr24);


        is12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferences.Editor editor=inputPref.edit();
                    editor.putBoolean(Constants.IS_24_HOURS_FORMAT,false);
                    editor.apply();
                    is24hrs=false;
                    writeTime();
                }
            }
        });

        is24.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferences.Editor editor=inputPref.edit();
                    editor.putBoolean(Constants.IS_24_HOURS_FORMAT,true);
                    editor.apply();
                    is24hrs=true;
                    writeTime();
                }
            }
        });

        inputPref = getSharedPreferences("TimePrefs",MODE_PRIVATE);


        bbh=inputPref.getInt("bbh",8);
        bbm=inputPref.getInt("bbm",0);
        abh=inputPref.getInt("abh",10);
        abm=inputPref.getInt("abm",0);

        blh=inputPref.getInt("blh",12);
        blm=inputPref.getInt("blm",0);
        alh=inputPref.getInt("alh",14);
        alm=inputPref.getInt("alm",0);

        bdh=inputPref.getInt("bdh",20);
        bdm=inputPref.getInt("bdm",0);
        adh=inputPref.getInt("adh",22);
        adm=inputPref.getInt("adm",0);

        is24hrs=inputPref.getBoolean(Constants.IS_24_HOURS_FORMAT,false);
        is12.setChecked(!is24hrs);
        is24.setChecked(is24hrs);
       writeTime();



    }

    public void writeTime()
    {
        bb.setText(TimingClass.getTime(bbh,bbm,is24hrs));
        ab.setText(TimingClass.getTime(abh,abm,is24hrs));
        bl.setText(TimingClass.getTime(blh,blm,is24hrs));
        al.setText(TimingClass.getTime(alh,alm,is24hrs));
        bd.setText(TimingClass.getTime(bdh,bdm,is24hrs));
        ad.setText(TimingClass.getTime(adh,adm,is24hrs));

    }

    public void TimeDialog(View v)
    {
        final int id=v.getId();
        TimePickerDialog.OnTimeSetListener onTimeSetListener=new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                switch (id)
                {
                    case R.id.pref_beforeBreakfast:
                        bbh=hourOfDay;
                        bbm=minute;
                        break;
                    case R.id.pref_afterBreakfast:
                        abh=hourOfDay;
                        abm=minute;
                        break;
                    case R.id.pref_beforeLunch:
                        blh=hourOfDay;
                        blm=minute;
                        break;
                    case R.id.pref_afterLunch:
                        alh=hourOfDay;
                        alm=minute;
                        break;
                    case R.id.pref_beforeNight:
                        bdh=hourOfDay;
                        bdm=minute;
                        break;
                    case R.id.pref_afterNight:
                        adh=hourOfDay;
                        adm=minute;
                        break;
                    default:
                        break;

                }
                writeTime();
            }
        };
        Calendar calendar=Calendar.getInstance();
        TimePickerDialog.newInstance(onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24hrs).show(getFragmentManager(),"TIME_PICKER");

    }

    public void saveTimePrefs(View v)
    {
        SharedPreferences.Editor prefEditor=inputPref.edit();
        prefEditor.putInt("bbh",bbh);
        prefEditor.putInt("bbm",bbm);
        prefEditor.putInt("abh",abh);
        prefEditor.putInt("abm",abm);
        prefEditor.putInt("blh",blh);
        prefEditor.putInt("blm",blm);
        prefEditor.putInt("alh",alh);
        prefEditor.putInt("alm",alm);
        prefEditor.putInt("bdh",bdh);
        prefEditor.putInt("bdm",bdm);
        prefEditor.putInt("adh", adh);
        prefEditor.putInt("adm", adm);
        prefEditor.apply();
        Toast.makeText(getBaseContext(), "Alarm Timings changed successfully",Toast.LENGTH_LONG).show();
        MainActivity.setAlarm(this);
    }


    public void setDefaultTimePrefs(View v)
    {
        SharedPreferences.Editor prefEditor=inputPref.edit();
        bbh=8;
        bbm=0;
        abh=10;
        abm=0;

        blh=12;
        blm=0;
        alh=14;
        alm=0;

        bdh=20;
        bdm=0;
        adh=22;
        adm=0;

        prefEditor.putInt("bbh",bbh);
        prefEditor.putInt("bbm",bbm);
        prefEditor.putInt("abh",abh);
        prefEditor.putInt("abm",abm);
        prefEditor.putInt("blh",blh);
        prefEditor.putInt("blm",blm);
        prefEditor.putInt("alh",alh);
        prefEditor.putInt("alm",alm);
        prefEditor.putInt("bdh",bdh);
        prefEditor.putInt("bdm",bdm);
        prefEditor.putInt("adh",adh);
        prefEditor.putInt("adm",adm);
        prefEditor.apply();
        writeTime();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
