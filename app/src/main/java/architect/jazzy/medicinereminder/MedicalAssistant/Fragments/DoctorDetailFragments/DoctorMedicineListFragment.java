package architect.jazzy.medicinereminder.MedicalAssistant.Fragments.DoctorDetailFragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
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

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.MedicalAssistant.Activities.DoctorDetail;
import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.DoctorDetailFragments.Adapters.MedicineListAdapter;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.DataHandler;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Doctor;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorMedicineListFragment extends Fragment {

  private static final String TAG = "DMedicineListFragment";
  Doctor doctor;
  LinearLayout noMed;
  View v;
  FragmentInteractionListener fragmentInteractionListener;


  public DoctorMedicineListFragment() {
    // Required empty public constructor
  }

  public static DoctorMedicineListFragment newInstance(Doctor doctor) {
    DoctorMedicineListFragment fragment = new DoctorMedicineListFragment();
    Bundle args = new Bundle();
    args.putParcelable(Constants.BUNDLE_DOCTOR, doctor);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.doctor = getArguments().getParcelable(Constants.BUNDLE_DOCTOR);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    v = inflater.inflate(R.layout.fragment_doctor_medicine_list, container, false);
    noMed = (LinearLayout) v.findViewById(R.id.noMed);

//        Log.e(TAG,"OnCreateView");
    refreshLayout();

    return v;
  }

  public void refreshLayout() {
    RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    DataHandler handler = new DataHandler(getActivity());
    ArrayList<Medicine> medicines = handler.getMedicineListByDoctor(doctor);
    handler.close();
    if (medicines == null) {
      noMed.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
      return;
    }
//        Log.e(TAG,"refreshLayout");
    noMed.setVisibility(View.GONE);
    recyclerView.setVisibility(View.VISIBLE);
    MedicineListAdapter adapter = new MedicineListAdapter(getActivity(), medicines);
//        Log.e(TAG,"Setting Adapter");
    adapter.setItemClickListener(new MedicineListAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(int position, ArrayList<Medicine> medicines) {
        showDetails(position, medicines);
      }
    });
//        Log.e(TAG,"Displaying medicine List");
    recyclerView.setAdapter(adapter);
  }

  @Override
  @TargetApi(14)
  @SuppressWarnings("deprecation")
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    fragmentInteractionListener = (FragmentInteractionListener) activity;
    ((DoctorDetail) activity).setActivityResultListener(new DoctorDetail.ActivityResultListener() {
      @Override
      public void medicineListActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO
        refreshLayout();
      }
    });
  }

  @Override
  @TargetApi(21)
  public void onAttach(Context activity) {
    super.onAttach(activity);
    fragmentInteractionListener = (FragmentInteractionListener) activity;
    ((DoctorDetail) activity).setActivityResultListener(new DoctorDetail.ActivityResultListener() {
      @Override
      public void medicineListActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO
        refreshLayout();
      }
    });
  }

  public void showDetails(int position, ArrayList<Medicine> medicines) {
    fragmentInteractionListener.showDetails(position, medicines);
  }

  public interface FragmentInteractionListener {
    void showDetails(int position, ArrayList<Medicine> medicines);
  }
}
