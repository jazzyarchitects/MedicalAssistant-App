package architect.jazzy.medicinereminder.Services;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

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

import architect.jazzy.medicinereminder.Parsers.FeedParser;
import architect.jazzy.medicinereminder.Models.FeedItem;

/**
 * Created by Jibin_ism on 09-Sep-15.
 */
public class NewsRetrieverService extends AsyncTask<Void, Void, Void> {

    private static final String TAG="NewsRetriever";
    @Override
    protected Void doInBackground(Void... params) {
        try {
            Log.e(TAG, "DoInBackground");
            URL url = new URL(FeedParser.feedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            if (inputStream == null) {
                ArrayList<FeedItem> items= FeedParser.parse();
                feedParserListener.onFeedsParsed(items);
                return null;
            }

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

            ArrayList<FeedItem> items= FeedParser.parse();

            feedParserListener.onFeedsParsed(items);
            return null;

        } catch (Exception e) {
            e.getStackTrace();
        }

        ArrayList<FeedItem> items= FeedParser.parse();
        feedParserListener.onFeedsParsed(items);
        return null;
    }

    FeedParserListener feedParserListener;
    public interface FeedParserListener{
        void onFeedsParsed(ArrayList<FeedItem> items);
    }
    public void setFeedParserListener(FeedParserListener feedParserListener){
        this.feedParserListener=feedParserListener;
    }


}
