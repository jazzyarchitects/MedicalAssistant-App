package architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.Adapters.ViewPagerAdapter;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.ThisApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorDetailFragment extends Fragment implements ViewPagerAdapter.ViewPagerFragmentInteractionListener{

    private static final String TAG="DoctorDetailFragment";
    private static final int COVER_PIC_REQUEST_CODE = 121;
    View v;
    TextView doctorNameView;
    ImageView doctorImageView;
    TabLayout tabLayout;
    ViewPager viewPager;
    Doctor doctor;


    public static DoctorDetailFragment newInstance(Doctor doctor){
        DoctorDetailFragment fragment=new DoctorDetailFragment();
        Bundle args=new Bundle();
        args.putParcelable(Constants.BUNDLE_DOCTOR, doctor);
        fragment.setArguments(args);
        return fragment;
    }

    public DoctorDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctor=getArguments().getParcelable(Constants.BUNDLE_DOCTOR);

        /**Analytics Code*/
        Tracker t = ((ThisApplication) getActivity().getApplication()).getTracker(
                ThisApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Doctor Detail");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.doctor_detail_layout,container,false);

        Log.e(TAG,"OnCreateView" );
        doctorNameView=(TextView)v.findViewById(R.id.doctorName);
        doctorImageView=(ImageView)v.findViewById(R.id.doctorImage);
        tabLayout=(TabLayout)v.findViewById(R.id.tabLayout);
        viewPager=(ViewPager)v.findViewById(R.id.viewPager);

        doctorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select Doctor's Photo"), COVER_PIC_REQUEST_CODE);
            }
        });

        setup();
        setHasOptionsMenu(true);
        return v;
    }

    void setup(){
        Log.e(TAG,"setup");
        try {
            Bitmap bitmap=Constants.getScaledBitmap(doctor.getPhoto(), 100, 100);
            if(bitmap!=null) {
                doctorImageView.setImageBitmap(bitmap);
            }else{
                doctorImageView.setImageResource(R.drawable.userlogin);
            }
        }catch (Exception e){
            e.printStackTrace();
            doctorImageView.setImageResource(R.drawable.userlogin);
        }

        doctorNameView.setText(doctor.getName());


        ViewPagerAdapter adapter=new ViewPagerAdapter(this.getChildFragmentManager(),doctor);
        adapter.setViewPagerFragmentListener(this);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
        Log.e(TAG,"Setup finished");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==COVER_PIC_REQUEST_CODE){
            if(resultCode==Activity.RESULT_OK){
                imageChangeListener.onDoctorImageChange(resultCode,data);
                doctorImageView.setImageBitmap(Constants.getScaledBitmap(getImagePath(data,getActivity()), doctorImageView.getMeasuredWidth(), doctorImageView.getMeasuredHeight()));
            }
        }
    }

    ImageChangeListener imageChangeListener;

    @Override
    public void onDoctorSaved(Doctor doctor) {
        this.doctor=doctor;
        setup();
    }

    public interface ImageChangeListener{
        void onDoctorImageChange(int resultCode, Intent data);
    }
    public void setImageChangeListener(ImageChangeListener imageChangeListener){
        this.imageChangeListener=imageChangeListener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        imageChangeListener=(ImageChangeListener)activity;
    }




    public static String getImagePath(Intent result, Activity activity) {
        Uri imageUri = result.getData();
        String[] filePathColoumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(imageUri, filePathColoumn, null, null, null);
        cursor.moveToFirst();
        int coloumnIndex = cursor.getColumnIndex(filePathColoumn[0]);
        String picturePath = cursor.getString(coloumnIndex);
        cursor.close();
        return picturePath;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.doctor_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.deleteDoctor){
            deleteDoctor();
        }
        return super.onOptionsItemSelected(item);
    }

    void deleteDoctor(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setMessage("Remove this doctor from your list?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataHandler handler=new DataHandler(getActivity());
                        handler.deleteDoctor(doctor);
                        handler.close();
                        dialog.dismiss();
                        getActivity().onBackPressed();
                    }
                })
                .setTitle("Remove doctor?")
                .show();
    }
}
