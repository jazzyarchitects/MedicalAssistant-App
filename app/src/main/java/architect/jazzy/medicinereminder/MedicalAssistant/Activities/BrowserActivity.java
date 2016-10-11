package architect.jazzy.medicinereminder.MedicalAssistant.Activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import architect.jazzy.medicinereminder.R;

public class BrowserActivity extends AppCompatActivity {


    WebView webView;
    ProgressBar progressBar;
    Uri newsUrl;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
    }
}
