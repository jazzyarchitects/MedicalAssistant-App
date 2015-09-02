package architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
public class DoctorDetailFragment extends Fragment {

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

        try{
            doctorImageView.setImageURI(doctor.getPhotoUri());
        }catch (Exception e){
            e.printStackTrace();
        }

        doctorNameView.setText(doctor.getName());

        tabLayout=(TabLayout)v.findViewById(R.id.tabLayout);
        viewPager=(ViewPager)v.findViewById(R.id.viewPager);

        ViewPagerAdapter adapter=new ViewPagerAdapter(getFragmentManager(),doctor);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);

        return v;
    }


}
