package architect.jazzy.medicinereminder.Fragments.SearchFragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

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

import architect.jazzy.medicinereminder.Activities.MainActivity;
import architect.jazzy.medicinereminder.Adapters.SearchListAdapter;
import architect.jazzy.medicinereminder.Fragments.BrowserFragment;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Parsers.SearchResultParser;
import architect.jazzy.medicinereminder.Models.SearchQuery;
import architect.jazzy.medicinereminder.Models.SearchResult;
import architect.jazzy.medicinereminder.Models.WebDocument;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    View v;
    EditText searchQuery;
    String term;
    RecyclerView recyclerView;
    MainActivity activity;
    SearchListAdapter searchListAdapter=null;
    TextView spellingView;
    boolean hideSuggestion=true;
    ArrayList<WebDocument> documents=null;
    boolean initalSearch=true;

    public static SearchFragment initiate(String searchQuery) {
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_SEARCH_TERM, searchQuery);
        fragment.setArguments(bundle);
        return fragment;
    }

    int i=0;
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_search, container, false);

//        Log.e(TAG, String.valueOf(i++));
        searchQuery = (EditText) v.findViewById(R.id.searchLayout).findViewById(R.id.searchQuery);
//        Log.e(TAG, String.valueOf(i++));
        spellingView=(TextView)v.findViewById(R.id.suggestion);
//        Log.e(TAG, String.valueOf(i++));
        searchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    performSearch(v.getText().toString());
                    initalSearch = true;
                    searchWeb(v.getText().toString());
                    hideSuggestion = true;
                }
                return false;
            }
        });
        Log.e(TAG, String.valueOf(i++));
        try {
            term = getArguments().getString(Constants.BUNDLE_SEARCH_TERM);
            getArguments().clear();
//            Log.e(TAG, String.valueOf(i++));
        }catch (Exception e){
            term="";
//            Log.e(TAG, String.valueOf(i++));
        }
//        Log.e(TAG, String.valueOf(i++));
//        Log.e(TAG, String.valueOf(i++));
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
//        Log.e(TAG, String.valueOf(i++));
        if(searchListAdapter!=null) {
            Log.e(TAG, String.valueOf(i++));
//            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            recyclerView.setAdapter(searchListAdapter);
            searchWeb(term);
//            Log.e(TAG, String.valueOf(i++));
        }
        try{
//            Log.e(TAG, String.valueOf(i++));
            if(!term.isEmpty())
//                Log.e(TAG, String.valueOf(i++));
                searchWeb(term);
//            Log.e(TAG, String.valueOf(i++));
        }catch (NullPointerException e){
            e.printStackTrace();
//            Log.e(TAG, String.valueOf(i++));
        }

//        Log.e(TAG, String.valueOf(i++));
        searchQuery.setText(term);
//        Log.e(TAG, String.valueOf(i++));


        try {
            Log.e(TAG, String.valueOf(i++));
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=(MainActivity)activity;
    }

    public void searchWeb(String s) {
        SearchQuery query = new SearchQuery();
        query.setTerm(s);
        new Searcher().execute(query);
    }

    class Searcher extends AsyncTask<SearchQuery, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setIndeterminate(true);
            dialog.setMessage("Searching...");
            if(initalSearch)
                dialog.show();
        }

        @Override
        protected Void doInBackground(SearchQuery... params) {

            try {
                URL url = SearchQuery.getSearchQueryURL(params[0]);
                Log.e(TAG, "Search URL: " + url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(false);
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(10000);

                InputStream inputStream = connection.getInputStream();
                if (inputStream == null)
                    return null;

                File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmpMR");
                if (inputStream != null) {
//                    Log.e("FeedParser","In not null");
                    folder.mkdirs();
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(folder, "tmpMR02.tmp"));
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
//                        Log.e("FeedParser","Try 1");
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
//                            Log.e("FeedParser","In While");
                            fileOutputStream.write(buffer, 0, bufferLength);
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        File file = new File(folder, "tmpMR02.tmp");
                        File out = new File(folder, Constants.SEARCH_FILE_NAME);

                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        PrintWriter writer = new PrintWriter(new FileWriter(out));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line);
                        }
                        reader.close();
                        writer.close();
                    } catch (FileNotFoundException fe) {
                        fe.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initalSearch=false;
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            final SearchResult result = SearchResultParser.parse();
            if (result != null) {

                if(result.getCount()==0 && !result.getSpellingCorrection().isEmpty() && hideSuggestion){
                    initalSearch=true;
                    spellingView.setVisibility(View.VISIBLE);
                    spellingView.setText(Constants.getSuggestionText(result.getTerm(),result.getSpellingCorrection()));
                    hideSuggestion=false;
                    searchWeb(result.getSpellingCorrection());
                    return;
                }else{
                    if(hideSuggestion){
                        spellingView.setVisibility(View.GONE);
                    }
                }

                if(result.getCount()==0 && result.getSpellingCorrection().isEmpty()){
                    //TODO: Redirect to google
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("Search Failed")
                            .setMessage("Sorry..!! We couldn't find anything in our database regarding "+result.getTerm()+"" +
                                    "\nWould you like to search other sources on the internet?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //TODO: show error
                                }
                            })
                            .setPositiveButton("Yeah Sure", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    activity.displayFragment(BrowserFragment.getInstance("https://www.google.com/search?q=" + result.getTerm().replace(" ", "+"), false));
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return;
                }

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                if(result.getRetstart()>result.getRetmax()){
                    documents.addAll(result.getWebDocuments());
                }else{
                    documents=result.getWebDocuments();
                }
                SearchListAdapter adapter = new SearchListAdapter(getActivity(), documents);
                searchListAdapter=adapter;
                ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPosition(result.getRetstart());
                adapter.setItemClickListener(new SearchListAdapter.ItemClickListener() {
                    @Override
                    public void OnItemClick(ArrayList<WebDocument> documents, int position) {
                        openDetail(documents,position);
                    }
                });
                searchListAdapter.setItemClickListener(new SearchListAdapter.ItemClickListener() {
                    @Override
                    public void OnItemClick(ArrayList<WebDocument> documents, int position) {
                        openDetail(documents,position);
                    }
                });
                adapter.setAdapterPositionListener(new SearchListAdapter.AdapterPositionListener() {
                    @Override
                    public void onLastItemLoaded() {
                        if(result.getCount()>result.getRetstart()+result.getRetmax()){

                            Snackbar.make(recyclerView,"Fetching more result",Snackbar.LENGTH_SHORT).show();

                            SearchQuery query=new SearchQuery();
                            query.setFileName(result.getFile());
                            query.setServer(result.getServer());
                            query.setRetstart(result.getRetstart() + result.getRetmax() + 1);
                            new Searcher().execute(query);
                        }
                    }
                });
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setVisibility(View.GONE);
            }

        }

    }

    public void openDetail(ArrayList<WebDocument> documents,int position){
        activity.displayFragment(SearchDetailMainFragment.newInstance(documents, searchQuery.getText().toString(),position),true);
    }


}
