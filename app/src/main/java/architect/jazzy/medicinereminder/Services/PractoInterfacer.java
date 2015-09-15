package architect.jazzy.medicinereminder.Services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 15-Sep-15.
 */
public class PractoInterfacer extends AsyncTask<String,Void, String > {

    PractoServerListener practoServerListener;
    Context context;
    boolean error=false;
    public PractoInterfacer(Context context) {
        super();
        this.context=context;
        error=false;
    }

    @Override
    protected String doInBackground(String... params) {
        try{
            URL url=new URL(params[0]);
            HttpsURLConnection connection=(HttpsURLConnection)url.openConnection();
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-CLIENT-ID", context.getResources().getString(R.string.client_id));
            connection.setRequestProperty("X-API-KEY", context.getResources().getString(R.string.api_key));

            Log.e("PractoInterfacer", connection.getRequestProperties().toString());

            InputStream is=connection.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(is));
            String response="";
            String line=null;
            while((line=reader.readLine())!=null){
                response+=line;
            }

            if(!response.isEmpty()){
                return response;
            }

            error=true;
            is=connection.getErrorStream();
            reader=new BufferedReader(new InputStreamReader(is));
            response="";
            line=null;
            while((line=reader.readLine())!=null){
                response+=line;
            }
            return response;

        }catch (Exception e){
            e.printStackTrace();
        }



        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(s==null || error){
            practoServerListener.onError(s);
            return;
        }

        practoServerListener.onSuccess(s);


    }


    public interface PractoServerListener{
        void onError(String error);
        void onSuccess(String response);
    }
    public void setPractoServerListener(PractoServerListener practoServerListener){
        this.practoServerListener=practoServerListener;
    }
}
