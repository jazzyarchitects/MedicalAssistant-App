package architect.jazzy.medicinereminder.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDoctorFragment extends Fragment {

    View v;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final String TAG = "AddDoctorFragment";
    public static final int CONTACT_PICK_REQUEST_CODE = 121;
    private Uri uriContact;
    private String contactID;
    ImageView imageView;
    Doctor doctor;

    Button save;
    EditText docName, docPhone1, docPhone2, docAddress, docHospital, docNotes;

    public static AddDoctorFragment newInstance(Doctor doctor) {
        AddDoctorFragment fragment = new AddDoctorFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.BUNDLE_DOCTOR, doctor);
        fragment.setArguments(args);
        return fragment;
    }

    public AddDoctorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_doctor, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add Doctor");


        imageView = (ImageView) v.findViewById(R.id.docPic);
        docAddress = (EditText) v.findViewById(R.id.docAddress);
        docHospital = (EditText) v.findViewById(R.id.docHospital);
        docName = (EditText) v.findViewById(R.id.docName);
        docPhone1 = (EditText) v.findViewById(R.id.docPhone1);
        docPhone2 = (EditText) v.findViewById(R.id.docPhone2);
        docNotes = (EditText) v.findViewById(R.id.docNotes);
        save = (Button) v.findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDoctor();
            }
        });

        if (mMap != null) {
            try {
//                setUpMapIfNeeded();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        try {
            doctor = getArguments().getParcelable(Constants.BUNDLE_DOCTOR);
        } catch (NullPointerException e) {
            doctor = new Doctor();
        }
        updateForm();
        setHasOptionsMenu(true);

        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Log.e(TAG, "Contact Result 2: "+resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CONTACT_PICK_REQUEST_CODE) {
//                Log.e(TAG, "Contact Result 2");
                uriContact = data.getData();
                retrieveContactDetails();
            }
        }
    }

    void retrieveContactDetails() {
        retrieveContactNumber();
    }

    void updateForm() {
        if (doctor == null) {
            doctor = new Doctor();
        }
        docName.setText(doctor.getName());
        docAddress.setText(doctor.getAddress());
        docPhone1.setText(doctor.getPhone_1());
        docPhone2.setText(doctor.getPhone_2());
        docNotes.setText(doctor.getNotes());
        try{
            imageView.setImageURI(doctor.getPhotoUri());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void saveDoctor() {
        if (docName.getText().toString().length() < 3) {
            docName.setError("Name cannot be less than 3 characters");
            return;
        }
        if (docPhone1.getText().toString().isEmpty() && docPhone2.getText().toString().isEmpty()) {
            docPhone1.setError("Phone number cannot be empty");
            return;
        }

        doctor.setName(docName.getText().toString());
        doctor.setPhone_1(docPhone1.getText().toString());
        doctor.setPhone_2(docPhone2.getText().toString());
        doctor.setAddress(docAddress.getText().toString());
        doctor.setNotes(docNotes.getText().toString());
        doctor.setHospital(docHospital.getText().toString());


        DataHandler dataHandler = new DataHandler(getActivity());
        dataHandler.insertDoctor(doctor);
        dataHandler.close();
        Toast.makeText(getActivity(), "Doctor details saved", Toast.LENGTH_SHORT).show();
    }


    void retrieveContactNumber() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {
            doctor.setName(retrieveContactName());
            doctor.setPhotoUri(retrieveContactUri(cursor));
            doctor.setId(retrieveContactId(cursor));


            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        new String[]{doctor.getId()},
                        null);
                phoneCursor.moveToFirst();
                int i = 1;
                do {
//                    String type=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//                    String s=(String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(getActivity().getResources(),Integer.parseInt(type),"");
                    String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (i == 1) {
                        doctor.setPhone_1(phone);
                    } else if (i == 2) {
                        doctor.setPhone_2(phone);
                    } else {
                        break;
                    }
                    i++;
                } while (phoneCursor.moveToNext());
                phoneCursor.close();
            }
            cursor.close();
            updateForm();
        }
    }

    Uri retrieveContactUri(Cursor cursor) {
        try {
            return Uri.parse(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    String retrieveContactId(Cursor cursor) {
        return contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
    }


    String retrieveContactName() {
        String contactName = "";
        Cursor cursor = getActivity().getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
//        Log.e(TAG,"Contact Name: "+contactName);
        return contactName;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_doctor_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.selectContact) {
            selectDoctorFromContacts();
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectDoctorFromContacts() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, CONTACT_PICK_REQUEST_CODE);
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
//            setUpMapIfNeeded();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    /**
     * Google Map Functions
     *
     * @throws NullPointerException
     */
    private void setUpMapIfNeeded() throws NullPointerException {
        // Do a null check to confirm that we have not already instantiated the map.
//        if (mMap == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
//                    .getMap();
//            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//                setUpMap();
//            }
//        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

}
