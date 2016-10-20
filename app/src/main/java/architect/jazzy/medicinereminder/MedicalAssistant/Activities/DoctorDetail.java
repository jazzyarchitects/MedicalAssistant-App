package architect.jazzy.medicinereminder.MedicalAssistant.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.HelperClasses.FirebaseConstants;
import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.DoctorDetailFragments.Adapters.ViewPagerAdapter;
import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.DoctorDetailFragments.DoctorDetailFragment;
import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.DoctorDetailFragments.DoctorMedicineListFragment;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.DataHandler;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.RealPathUtil;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Doctor;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;
import architect.jazzy.medicinereminder.R;

public class DoctorDetail extends AppCompatActivity implements ViewPagerAdapter.ViewPagerFragmentInteractionListener,
    DoctorDetailFragment.ImageChangeListener, DoctorMedicineListFragment.FragmentInteractionListener {

  private static final String TAG = "DoctorDetailActivity";
  private static final int COVER_PIC_REQUEST_CODE = 121;
  final int SHOW_LIST_REQUEST_CODE = 9865;
  TextView doctorNameView;
  ImageView doctorImageView;
  Doctor doctor;
  boolean setupDone = false;
  boolean menuSetup = false;
  AdRequest adRequest;
  AdView adView;
  Toolbar toolbar;
  int textColor = Color.WHITE;
  ActivityResultListener activityResultListener;
  int backgroundColor = Color.WHITE;
  DoctorDetailImageChangeListener doctorDetailImageChangeListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.doctor_detail_layout);

    FirebaseConstants.Analytics.logCurrentScreen(this, "DoctorDetail");

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    doctor = getIntent().getParcelableExtra("doctor");

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    adView = (AdView) findViewById(R.id.bannerAd);
//    AdRequest.Builder builder = new AdRequest.Builder()
//        .addTestDevice("5C8BFD2BD4F4C415F7456E231E186EE5")
//        .addTestDevice("2EDDA47AED66B1BF9537214AF158BBE2");
//    adRequest = builder.build();
//    adView.loadAd(adRequest);

//    adView.setAdListener(new AdListener() {
//      @Override
//      public void onAdClosed() {
//        Log.e(TAG, "onAdClosed");
//      }
//
//      @Override
//      public void onAdLoaded() {
//        super.onAdLoaded();
//        Log.e(TAG, "onAdLoaded");
//      }
//
//      @Override
//      public void onAdOpened() {
//        super.onAdOpened();
//        Log.e(TAG, "onAdOpened");
//      }
//
//      @Override
//      public void onAdLeftApplication() {
//        super.onAdLeftApplication();
//        Log.e(TAG, "onAdLefApplication");
//      }
//
//      @Override
//      public void onAdFailedToLoad(int i) {
//        super.onAdFailedToLoad(i);
//        Log.e(TAG, "onAdFailedToLoad: "+i);
//      }
//    });
//

    doctorNameView = (TextView) findViewById(R.id.doctorName);
    doctorImageView = (ImageView) findViewById(R.id.doctorImage);

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
        DataHandler handler = new DataHandler(DoctorDetail.this);
        handler.updateDoctor(doctor);
        handler.close();
        return false;
      }
    });

    backgroundColor = Constants.getThemeColor(this);


    dimNotificationBar();
    if (!setupDone)
      setup();
//    setupColors();
//    setupTabs();
  }

  @Override
  public void onDoctorImageChange(int resultCode, Intent data) {
    if (doctorDetailImageChangeListener != null) {
      doctorDetailImageChangeListener.onDoctorImageChanged(resultCode, data);
    }
  }

  void setup() {
    setupDone = true;
//    doctorNameView.setText(doctor.getName());
    if (doctor.getPhoto() == null || doctor.getPhoto().isEmpty()) {
      return;
    }
//    Picasso.with(this)
//        .load(doctor.getPhoto())
//        .noFade()
//        .error(R.drawable.userlogin)
//        .placeholder(R.drawable.userlogin)
//        .skipMemoryCache()
//        .into(doctorImageView);

    try {
      Bitmap bitmap;
      String path = doctor.getPhoto();
      Log.e(TAG, "Doctor image path: " + path);
      bitmap = Constants.getScaledBitmap(doctor.getPhoto(), 120, 120);
      doctorImageView.setImageBitmap(bitmap);
//      if (bitmap != null) {
//        Palette.Builder builder = Palette.from(bitmap);
//        Palette palette = builder.generate();
//        try {
//          int color = palette.getLightVibrantColor(0);
//          int color2 = palette.getDarkVibrantColor(0);
//          int color3 = palette.getDarkMutedColor(0);
//          int color4 = palette.getLightMutedColor(0);
//          int color5 = palette.getMutedColor(0);
//          int color6 = palette.getVibrantColor(0);
//
//          color = color == 0 ? color2 : color;
//          color = color == 0 ? color3 : color;
//          color = color == 0 ? color4 : color;
//          color = color == 0 ? color5 : color;
//          color = color == 0 ? color6 : color;
//
//          doctor.setBackgroundColor(color);
//          toolbar.setBackgroundColor(color);
//          backgroundColor = color;
//          float[] hsv = new float[3];
//          Color.colorToHSV(color, hsv);
//          if (hsv[2] > 0.75) {
//            textColor = Color.BLACK;
//            doctor.setTextColor(textColor);
//            doctorNameView.setTextColor(textColor);
//          }
//          doctor.setBackgroundColor(color);
//        } catch (NullPointerException e) {
//          e.printStackTrace();
//        }
//      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void dimNotificationBar() {
    final View decorView = getWindow().getDecorView();
    final int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
    decorView.setSystemUiVisibility(uiOptions);
    decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
      @Override
      public void onSystemUiVisibilityChange(int visibility) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            DoctorDetail.this.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                decorView.setSystemUiVisibility(uiOptions);
              }
            });
          }
        }, 5000);
      }
    });
  }

  @SuppressWarnings("deprecation")
//  void setupTabs() {
//    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), doctor);
//    adapter.setViewPagerFragmentListener(this);
//    viewPager.setAdapter(adapter);
//    tabLayout.setupWithViewPager(viewPager);
//    tabLayout.setTabsFromPagerAdapter(adapter);
//  }

  @Override
  public void onDoctorSaved(Doctor doctor) {
    this.doctor = doctor;
    setup();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.doctor_detail_menu, menu);
    menuSetup = true;
    if (!setupDone) {
      setup();
    }
    for (int i = 0; i < menu.size(); i++) {
      menu.getItem(i).getIcon().setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.deleteDoctor) {
      deleteDoctor();
    }
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return true;
  }

  void deleteDoctor() {
    AlertDialog.Builder builder = new AlertDialog.Builder(DoctorDetail.this);
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
            DataHandler handler = new DataHandler(DoctorDetail.this);
            handler.deleteDoctor(doctor);
            handler.close();
            dialog.dismiss();
            onBackPressed();
          }
        })
        .setTitle("Remove doctor?")
        .show();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == COVER_PIC_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        String path = RealPathUtil.getPathFromURI(this, data.getData());
        doctorImageView.setImageBitmap(Constants.getScaledBitmap(path, doctorImageView.getMeasuredWidth(), doctorImageView.getMeasuredHeight()));
        doctor.setPhoto(path);
        DataHandler handler = new DataHandler(this);
        handler.updateDoctor(doctor);
        handler.close();
        setup();
//        setupColors();
      }
    }
    if (requestCode == SHOW_LIST_REQUEST_CODE) {
      activityResultListener.medicineListActivityResult(requestCode, resultCode, data);
    }
  }

  public void setDoctorDetailImageChangeListener(DoctorDetailImageChangeListener doctorDetailImageChangeListener) {
    this.doctorDetailImageChangeListener = doctorDetailImageChangeListener;
  }

  public void setActivityResultListener(ActivityResultListener activityResultListener) {
    this.activityResultListener = activityResultListener;
  }

  @Override
  public void showDetails(int position, ArrayList<Medicine> medicines) {
    Intent i = new Intent(this, MedicineDetails.class);
    Bundle bundle = new Bundle();
    bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST, medicines);
    i.putExtra(Constants.MEDICINE_POSITION, position);
    i.putExtras(bundle);
    startActivityForResult(i, SHOW_LIST_REQUEST_CODE);
  }

  public interface DoctorDetailImageChangeListener {
    void onDoctorImageChanged(int resultCode, Intent data);
  }

  public interface ActivityResultListener {
    void medicineListActivityResult(int requestCode, int resultCode, Intent data);
  }

}
