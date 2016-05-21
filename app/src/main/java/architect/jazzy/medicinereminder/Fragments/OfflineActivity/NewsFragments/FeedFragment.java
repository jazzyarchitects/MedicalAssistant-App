package architect.jazzy.medicinereminder.Fragments.OfflineActivity.NewsFragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import architect.jazzy.medicinereminder.Activities.MainActivity;
import architect.jazzy.medicinereminder.Models.FeedItem;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    FeedItem feed;
    TextView titleView, descriptionView;
    LinearLayout feedHolder;

    public static FeedFragment newInstance(FeedItem item) {
        FeedFragment fragment = new FeedFragment();
        Bundle args=new Bundle();
        args.putParcelable("feed",item);
        fragment.setArguments(args);
        return fragment;
    }

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feed=getArguments().getParcelable("feed");
//        Log.e("FeedFragment","Feed Contents: "+feed.getTitle()+" "+feed.getUrl()+" "+feed.getDescription());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_feed, container, false);
        titleView=(TextView)v.findViewById(R.id.title);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
//        descriptionView=(TextView)v.findViewById(R.id.description);
        feedHolder=(LinearLayout)v.findViewById(R.id.feed);

//        Log.e("FeedFragment", "Fragment Created");
        titleView.setText(feed.getTitle());
//        descriptionView.setText(Html.fromHtml(feed.getDescription()));
        feedHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedDetails(feed.getUrl());
            }
        });
        return v;
    }

    public void showFeedDetails(String url){
        ((MainActivity)getActivity()).showFeed(url);
    }


}
