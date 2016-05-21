package architect.jazzy.medicinereminder.Activities;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import architect.jazzy.medicinereminder.Fragments.OnlineActivity.Registration.LoginFragment;
import architect.jazzy.medicinereminder.Fragments.OnlineActivity.Registration.SignupFragment;
import architect.jazzy.medicinereminder.Models.User;
import architect.jazzy.medicinereminder.R;

public class RegistrationActivity extends AppCompatActivity implements LoginFragment.FragmentInteractionListener,
        SignupFragment.FragmentInteractionListener, LoginFragment.AuthenticationListener, SignupFragment.AuthenticationListener {

    private static final String TAG = "RegistrationActivity";
    Toolbar toolbar;

    Drawable drawable;
    ColorMatrixColorFilter filter;
    ImageView backgroundView;
    @Override
    protected void onResume() {
//        overridePendingTransition(R.anim.fragment_in_from_left, R.anim.fragment_out_from_right);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(User.isUserLoggedIn(this)){
            onUserAuthenticated();
            return;
        }else {
            setContentView(R.layout.activity_registration);
            displayFragment(new LoginFragment(), false);
        }


        backgroundView=(ImageView)findViewById(R.id.background);
        drawable = backgroundView.getDrawable().mutate();
        ColorMatrix colorMatrix=new ColorMatrix();
        colorMatrix.setSaturation(0.2f);
        filter = new ColorMatrixColorFilter(colorMatrix);
        drawable.setColorFilter(filter);
        backgroundView.setImageDrawable(drawable);

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                BitmapDrawable bitmapDrawable=(BitmapDrawable)drawable;

                RenderScript renderScript=RenderScript.create(RegistrationActivity.this);
                Allocation input=Allocation.createFromBitmap(renderScript, bitmapDrawable.getBitmap() , Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
                Allocation output=Allocation.createTyped(renderScript, input.getType());
                if(Build.VERSION.SDK_INT>=17) {
                    ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
                    intrinsicBlur.setRadius(5.0f);
                    intrinsicBlur.setInput(input);
                    intrinsicBlur.forEach(output);
                    output.copyTo(bitmapDrawable.getBitmap());
                }

                drawable=new BitmapDrawable(getResources(),bitmapDrawable.getBitmap());
                drawable.setColorFilter(filter);
                backgroundView.post(new Runnable() {
                    @Override
                    public void run() {
                        backgroundView.setImageDrawable(drawable);
                    }
                });

            }
        });
        thread.start();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(" ");
        }
        dimNotificationBar();

    }


    public void displayFragment(Fragment fragment, boolean add) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (add) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void dimNotificationBar() {
        final View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        RegistrationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                            }
                        });
                    }
                }, 5000);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRegisterNow() {
        displayFragment(new SignupFragment(),false);
    }

    @Override
    public void onLoginNow() {
        displayFragment(new LoginFragment(), false);
    }

    @Override
    public void onUserAuthenticated() {
        //TODO: Todo
        startActivity(new Intent(this, OnlineActivity.class));
        finish();
    }
}
