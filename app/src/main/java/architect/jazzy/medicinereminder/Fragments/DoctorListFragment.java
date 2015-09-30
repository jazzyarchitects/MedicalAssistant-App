package architect.jazzy.medicinereminder.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Adapters.DoctorListAdapter;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.ThisApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorListFragment extends Fragment {

    private static final String TAG="DoctorListFragment";
    RecyclerView doctorList;
    Context mContext;
    View v;
    FloatingActionButton floatingActionButton;

    public DoctorListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**Analytics Code*/
        Tracker t = ((ThisApplication) getActivity().getApplication()).getTracker(
                ThisApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Doctor List");
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext=getActivity();
        v=inflater.inflate(R.layout.fragment_list, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        doctorList=(RecyclerView)v.findViewById(R.id.recyclerView);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Your Doctors");
        doctorList.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        floatingActionButton=(FloatingActionButton)v.findViewById(R.id.floatingActionButton);
        floatingActionButton.setBackgroundTintList(Constants.getFabBackground(getActivity()));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentInteractionListenr.addDoctor();
            }
        });
        refreshLayout();
        setHasOptionsMenu(false);
        return  v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    void refreshLayout(){
        DataHandler handler=new DataHandler(mContext);
        ArrayList<Doctor> doctors=handler.getDoctorList();
        if(!doctors.isEmpty()) {
            DoctorListAdapter adapter = new DoctorListAdapter(mContext, doctors);
            adapter.setItemClickListener(new DoctorListAdapter.ItemClickListener() {
                @Override
                public void onItemClick(int position, Doctor doctor) {
                    Log.e(TAG,"Item clicked "+position );
                    onFragmentInteractionListenr.onDoctorSelected(doctor);
                }
            });
            doctorList.setAdapter(adapter);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        menuItemClickListener=(OnMenuItemClickListener)activity;
        onFragmentInteractionListenr=(OnFragmentInteractionListenr)activity;
    }


    OnMenuItemClickListener menuItemClickListener;
    public interface OnMenuItemClickListener{
        void onAddDoctorClicked();
    }
    public void setMenuItemClickListener(OnMenuItemClickListener menuItemClickListener){
        this.menuItemClickListener=menuItemClickListener;
    }

    OnFragmentInteractionListenr onFragmentInteractionListenr;
    public interface OnFragmentInteractionListenr{
        void onDoctorSelected(Doctor doctor);
        void addDoctor();
    }
}
