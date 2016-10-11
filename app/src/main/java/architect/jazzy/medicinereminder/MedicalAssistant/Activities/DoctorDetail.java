package architect.jazzy.medicinereminder.MedicalAssistant.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import architect.jazzy.medicinereminder.BuildConfig;
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
  View v;
  TextView doctorNameView;
  ImageView doctorImageView;
  TabLayout tabLayout;
  Drawable userImage;
  final int SHOW_LIST_REQUEST_CODE = 9865;
  ViewPager viewPager;
  Doctor doctor;
  boolean setupDone = false;
  boolean menuSetup = false;
  AdRequest adRequest;
  AdView adView;
  Toolbar toolbar;
  int textColor = Color.WHITE;
  ActivityResultListener activityResultListener;
  int backgroundColor = Color.WHITE;

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


    adView = (AdView) findViewById(R.id.bannerAd);

    AdRequest.Builder builder = new AdRequest.Builder()
        .setIsDesignedForFamilies(true)
        .addTestDevice("5C8BFD2BD4F4C415F7456E231E186EE5")
        .addTestDevice("2EDDA47AED66B1BF9537214AF158BBE2");
    adRequest = builder.build();
    adView.loadAd(adRequest);
//        adView.setVisibility(View.GONE);

    adView.setAdListener(new AdListener() {
      @Override
      public void onAdLoaded() {
        super.onAdLoaded();
        adView.setVisibility(View.VISIBLE);
      }
    });

    doctorNameView = (TextView) findViewById(R.id.doctorName);
    doctorImageView = (ImageView) findViewById(R.id.doctorImage);
    tabLayout = (TabLayout) findViewById(R.id.tabLayout);
    viewPager = (ViewPager) findViewById(R.id.viewPager);

    doctorImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Select Doctor's Photo"), COVER_PIC_REQUEST_CODE);
      }
    });

    userImage = ResourcesCompat.getDrawable(getResources(), R.drawable.userlogin, null).mutate();
    userImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

    doctorImageView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        doctorImageView.setImageDrawable(userImage);
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
    setupTabs();
  }


  @Override
  public void onDoctorImageChange(int resultCode, Intent data) {
    if (doctorDetailImageChangeListener != null) {
      doctorDetailImageChangeListener.onDoctorImageChanged(resultCode, data);
    }
  }

  void setup() {
    setupDone = true;
    try {
      Bitmap bitmap;
      String path = doctor.getPhoto();
      Log.e(TAG, "Doctor image path: " + path);
      try {
//                Uri uri = Uri.parse(path);
        doctorImageView.setImageBitmap(Constants.getScaledBitmap(doctor.getPhoto(), 150, 150));
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
          int color = palette.getLightVibrantColor(0);
          int color2 = palette.getDarkVibrantColor(0);
          int color3 = palette.getDarkMutedColor(0);
          int color4 = palette.getLightMutedColor(0);
          int color5 = palette.getMutedColor(0);
          int color6 = palette.getVibrantColor(0);

          color = color == 0 ? color2 : color;
          color = color == 0 ? color3 : color;
          color = color == 0 ? color4 : color;
          color = color == 0 ? color5 : color;
          color = color == 0 ? color6 : color;

          tabLayout.setBackgroundColor(color);
          doctor.setBackgroundColor(color);
          toolbar.setBackgroundColor(color);
          backgroundColor = color;
          float[] hsv = new float[3];
          Color.colorToHSV(color, hsv);
          if (hsv[2] > 0.75) {
            textColor = Color.BLACK;
            doctor.setTextColor(textColor);
            doctorNameView.setTextColor(textColor);
            tabLayout.setTabTextColors(Color.parseColor("#555555"), Color.BLACK);

            colorizeToolbar(toolbar, textColor, this);
          }
          doctor.setBackgroundColor(color);
        } catch (NullPointerException e) {
          e.printStackTrace();
        }
      } else {
        doctorImageView.setImageDrawable(userImage);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    doctorNameView.setText(doctor.getName());
  }

//  void setupColors() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && false) {
//      Window window = getWindow();
//      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//      window.setStatusBarColor(backgroundColor);
//    }
//  }

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
  void setupTabs() {
    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), doctor);
    adapter.setViewPagerFragmentListener(this);
    viewPager.setAdapter(adapter);
    tabLayout.setupWithViewPager(viewPager);
    tabLayout.setTabsFromPagerAdapter(adapter);
  }

  @Override
  public void onDoctorSaved(Doctor doctor) {
    this.doctor = doctor;
    setup();
  }

  /**
   * Use this method to colorize toolbar icons to the desired target color
   *
   * @param toolbarView       toolbar view being colored
   * @param toolbarIconsColor the target color of toolbar icons
   * @param activity          reference to activity needed to register observers
   */
  public static void colorizeToolbar(Toolbar toolbarView, int toolbarIconsColor, Activity activity) {
    final PorterDuffColorFilter colorFilter
        = new PorterDuffColorFilter(toolbarIconsColor, PorterDuff.Mode.MULTIPLY);

    for (int i = 0; i < toolbarView.getChildCount(); i++) {

      Log.e(TAG, "Toolbar icon instance of: " + (toolbarView.getChildAt(i).toString()));

      final View v = toolbarView.getChildAt(i);

      //Step 1 : Changing the color of back button (or open drawer button).
      if (v instanceof ImageButton) {
        //Action Bar back button
        ((ImageButton) v).getDrawable().setColorFilter(colorFilter);
      }

      if (v instanceof ActionMenuItemView) {
        int drawableCount = ((ActionMenuItemView) v).getCompoundDrawables().length;
        for (int k = 0; k < drawableCount; k++) {

          if (((ActionMenuItemView) v).getCompoundDrawables()[k] != null) {
            final int finalK = k;

            //Important to set the color filter in seperate thread,
            //by adding it to the message queue
            //Won't work otherwise.
            v.post(new Runnable() {
              @Override
              public void run() {
                ((ActionMenuItemView) v).getCompoundDrawables()[finalK].setColorFilter(colorFilter);
              }
            });
          }
        }

      }

      if (v instanceof ActionMenuView) {
        for (int j = 0; j < ((ActionMenuView) v).getChildCount(); j++) {

          //Step 2: Changing the color of any ActionMenuViews - icons that
          //are not back button, nor text, nor overflow menu icon.
          final View innerView = ((ActionMenuView) v).getChildAt(j);

          if (innerView instanceof ActionMenuItemView) {
            int drawablesCount = ((ActionMenuItemView) innerView).getCompoundDrawables().length;
            for (int k = 0; k < drawablesCount; k++) {
              if (((ActionMenuItemView) innerView).getCompoundDrawables()[k] != null) {
                final int finalK = k;

                //Important to set the color filter in seperate thread,
                //by adding it to the message queue
                //Won't work otherwise.
                innerView.post(new Runnable() {
                  @Override
                  public void run() {
                    ((ActionMenuItemView) innerView).getCompoundDrawables()[finalK].setColorFilter(colorFilter);
                  }
                });
              }
            }
          }
        }
      }

      //Step 3: Changing the color of title and subtitle.
      toolbarView.setTitleTextColor(toolbarIconsColor);
      toolbarView.setSubtitleTextColor(toolbarIconsColor);
    }
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


  DoctorDetailImageChangeListener doctorDetailImageChangeListener;

  public void setDoctorDetailImageChangeListener(DoctorDetailImageChangeListener doctorDetailImageChangeListener) {
    this.doctorDetailImageChangeListener = doctorDetailImageChangeListener;
  }

  public interface DoctorDetailImageChangeListener {
    void onDoctorImageChanged(int resultCode, Intent data);
  }


  public void setActivityResultListener(ActivityResultListener activityResultListener) {
    this.activityResultListener = activityResultListener;
  }

  public interface ActivityResultListener {
    void medicineListActivityResult(int requestCode, int resultCode, Intent data);
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

}
