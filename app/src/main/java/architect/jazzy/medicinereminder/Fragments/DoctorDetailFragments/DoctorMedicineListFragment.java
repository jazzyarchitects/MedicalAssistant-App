package architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Activities.MainActivity;
import architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.Adapters.MedicineListAdapter;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorMedicineListFragment extends Fragment {

    Doctor doctor;
    LinearLayout noMed;

    public static DoctorMedicineListFragment newInstance(Doctor doctor){
        DoctorMedicineListFragment fragment=new DoctorMedicineListFragment();
        Bundle args=new Bundle();
        args.putParcelable(Constants.BUNDLE_DOCTOR, doctor);
        fragment.setArguments(args);
        return fragment;
    }

    public DoctorMedicineListFragment() {
        // Required empty public constructor
    }


    View v;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.doctor=getArguments().getParcelable(Constants.BUNDLE_DOCTOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_doctor_medicine_list, container, false);
        noMed=(LinearLayout)v.findViewById(R.id.noMed);

        refreshLayout();

        return v;
    }

    public void refreshLayout(){
        RecyclerView recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);


        DataHandler handler=new DataHandler(getActivity());
        ArrayList<Medicine> medicines=handler.getMedicineListByDoctor(doctor);
        if(medicines.isEmpty()){
            noMed.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        noMed.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        MedicineListAdapter adapter=new MedicineListAdapter(getActivity(),medicines);
        adapter.setItemClickListener(new MedicineListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ArrayList<String> medicines) {
                showDetails(position,medicines);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fragmentInteractionListener=(FragmentInteractionListener)activity;

        ((MainActivity)activity).setActivityResultListener(new MainActivity.ActivityResultListener() {
            @Override
            public void medicineListActivityResult(int requestCode, int resultCode, Intent data) {
                //TODO
                refreshLayout();
            }
        });

    }


    FragmentInteractionListener fragmentInteractionListener;
    public void showDetails(int position, ArrayList<String> medicineNames) {
        fragmentInteractionListener.showDetails(position, medicineNames);
    }

    public interface FragmentInteractionListener{
        void showDetails(int position, ArrayList<String> medicineNames);
    }
}
