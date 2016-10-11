package architect.jazzy.medicinereminder.MedicalAssistant.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Doctor;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 26-Aug-15.
 */
public class DoctorSpinnerAdapter extends ArrayAdapter<Doctor> {

  ArrayList<Doctor> doctors;
  LayoutInflater inflater;
  TextView docName;
  ImageView docImage;

  public DoctorSpinnerAdapter(Context context, ArrayList<Doctor> doctors) {
    super(context, R.layout.layout_doctor_spinner);
    this.doctors = doctors;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public Doctor getItem(int position) {
    return doctors.get(position);
  }

  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {

    View v;
    if (convertView == null) {
      v = inflater.inflate(R.layout.layout_doctor_spinner, parent, false);
    } else {
      v = convertView;
    }

    Doctor doctor;
    if (position == 0) {
      doctor = null;
    } else {
      try {
        doctor = doctors.get(position - 1);
      } catch (IndexOutOfBoundsException e) {
        doctor = null;
      }
    }

    docName = (TextView) v.findViewById(R.id.textView);
    docImage = (ImageView) v.findViewById(R.id.imageView);

    String name = "Select a doctor";
    if (doctor != null) {
      name = doctor.getName();
      try {
        Bitmap bitmap = Constants.getScaledBitmap(doctor.getPhoto(), 30, 30);
        if (bitmap != null) {
          docImage.setImageBitmap(bitmap);
        } else {
          docImage.setImageResource(R.drawable.userlogin);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      docImage.setImageResource(R.drawable.userlogin);
    }


    docName.setText(name);
    return v;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    View v;
    if (convertView == null) {
      v = inflater.inflate(R.layout.layout_doctor_spinner, parent, false);
    } else {
      v = convertView;
    }

    Doctor doctor;
    if (position == 0) {
      doctor = null;
    } else {
      try {
        doctor = doctors.get(position - 1);
      } catch (IndexOutOfBoundsException e) {
        doctor = null;
      }
    }

    docName = (TextView) v.findViewById(R.id.textView);
    docImage = (ImageView) v.findViewById(R.id.imageView);

    String name = "Select a doctor";
    if (doctor != null) {
      name = doctor.getName();
      try {
        Bitmap bitmap = Constants.getScaledBitmap(doctor.getPhoto(), 30, 30);
        if (bitmap != null) {
          docImage.setImageBitmap(bitmap);
        } else {
          docImage.setImageResource(R.drawable.userlogin);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      docImage.setImageResource(R.drawable.userlogin);
    }


    docName.setText(name);
    return v;
  }

  @Override
  public int getCount() {
    return doctors == null ? 1 : doctors.size() + 1;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }
}
