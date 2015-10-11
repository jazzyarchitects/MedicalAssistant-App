package architect.jazzy.medicinereminder.CustomComponents;

import android.app.Fragment;

import java.util.Stack;

/**
 * Created by Jibin_ism on 18-Sep-15.
 */
public class FragmentBackStack {
    private static String TAG="FragmentBackStack";

    private static final int FRAGMENT=100;
    private static final int SUPPORT=102;

    Stack<Fragment> fragmentStack=new Stack<>();
    Stack<android.support.v4.app.Fragment> supportFragmentStack=new Stack<>();
    Stack<Integer> indicatorStack=new Stack<>();

    public void push(Fragment fragment){
//        Log.e(TAG,"Pushing fragment ");
        fragmentStack.push(fragment);
        indicatorStack.push(FRAGMENT);
    }

    public void push(android.support.v4.app.Fragment fragment){
//        Log.e(TAG,"pushing support fragment ");
        supportFragmentStack.push(fragment);
        indicatorStack.push(SUPPORT);
    }

    public boolean empty(){
        return indicatorStack.empty();
    }

    public Fragment pop(){
        if(empty()){
            return null;
        }
//        Log.e(TAG,"pop fragment: ");
        if(indicatorStack.get(indicatorStack.size()-1)==FRAGMENT){
            indicatorStack.pop();
            return fragmentStack.pop();
        }
        return null;
    }

    public android.support.v4.app.Fragment popSupport(){
        if(empty()){
            return null;
        }
        if(indicatorStack.get(indicatorStack.size()-1)==SUPPORT){
            indicatorStack.pop();
            return supportFragmentStack.pop();
        }
        return null;
    }

}
