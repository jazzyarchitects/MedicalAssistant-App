package architect.jazzy.medicinereminder.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

import architect.jazzy.medicinereminder.CustomViews.CircleView;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.TimingClass;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;

public class DashboardFragment extends Fragment {

    View v;
    CircleView todayView, medicineCountView, appointmentCountView;

    public DashboardFragment() {
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
        v=inflater.inflate(R.layout.fragment_dashboard, container, false);

        todayView=(CircleView)v.findViewById(R.id.mainIndicator);
        medicineCountView=(CircleView)v.findViewById(R.id.medicineCountView);
        appointmentCountView=(CircleView)v.findViewById(R.id.appointmentCount);

        Calendar calendar=Calendar.getInstance();
        todayView.setBigText(calendar.get(Calendar.DATE)+" "+TimingClass.getEquivalentMonth(calendar.get(Calendar.MONTH))+" "+calendar.get(Calendar.YEAR));

        DataHandler handler=new DataHandler(getActivity());
        ArrayList<Medicine> medicines=handler.getTodaysMedicine();
        if(medicines==null){
            medicineCountView.setBigText("0");
        }else{
            if(medicines.size()==0){
                medicineCountView.setBigText("0");
            }else{
                medicineCountView.setBigText(String.valueOf(medicines.size()));
            }
        }
        return v;
    }


}
