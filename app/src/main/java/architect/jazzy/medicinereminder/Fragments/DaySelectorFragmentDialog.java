package architect.jazzy.medicinereminder.Fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;

import architect.jazzy.medicinereminder.R;

public class DaySelectorFragmentDialog extends DialogFragment implements CheckBox.OnCheckedChangeListener {


    Context mContext;
    GridView gridView;

    DialogFragment fragment;

    boolean[] selectedDays=new boolean[]{true, true, true, true, true, true, true};

    int[] days = new int[]{R.id.daySunday, R.id.dayMonday, R.id.dayTuesday, R.id.dayWednesday,
            R.id.dayThursday, R.id.dayFriday, R.id.daySaturday};

    private OnFragmentInteractionListener mListener;

    public DaySelectorFragmentDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
            selectedDays=getArguments().getBooleanArray("SELECTED_DAYS");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.day_selection_fragment, null);
        fragment = this;
        mContext = getActivity().getApplicationContext();
        getDialog().setTitle("Select Days");

        for (int i=0;i<7;i++){
            ((CheckBox)v.findViewById(days[i])).setChecked(selectedDays[i]);
            ((CheckBox)v.findViewById(days[i])).setOnCheckedChangeListener(this);
        }

        return v;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position=0;
        int id=buttonView.getId();
        for(int i=0;i<days.length;i++){
            if(days[i]==id){
                position=i;
                break;
            }
        }
        Log.e("DaySelectorFragment","CLiked "+position+isChecked);
        mListener.onDaySelectionChanged(position, isChecked);
    }

    public interface OnFragmentInteractionListener {
        void onDaySelectionChanged(int position, Boolean isChecked);
    }

}
