package architect.jazzy.medicinereminder.Activities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.BroadcastRecievers.AlarmReceiver;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.ThisApplication;


public class FullScreenLockScreen extends AppCompatActivity {

    Cursor c;
    LinearLayout[] displayViews;
    ArrayList<Medicine> medicineList;
    PowerManager.WakeLock wl;
    SharedPreferences sharedPreferences;
    Uri toneUri;
    Ringtone r;
    int notificationId;
    MediaPlayer player;
    RecyclerView medicineListView;
    RelativeLayout base;

    MaterialRippleLayout mute, snooze, done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_lock_screen);


        Tracker t = ((ThisApplication) this.getApplication()).getTracker(
                ThisApplication.TrackerName.APP_TRACKER);
        t.setScreenName("FullScreenLockScreen");
        t.enableAdvertisingIdCollection(true);
        t.enableExceptionReporting(true);
        t.send(new HitBuilders.AppViewBuilder().build());


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        toneUri= Uri.parse(sharedPreferences.getString("popup_ringtone", String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))));
        Log.i("tone", toneUri.toString());

        r=RingtoneManager.getRingtone(this, toneUri);
        r.setStreamType(AudioManager.STREAM_ALARM);
        r.play();

        medicineList = new ArrayList<>();

        medicineList=getIntent().getExtras().getParcelableArrayList(Constants.MEDICINE_NAME_LIST);

        notificationId=getIntent().getIntExtra("NotificationId",0);

        if(r==null || !r.isPlaying())
        {
            TextView stopr=(TextView)findViewById(R.id.stopr);
            ((RelativeLayout)findViewById(R.id.full_base)).removeView(stopr);
        }

        base=(RelativeLayout)findViewById(R.id.full_base);


        WallpaperManager wallpaperManager=WallpaperManager.getInstance(this);
        Drawable screenWallpaper=wallpaperManager.getDrawable();
        BitmapDrawable bitmapDrawable=(BitmapDrawable)screenWallpaper;
        Bitmap wallpaperBitmap=Bitmap.createBitmap(bitmapDrawable.getBitmap());

        RenderScript rs=RenderScript.create(this);
        Allocation input=Allocation.createFromBitmap(rs, wallpaperBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output=Allocation.createTyped(rs, input.getType());
        if(Build.VERSION.SDK_INT>=17) {
            ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            intrinsicBlur.setRadius(20.f);
            intrinsicBlur.setInput(input);
            intrinsicBlur.forEach(output);
            output.copyTo(wallpaperBitmap);
        }

        Drawable drawable=new BitmapDrawable(getResources(),wallpaperBitmap);
        base.setBackgroundDrawable(drawable);

        mute=(MaterialRippleLayout)findViewById(R.id.mute);
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTone(v);
            }
        });

        snooze=(MaterialRippleLayout)findViewById(R.id.snooze);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snoozeFull(v);
            }
        });

        done=(MaterialRippleLayout)findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneFull(v);
            }
        });

        medicineListView=(RecyclerView)findViewById(R.id.medicineList);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        medicineListView.setHasFixedSize(true);
        medicineListView.setLayoutManager(layoutManager);
        MedicineListAdapter listAdapter=new MedicineListAdapter(this, medicineList);
        medicineListView.setAdapter(listAdapter);

    }

    class MedicineListAdapter extends architect.jazzy.medicinereminder.Adapters.MedicineListAdapter{

        Context context;

        public MedicineListAdapter(Context context, ArrayList<Medicine> medicines) {
            super(context, medicines);
            this.context=context;
        }

        @Override
        public architect.jazzy.medicinereminder.Adapters.MedicineListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View v=inflater.inflate(R.layout.listitem_lock_screen,parent,false);
            return new ViewHolder(v);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_VOLUME_UP || keyCode==KeyEvent.KEYCODE_VOLUME_DOWN || keyCode==KeyEvent.KEYCODE_VOLUME_MUTE)
        {
            if(r!=null && r.isPlaying())
                r.stop();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onDestroy() {
        if(player!=null)
        {
            player.stop();
            player.release();
            player=null;
        }
        if(r!=null && r.isPlaying())
            r.stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        stopTone(null);
        super.onBackPressed();
    }

    public void stopTone(View v)
    {

        if(r!=null && r.isPlaying())
            r.stop();
        if(player!=null)
        {
            player.stop();
            player.release();
            player=null;
        }
    }

    public void snoozeFull(View v)
    {
        if(player!=null) {
            player.stop();
            player.release();
            player = null;
        }
        stopTone(v);
        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(this, AlarmReceiver.class);
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST,medicineList);
        i.putExtras(bundle);
        PendingIntent alarmservice=PendingIntent.getBroadcast(getApplicationContext(),12531,i,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 60 * 1000, alarmservice);
        Toast.makeText(getBaseContext(), "Alarm snoozed for 10 minutes", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void doneFull(View v)
    {
        if(player!=null) {
            player.stop();
            player.release();
            player = null;
        }
        stopTone(v);
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        finish();
    }

}
