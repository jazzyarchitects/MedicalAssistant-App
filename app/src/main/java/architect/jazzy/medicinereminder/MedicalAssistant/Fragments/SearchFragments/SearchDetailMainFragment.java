package architect.jazzy.medicinereminder.MedicalAssistant.Fragments.SearchFragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.MedicalAssistant.Activities.MainActivity;
import architect.jazzy.medicinereminder.MedicalAssistant.Adapters.SearchResultPagerAdapter;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.WebDocument;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchDetailMainFragment extends Fragment {

    EditText searchQuery;
    ViewPager viewPager;
    MainActivity activity;


    public static SearchDetailMainFragment newInstance(ArrayList<WebDocument> documents, String searchQuery,@Nullable Integer position){
        SearchDetailMainFragment fragment=new SearchDetailMainFragment();
        Bundle args=new Bundle();
        args.putParcelableArrayList(Constants.BUNDLE_WEB_DOCUMENT, documents);
        args.putString(Constants.BUNDLE_SEARCH_TERM, searchQuery);
        if(position==null){
            position=0;
        }
        args.putInt("position",position);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchDetailMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=(MainActivity)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_search_detail_main, container, false);

        v.findViewById(R.id.root).setBackgroundDrawable(getResources().getDrawable(R.drawable.back_night));
        v.findViewById(R.id.searchLayout).setBackgroundColor(Constants.getThemeColor(getActivity()));
        try{
//            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Search Results");
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        viewPager=(ViewPager)v.findViewById(R.id.viewPager);
        searchQuery = (EditText) v.findViewById(R.id.searchLayout).findViewById(R.id.searchQuery);

        ArrayList<WebDocument> documents=getArguments().getParcelableArrayList(Constants.BUNDLE_WEB_DOCUMENT);
        String search=getArguments().getString(Constants.BUNDLE_SEARCH_TERM);

        searchQuery.setText(search);
        SearchResultPagerAdapter adapter=new SearchResultPagerAdapter(getFragmentManager(),documents);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getArguments().getInt("position"));
        searchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    performSearch(v.getText().toString());
                    activity.displayFragment(SearchFragment.initiate(v.getText().toString()),false);

                }
                return false;
            }
        });

        return v;

    }


}
