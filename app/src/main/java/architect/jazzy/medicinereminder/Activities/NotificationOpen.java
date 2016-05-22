package architect.jazzy.medicinereminder.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import java.util.ArrayList;

import architect.jazzy.medicinereminder.Adapters.MedicineListAdapter;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.ThisApplication;


public class NotificationOpen extends AppCompatActivity {

    Bundle bundle;
    RecyclerView medicineListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_open);

//        AdView bannerAd=(AdView)this.findViewById(R.id.bannerAdNO);
//        AdRequest adRequest=new AdRequest.Builder().build();
//              //  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//               // .addTestDevice("8143FD5F7B003AB85585893D768C3142")
//
//        bannerAd.loadAd(adRequest);


        bundle=new Bundle();
        bundle=getIntent().getExtras();
        ArrayList<Medicine> medicineList;
        medicineList=bundle.getParcelableArrayList(Constants.MEDICINE_NAME_LIST);
        medicineListView=(RecyclerView)findViewById(R.id.medicineList);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        medicineListView.setHasFixedSize(true);
        medicineListView.setLayoutManager(layoutManager);
        MedicineListAdapter listAdapter=new MedicineListAdapter(this,medicineList);
        medicineListView.setAdapter(listAdapter);

    }

    public static ArrayList<Medicine> getMedicineData(Context context, ArrayList<Medicine> list)
    {
        ArrayList<Medicine> dataSet=new ArrayList<>();
        ArrayList<String> name,imageId;
        DataHandler dataHandler=new DataHandler(context);
        ArrayList<Medicine> medicines=dataHandler.getMedicineList();
        if(medicines!=null)
        {
            for(Medicine medicine: medicines){
                if(!list.contains(medicine.getMedName()))
                    continue;
                dataSet.add(medicine);

            }
        }
        dataHandler.close();
        return  dataSet;
    }




}
