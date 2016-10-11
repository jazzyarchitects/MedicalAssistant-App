package architect.jazzy.medicinereminder.MedicalAssistant.Fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.MedicalAssistant.Adapters.DoctorListAdapter;
import architect.jazzy.medicinereminder.MedicalAssistant.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Doctor;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorListFragment extends Fragment {

    private static final String TAG="DoctorListFragment";
    private static final int CALL_PHONE_CODE = 7485;
    RecyclerView doctorList;
    Context mContext;
    View v;
    FloatingActionButton floatingActionButton;
    ActionMode actionMode;

    public DoctorListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    LinearLayout emptyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext=getActivity();
        v=inflater.inflate(R.layout.fragment_list, container, false);
        emptyList = (LinearLayout)v.findViewById(R.id.emptyList);
        emptyList.setVisibility(View.VISIBLE);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        doctorList=(RecyclerView)v.findViewById(R.id.recyclerView);
        doctorList.setVisibility(View.GONE);
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

    ArrayList<Doctor> doctors;
    void refreshLayout(){
        DataHandler handler=new DataHandler(mContext);
        doctors = handler.getDoctorList();
        if(doctors==null){
            doctorList.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        }
        if(!doctors.isEmpty()) {
            doctorList.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
            DoctorListAdapter adapter = new DoctorListAdapter(getActivity(), doctors);
            adapter.setItemClickListener(new DoctorListAdapter.ItemClickListener() {
                @Override
                public void onItemClick(int position, Doctor doctor) {
                    onFragmentInteractionListenr.onDoctorSelected(doctor);
                }

                @Override
                public void onItemLongClick(int position, Doctor doctor) {
                    handleLongClickOnDoctor(position, doctor);
                }
            });
            doctorList.setAdapter(adapter);
        }else {
            doctorList.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout();
    }

    @Override
    @TargetApi(14)
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        menuItemClickListener=(OnMenuItemClickListener)activity;
        onFragmentInteractionListenr=(OnFragmentInteractionListenr)activity;
    }

    @Override
    @TargetApi(21)
    public void onAttach(Context activity) {
        super.onAttach(activity);
        menuItemClickListener=(OnMenuItemClickListener)activity;
        onFragmentInteractionListenr=(OnFragmentInteractionListenr)activity;
    }

    ActionMode.Callback actionCallback=new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_doctor_list,menu);
            MenuItem callItem=menu.findItem(R.id.callDoctor);
            Drawable drawable=callItem.getIcon().mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            callItem.setIcon(drawable);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Now what?");
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id=item.getItemId();
            switch (id){
                case R.id.deleteDoctor:
                    deleteDoctor();
                    break;
                case R.id.callDoctor:
                    callDoctor();
                    actionMode.finish();
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };
    Doctor toBeDeletedDoctor=null;
    int removedPosition;
    void handleLongClickOnDoctor(int position, Doctor doctor){
        actionMode=getActivity().startActionMode(actionCallback);
        this.toBeDeletedDoctor=doctor;
        this.removedPosition=position;
    }

    void deleteDoctor(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Doctor")
                .setCancelable(true)
                .setMessage("Permanently delete this doctor?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataHandler handler=new DataHandler(getActivity());
                        handler.deleteDoctor(toBeDeletedDoctor);
                        handler.close();
                        doctors.remove(removedPosition);
                        doctorList.getAdapter().notifyItemRemoved(removedPosition);
                        try{
                            actionMode.finish();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        refreshLayout();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    void callDoctor(){
        final Activity context = getActivity();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.CALL_PHONE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, 0);
                    builder.setTitle("Permission required")
                            .setMessage("Permission is required calling a doctor")
                            .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                @Override
                                @TargetApi(23)
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestPermissions(
                                            new String[]{Manifest.permission.CALL_PHONE},
                                            CALL_PHONE_CODE);
                                }
                            })
                            .show();

                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.CALL_PHONE},
                            CALL_PHONE_CODE);
                }
            }else{
                Intent callIntent=new Intent(Intent.ACTION_CALL);
                Uri uri=Uri.parse("tel:" + (toBeDeletedDoctor.getPhone_1().isEmpty() ? toBeDeletedDoctor.getPhone_2() : toBeDeletedDoctor.getPhone_1()));
                callIntent.setData(uri);
                startActivity(callIntent);
            }
        }else{
            Intent callIntent=new Intent(Intent.ACTION_CALL);
            Uri uri=Uri.parse("tel:" + (toBeDeletedDoctor.getPhone_1().isEmpty() ? toBeDeletedDoctor.getPhone_2() : toBeDeletedDoctor.getPhone_1()));
            callIntent.setData(uri);
            startActivity(callIntent);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CALL_PHONE_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent callIntent=new Intent(Intent.ACTION_CALL);
                    Uri uri=Uri.parse("tel:" + (toBeDeletedDoctor.getPhone_1().isEmpty() ? toBeDeletedDoctor.getPhone_2() : toBeDeletedDoctor.getPhone_1()));
                    callIntent.setData(uri);
                    startActivity(callIntent);
                }else{
                    Toast.makeText(getActivity(), "Permission required to make the call", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}
