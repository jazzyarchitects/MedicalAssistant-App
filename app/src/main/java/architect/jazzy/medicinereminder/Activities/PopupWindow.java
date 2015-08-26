package architect.jazzy.medicinereminder.Activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.HashMap;

import architect.jazzy.medicinereminder.Adapters.PopupListAdapter;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.AlarmReciever;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;


public class PopupWindow extends AppCompatActivity implements MediaPlayer.OnPreparedListener{

    ArrayList<String> medicineList;
    Uri toneUri;
    SharedPreferences sharedPreferences;
    ListView list;
    MaterialRippleLayout snooze, done;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_window);
        medicineList = new ArrayList<>();
        medicineList=getIntent().getStringArrayListExtra("MedicineList");
        list=(ListView)findViewById(R.id.list);
        snooze=(MaterialRippleLayout)findViewById(R.id.snooze);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snooze10(v);
            }
        });

        done=(MaterialRippleLayout)findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneAlarm(v);
            }
        });


        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        toneUri= Uri.parse(sharedPreferences.getString("popup_ringtone", String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))));

        if(!isScreenOn())
        {

            Intent i=new Intent(this, FullScreenLockScreen.class);
            i.putStringArrayListExtra("medicineList",medicineList);
            int id=getIntent().getIntExtra("NotificationId",0);
            i.putExtra("NotificationId",id);

            startActivity(i);

            finish();
        }

        populateMedicineList();
        setFinishOnTouchOutside(false);
    }

    public boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void populateMedicineList()
    {
        ArrayList<Medicine> dataSet=new ArrayList<>();
        //medicineList contains the medicine Name  16 0
        DataHandler handler=new DataHandler(this);
        for(int i=0;i<medicineList.size();i++){
            Medicine medicine=handler.findRow(medicineList.get(i));
            dataSet.add(medicine);
        }
        handler.close();
        PopupListAdapter adapter=new PopupListAdapter(this,dataSet);
        list.setAdapter(adapter);
    }

    public void snooze10(View v)
    {
        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(this, AlarmReciever.class);
        Bundle bundle=new Bundle();
        bundle.putStringArrayList("MedicineList",medicineList);
        i.putExtras(bundle);
        PendingIntent alarmservice=PendingIntent.getBroadcast(getApplicationContext(),12532,i,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 60 * 1000, alarmservice);
        Toast.makeText(getBaseContext(),"Alarm snoozed for 10 minutes",Toast.LENGTH_SHORT).show();
        finish();
    }

    public void doneAlarm(View v)
    {
        finish();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
