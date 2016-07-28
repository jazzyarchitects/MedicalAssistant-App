package architect.jazzy.medicinereminder.Fragments.OfflineActivity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
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


import architect.jazzy.medicinereminder.Activities.MainActivity;
import architect.jazzy.medicinereminder.Fragments.OfflineActivity.DoctorDetailFragments.DoctorDetailFragment;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.ThisApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDoctorFragment extends Fragment {

    View v;
    private static final String TAG = "AddDoctorFragment";
    public static final int CONTACT_PICK_REQUEST_CODE = 120;
    private Uri uriContact;
    private String contactID;
    ImageView imageView;
    Doctor doctor;
    public static final int READ_CONTACTS_CODE = 15934;

    private static final int COVER_PIC_REQUEST_CODE = 121;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_doctor, container, false);



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

        docName.setSelected(true);

        setEditTextIcons(Constants.getThemeColor(getActivity()));
        
        try {
            doctor = getArguments().getParcelable(Constants.BUNDLE_DOCTOR);
        } catch (NullPointerException e) {
            doctor = new Doctor();
        }
        updateForm();
        setHasOptionsMenu(true);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select Doctor's Photo"), COVER_PIC_REQUEST_CODE);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                imageView.setImageResource(R.drawable.userlogin);
                doctor.setPhoto("");
                return false;
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add Doctor");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CONTACT_PICK_REQUEST_CODE) {
                uriContact = data.getData();
                retrieveContactDetails();
            }
            if(requestCode==COVER_PIC_REQUEST_CODE){
                String path=DoctorDetailFragment.getImagePath(data, getActivity());
                imageView.setImageBitmap(Constants.getScaledBitmap(path, imageView.getMeasuredWidth(), imageView.getMeasuredHeight()));
                doctor.setPhoto(path);

                try{
                    BitmapDrawable drawable=(BitmapDrawable)imageView.getDrawable();
                    Bitmap bitmap=drawable.getBitmap();
                    Palette.Builder builder = Palette.from(bitmap);
                    Palette palette = builder.generate();

                    try {
                        int color = Color.HSVToColor(palette.getSwatches().get(0).getHsl());
                        setEditTextIcons(color);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

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
            BitmapDrawable drawable=(BitmapDrawable)imageView.getDrawable();
            Bitmap bitmap=drawable.getBitmap();
            Palette.Builder builder = Palette.from(bitmap);
            Palette palette = builder.generate();
            
            try {
                int color = Color.HSVToColor(palette.getSwatches().get(0).getHsl());
                setEditTextIcons(color);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    void setEditTextIcons(int color){
        try {
            Constants.scaleEditTextImage(getActivity(), docPhone1, R.drawable.ic_call_black_24dp, color);
            Constants.scaleEditTextImage(getActivity(), docPhone2, R.drawable.ic_call_black_24dp, color);
            Constants.scaleEditTextImage(getActivity(), docAddress, R.drawable.ic_location_city_black_24dp, color, false);
            Constants.scaleEditTextImage(getActivity(), docHospital, R.drawable.ic_business_black_24dp, color, false);
            Constants.scaleEditTextImage(getActivity(), docNotes, R.drawable.ic_action_edit, color);
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
        onFragmentInteractionListener.showDoctors();
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
        }
        updateForm();
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

    public void selectDoctorFromContacts() {
        final Activity context = getActivity();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, 0);
                    builder.setTitle("Permission required")
                            .setMessage("Permission is required for reading contacts.")
                            .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                @Override
                                @TargetApi(23)
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestPermissions(
                                            new String[]{Manifest.permission.READ_CONTACTS},
                                            READ_CONTACTS_CODE);
                                }
                            })
                            .show();

                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_CONTACTS},
                            READ_CONTACTS_CODE);
                }
            }else{
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, CONTACT_PICK_REQUEST_CODE);
            }
        }else{
            Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(i, CONTACT_PICK_REQUEST_CODE);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_contact_select, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.addDoctor){
            selectDoctorFromContacts();
        }
        return super.onOptionsItemSelected(item);
    }

    OnFragmentInteractionListener onFragmentInteractionListener;

    public interface OnFragmentInteractionListener {
        void showDoctors();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onFragmentInteractionListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionResult: "+requestCode);
        switch (requestCode){
            case AddDoctorFragment.READ_CONTACTS_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(i, CONTACT_PICK_REQUEST_CODE);
                }else{
                    Toast.makeText(getActivity(), "Permission required to read contacts", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}
