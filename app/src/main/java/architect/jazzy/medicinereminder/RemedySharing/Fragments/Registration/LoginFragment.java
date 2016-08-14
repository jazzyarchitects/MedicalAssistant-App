package architect.jazzy.medicinereminder.RemedySharing.Fragments.Registration;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import architect.jazzy.medicinereminder.RemedySharing.OnlineActivity;
import architect.jazzy.medicinereminder.RemedySharing.HelperClasses.BackendUrls;
import architect.jazzy.medicinereminder.RemedySharing.Models.Client;
import architect.jazzy.medicinereminder.RemedySharing.Models.User;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.RemedySharing.Services.BackendInterfacer;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    TextView registerNow, skipLogin;
    EditText ETemail, ETpassword;
    Button Blogin;
    Context mContext;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext=getActivity();

        ETemail=(EditText)view.findViewById(R.id.et_email);
        ETpassword=(EditText)view.findViewById(R.id.et_password);
        Blogin=(Button)view.findViewById(R.id.loginButton);
        skipLogin=(TextView)view.findViewById(R.id.skip);



        registerNow=(TextView)view.findViewById(R.id.registerNow);
        registerNow.setText(Html.fromHtml("Not registered yet? <a href='#'>Register here...</a>"));
        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentInteractionListener.onRegisterNow();
            }
        });

        skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, OnlineActivity.class));
                getActivity().finish();
            }
        });

        Blogin.setOnClickListener(loginClickListener);

//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

    }

    View.OnClickListener loginClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email=ETemail.getText().toString();
            String password=ETpassword.getText().toString();

            if(email.isEmpty() || (!isValidEmail(email) && !isValidMobile(email))){
                ETemail.setError("Invalid Email id or mobile");
                return;
            }
            if(password.isEmpty()){
                ETpassword.setError("Password cannot be empty");
                return;
            }

            HashMap<String , String> dataSet=new HashMap<>();
            if(isValidEmail(email)){
                dataSet.put("email",email);
            }else{
                dataSet.put("mobile",email);
            }
            dataSet.put("password",password);

            BackendInterfacer backendInterfacer=new BackendInterfacer(BackendUrls.LOGIN, "POST", dataSet);
            backendInterfacer.setBackendListener(new BackendInterfacer.BackendListener() {
                ProgressDialog progressDialog;
                @Override
                public void onPreExecute() {
                    progressDialog=new ProgressDialog(mContext);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Logging you in...");
                    progressDialog.show();
                }

                @Override
                public void onError(final Exception e) {
                    Blogin.post(new Runnable() {
                        @Override
                        public void run() {
                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }

                            AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
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
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    if(result==null || result.isEmpty()){
                        return;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.getBoolean("success")){
                            JSONObject data=jsonObject.getJSONObject("data");
                            User user = User.parseUserResponse(data.getJSONObject("detail"));
                            if(user==null){
                                Toast.makeText(mContext, "Error registering. Please try again", Toast.LENGTH_LONG).show();
                                return;
                            }
                            User.saveUser(mContext, user);

                            Client client=Client.parseClientObject(data.getJSONObject("client"));
                            Client.saveClient(mContext, client);
                            authenticationListener.onUserAuthenticated();
                        }else{
                            AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                            builder.setTitle("Error");
                            builder.setMessage(jsonObject.optString("message").replace("_"," "));
                            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            backendInterfacer.execute();

        }
    };

    boolean isValidEmail(String email){
        return true;
    }
    boolean isValidMobile(String mobile){
        return true;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        fragmentInteractionListener=(FragmentInteractionListener)activity;
        authenticationListener=(AuthenticationListener)activity;
    }

    FragmentInteractionListener fragmentInteractionListener;
    public interface FragmentInteractionListener{
        void onRegisterNow();
    }
    AuthenticationListener authenticationListener;
    public interface AuthenticationListener{
        void onUserAuthenticated();
    }
}
