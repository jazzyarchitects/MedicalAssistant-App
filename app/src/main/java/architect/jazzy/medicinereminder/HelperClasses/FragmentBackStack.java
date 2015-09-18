package architect.jazzy.medicinereminder.HelperClasses;

import android.app.Fragment;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jibin_ism on 18-Sep-15.
 */
public class FragmentBackStack {
    static int top=0;
    private static String TAG="FragmentBackStack";

    Map<Integer, Fragment> fragmentMap=new HashMap<>();
    Map<Integer, android.support.v4.app.Fragment> supportFragmentMap=new HashMap<>();

    public void push(Fragment fragment){
        top++;
        Log.e(TAG,"Pushing fragment" );
        fragmentMap.put(top,fragment);
    }

    public void push(android.support.v4.app.Fragment fragment){
        top++;
        Log.e(TAG,"pushing support fragment");
        supportFragmentMap.put(top,fragment);
    }

    public boolean empty(){
        Log.e(TAG,"Empty check");
        return top<=1;
    }

    public Fragment pop(){
        if(top==0){
            return null;
        }
        Log.e(TAG,"pop fragment");
        if(fragmentMap.containsKey(top-1)){
            top--;
            Fragment fragment= fragmentMap.get(top);
            fragmentMap.remove(top);
            return fragment;
        }
        return null;
    }

    public android.support.v4.app.Fragment popSupport(){
        if(top==0){
            return null;
        }
        if(supportFragmentMap.containsKey(top-1)){
            Log.e(TAG,"pop support fragment: "+(top-1)+" "+supportFragmentMap.get(top-1).toString());
            top--;
            android.support.v4.app.Fragment fragment=supportFragmentMap.get(top);
            supportFragmentMap.remove(top);
            return fragment;
        }
        return null;
    }

}
