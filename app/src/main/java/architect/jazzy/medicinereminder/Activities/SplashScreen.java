package architect.jazzy.medicinereminder.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import architect.jazzy.medicinereminder.HelperClasses.AlarmSetterService;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.ThisApplication;

public class SplashScreen extends ActionBarActivity {
    Typeface fontJA,fontText;
    Thread timer;


    private InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        try{
            fontJA= Typeface.createFromAsset(getAssets(),"calist.ttf");
            fontText=Typeface.createFromAsset(getAssets(),"freescpt.ttf");
            ((TextView)findViewById(R.id.ssmr)).setTypeface(fontJA);
            ((TextView)findViewById(R.id.fullscreen_content)).setTypeface(fontText);

        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
        }

        Tracker t = ((ThisApplication) this.getApplication()).getTracker(
                ThisApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Splash Screen");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.AppViewBuilder().build());



        Intent startAlarmServiceIntent=new Intent(this, AlarmSetterService.class);
        startAlarmServiceIntent.setAction("CANCEL");
        startService(startAlarmServiceIntent);
        startAlarmServiceIntent.setAction("CREATE");
        startService(startAlarmServiceIntent);


        //TODO: uncomment ad

        interstitialAd=new InterstitialAd(SplashScreen.this);
        interstitialAd.setAdUnitId("ca-app-pub-6208186273505028/3306536191");
        AdRequest adRequest=new AdRequest.Builder().build();
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("8143FD5F7B003AB85585893D768C3142");

        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
//               displayInterstitial();
            }
        });

        timer = new Thread()
        {
            @Override
            public void run() {
                try{
                    sleep(1000);
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                }finally {

                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.main_show, R.anim.splash_hide);
                }
            }
        };
        timer.start();
    }

    public void displayInterstitial()
    {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.interrupt();
    }
}
