package architect.jazzy.medicinereminder.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Timer;
import java.util.TimerTask;

import architect.jazzy.medicinereminder.Fragments.OnlineActivity.RemedyFeedFragment;
import architect.jazzy.medicinereminder.Fragments.OnlineActivity.UserDetailsFragment;
import architect.jazzy.medicinereminder.HelperClasses.FirebaseConstants;
import architect.jazzy.medicinereminder.Models.User;
import architect.jazzy.medicinereminder.R;

public class OnlineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    boolean isUserLoggedIn;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_online);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dimNotificationBar();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerLayout = navigationView.getHeaderView(0);
        User user = User.getUser(this);
        if (User.isUserLoggedIn(this)) {
            ((TextView) headerLayout.findViewById(R.id.userName)).setText(user.getName());
            ((TextView) headerLayout.findViewById(R.id.email)).setText(user.getEmail());
        }


        Intent i = getIntent();
        try {
            String s = i.getStringExtra("fragment");
            if (s.equalsIgnoreCase(UserDetailsFragment.TAG)) {
                displayFragment(new UserDetailsFragment(), false);
            } else {
                throw new Exception("null");
            }
        } catch (Exception e) {
            displayFragment(new RemedyFeedFragment(), false);
        }
    }

    int uiOptions;
    private void dimNotificationBar() {
        final View decorView = getWindow().getDecorView();
        uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
//        if( Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
//            uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
//            uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//        }
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        OnlineActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                decorView.setSystemUiVisibility(uiOptions);
                            }
                        });
                    }
                }, 5000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            setSupportActionBar(toolbar);
            getSupportActionBar().show();
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUserLoggedIn = User.isUserLoggedIn(this);
    }

    public void displayFragment(Fragment fragment, boolean add) {
        setSupportActionBar(toolbar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (add) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.remedyFeed:
                FirebaseConstants.Analytics.logCurrentScreen(this, "RemedyFeed");
                displayFragment(new RemedyFeedFragment(), true);
                break;
            case R.id.myDetails:
                FirebaseConstants.Analytics.logCurrentScreen(this, "UserDetails");
                if (isUserLoggedIn) {
                    displayFragment(new UserDetailsFragment(), true);
                } else {
                    showLoginAlert();
                }
                break;
            case R.id.logout:
                if (isUserLoggedIn) {
                    FirebaseConstants.Analytics.logCurrentScreen(this, "Logout");
                    User.logout(this);
                    Toast.makeText(getApplicationContext(), "Logged out Successfully", Toast.LENGTH_LONG).show();
                }
                finish();
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    FirebaseAnalytics firebaseAnalytics;
    Bundle loginAlertAnalyticsBundle;
    void showLoginAlert() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        loginAlertAnalyticsBundle = new Bundle();
        loginAlertAnalyticsBundle.putString(FirebaseConstants.Analytics.BUNDLE_LOGIN_ALERT_SHOWN, "OnlineActivity");
        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity.this);
        builder.setTitle("Login Required")
                .setMessage("You need to login to be able to vote or comment")
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loginAlertAnalyticsBundle.putString(FirebaseConstants.Analytics.BUNDLE_LOGIN_ALERT_ACTION, "Dismiss");
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loginAlertAnalyticsBundle.putString(FirebaseConstants.Analytics.BUNDLE_LOGIN_ALERT_ACTION, "Loggin In");
                        dialogInterface.dismiss();
                        startActivity(new Intent(OnlineActivity.this, RegistrationActivity.class));
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        loginAlertAnalyticsBundle.putString(FirebaseConstants.Analytics.BUNDLE_LOGIN_ALERT_ACTION_METHOD, "Dismiss");
                        firebaseAnalytics.logEvent(FirebaseConstants.Analytics.EVENT_LOGIN_ALERT, loginAlertAnalyticsBundle);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        loginAlertAnalyticsBundle.putString(FirebaseConstants.Analytics.BUNDLE_LOGIN_ALERT_ACTION_METHOD, "Cancel");
                        firebaseAnalytics.logEvent(FirebaseConstants.Analytics.EVENT_LOGIN_ALERT, loginAlertAnalyticsBundle);
                    }
                })
                .show();
    }
}
