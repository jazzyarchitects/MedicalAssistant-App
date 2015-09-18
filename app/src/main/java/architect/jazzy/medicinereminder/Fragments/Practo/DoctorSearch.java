package architect.jazzy.medicinereminder.Fragments.Practo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import architect.jazzy.medicinereminder.Fragments.Practo.Adapter.SearchCriteriaAdapter;
import architect.jazzy.medicinereminder.HelperClasses.Practo;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.Services.PractoInterfacer;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorSearch extends Fragment {


    View v;
    TextView simpleText;
    ViewPager viewPager;
    Button findNowButton;
    int curentPosition=0;


    public DoctorSearch() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(getClass().getName(), "OnCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_doctor_search, container, false);
        simpleText=(TextView)v.findViewById(R.id.simpleText);
        viewPager=(ViewPager)v.findViewById(R.id.viewPager);
        findNowButton=(Button)v.findViewById(R.id.findNowButton);


        PractoInterfacer practoInterfacer=new PractoInterfacer(getActivity());
        practoInterfacer.setPractoServerListener(new PractoInterfacer.PractoServerListener() {
            @Override
            public void onError(String error) {
                simpleText.setText("Error\n\n" + error);
            }

            @Override
            public void onSuccess(String response) {
                simpleText.setText("Success:\n\n" + response);
            }
        });
        practoInterfacer.execute(Practo.getDoctorListUrl());

        Log.e(getClass().getName(),"OnCreateView");
        SearchCriteriaAdapter adapter=new SearchCriteriaAdapter(this.getChildFragmentManager());
        viewPager.setAdapter(null);
        viewPager.setAdapter(adapter);

        return v;
    }

    @Override
    public void onPause() {
        curentPosition=viewPager.getCurrentItem();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        curentPosition=viewPager.getCurrentItem();
        super.onDestroyView();
    }


}
