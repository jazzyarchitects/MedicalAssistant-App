package architect.jazzy.medicinereminder.MedicalAssistant.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import architect.jazzy.medicinereminder.RemedySharing.RegistrationActivity;
import architect.jazzy.medicinereminder.RemedySharing.Models.User;
import architect.jazzy.medicinereminder.R;

public class ExportFragment extends Fragment {
    private ExportConfirmListener mListener;
    Button exportConfirm;

    public ExportFragment() {
        // Required empty public constructor
    }

    public static ExportFragment newInstance(String param1, String param2) {
        ExportFragment fragment = new ExportFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_export, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exportConfirm = (Button) view.findViewById(R.id.exportButton);
        exportConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (User.isUserLoggedIn(getActivity())) {
                    mListener.onExportConfirmed();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Login Required")
                            .setMessage("You need to login to be able to vote or comment")
                            .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    getActivity().finish();
                                }
                            }).setNegativeButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(getActivity(), RegistrationActivity.class));
                        }
                    })
                            .show();
                }
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ExportConfirmListener) {
            mListener = (ExportConfirmListener) context;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    @TargetApi(14)
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof ExportConfirmListener) {
            mListener = (ExportConfirmListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ExportConfirmListener {
        void onExportConfirmed();
    }
}
