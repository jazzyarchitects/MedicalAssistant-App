package architect.jazzy.medicinereminder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 26-Aug-15.
 */
public class DoctorSpinnerAdapter extends ArrayAdapter<Doctor>{

    ArrayList<Doctor> doctors;
    LayoutInflater inflater;
    TextView docName;
    ImageView docImage;
    public DoctorSpinnerAdapter(Context context, ArrayList<Doctor> doctors) {
        super(context, R.layout.layout_doctor_spinner);
        this.doctors=doctors;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Doctor getItem(int position) {
        return doctors.get(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v=inflater.inflate(R.layout.layout_doctor_spinner,parent,false);

        Doctor doctor=doctors.get(position);
        docName = (TextView)v.findViewById(R.id.textView);
        docImage=(ImageView)v.findViewById(R.id.imageView);

        try {
            docImage.setImageURI(doctor.getPhotoUri());
        }catch (Exception e){
            e.printStackTrace();
        }

        docName.setText(doctor.getName());



        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return doctors.size();
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(doctors.get(position).getId());
    }
}
