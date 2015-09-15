package architect.jazzy.medicinereminder.Fragments.Practo;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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

    public DoctorSearch() {
        // Required empty public constructor
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

        SearchCriteriaAdapter adapter=new SearchCriteriaAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);

        return v;
    }


}
