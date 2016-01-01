package architect.jazzy.medicinereminder.Fragments.OfflineActivity.DoctorDetailFragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import architect.jazzy.medicinereminder.Activities.MainActivity;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailFragment extends Fragment {

    View v;
    EditText phone1, phone2, address, hospital, notes, name;
    Button saveButton;
    Doctor doctor;

    public ContactDetailFragment() {
        // Required empty public constructor
    }

    public static ContactDetailFragment newInstance(Doctor doctor){
        ContactDetailFragment fragment=new ContactDetailFragment();
        Bundle args=new Bundle();
        args.putParcelable(Constants.BUNDLE_DOCTOR, doctor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.doctor=getArguments().getParcelable(Constants.BUNDLE_DOCTOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_contact_detail, container, false);
        phone1=(EditText)v.findViewById(R.id.docPhone1);
        phone2=(EditText)v.findViewById(R.id.docPhone2);
        address=(EditText)v.findViewById(R.id.docAddress);
        hospital=(EditText)v.findViewById(R.id.docHospital);
        notes=(EditText)v.findViewById(R.id.docNotes);
        name=(EditText)v.findViewById(R.id.docName);
        saveButton=(Button)v.findViewById(R.id.saveButton);

        phone1.setText(doctor.getPhone_1());
        phone2.setText(doctor.getPhone_2());
        address.setText(doctor.getAddress());
        hospital.setText(doctor.getHospital());
        notes.setText(doctor.getNotes());
        name.setText(doctor.getName());
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDoctor();
            }
        });

        return v;
    }

    private void saveDoctor(){
        if (name.getText().toString().length() < 3) {
            name.setError("Name cannot be less than 3 characters");
            return;
        }
        if (phone1.getText().toString().isEmpty() && phone2.getText().toString().isEmpty()) {
            phone1.setError("Phone number cannot be empty");
            return;
        }

        doctor.setName(name.getText().toString());
        doctor.setPhone_1(phone1.getText().toString());
        Log.d("ContactDetailFragment","Phone 1: "+phone1.getText().toString());
        doctor.setPhone_2(phone2.getText().toString());
        doctor.setAddress(address.getText().toString());
        doctor.setNotes(notes.getText().toString());
        doctor.setHospital(hospital.getText().toString());


        DataHandler dataHandler = new DataHandler(getActivity());
        dataHandler.updateDoctor(doctor);
        dataHandler.close();
        Toast.makeText(getActivity(), "Doctor details saved", Toast.LENGTH_SHORT).show();
        doctorStateListener.onDoctorSaved(doctor);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity)activity).setDoctorDetailImageChangeListener(new MainActivity.DoctorDetailImageChangeListener() {
            @Override
            public void onDoctorImageChanged(int resultCode, Intent data) {
                doctor.setPhotoPath(DoctorDetailFragment.getImagePath(data,getActivity()));
                doctor.setPhotoUri(null);
            }
        });
    }


    DoctorStateListener doctorStateListener;
    public interface DoctorStateListener{
        void onDoctorSaved(Doctor doctor);
    }
    public void setDoctorStateListener(DoctorStateListener doctorStateListener){
        this.doctorStateListener=doctorStateListener;
    }
}
