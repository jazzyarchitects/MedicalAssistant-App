package architect.jazzy.medicinereminder.Fragments.OnlineActivity.Registration;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import architect.jazzy.medicinereminder.HelperClasses.BackendUrls;
import architect.jazzy.medicinereminder.Models.Client;
import architect.jazzy.medicinereminder.Models.User;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.Services.BackendInterfacer;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {

    TextView loginNow;
    EditText etName, etEmail, etMobile, etPassword, etDD, etMM, etYYYY;
    int[] daysInMonth={31,29,31,30,31,30,31,31,30,31,30,31};
    String sex = "M";
    Button bSignup;
    Context mContext;
    RadioButton rbMale, rbFemale;
    FragmentInteractionListener fragmentInteractionListener;
    AuthenticationListener authenticationListener;

    View.OnClickListener signupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = etEmail.getText().toString();
            if (email.isEmpty()) {
                etEmail.setError("Cannot be empty");
                return;
            }
            String name = etName.getText().toString();
            if (name.isEmpty()) {
                etName.setError("Cannot be empty");
                return;
            }
            Integer dd, mm, yyyy;
            try {

                dd = Integer.parseInt(etDD.getText().toString());
                mm = Integer.parseInt(etMM.getText().toString());
                yyyy = Integer.parseInt(etYYYY.getText().toString());
                if(dd>daysInMonth[mm-1]){
                    etDD.setError("Invalid day in this month");
                }

            } catch (Exception e) {
                return;
            }

            String mobile = etMobile.getText().toString();
            if (mobile.isEmpty()) {
                etMobile.setError("Cannot be empty");
                return;
            }

            String password = etPassword.getText().toString();
            if (password.isEmpty()) {
                etPassword.setError("Cannot be empty");
                return;
            } else if (password.length() < 8) {
                etPassword.setError("Password should be atleast 8 characters long");
                return;
            }

            sex = "M";
            if (rbFemale.isChecked()) {
                sex = "F";
            }

            HashMap<String, String> dataSet = new HashMap<>();
            dataSet.put("name", name);
//            dataSet.put("age", String.valueOf(age));
            dataSet.put("dob",String.valueOf(dd)+"-"+String.valueOf(mm)+"-"+String.valueOf(yyyy));
            dataSet.put("password", password);
            dataSet.put("sex", sex);
            dataSet.put("mobile", mobile);
            dataSet.put("email", email);

            BackendInterfacer backendInterfacer = new BackendInterfacer(BackendUrls.SIGNUP, "POST", dataSet);
            backendInterfacer.setBackendListener(new BackendInterfacer.BackendListener() {
                ProgressDialog progressDialog;

                @Override
                public void onPreExecute() {
                    progressDialog = new ProgressDialog(mContext);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Registering you...");
                    progressDialog.show();
                }

                @Override
                public void onError(final Exception e) {
                    bSignup.post(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage(e.getMessage());
                            builder.setTitle("Error");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                        }
                    });
                }

                @Override
                public void onResult(final String result) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (result == null || result.isEmpty()) {
                        return;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getBoolean("success")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            User user = User.parseUserResponse(data.getJSONObject("detail"));
                            if (user == null) {
                                Toast.makeText(mContext, "Error registering. Please try again", Toast.LENGTH_LONG).show();
                                return;
                            }
                            User.saveUser(mContext, user);

                            Client client = Client.parseClientObject(data.getJSONObject("client"));
                            Client.saveClient(mContext, client);
                            authenticationListener.onUserAuthenticated();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Error");
                            builder.setMessage(jsonObject.optString("message").replace("_", " "));
                            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            backendInterfacer.execute();

        }
    };

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getActivity();
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginNow = (TextView) view.findViewById(R.id.loginNow);

        etEmail = (EditText) view.findViewById(R.id.et_email);
        etName = (EditText) view.findViewById(R.id.et_name);
        etDD=(EditText)view.findViewById(R.id.userDOBdd);
        etMM=(EditText)view.findViewById(R.id.userDOBmm);
        etYYYY=(EditText)view.findViewById(R.id.userDOByyyy);
        etMobile = (EditText) view.findViewById(R.id.et_mobile);
        etPassword = (EditText) view.findViewById(R.id.et_password);
        rbMale = (RadioButton) view.findViewById(R.id.rb_male);
        rbFemale = (RadioButton) view.findViewById(R.id.rb_female);
        bSignup = (Button) view.findViewById(R.id.signupButton);

        bSignup.setOnClickListener(signupListener);

        loginNow.setText(Html.fromHtml("Already registered? <a href='#'>Sign in here...</a>"));
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentInteractionListener.onLoginNow();
            }
        });

        etDD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try{
                    Integer dd=Integer.parseInt(charSequence.toString());
                    if(dd>31 || dd<1){
                        etDD.setError("Invalid Date");
                    }
                }catch (Exception e){
                    Log.d("Signup", "Invalid Int dd");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etMM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try{
                    Integer mm=Integer.parseInt(charSequence.toString());
                    if(mm>12 || mm<1){
                        etMM.setError("Invalid Month");
                    }
                }catch (Exception e){
                    Log.d("Signup", "Invalid Int mm");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etYYYY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try{
                    Integer yyyy=Integer.parseInt(charSequence.toString());
                    if(yyyy<1890 || yyyy> Calendar.getInstance().get(Calendar.YEAR)){
                        etYYYY.setError("Invalid Year");
                    }
                }catch (Exception e){
                    Log.d("Signup", "Invalid Int yyyy");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentInteractionListener = (FragmentInteractionListener) context;
        authenticationListener = (AuthenticationListener) context;
    }

    public interface FragmentInteractionListener {
        void onLoginNow();
    }

    public interface AuthenticationListener {
        void onUserAuthenticated();
    }

}
