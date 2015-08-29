package architect.jazzy.medicinereminder.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Adapters.NewsListAdapter;
import architect.jazzy.medicinereminder.Fragments.AddDoctorFragment;
import architect.jazzy.medicinereminder.Fragments.AddMedicineFragment;
import architect.jazzy.medicinereminder.Fragments.DaySelectorFragmentDialog;
import architect.jazzy.medicinereminder.Fragments.DoctorListFragment;
import architect.jazzy.medicinereminder.Fragments.EmojiSelectFragment;
import architect.jazzy.medicinereminder.Fragments.MedicineListFragment;
import architect.jazzy.medicinereminder.Fragments.NewsDetailFragment;
import architect.jazzy.medicinereminder.Fragments.NewsListFragment;
import architect.jazzy.medicinereminder.Fragments.SearchFragment;
import architect.jazzy.medicinereminder.HelperClasses.AlarmSetterService;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.FeedItem;
import architect.jazzy.medicinereminder.R;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        EmojiSelectFragment.OnFragmentInteractionListener,  DaySelectorFragmentDialog.OnFragmentInteractionListener,
        MedicineListFragment.FragmentInteractionListener, AddMedicineFragment.FragmentInteractionListener,
        NewsListAdapter.FeedClickListener, DoctorListFragment.OnMenuItemClickListener{

    public static final String TAG="MainActivity";

    final int SHOW_LIST_REQUEST_CODE = 123;
    FrameLayout frameLayout;
    DrawerLayout drawerLayout;
    ActivityClickListener activityClickListener;
    ActivityResultListener activityResultListener;
    EditText searchQuery;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle=new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        frameLayout=(FrameLayout)findViewById(R.id.frame);


        navigationView = (NavigationView)findViewById(R.id.navigationView);
        searchQuery=(EditText)navigationView.findViewById(R.id.searchQuery);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.add).setChecked(true);

        searchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    performSearch(v.getText().toString());
                    searchQuery.setText("");
                }
                return false;
            }
        });

        displayFragment(new AddMedicineFragment(), true);
    }

    private void performSearch(String s){
        displayFragment(SearchFragment.initiate(s),true);
        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if(activityKeyClickListener!=null){
            if(activityKeyClickListener.onBackKeyPressed()){
                return;
            }
        }
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }else if(getFragmentManager().getBackStackEntryCount()>1){
            getFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        AddMedicineFragment.setAlarm(MainActivity.this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id=menuItem.getItemId();

        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        switch (id){
            case R.id.showMedicineList:
                showMedicines();
                break;
            case R.id.action_settings:
                showSettings();
                break;
            case R.id.add:
                addMedicine(false);
                break;
            case R.id.credits:
                showCredits();
                break;
            case R.id.news:
                displayFragment(new NewsListFragment(),true);
                break;
            case R.id.addDoctor:
                displayFragment(new DoctorListFragment(),true);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onEmojiSelected(int position) {
        activityClickListener.emojiClick(position);
    }

    @Override
    public void onDaySelectionChanged(int position, Boolean isChecked) {
        activityClickListener.daySelectionChanged(position, isChecked);
    }

    @Override
    public void addMedicine() {
        addMedicine(false);
    }

    void showSettings(){
        Intent prefIntent = new Intent(this, BasicPreferences.class);
        startActivity(prefIntent);
    }

    void showMedicines(){
        Fragment fragment=new MedicineListFragment();
        displayFragment(fragment, false);
    }

    void addMedicine(boolean add){
        Fragment fragment=new AddMedicineFragment();
        displayFragment(fragment, add);

    }

    public void displayFragment(Fragment fragment){
        displayFragment(fragment,false);
    }

    public void displayFragment(Fragment fragment, boolean add){
        try {
            getSupportActionBar().show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame, fragment);
        if(add) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
//        Log.e("MainActivity", "Back Stack Count after Push:" + getFragmentManager().getBackStackEntryCount());
    }
    void showCredits(){
        startActivity(new Intent(this, Credits.class));
    }


    public static void setAlarm(Context context) {
        Intent startAlarmServiceIntent = new Intent(context, AlarmSetterService.class);
        startAlarmServiceIntent.setAction("CANCEL");
        context.startService(startAlarmServiceIntent);
        startAlarmServiceIntent.setAction("CREATE");
        context.startService(startAlarmServiceIntent);
    }


    @Override
    public void showDetails(int position, ArrayList<String> medicineNames) {
            Intent i = new Intent(this, MedicineDetails.class);
            i.putExtra(Constants.MEDICINE_NAME_LIST, medicineNames);
            i.putExtra(Constants.MEDICINE_POSITION, position);
            startActivityForResult(i, SHOW_LIST_REQUEST_CODE);
    }

    @Override
    public void onFeedClick(FeedItem item) {
        showFeed(item.getUrl());
    }



    public void showDaySelection(View v) {
        activityClickListener.daySelectionClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"Activity Result "+requestCode);
        if (requestCode == SHOW_LIST_REQUEST_CODE) {
            activityResultListener.medicineListActivityResult(requestCode,resultCode,data);
        }
    }

    public void showFeed(String url){
        displayFragment(NewsDetailFragment.getInstance(url),true);
    }
    public void showFeed(String url, boolean isNews){
        displayFragment(NewsDetailFragment.getInstance(url,isNews),true);
    }

    @Override
    public void onAddDoctorClicked() {
        displayFragment(new AddDoctorFragment(),true);
    }

    public interface ActivityClickListener{
        void daySelectionChanged(int position, boolean isCheck);
        void daySelectionClick();
        void emojiClick(int position);
    }

    public interface ActivityResultListener{
        void medicineListActivityResult(int requestCode, int resultCode, Intent data);
    }


    public void setActivityClickListener(ActivityClickListener activityClickListener){
        this.activityClickListener=activityClickListener;
    }

    public void setActivityResultListener(ActivityResultListener activityResultListener){
        this.activityResultListener=activityResultListener;
    }

    ActivityKeyClickListener activityKeyClickListener;
    public interface ActivityKeyClickListener{
        boolean onBackKeyPressed();
    }
    public void setActivityKeyClickListener(ActivityKeyClickListener activityKeyClickListener){
        this.activityKeyClickListener=activityKeyClickListener;
    }


}

