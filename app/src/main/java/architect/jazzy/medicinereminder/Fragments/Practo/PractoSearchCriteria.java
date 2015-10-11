package architect.jazzy.medicinereminder.Fragments.Practo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import architect.jazzy.medicinereminder.Fragments.Practo.Adapter.SearchCriteriaAdapter;
import architect.jazzy.medicinereminder.R;

public class PractoSearchCriteria extends Fragment {

    int searchCriteria=101;
    View v;

    public static PractoSearchCriteria newInstance(int searchCriteria) {
        PractoSearchCriteria fragment = new PractoSearchCriteria();
        Bundle args = new Bundle();
        args.putInt("criteria",searchCriteria);
        fragment.setArguments(args);
        return fragment;
    }

    public PractoSearchCriteria() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchCriteria=getArguments().getInt("criteria");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_practo_search_criteria, container, false);
//        Log.e(getClass().getName(),"On Create");
        Spinner spinner=(Spinner)v.findViewById(R.id.spinner);
        RelativeLayout relativeLayout=(RelativeLayout)v.findViewById(R.id.viewPagerBackground);

        int backgroundId=R.drawable.back_city;

        switch (searchCriteria){
            case SearchCriteriaAdapter.OPTION_CITY:
                backgroundId=R.drawable.back_city;
                break;
            case SearchCriteriaAdapter.OPTION_LOCALITY:
                backgroundId=R.drawable.back_street;
                break;
            case SearchCriteriaAdapter.OPTION_SORT_BY:
                break;
            case SearchCriteriaAdapter.OPTION_SEARCH_FOR:
//                backgroundId=R.drawable.back_clinic;
                break;
            case SearchCriteriaAdapter.OPTION_SPECIALIZATION:
//                backgroundId=R.drawable.back_doc;
                break;
            default:
                backgroundId=R.drawable.back_city;
                break;
        }

        relativeLayout.setBackgroundResource(backgroundId);

        return v;
    }


    SpinnerItemSelectedListener spinnerItemSelectedListener;
    public interface SpinnerItemSelectedListener{
        void onSpinnerItemSelected(int position);
    }
    public void setSpinnerItemSelectedListener(SpinnerItemSelectedListener spinnerItemSelectedListener){
        this.spinnerItemSelectedListener=spinnerItemSelectedListener;
    }
}
