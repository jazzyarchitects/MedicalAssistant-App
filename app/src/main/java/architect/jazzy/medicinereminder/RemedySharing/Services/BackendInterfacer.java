package architect.jazzy.medicinereminder.RemedySharing.Services;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.RemedySharing.Models.Client;

/**
 * Created by Jibin_ism on 13-Dec-15.
 */
public class BackendInterfacer extends AsyncTask<Void, Void, String> {

    private static final String TAG = "BackendInterfacer";
    String method;
    URL mUrl;
    Client client;
    HashMap<String, String> mDataSet;

    public BackendInterfacer(Context context, String url, String method, HashMap<String, String> dataSet) {
        try {
            this.mUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.mDataSet = dataSet;
        this.method = method;
        if (!method.equalsIgnoreCase("PUT") && !method.equalsIgnoreCase("POST") && !method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("DELETE")) {
            throw new RuntimeException("Unexpected Backend interfacer method: " + method);
        }
        if(context!=null) {
            this.client = Client.getClient(context);
        }
    }

    public BackendInterfacer(String url, String method, HashMap<String, String> dataSet) {
        this(null,url, method, dataSet);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (backendListener != null) {
            backendListener.onPreExecute();
        }
        if(simpleBackendListener!=null){
            simpleBackendListener.onPreExecute();
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) mUrl.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(45000);
            urlConnection.setDoInput(true);

            if (client != null) {
                urlConnection.setRequestProperty("x-access-id", client.getId());
                urlConnection.setRequestProperty("x-access-key", client.getKey());
            }
            urlConnection.setRequestProperty("x-service-id", "androidApp1958-2013JE0305");


            if (mDataSet != null) {
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                osw.write(Constants.getPostDataString(mDataSet));
                osw.flush();
                osw.close();
                os.close();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String s = "";
            String line;
            while ((line = reader.readLine()) != null) {
                s += line;
            }
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            if (errorListener != null) {
                errorListener.onError(e);
            }
            if (backendListener != null) {
                backendListener.onError(e);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        if (backendListener == null && resultListener == null) {
//            throw new RuntimeException("Result not passed to calling function...");
//        }
        if(simpleBackendListener!=null){
            simpleBackendListener.onResult(s);
        }
        if (resultListener != null) {
            resultListener.onResult(s);
        }
        if (backendListener != null) {
            backendListener.onResult(s);
        }
    }


    BackendListener backendListener;
    ResultListener resultListener;
    ErrorListener errorListener;
    SimpleBackendListener simpleBackendListener;

    public interface BackendListener {
        void onPreExecute();

        void onError(Exception e);

        void onResult(String result);
    }

    public interface ResultListener {
        void onResult(String result);
    }

    public interface ErrorListener {
        void onError(Exception e);
    }

    public interface SimpleBackendListener{
        void onPreExecute();
        void onResult(String result);
    }

    public void setBackendListener(BackendListener backendListener) {
        this.backendListener = backendListener;
    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setSimpleBackendListener(SimpleBackendListener simpleBackendListener) {
        this.simpleBackendListener = simpleBackendListener;
    }
}
