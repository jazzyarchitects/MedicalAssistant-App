package architect.jazzy.medicinereminder.Activities;

import android.content.DialogInterface;
import android.content.Intent;
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

import architect.jazzy.medicinereminder.Fragments.OnlineActivity.RemedyFeedFragment;
import architect.jazzy.medicinereminder.Fragments.OnlineActivity.UserDetailsFragment;
import architect.jazzy.medicinereminder.Models.User;
import architect.jazzy.medicinereminder.R;

public class OnlineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    boolean isUserLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(!User.isUserLoggedIn(this)){
//            startActivity(new Intent(this, RegistrationActivity.class));
//            finish();
//            return;
//        }


        setContentView(R.layout.activity_online);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                displayFragment(new RemedyFeedFragment(), true);
                break;
            case R.id.myDetails:
                if (isUserLoggedIn) {
                    displayFragment(new UserDetailsFragment(), true);
                } else {
                    showLoginAlert();
                }
                break;
            case R.id.logout:
                if (isUserLoggedIn) {
                    User.logout(this);
                    Toast.makeText(getApplicationContext(), "Logged out Successfully", Toast.LENGTH_LONG).show();
                }
                finish();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void showLoginAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity.this);
        builder.setTitle("Login Required")
                .setMessage("You need to login to be able to vote or comment")
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(OnlineActivity.this, RegistrationActivity.class));
                dialogInterface.dismiss();
            }
        })
        .show();
    }
}
