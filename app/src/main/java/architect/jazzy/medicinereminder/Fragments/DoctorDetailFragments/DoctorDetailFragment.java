package architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.Adapters.ViewPagerAdapter;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorDetailFragment extends Fragment implements ViewPagerAdapter.ViewPagerFragmentInteractionListener{

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.doctor_detail_layout,container,false);

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

        return v;
    }

    void setup(){
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==COVER_PIC_REQUEST_CODE){
            if(resultCode==Activity.RESULT_OK){
                imageChangeListener.onDoctorImageChange(resultCode,data);
                doctorImageView.setImageBitmap(Constants.getScaledBitmap(getImagePath(data), doctorImageView.getMeasuredWidth(), doctorImageView.getMeasuredHeight()));
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




    String getImagePath(Intent result) {
        Uri imageUri = result.getData();
        String[] filePathColoumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(imageUri, filePathColoumn, null, null, null);
        cursor.moveToFirst();
        int coloumnIndex = cursor.getColumnIndex(filePathColoumn[0]);
        String picturePath = cursor.getString(coloumnIndex);
        cursor.close();
        return picturePath;
    }
}
