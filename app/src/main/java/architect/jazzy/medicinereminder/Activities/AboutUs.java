package architect.jazzy.medicinereminder.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.ThisApplication;

public class AboutUs extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);


        Tracker t = ((ThisApplication) getApplication()).getTracker(
                ThisApplication.TrackerName.APP_TRACKER);
        t.setScreenName("AboutUs");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.AppViewBuilder().build());
    }



}
