package architect.jazzy.medicinereminder.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Calendar;

import architect.jazzy.medicinereminder.CustomViews.CircleView;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.TimingClass;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;

public class DashboardFragment extends Fragment {

    private static final String TAG="Dashboard";
    View v;
    CircleView todayView, medicineCountView, appointmentCountView;
    FloatingActionButton floatingActionButton;
    RelativeLayout relativeLayout;
    boolean isMenuOpen=false;

    public static final int ADD_DOCTOR_FAB_ID=305;
    public static final int ADD_MEDICINE_FAB_ID=218;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_dashboard, container, false);

        isMenuOpen=false;
        todayView=(CircleView)v.findViewById(R.id.mainIndicator);
        medicineCountView=(CircleView)v.findViewById(R.id.medicineCountView);
        appointmentCountView=(CircleView)v.findViewById(R.id.appointmentCount);
        floatingActionButton=(FloatingActionButton)v.findViewById(R.id.floatingActionButton);
        relativeLayout=(RelativeLayout)v.findViewById(R.id.r1);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"fab add onclick");
                addNewItems();
            }
        });

        Calendar calendar=Calendar.getInstance();
        todayView.setBigText(calendar.get(Calendar.DATE)+" "+TimingClass.getEquivalentMonth(calendar.get(Calendar.MONTH))+" "+calendar.get(Calendar.YEAR));

        DataHandler handler=new DataHandler(getActivity());
        ArrayList<Medicine> medicines=handler.getTodaysMedicine();
        if(medicines==null){
            medicineCountView.setBigText("0");
        }else{
            if(medicines.size()==0){
                medicineCountView.setBigText("0");
            }else{
                medicineCountView.setBigText(String.valueOf(medicines.size()));
            }
        }
        return v;
    }

    void addNewItems(){
        if(isMenuOpen){
            return;
        }
        isMenuOpen=true;
        Log.e(TAG, "fab doc creating");
        FloatingActionButton addDocFab=new FloatingActionButton(getActivity());
        addDocFab.setImageResource(R.drawable.ic_action_user);
        addDocFab.setClickable(true);
        addDocFab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}},new int[]{getResources().getColor(R.color.actionBackground)}));
        addDocFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "fab add doctor onclick");
                onFragmentInteractionListener.addDoctor();
            }
        });
        RelativeLayout.LayoutParams docFabLayoutParams=new RelativeLayout.LayoutParams((int)getResources().getDimension(R.dimen.floatingActionButtonSize),(int)getResources().getDimension(R.dimen.floatingActionButtonSize));

        int bottom=10 + (int) getResources().getDimension(R.dimen.floatingActionButtonSize) + 10 + 10;
        docFabLayoutParams.setMargins(10, 10, 10, bottom);
        docFabLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        docFabLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addDocFab.setLayoutParams(docFabLayoutParams);


        FloatingActionButton addMedFab=new FloatingActionButton(getActivity());
        Drawable drawable=getResources().getDrawable(R.drawable.ic_action_pill);
        try {
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        relativeLayout.addView(addDocFab);
        addMedFab.setImageDrawable(drawable);
        addMedFab.setClickable(true);
        addMedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentInteractionListener.addMedicine();
            }
        });
        addMedFab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.color_death)}));
        RelativeLayout.LayoutParams addMedLayoutParams=new RelativeLayout.LayoutParams((int)getResources().getDimension(R.dimen.floatingActionButtonSize),(int)getResources().getDimension(R.dimen.floatingActionButtonSize));
        addMedLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addMedLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottom*=2;
        bottom-=10;
        addMedLayoutParams.setMargins(10, 10, 10, bottom);
        addMedFab.setLayoutParams(addMedLayoutParams);


        relativeLayout.addView(addMedFab);

        addDocFab.startAnimation(getShowAnimation(true));
        addMedFab.startAnimation(getShowAnimation(false));

    }

    Animation getShowAnimation(boolean isFirst){
        Log.e(TAG,"fab getting animation");
        int fromX=20+(int)getResources().getDimension(R.dimen.floatingActionButtonSize);
        TranslateAnimation animation=new TranslateAnimation(fromX,0,0,0);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setDuration(getResources().getInteger(R.integer.fabAnimDuration));
        if(!isFirst) {
            animation.setStartOffset(getResources().getInteger(R.integer.fabAnimDuration) / 2);
        }
        return animation;
    }

    OnFragmentInteractionListener onFragmentInteractionListener;
    public interface OnFragmentInteractionListener{
        void addMedicine();
        void addDoctor();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onFragmentInteractionListener=(OnFragmentInteractionListener)activity;
    }
}
