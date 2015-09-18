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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Adapters.NewsListAdapter;
import architect.jazzy.medicinereminder.Fragments.AddDoctorFragment;
import architect.jazzy.medicinereminder.Fragments.AddMedicineFragment;
import architect.jazzy.medicinereminder.Fragments.DashboardFragment;
import architect.jazzy.medicinereminder.CustomViews.DaySelectorFragmentDialog;
import architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.DoctorDetailFragment;
import architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.DoctorMedicineListFragment;
import architect.jazzy.medicinereminder.Fragments.DoctorListFragment;
import architect.jazzy.medicinereminder.Fragments.EmojiSelectFragment;
import architect.jazzy.medicinereminder.Fragments.MedicineListFragment;
import architect.jazzy.medicinereminder.Fragments.NewsFragments.NewsDetailFragment;
import architect.jazzy.medicinereminder.Fragments.NewsFragments.NewsListFragment;
import architect.jazzy.medicinereminder.Fragments.Practo.DoctorSearch;
import architect.jazzy.medicinereminder.Fragments.SearchFragments.SearchFragment;
import architect.jazzy.medicinereminder.HelperClasses.FragmentBackStack;
import architect.jazzy.medicinereminder.Services.AlarmSetterService;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.Models.FeedItem;
import architect.jazzy.medicinereminder.R;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        EmojiSelectFragment.OnFragmentInteractionListener,  DaySelectorFragmentDialog.OnFragmentInteractionListener,
        MedicineListFragment.FragmentInteractionListener, AddMedicineFragment.FragmentInteractionListener,
        NewsListAdapter.FeedClickListener, DoctorListFragment.OnMenuItemClickListener,
        DoctorMedicineListFragment.FragmentInteractionListener, DoctorListFragment.OnFragmentInteractionListenr,
        DoctorDetailFragment.ImageChangeListener, DashboardFragment.OnFragmentInteractionListener{

    public static final String TAG="MainActivity";

    FragmentBackStack fragmentBackStack=new FragmentBackStack();
    final int SHOW_LIST_REQUEST_CODE = 123;
    FrameLayout frameLayout;
    DrawerLayout drawerLayout;
    ActivityClickListener activityClickListener;
    ActivityResultListener activityResultListener;
    EditText searchQuery;
    NavigationView navigationView;
    private InterstitialAd interstitialAd;
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



        Intent startAlarmServiceIntent=new Intent(this, AlarmSetterService.class);
        startAlarmServiceIntent.setAction("CANCEL");
        startService(startAlarmServiceIntent);
        startAlarmServiceIntent.setAction("CREATE");
        startService(startAlarmServiceIntent);


        //TODO: uncomment ad

        interstitialAd=new InterstitialAd(MainActivity.this);
        interstitialAd.setAdUnitId("ca-app-pub-6208186273505028/3306536191");
        AdRequest adRequest=new AdRequest.Builder().build();
        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        //.addTestDevice("8143FD5F7B003AB85585893D768C3142");

//        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
//               displayInterstitial();
            }
        });


        navigationView = (NavigationView)findViewById(R.id.navigationView);
        searchQuery=(EditText)navigationView.findViewById(R.id.searchQuery);
        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.getMenu().findItem(R.id.add).setChecked(true);

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

        displayFragment(new DashboardFragment(), true);
    }
    public void displayInterstitial()
    {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }

    }
    private void performSearch(String s){
        displayFragment(SearchFragment.initiate(s), true);
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
        }else if(!fragmentBackStack.empty()){
            Fragment fragment=fragmentBackStack.pop();
            if(fragment==null){
                Log.e(TAG,"popping support fragment");
                android.support.v4.app.Fragment fragment1=fragmentBackStack.popSupport();
                displaySupportFragment(fragment1,false);
                return;
            }
            Log.e(TAG,"popping fragment");
            displayFragment(fragment,false);
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

        Fragment fragment=null;
        android.support.v4.app.Fragment supportFragment=null;
        try {
            fragment = getFragmentManager().findFragmentById(R.id.frame);
        }catch (Exception e){
            supportFragment=getSupportFragmentManager().findFragmentById(R.id.frame);
        }
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        switch (id){
            case R.id.showMedicineList:
                if(!(fragment instanceof MedicineListFragment)) {
                    showMedicines();
                }
                break;
            case R.id.action_settings:
                showSettings();
                break;
//            case R.id.add:
//                addMedicine(false);
//                break;
            case R.id.credits:
                showCredits();
                break;
            case R.id.news:
                if(!(fragment instanceof NewsListFragment)) {
                    displayFragment(new NewsListFragment(), true);
                }
                break;
            case R.id.addDoctor:
                if(!(fragment instanceof DoctorListFragment)) {
                    displayFragment(new DoctorListFragment(), true);
                }
                break;
            case R.id.circularTest:
                if(!(fragment instanceof DashboardFragment)) {
                    displayFragment(new DashboardFragment(), true);
                }
                break;
            case R.id.practoSearch:
                if(!(supportFragment instanceof DoctorSearch)){
                    displaySupportFragment(new DoctorSearch(), true);
                }
                break;
            default:
                break;
        }
        drawerLayout.closeDrawers();
        return false;
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
        displayFragment(fragment, false);
    }

    public void displayFragment(Fragment fragment, boolean add){
        ((FrameLayout)findViewById(R.id.frame)).removeAllViewsInLayout();
        try {
            getSupportActionBar().show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        Log.e(TAG, "display fragment");
        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame, fragment);
        if(add) {
            fragmentBackStack.push(fragment);
        }
        transaction.commit();
//        Log.e("MainActivity", "Back Stack Count after Push:" + getFragmentManager().getBackStackEntryCount());
    }

    public void displaySupportFragment(android.support.v4.app.Fragment fragment, boolean add){
        try {
            getSupportActionBar().show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        if(fragment==null){
            return;
        }
        try{
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.frame)).commit();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.e(TAG,"display Support fragment: "+fragment.toString());

        android.support.v4.app.FragmentManager fragmentManager=getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame,fragment).commit();
        if(add){
            fragmentBackStack.push(fragment);
        }
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



    public void showDaySelection(View v) {
        activityClickListener.daySelectionClick();
    }


    public void showFeed(String url){
        displayFragment(NewsDetailFragment.getInstance(url), true);
    }

    public void showFeed(String url, boolean isNews){
        displayFragment(NewsDetailFragment.getInstance(url, isNews), true);
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
        addMedicine(true);
    }

    @Override
    public void showNews() {
        displayFragment(new NewsListFragment(),true);
    }

    @Override
    public void onAddDoctorClicked() {
        displayFragment(new AddDoctorFragment(), true);
    }

    @Override
    public void onDoctorSelected(Doctor doctor) {
        displayFragment(DoctorDetailFragment.newInstance(doctor),true);
    }

    @Override
    public void addDoctor() {
        AddDoctorFragment fragment=new AddDoctorFragment();
        displayFragment(fragment,true);
    }

    @Override
    public void onDoctorImageChange(int resultCode, Intent data) {
        if(doctorDetailImageChangeListener!=null){
            doctorDetailImageChangeListener.onDoctorImageChanged(resultCode,data);
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "Activity Result " + requestCode);
        if (requestCode == SHOW_LIST_REQUEST_CODE) {
            activityResultListener.medicineListActivityResult(requestCode,resultCode,data);
        }
    }






    ActivityKeyClickListener activityKeyClickListener;
    DoctorDetailImageChangeListener doctorDetailImageChangeListener;




    public void setDoctorDetailImageChangeListener(DoctorDetailImageChangeListener doctorDetailImageChangeListener){
        this.doctorDetailImageChangeListener=doctorDetailImageChangeListener;
    }

    public void setActivityKeyClickListener(ActivityKeyClickListener activityKeyClickListener){
        this.activityKeyClickListener=activityKeyClickListener;
    }

    public void setActivityResultListener(ActivityResultListener activityResultListener){
        this.activityResultListener=activityResultListener;
    }

    public void setActivityClickListener(ActivityClickListener activityClickListener){
        this.activityClickListener=activityClickListener;
    }




    public interface ActivityClickListener{
        void daySelectionChanged(int position, boolean isCheck);
        void daySelectionClick();
        void emojiClick(int position);
    }

    public interface ActivityResultListener{
        void medicineListActivityResult(int requestCode, int resultCode, Intent data);
    }

    public interface DoctorDetailImageChangeListener{
        void onDoctorImageChanged(int resultCode, Intent data);
    }

    public interface ActivityKeyClickListener{
        boolean onBackKeyPressed();
    }
}

