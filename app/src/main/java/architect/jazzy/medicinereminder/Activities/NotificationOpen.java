package architect.jazzy.medicinereminder.Activities;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;

import architect.jazzy.medicinereminder.Adapters.MedicineListAdapter;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.ThisApplication;
import architect.jazzy.medicinereminder.R;


public class NotificationOpen extends ActionBarActivity {

    Bundle bundle;
    RecyclerView medicineListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_open);


        Tracker t = ((ThisApplication) this.getApplication()).getTracker(
                ThisApplication.TrackerName.APP_TRACKER);
        t.setScreenName("NotificationOpen");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.AppViewBuilder().build());



        AdView bannerAd=(AdView)this.findViewById(R.id.bannerAdNO);
        AdRequest adRequest=new AdRequest.Builder().build();
              //  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
               // .addTestDevice("8143FD5F7B003AB85585893D768C3142")

        bannerAd.loadAd(adRequest);


        bundle=new Bundle();
        bundle=getIntent().getExtras();
        ArrayList<String> medicineList;
        medicineList=bundle.getStringArrayList("medicineList");

        medicineListView=(RecyclerView)findViewById(R.id.medicineList);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        medicineListView.setHasFixedSize(true);
        medicineListView.setLayoutManager(layoutManager);
        ArrayList<HashMap<String, ArrayList<String>>> dataSet=NotificationOpen.getMedicineData(this, medicineList);
        MedicineListAdapter listAdapter=new MedicineListAdapter(this,dataSet);
        medicineListView.setAdapter(listAdapter);

    }

    public static ArrayList<HashMap<String, ArrayList<String>>> getMedicineData(Context context, ArrayList<String> list)
    {
        ArrayList<HashMap<String, ArrayList<String>>> dataSet=new ArrayList<>();

        ArrayList<String> name,imageId;
        DataHandler dataHandler=new DataHandler(context);
        dataHandler.open();
        Cursor c=dataHandler.returnData();
        if(c.moveToFirst())
        {
            do{
                if(!list.contains(c.getString(0)))
                    continue;
                HashMap<String, ArrayList<String>> data=new HashMap<>();
                name=new ArrayList<>();
                imageId=new ArrayList<>();

                name.add(c.getString(0));
                imageId.add(c.getString(16));
                data.put(MedicineListAdapter.MEDICINE_NAME, name);
                data.put(MedicineListAdapter.IMAGE_ID,imageId);
                dataSet.add(data);

            }while(c.moveToNext());
        }
        dataHandler.close();
        return  dataSet;
    }




}
