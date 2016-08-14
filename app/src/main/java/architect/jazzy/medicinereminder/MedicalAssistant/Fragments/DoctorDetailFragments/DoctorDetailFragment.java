package architect.jazzy.medicinereminder.MedicalAssistant.Fragments.DoctorDetailFragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import architect.jazzy.medicinereminder.MedicalAssistant.Activities.MainActivity;
import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.DoctorDetailFragments.Adapters.ViewPagerAdapter;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.DataHandler;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.RealPathUtil;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Doctor;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorDetailFragment extends Fragment implements ViewPagerAdapter.ViewPagerFragmentInteractionListener {

    private static final String TAG = "DoctorDetailFragment";
    private static final int COVER_PIC_REQUEST_CODE = 121;
    View v;
    TextView doctorNameView;
    ImageView doctorImageView;
    TabLayout tabLayout;
    ViewPager viewPager;
    Doctor doctor;
    int textColor = Color.WHITE;


    public static DoctorDetailFragment newInstance(Doctor doctor) {
        DoctorDetailFragment fragment = new DoctorDetailFragment();
        Bundle args = new Bundle();
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
        doctor = getArguments().getParcelable(Constants.BUNDLE_DOCTOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.doctor_detail_layout, container, false);

        setHasOptionsMenu(true);
        doctorNameView = (TextView) v.findViewById(R.id.doctorName);
        doctorImageView = (ImageView) v.findViewById(R.id.doctorImage);
        tabLayout = (TabLayout) v.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) v.findViewById(R.id.viewPager);

        doctorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select Doctor's Photo"), COVER_PIC_REQUEST_CODE);
            }
        });

        doctorImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doctorImageView.setImageResource(R.drawable.userlogin);
                doctor.setPhoto("");
                DataHandler handler = new DataHandler(getActivity());
                handler.updateDoctor(doctor);
                handler.close();
                return false;
            }
        });

        setup();
        setupTabs();
        return v;
    }

    void setup() {

        try {
            Bitmap bitmap;
            String path = doctor.getPhoto();
            try {
                Uri uri = Uri.parse(path);
                doctorImageView.setImageURI(uri);
                bitmap = ((BitmapDrawable) doctorImageView.getDrawable()).getBitmap();
            } catch (Exception e) {
                bitmap = Constants.getScaledBitmap(doctor.getPhoto(), 150, 150);
                if (bitmap != null) {
                    doctorImageView.setImageBitmap(bitmap);
                }
            }
            if (bitmap != null) {
                Palette.Builder builder = Palette.from(bitmap);
                Palette palette = builder.generate();
                try {
                    int color = Color.HSVToColor(palette.getSwatches().get(0).getHsl());
                    tabLayout.setBackgroundColor(color);
                    ((MainActivity) getActivity()).getToolbar().setBackgroundColor(color);
                    float[] hsv = new float[3];
                    Color.colorToHSV(color, hsv);
                    if (hsv[2] > 0.7) {
                        textColor = Color.BLACK;
                        doctor.setBackgroundColor(textColor);
                        tabLayout.setTabTextColors(Color.parseColor("#555555"), Color.BLACK);
                    }
                    doctor.setBackgroundColor(color);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                doctorImageView.setImageResource(R.drawable.userlogin);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        doctorNameView.setText(doctor.getName());

    }

    void setupTabs() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getChildFragmentManager(), doctor);
        adapter.setViewPagerFragmentListener(this);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COVER_PIC_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                imageChangeListener.onDoctorImageChange(resultCode, data);
                String path = RealPathUtil.getPathFromURI(getActivity(), data.getData());
                doctorImageView.setImageBitmap(Constants.getScaledBitmap(path, doctorImageView.getMeasuredWidth(), doctorImageView.getMeasuredHeight()));
                doctor.setPhoto(path);
                DataHandler handler = new DataHandler(getActivity());
                handler.updateDoctor(doctor);
                handler.close();
            }
        }
    }

    ImageChangeListener imageChangeListener;

    @Override
    public void onDoctorSaved(Doctor doctor) {
        this.doctor = doctor;
        setup();
    }

    public interface ImageChangeListener {
        void onDoctorImageChange(int resultCode, Intent data);
    }

    public void setImageChangeListener(ImageChangeListener imageChangeListener) {
        this.imageChangeListener = imageChangeListener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        imageChangeListener = (ImageChangeListener) activity;
    }


    public static String getImagePath(Intent result, Activity activity) {
        Uri imageUri = result.getData();
        String[] filePathColoumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(imageUri, filePathColoumn, null, null, null);
        if (cursor == null) {
            return "";
        }
        cursor.moveToFirst();
        int coloumnIndex = cursor.getColumnIndex(filePathColoumn[0]);
        String picturePath = cursor.getString(coloumnIndex);
        cursor.close();
        return picturePath == null ? imageUri.toString() : picturePath;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.doctor_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteDoctor) {
            deleteDoctor();
        }
        return true;
    }

    void deleteDoctor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        DataHandler handler = new DataHandler(getActivity());
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
