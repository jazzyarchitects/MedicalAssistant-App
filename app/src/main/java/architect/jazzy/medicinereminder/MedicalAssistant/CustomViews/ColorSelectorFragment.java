package architect.jazzy.medicinereminder.MedicalAssistant.CustomViews;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColorSelectorFragment extends DialogFragment implements View.OnClickListener{

    private static final String TAG="ColorSelectorFragment";
    CircleView selected,red,green,blue,dark,light;
    Button saveButton;
    int color;
    public ColorSelectorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try{
            getDialog().requestWindowFeature(STYLE_NO_TITLE);
        }catch (Exception e){
            e.printStackTrace();
        }
        View v=inflater.inflate(R.layout.color_selector,container,false);

        selected=(CircleView)v.findViewById(R.id.circleSelected);
        red=(CircleView)v.findViewById(R.id.circleRed);
        green=(CircleView)v.findViewById(R.id.circleGreen);
        blue=(CircleView)v.findViewById(R.id.circleDefault);
        dark=(CircleView)v.findViewById(R.id.circleDark);
        light=(CircleView)v.findViewById(R.id.circleLight);
        saveButton=(Button)v.findViewById(R.id.saveButton);

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences(Constants.SETTING_PREF, Context.MODE_PRIVATE);
        int selectedColor=sharedPreferences.getInt(Constants.THEME_COLOR, getResources().getColor(R.color.themeColorDefault));
        selected.setFillColor(selectedColor);
        this.color=selectedColor;

        red.setOnClickListener(this);
        green.setOnClickListener(this);
        blue.setOnClickListener(this);
        dark.setOnClickListener(this);
        light.setOnClickListener(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorChangeListener.onThemeColorChange(color);
                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        int selectedColor;
        switch (v.getId()){
            case R.id.circleRed:
                selectedColor=getResources().getColor(R.color.themeColorRed);
                break;
            case R.id.circleGreen:
                selectedColor=getResources().getColor(R.color.themeColorGreen);
                break;
            case R.id.circleDefault:
                selectedColor=getResources().getColor(R.color.themeColorDefault);
                break;
            case R.id.circleDark:
                selectedColor=getResources().getColor(R.color.themeColorDark);
                break;
            case R.id.circleLight:
                selectedColor=getResources().getColor(R.color.themeColorLight);
                break;
            default:
                selectedColor=getResources().getColor(R.color.themeColorDefault);
                break;
        }
        Log.e(TAG,"Selected color: "+selectedColor);
        selected.setFillColor(selectedColor);
        this.color=selectedColor;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        colorChangeListener=(OnColorChangeListener)activity;
    }

    OnColorChangeListener colorChangeListener;
    public interface OnColorChangeListener{
        void onThemeColorChange(int color);
    }
    public void setColorChangeListener(OnColorChangeListener colorChangeListener){
        this.colorChangeListener=colorChangeListener;
    }

    @Override
    public void onStart() {
        super.onStart();
//        Dialog dialog = getDialog();
//        if (dialog != null) {
//            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
    }
}
