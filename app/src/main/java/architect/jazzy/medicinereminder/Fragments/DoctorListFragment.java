package architect.jazzy.medicinereminder.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Adapters.DoctorListAdapter;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorListFragment extends Fragment {


    RecyclerView doctorList;
    Context mContext;
    View v;

    public DoctorListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext=getActivity();
        v=inflater.inflate(R.layout.fragment_doctor_list, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        doctorList=(RecyclerView)v.findViewById(R.id.recyclerView);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Your Doctors");
        doctorList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        DataHandler handler=new DataHandler(mContext);
        ArrayList<Doctor> doctors=handler.getDoctorList();
        if(!doctors.isEmpty()) {
            DoctorListAdapter adapter = new DoctorListAdapter(mContext, doctors);
            doctorList.setAdapter(adapter);
        }
        setHasOptionsMenu(true);
        return  v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        menuItemClickListener=(OnMenuItemClickListener)activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_medicine_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.newMedicine){
            menuItemClickListener.onAddDoctorClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    OnMenuItemClickListener menuItemClickListener;
    public interface OnMenuItemClickListener{
        void onAddDoctorClicked();
    }
    public void setMenuItemClickListener(OnMenuItemClickListener menuItemClickListener){
        this.menuItemClickListener=menuItemClickListener;
    }
}
