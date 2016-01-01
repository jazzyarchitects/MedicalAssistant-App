package architect.jazzy.medicinereminder.Fragments.OfflineActivity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.CustomComponents.TimingClass;
import architect.jazzy.medicinereminder.CustomViews.LabelledImage;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImportFragment extends Fragment {

    Context mContext;
    RecyclerView medicineRecycler, doctorRecycler;
    ArrayList<Medicine> medicines;
    ArrayList<Doctor> doctors;
    Button importConfirm;

    public ImportFragment() {
        // Required empty public constructor
    }

    public static ImportFragment newInstance(ArrayList<Medicine> medicines, ArrayList<Doctor> doctors) {
        ImportFragment fragment = new ImportFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("medicines", medicines);
        args.putParcelableArrayList("doctors", doctors);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        medicines = getArguments().getParcelableArrayList("medicines");
        doctors = getArguments().getParcelableArrayList("doctors");
        mContext=getActivity();
        return inflater.inflate(R.layout.fragment_import, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        medicineRecycler = (RecyclerView) view.findViewById(R.id.medicineRecycler);
        doctorRecycler = (RecyclerView) view.findViewById(R.id.doctorRecycler);
        importConfirm=(Button)view.findViewById(R.id.importButton);

        medicineRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        doctorRecycler.setLayoutManager(new LinearLayoutManager(mContext));

        medicineRecycler.setHasFixedSize(true);
        doctorRecycler.setHasFixedSize(true);

        medicineRecycler.setAdapter(new ImportMedicineRecyclerAdapter(mContext, medicines));
        doctorRecycler.setAdapter(new ImportDoctorRecyclerAdapter(mContext, doctors));

        importConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importConfirmListener.onImportConfirmed();
            }
        });
    }


    class ImportMedicineRecyclerAdapter extends RecyclerView.Adapter<ImportMedicineRecyclerAdapter.ViewHolder> {

        Context mContext;
        ArrayList<Medicine> medicines;

        public ImportMedicineRecyclerAdapter(Context context, ArrayList<Medicine> medicines) {
            mContext = context;
            this.medicines = medicines;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.import_medicine_recycler_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Medicine medicine = medicines.get(position);

            holder.medName.setText(medicine.getMedName());
            if (medicine.getBreakfast().equalsIgnoreCase("before")) {
                holder.morning.setText("Before", "Before");
            } else if (medicine.getBreakfast().equalsIgnoreCase("after")) {
                holder.morning.setText("After", "After");
            } else {
                holder.morning.setVisibility(View.GONE);
            }

            if (medicine.getLunch().equalsIgnoreCase("before")) {
                holder.noon.setText("Before", "Before");
            } else if (medicine.getLunch().equalsIgnoreCase("after")) {
                holder.noon.setText("After", "After");
            } else {
                holder.noon.setVisibility(View.GONE);
            }

            if (medicine.getDinner().equalsIgnoreCase("before")) {
                holder.night.setText("Before", "Before");
            } else if (medicine.getDinner().equalsIgnoreCase("after")) {
                holder.night.setText("After", "After");
            } else {
                holder.night.setVisibility(View.GONE);
            }

            if (medicine.getCustomTime() != null) {
                holder.custom.setText(TimingClass.getTime(medicine.getCustomTime().getHour(), medicine.getCustomTime().getMinute(), mContext.getSharedPreferences(Constants.SETTING_PREF, Context.MODE_PRIVATE).getBoolean(Constants.IS_24_HOURS_FORMAT, false)), "");
            }else{
                holder.custom.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return medicines==null?0:medicines.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout linearLayout;
            TextView medName;
            LabelledImage morning, noon, night, custom;

            public ViewHolder(View itemView) {
                super(itemView);
                linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
                medName = (TextView) itemView.findViewById(R.id.medName);
                morning = (LabelledImage) itemView.findViewById(R.id.morning);
                night = (LabelledImage) itemView.findViewById(R.id.night);
                noon = (LabelledImage) itemView.findViewById(R.id.noon);
                custom = (LabelledImage) itemView.findViewById(R.id.custom);
            }

            public void setTextSize(float textSize){
                morning.setTextSize(textSize);
                noon.setTextSize(textSize);
                night.setTextSize(textSize);
                custom.setTextSize(textSize);
            }
        }
    }

    class ImportDoctorRecyclerAdapter extends RecyclerView.Adapter<ImportDoctorRecyclerAdapter.ViewHolder> {

        Context mContext;
        ArrayList<Doctor> doctors;

        public ImportDoctorRecyclerAdapter(Context context, ArrayList<Doctor> doctors) {
            mContext = context;
            this.doctors = doctors;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.import_doctor_recycler_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
                Doctor doctor = doctors.get(position);
                holder.doctorName.setText(doctor.getName());
                holder.doctorContact.setText(doctor.getPhone_1() + " ," + doctor.getPhone_2());
        }

        @Override
        public int getItemCount() {
            return doctors==null?0:doctors.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView doctorName, doctorContact;

            public ViewHolder(View itemView) {
                super(itemView);
                doctorName=(TextView)itemView.findViewById(R.id.doctorName);
                doctorContact=(TextView)itemView.findViewById(R.id.doctorContact);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        importConfirmListener=(ImportConfirmListener)activity;
    }

    ImportConfirmListener importConfirmListener;
    public interface ImportConfirmListener{
        void onImportConfirmed();
    }
}
