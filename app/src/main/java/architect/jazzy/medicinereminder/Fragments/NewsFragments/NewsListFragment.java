package architect.jazzy.medicinereminder.Fragments.NewsFragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import architect.jazzy.medicinereminder.Adapters.NewsListAdapter;
import architect.jazzy.medicinereminder.CustomViews.NewsFeedCategoryPopup;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.FeedItem;
import architect.jazzy.medicinereminder.Parsers.FeedParser;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.ThisApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsListFragment extends Fragment {

    private static final String TAG = "NewsListFragment";
    View v;
    RecyclerView newsList;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButton;
    String feedUrl = FeedParser.feedUrl;
    //    ImageView noNetworkImageView;
    SwipeRefreshLayout swipeRefreshLayout;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    boolean firstTime = false;

    private static final String SAVED_STATE_KEY = "STAV";

    public static NewsListFragment getInstance(Pair<String, String> category) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString("Title", category.first);
        args.putString("Url", category.second);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**Analytics Code*/
        Tracker t = ((ThisApplication) getActivity().getApplication()).getTracker(
                ThisApplication.TrackerName.APP_TRACKER);
        t.setScreenName("News List");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_news_list, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
//        noNetworkImageView=(ImageView)v.findViewById(R.id.noNetworkPic);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("News");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            Bundle args = getArguments();
            String title;
            title = args.getString("Title");
            feedUrl = args.getString("Url");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("News - " + title);
        } catch (NullPointerException e) {
            feedUrl = FeedParser.feedUrl;
        }

        RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.back);
        Drawable drawable = relativeLayout.getBackground().mutate();
        drawable.setColorFilter(Constants.getThemeColor(getActivity()), PorterDuff.Mode.MULTIPLY);
        relativeLayout.setBackgroundDrawable(drawable);


        newsList = (RecyclerView) v.findViewById(R.id.newsfeed);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        floatingActionButton = (FloatingActionButton) v.findViewById(R.id.floatingActionButton);
        floatingActionButton.setBackgroundTintList(Constants.getFabBackground(getActivity()));

        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.GRAY, Color.BLACK);
        swipeRefreshLayout.setDistanceToTriggerSync(50);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    Log.e(TAG, "OnSwipeRefreshListener");
                    new FeedsRetriever().execute();;
            }
        });


        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        newsList.setLayoutManager(staggeredGridLayoutManager);
        firstTime = !getActivity().getSharedPreferences(Constants.INTERNAL_PREF, Context.MODE_PRIVATE).getBoolean(Constants.NEWS_LIST_LOADED, false);

        if (firstTime) {
            if (Constants.isNetworkAvailable(getActivity()))
                new FeedsRetriever().execute();
            else {
//                noNetworkImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Error connecting... Please try again", Toast.LENGTH_LONG).show();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            ArrayList<FeedItem> items = FeedParser.parse();
            if (items == null) {
                new FeedsRetriever().execute();
            } else {
                NewsListAdapter adapter = new NewsListAdapter(getActivity(), items, getActivity());
                adapter.setFeedClickListener(new NewsListAdapter.FeedClickListener() {
                    @Override
                    public void onFeedClick(FeedItem item) {
                        feedClickListener.onFeedClick(item);
                    }
                });
                newsList.setAdapter(adapter);
            }
        }
        firstTime = false;
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsFeedCategoryPopup popup = new NewsFeedCategoryPopup(getActivity());
                popup.setCategorySelectListener(new NewsFeedCategoryPopup.CategorySelectListener() {
                    @Override
                    public void onCategorySelect(Pair<String, String> category) {
                        String title = category.first;
                        feedUrl = category.second;
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("News - " + title);
                        new FeedsRetriever().execute();
                    }
                });
                popup.show();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        feedClickListener = (FeedClickListener) activity;
    }

    public class FeedsRetriever extends AsyncTask<Void, Void, ArrayList<FeedItem>> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
//            noNetworkImageView.setVisibility(View.GONE);
            newsList.setVisibility(View.GONE);
            super.onPreExecute();
        }


        @Override
        protected ArrayList<FeedItem> doInBackground(Void... params) {
            try {
                Log.e("MainActivity", "DoInBackground");
                URL url = new URL(feedUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                if (inputStream == null)
                    return null;

                File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmpMR");
                if (inputStream != null) {
//                    Log.e("FeedParser","In not null");
                    folder.mkdirs();
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(folder, "tmpMR00.tmp"));
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
//                        Log.e("FeedParser","Try 1");
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
//                            Log.e("FeedParser","In While");
                            fileOutputStream.write(buffer, 0, bufferLength);
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        File file = new File(folder, "tmpMR00.tmp");
                        File out = new File(folder, "tmpMR01.tmp");

                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        PrintWriter writer = new PrintWriter(new FileWriter(out));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line.replace("&lt;", "<").replace("&gt;", ">"));
                        }
                        reader.close();
                        writer.close();
                    } catch (FileNotFoundException fe) {
                        fe.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return FeedParser.parse();
            } catch (Exception e) {
                e.getStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<FeedItem> items) {
            super.onPostExecute(items);
            swipeRefreshLayout.setRefreshing(false);
            Log.e("MainActivity", "In Post Execute");
            progressBar.setVisibility(View.GONE);
            if (items == null) {
//                noNetworkImageView.setVisibility(View.VISIBLE);
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Error Connecting...Swipe down to refresh...", Toast.LENGTH_LONG).show();
                }
                return;
            }

            newsList.setVisibility(View.VISIBLE);
            newsList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            if(getActivity()!=null) {
                getActivity().getSharedPreferences(Constants.INTERNAL_PREF, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(Constants.NEWS_LIST_LOADED, true)
                        .apply();
            }
            NewsListAdapter adapter = new NewsListAdapter(getActivity(), items, getActivity());
            adapter.setFeedClickListener(new NewsListAdapter.FeedClickListener() {
                @Override
                public void onFeedClick(FeedItem item) {
                    feedClickListener.onFeedClick(item);
                }
            });
            newsList.setAdapter(adapter);
        }
    }


    Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("urls", feedUrl);
        bundle.putBoolean("loaded", true);
        return bundle;
    }

    FeedClickListener feedClickListener;

    public interface FeedClickListener {
        void onFeedClick(FeedItem feedItem);
    }
}
