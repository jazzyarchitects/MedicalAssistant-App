package architect.jazzy.medicinereminder.MedicalAssistant.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.MedicalAssistant.Adapters.PopupListAdapter;
import architect.jazzy.medicinereminder.MedicalAssistant.BroadcastRecievers.AlarmReceiver;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.DataHandler;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;
import architect.jazzy.medicinereminder.R;


public class PopupWindow extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

  private static final String TAG = "PopupWindow";
  ArrayList<Medicine> medicines;
  Uri toneUri;
  SharedPreferences sharedPreferences;
  ListView list;
  MaterialRippleLayout snooze, done;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_popup_window);
    medicines = getIntent().getExtras().getParcelableArrayList(Constants.MEDICINE_NAME_LIST);
    list = (ListView) findViewById(R.id.list);
    snooze = (MaterialRippleLayout) findViewById(R.id.snooze);
    snooze.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        snooze10(v);
      }
    });

    done = (MaterialRippleLayout) findViewById(R.id.done);
    done.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        doneAlarm(v);
      }
    });


    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

    toneUri = Uri.parse(sharedPreferences.getString("popup_ringtone", String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))));

    if (!isScreenOn()) {

      Intent i = new Intent(this, FullScreenLockScreen.class);
      Bundle bundle = new Bundle();
      bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST, medicines);
      i.putExtras(bundle);
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

  public void populateMedicineList() {
    DataHandler handler = new DataHandler(this);
    for (Medicine medicine : medicines) {
      if (!handler.doesMedicineExists(medicine)) {
        medicines.remove(medicine);
      }
    }
    handler.close();
    for (Medicine medicine : medicines) {
      Log.e(TAG, "Medicine: " + medicine.toJSON());
    }
    PopupListAdapter adapter = new PopupListAdapter(this, medicines);
    list.setAdapter(adapter);
  }

  public void snooze10(View v) {
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    Intent i = new Intent(this, AlarmReceiver.class);
    Bundle bundle = new Bundle();
    bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST, medicines);
    i.putExtras(bundle);
    PendingIntent alarmservice = PendingIntent.getBroadcast(getApplicationContext(), 12532, i, PendingIntent.FLAG_UPDATE_CURRENT);
    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 60 * 1000, alarmservice);
    Toast.makeText(getBaseContext(), "Alarm snoozed for 10 minutes", Toast.LENGTH_SHORT).show();
    finish();
  }

  public void doneAlarm(View v) {
    finish();
  }

  @Override
  public void onPrepared(final MediaPlayer mp) {
    mp.start();
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(60 * 1000);
          mp.stop();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();
  }
}
