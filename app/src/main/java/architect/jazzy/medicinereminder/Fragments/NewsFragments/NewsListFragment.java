package architect.jazzy.medicinereminder.Fragments.NewsFragments;


import android.app.Fragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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

    View v;
    RecyclerView newsList;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButton;
    String feedUrl=FeedParser.feedUrl;
    ImageView noNetworkImageView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    boolean firstTime=false;

    public static NewsListFragment getInstance(Pair<String, String> category){
        NewsListFragment fragment=new NewsListFragment();
        Bundle args=new Bundle();
        args.putString("Title",category.first);
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
        firstTime=true;
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

        noNetworkImageView=(ImageView)v.findViewById(R.id.noNetworkPic);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("News");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try{
            Bundle args=getArguments();
            String title;
            title=args.getString("Title");
            feedUrl=args.getString("Url");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("News - "+title);
        }catch (NullPointerException e){
            feedUrl=FeedParser.feedUrl;
        }

        RelativeLayout relativeLayout=(RelativeLayout)v.findViewById(R.id.back);
        Drawable drawable=relativeLayout.getBackground().mutate();
        drawable.setColorFilter(Constants.getThemeColor(getActivity()), PorterDuff.Mode.MULTIPLY);
        relativeLayout.setBackgroundDrawable(drawable);


        newsList = (RecyclerView) v.findViewById(R.id.newsfeed);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        floatingActionButton=(FloatingActionButton)v.findViewById(R.id.floatingActionButton);
        floatingActionButton.setBackgroundTintList(Constants.getFabBackground(getActivity()));

        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        newsList.setLayoutManager(staggeredGridLayoutManager);

        if(firstTime) {
            if(Constants.isNetworkAvailable(getActivity()))
                new FeedsRetriever().execute();
            else
                noNetworkImageView.setVisibility(View.VISIBLE);
            Log.e("NewsList","First Time retrieving");
        }else{
            Log.e("NewsList","Showing saved file");
            progressBar.setVisibility(View.GONE);
            newsList.setAdapter(new NewsListAdapter(getActivity(),FeedParser.parse(),getActivity()));
        }
        firstTime=false;
//        try {
//            progressBar.setVisibility(View.GONE);
//            NewsListAdapter adapter = new NewsListAdapter(getActivity(), FeedParser.parse());
//            newsList.setAdapter(adapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsFeedCategoryPopup popup=new NewsFeedCategoryPopup(getActivity());
                popup.setCategorySelectListener(new NewsFeedCategoryPopup.CategorySelectListener() {
                    @Override
                    public void onCategorySelect(Pair<String, String> category) {
                        String title=category.first;
                        feedUrl=category.second;
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("News - "+title);
                        new FeedsRetriever().execute();
                    }
                });
                popup.show();
            }
        });

        return v;
    }


    public class FeedsRetriever extends AsyncTask<Void, Void, ArrayList<FeedItem>> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            noNetworkImageView.setVisibility(View.GONE);
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
            Log.e("MainActivity", "In Post Execute");
            progressBar.setVisibility(View.GONE);
            if (items == null) {
                noNetworkImageView.setVisibility(View.VISIBLE);
                return;
            }

            newsList.setVisibility(View.VISIBLE);
            newsList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            NewsListAdapter adapter = new NewsListAdapter(getActivity(), items,getActivity());
            newsList.setAdapter(adapter);

        }
    }

}
