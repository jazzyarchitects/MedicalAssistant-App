package architect.jazzy.medicinereminder.MedicalAssistant.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.HelperClasses.FirebaseConstants;
import architect.jazzy.medicinereminder.MedicalAssistant.Adapters.HorizontalMedicineListAdapter;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.DataHandler;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.RealPathUtil;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Doctor;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;
import architect.jazzy.medicinereminder.R;

public class DoctorDetail extends AppCompatActivity {

  private static final String TAG = "DoctorDetailActivity";
  private static final int COVER_PIC_REQUEST_CODE = 121;
//  final int SHOW_LIST_REQUEST_CODE = 9865;
  EditText doctorContactView, doctorOfficeView, doctorAddressView, doctorHospitalView, doctorNotesView, doctorNameView;
  TextView medicineText;
  Button saveButton, deleteButton;
  ImageView doctorImageView;
  RecyclerView recyclerView;
  Doctor doctor;
  Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.doctor_detail_layout);

    FirebaseConstants.Analytics.logCurrentScreen(this, "DoctorDetail");
    doctor = getIntent().getParcelableExtra("doctor");

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    AdRequest adRequest;
    AdView adView = (AdView) findViewById(R.id.adView);
    AdRequest.Builder builder = new AdRequest.Builder()
        .addTestDevice("5C8BFD2BD4F4C415F7456E231E186EE5")
        .addTestDevice("2EDDA47AED66B1BF9537214AF158BBE2");
    adRequest = builder.build();
    adView.loadAd(adRequest);

    doctorNameView = (EditText) findViewById(R.id.doctorName);
    doctorContactView = (EditText) findViewById(R.id.doctorPhone);
    doctorOfficeView = (EditText) findViewById(R.id.doctorOffice);
    doctorAddressView = (EditText) findViewById(R.id.doctorAddress);
    doctorHospitalView = (EditText) findViewById(R.id.doctorHospital);
    doctorNotesView = (EditText) findViewById(R.id.doctorNotes);
    medicineText = (TextView) findViewById(R.id.medicineLabel);
    doctorImageView = (ImageView) findViewById(R.id.doctorImage);
    saveButton = (Button) findViewById(R.id.saveButton);
    deleteButton = (Button) findViewById(R.id.discardButton);

    saveButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DataHandler handler = new DataHandler(DoctorDetail.this);
        doctor.setName(doctorNameView.getText().toString());
        doctor.setPhone_1(doctorContactView.getText().toString());
        doctor.setPhone_2(doctorOfficeView.getText().toString());
        doctor.setAddress(doctorAddressView.getText().toString());
        doctor.setNotes(doctorNotesView.getText().toString());
        doctor.setHospital(doctorHospitalView.getText().toString());
        handler.updateDoctor(doctor);
        handler.close();
        Toast.makeText(DoctorDetail.this, "Doctor detail updated", Toast.LENGTH_LONG).show();
      }
    });

    deleteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        deleteDoctor();
      }
    });

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
        setupImage();
        return false;
      }
    });

    if (doctor.getPhoto() == null || doctor.getPhoto().isEmpty()) {
      return;
    }
    doctorNameView.setText(doctor.getName());
    doctorContactView.setText(doctor.getPhone_1());
    doctorOfficeView.setText(doctor.getPhone_2());
    doctorAddressView.setText(doctor.getAddress());
    doctorHospitalView.setText(doctor.getHospital());
    doctorNotesView.setText(doctor.getNotes());

    dimNotificationBar();
    setupImage();
    setupMedicines();
  }

  void setupImage() {
    if (doctor.getPhoto() == null || doctor.getPhoto().isEmpty()) {
      doctorImageView.setImageResource(R.drawable.userlogin);
      return;
    }
    doctorImageView.setImageBitmap(Constants.getScaledBitmap(doctor.getPhoto(), 120, 120));
  }

  void setupMedicines() {
    recyclerView = (RecyclerView) findViewById(R.id.doctorMedicines);
    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    DataHandler handler = new DataHandler(this);
    ArrayList<Medicine> medicines = handler.getMedicineListByDoctor(doctor);
    handler.close();
    if (medicines == null) {
      recyclerView.setVisibility(View.GONE);
      medicineText.setVisibility(View.GONE);
      return;
    }
    recyclerView.setVisibility(View.VISIBLE);
    medicineText.setVisibility(View.VISIBLE);
    HorizontalMedicineListAdapter adapter = new HorizontalMedicineListAdapter(this, medicines);
    adapter.setItemClickListener(new HorizontalMedicineListAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(int position, ArrayList<Medicine> medicines) {
        Intent i = new Intent(DoctorDetail.this, MedicineDetails.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST, medicines);
        i.putExtra(Constants.MEDICINE_POSITION, position);
        i.putExtras(bundle);
        startActivity(i);
      }
    });
    recyclerView.setAdapter(adapter);
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_common, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
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
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == COVER_PIC_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        String path = RealPathUtil.getPathFromURI(this, data.getData());
        doctor.setPhoto(path);
        setupImage();
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    setupMedicines();
  }
}
