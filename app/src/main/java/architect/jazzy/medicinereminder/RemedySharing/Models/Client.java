package architect.jazzy.medicinereminder.RemedySharing.Models;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jibin_ism on 19-Dec-15.
 */
public class Client {
    private String id="",key="",user="";

    public Client() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static Client parseClientObject(JSONObject jsonObject){
        Client client=new Client();
        try{
            client.setId(jsonObject.getString("id"));
            client.setKey(jsonObject.getString("key"));
            client.setUser(jsonObject.optString("user"));
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return client;
    }

    public static void saveClient(Context context, Client client){
        SharedPreferences sharedPreferences=context.getSharedPreferences("ClientPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString("key",client.getKey());
        editor.putString("id",client.getId());
        editor.putString("user",client.getUser());

        editor.apply();
    }

    public static Client getClient(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("ClientPref",Context.MODE_PRIVATE);

        Client client=new Client();
        client.setKey(sharedPreferences.getString("key",""));
        client.setId(sharedPreferences.getString("id",""));
        client.setUser(sharedPreferences.getString("user",""));

        return client;
    }


}
