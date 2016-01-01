package architect.jazzy.medicinereminder.Fragments.OfflineActivity.SearchFragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Activities.MainActivity;
import architect.jazzy.medicinereminder.Fragments.OfflineActivity.BrowserFragment;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.WebDocument;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchDetailFragment extends Fragment {

    WebDocument document;
    TextView titleView, altTitleView, summaryView, urlView, organizationView;
    MainActivity activity;

    public static SearchDetailFragment newInstance(WebDocument document){
        SearchDetailFragment searchDetailFragment=new SearchDetailFragment();
        Bundle args=new Bundle();
        args.putParcelable(Constants.BUNDLE_WEB_DOCUMENT, document);
        searchDetailFragment.setArguments(args);
        return searchDetailFragment;
    }

    public SearchDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_search_detail, container, false);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        this.document=getArguments().getParcelable(Constants.BUNDLE_WEB_DOCUMENT);

        titleView=(TextView)v.findViewById(R.id.title);
        altTitleView=(TextView)v.findViewById(R.id.altTitle);
        summaryView=(TextView)v.findViewById(R.id.fullSummary);
        urlView=(TextView)v.findViewById(R.id.url);
        organizationView=(TextView)v.findViewById(R.id.organizationName);


        titleView.setText(document.getTitle());
        ArrayList<String> alts=document.getAltTitle();
        String s1="";
        for(int i=0;i<alts.size();i++){
            if(i==alts.size()-1){
                s1+=alts.get(i);
            }else{
                s1+=alts.get(i)+", ";
            }
        }
        if(s1.isEmpty()){
            altTitleView.setVisibility(View.GONE);
        }else {
            altTitleView.setVisibility(View.VISIBLE);
            altTitleView.setText(s1);
        }
        summaryView.setText(document.getFullSummary());
        urlView.setText(document.getUrl());
        urlView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    urlView.setText(Html.fromHtml("<u>" + document.getUrl() + "</u>"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    activity.displayFragment(BrowserFragment.getInstance(document.getUrl(), false));
                }else{
                    urlView.setText(document.getUrl());
                }
                return true;
            }
        });
        organizationView.setText(document.getOrganizationName());

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=(MainActivity)activity;
    }
}
