package architect.jazzy.medicinereminder.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Adapters.MedicineDetailsViewPagerAdapter;
import architect.jazzy.medicinereminder.CustomViews.CustomViewPager;
import architect.jazzy.medicinereminder.Fragments.EmojiSelectFragment;
import architect.jazzy.medicinereminder.Fragments.MedicineDetailFragment;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.ThisApplication;

public class MedicineDetails extends AppCompatActivity implements EmojiSelectFragment.OnFragmentInteractionListener
        {

    CustomViewPager viewPager;

    ArrayList<String> dataSet;
    MedicineDetailsViewPagerAdapter pagerAdapter;
    LinearLayout edit,delete;
    TextView editTextView,deleteTextView;
    Toolbar toolbar;
    Boolean isScrollingEnabled=true;
            onEmojiSetListener emojiSetListener=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);


        Tracker t = ((ThisApplication) this.getApplication()).getTracker(
                ThisApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Medicine Details");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.AppViewBuilder().build());

        edit=(LinearLayout)findViewById(R.id.editMedicine);
        delete=(LinearLayout)findViewById(R.id.deleteMedicine);
        toolbar=(Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        dataSet=getIntent().getStringArrayListExtra(Constants.MEDICINE_NAME_LIST);

        int currentPosition;
        try
        {
            currentPosition=getIntent().getIntExtra(Constants.MEDICINE_POSITION,0);
        }
        catch (Exception e)
        {
            currentPosition=0;
        }
        pagerAdapter=new MedicineDetailsViewPagerAdapter(getFragmentManager(),dataSet);

        viewPager= (CustomViewPager)findViewById(R.id.frame);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.setEnabledSwipe(true);


        editTextView=(TextView)findViewById(R.id.editButtonText);
        deleteTextView=(TextView)findViewById(R.id.deleteButtonText);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMedicine();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMedicine();
            }
        });

    }

    private void deleteMedicine(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Confirm delete");
        builder.setMessage("Are you sure you want to delete the selected medicines from the list?");
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int currentIndex=viewPager.getCurrentItem();
                Log.e("Current Fragment",String.valueOf(currentIndex));
                final MedicineDetailFragment medFragment=pagerAdapter.getFragment(currentIndex);
                Log.e("Current Fragment",medFragment.getMedName()+" 55");
                String name=medFragment.getMedName();
                dataSet.remove(name);
                viewPager.removeAllViews();
                viewPager.setAdapter(new MedicineDetailsViewPagerAdapter(getFragmentManager(),dataSet));
                viewPager.setCurrentItem(currentIndex + 1, true);

                DataHandler handler=new DataHandler(MedicineDetails.this);
                handler.open();
                handler.deleteRow(name);
                handler.close();
                if(dataSet.size()==0)
                    finish();
                Toast.makeText(MedicineDetails.this,"Medicine removed",Toast.LENGTH_LONG).show();
            }
        });
        MainActivity.setAlarm(getApplicationContext());
        builder.show();
    }

    private void editMedicine(){
        int currentIndex=viewPager.getCurrentItem();
        final MedicineDetailFragment medFragment=pagerAdapter.getFragment(currentIndex);
        if(isScrollingEnabled)
        {
            /*in save mode*/
            medFragment.edit();
            viewPager.setEnabledSwipe(false);
            isScrollingEnabled=false;
            editTextView.setText("Save");
            deleteTextView.setText("Discard");
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    medFragment.discard();
                    isScrollingEnabled = true;
                    viewPager.setEnabledSwipe(true);
                    editTextView.setText("Edit");
                    deleteTextView.setText("Delete");
                    Toast.makeText(getApplicationContext(), "Changes Discarded", Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            /*save mode exit*/
            medFragment.save();
            viewPager.setEnabledSwipe(true);
            editTextView.setText("Edit");
            deleteTextView.setText("Delete");
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMedicine();
                }
            });
            isScrollingEnabled=true;
        }
        MainActivity.setAlarm(getApplicationContext());
    }

    public void goBack(View v)
    {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        overridePendingTransition(R.anim.detailsopen_detail_show, R.anim.detailsopen_list_hide);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id=item.getItemId();
        switch (id){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

    @Override
    public void onEmojiSelected(int position) {
        if(emojiSetListener!=null){
            emojiSetListener.onEmojiSet(position);
        }
    }

    public  interface  onEmojiSetListener{
        void onEmojiSet(int position);
    }


    public void setEmojiSetListener(onEmojiSetListener onEmojiSetListener){
        this.emojiSetListener=onEmojiSetListener;
    }
}
