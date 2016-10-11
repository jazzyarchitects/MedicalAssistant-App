package architect.jazzy.medicinereminder.RemedySharing.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.RemedySharing.Models.User;
import architect.jazzy.medicinereminder.RemedySharing.RegistrationActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsFragment extends Fragment {

  public static final String TAG = "UserDetailsTag";
  EditText etUserName, etUserMobile, etUserEmail, etDD, etMM, etYYYY;
  Context mContex;
  AppCompatActivity parentActivity;
  CollapsingToolbarLayout collapsingToolbarLayout;

  public UserDetailsFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    if (!User.isUserLoggedIn(getActivity())) {
      startActivity(new Intent(getActivity(), RegistrationActivity.class));
      getActivity().finish();
      return null;
    }

    mContex = getActivity();
    return inflater.inflate(R.layout.fragment_user_details, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    parentActivity = (AppCompatActivity) getActivity();

    Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

    parentActivity.getSupportActionBar().hide();
    parentActivity.setSupportActionBar(toolbar);
    parentActivity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    parentActivity.getSupportActionBar().setTitle("");

    etUserName = (EditText) view.findViewById(R.id.userName);
    etUserMobile = (EditText) view.findViewById(R.id.userMobile);
    etUserEmail = (EditText) view.findViewById(R.id.userEmail);
    etDD = (EditText) view.findViewById(R.id.userDOBdd);
    etMM = (EditText) view.findViewById(R.id.userDOBmm);
    etYYYY = (EditText) view.findViewById(R.id.userDOByyyy);
    collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);

    toolbar.setTitle("Details");
    User user = User.getUser(mContex);

    etUserName.setText(user.getName());
    etUserEmail.setText(user.getEmail());
    etUserMobile.setText(user.getMobile());

    etDD.setText("19");
    etMM.setText("05");
    etYYYY.setText("1996");


  }
}
