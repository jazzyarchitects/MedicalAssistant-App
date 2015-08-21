package architect.jazzy.medicinereminder.Fragments;


import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.ThisApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsDetailFragment extends Fragment {


    WebView webView;
    ProgressBar progressBar;
    Uri newsUrl;
    ImageView logo;

    public static NewsDetailFragment getInstance(String url) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_SELECTED_NEWS, url);
        args.putBoolean("isNews", true);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewsDetailFragment getInstance(String url, boolean isNews) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_SELECTED_NEWS, url);
        args.putBoolean("isNews", isNews);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsDetailFragment() {
        // Required empty public constructor
    }

    View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_news_detail, container, false);


        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Tracker t = ((ThisApplication) getActivity().getApplication()).getTracker(
                ThisApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Browser");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.AppViewBuilder().build());

        boolean b = getArguments().getBoolean("isNews");
        try {
            String title="";
            if(b){
                title="News";
            }else{
                title="Search Result";
            }
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        logo = (ImageView) v.findViewById(R.id.logo);
        newsUrl = Uri.parse(getArguments().getString(Constants.BUNDLE_SELECTED_NEWS));
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
//        progressBar.setMax(100);
//        progressBar.setBackgroundColor(getResources().getColor(R.color.pager_footer));
        webView = (WebView) v.findViewById(R.id.webView);
        webView.setWebViewClient(new NewsClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                float progressLeft = 100 - newProgress;
//                Log.e("Progress ", String.valueOf(newProgress));
//                Log.e("Progress ", "Scale: " + (progressLeft / 100));
//                logo.setScaleX(progressLeft / 100);
//                logo.setScaleY(progressLeft / 100);
                progressBar.setProgress(newProgress);
                if (newProgress >= 50) {
                    logo.setVisibility(View.GONE);
                }

                super.onProgressChanged(view, newProgress);
            }
        });
        webView.loadUrl(String.valueOf(newsUrl));

        return v;
    }


    class NewsClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

    }
}
