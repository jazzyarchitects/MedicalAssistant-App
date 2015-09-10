package architect.jazzy.medicinereminder.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.HelperClasses.FeedParser;
import architect.jazzy.medicinereminder.HelperClasses.TimingClass;
import architect.jazzy.medicinereminder.Models.FeedItem;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.Services.NewsRetrieverService;

public class DashboardFragment extends Fragment {

    private static final String TAG = "Dashboard";
    View v;
    //    CircleView todayView, medicineCountView, appointmentCountView;
    TextView medicineCountView;
    FloatingActionButton floatingActionButton;
    RelativeLayout relativeLayout;
    boolean isMenuOpen = false;
    LinearLayout adViewContainer1,adViewContainer2;
    int bbh, bbm, abh, abm, alh, alm, blh, blm, adh, adm, bdh, bdm;
    boolean is24hr;
    TextView newsFeedView;
    int[] medicineViewIds = {R.id.medicine1, R.id.medicine2, R.id.medicine3, R.id.medicine4, R.id.medicine5, R.id.medicine6};
    String[] medicineViewTimes = {"Before Breakfast", "After Breakfast", "Before Lunch", "After Lunch", "Before Dinner", "After Dinner"};

    public static final int ADD_DOCTOR_FAB_ID = 305;
    public static final int ADD_MEDICINE_FAB_ID = 218;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        getDefaultTime();
        isMenuOpen = false;
//        todayView=(CircleView)v.findViewById(R.id.mainIndicator);
//        medicineCountView=(CircleView)v.findViewById(R.id.medicineCountView);
//        appointmentCountView=(CircleView)v.findViewById(R.id.appointmentCount);
        floatingActionButton = (FloatingActionButton) v.findViewById(R.id.floatingActionButton);
        relativeLayout = (RelativeLayout) v.findViewById(R.id.r1);
        medicineCountView = (TextView) v.findViewById(R.id.medicineCount);
        adViewContainer1 = (LinearLayout) v.findViewById(R.id.adViewContainer1);
        adViewContainer2 = (LinearLayout) v.findViewById(R.id.adViewContainer2);
        newsFeedView=(TextView)v.findViewById(R.id.newsfeed);
        newsFeedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentInteractionListener.showNews();
            }
        });

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Dashboard");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        DataHandler handler = new DataHandler(getActivity());
        final ArrayList<Medicine> medicines = handler.getTodaysMedicine();
        medicineCountView.setText(String.valueOf(medicines.size()));
        int i = 0;
        while (i < 6) {
            if (i >= medicines.size()) {
                break;
            }
            TextView view = (TextView) v.findViewById(medicineViewIds[i]);
            view.setText(Html.fromHtml(getMedicineDetailDisplayString(medicines.get(i), i)));
            i++;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        });
        thread.start();

        try{
            ArrayList<FeedItem> items= FeedParser.parse();
            String s="";
            for (FeedItem item : items) {
                s += "   " + item.getTitle() + "  |";
            }
            newsFeedView.setSelected(true);
            newsFeedView.setText(s);
        }catch (Exception e){
            e.printStackTrace();
        }

        NewsRetrieverService newsRetrieverService=new NewsRetrieverService();
        newsRetrieverService.setFeedParserListener(new NewsRetrieverService.FeedParserListener() {
            @Override
            public void onFeedsParsed(final ArrayList<FeedItem> items) {
                newsFeedView.post(new Runnable() {
                    @Override
                    public void run() {
                        String s="";
                        if(items!=null) {
                            for (FeedItem item : items) {
                                s += "   " + item.getTitle() + "  |";
                            }
                            newsFeedView.setSelected(true);
                            newsFeedView.setText(s);
                        }
                    }
                });
            }
        });
        newsRetrieverService.execute();


        return v;
    }


    void initialize() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "fab add onclick");

                addNewItems();
            }
        });

//        medicineCountView.setText(String.valueOf(medicines.size()));


        //TODO: Change this test ad Unit id
        AdLoader adLoader = new AdLoader.Builder(getActivity(), "ca-app-pub-3940256099942544/2247696110")
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                        Log.e(TAG, "Install app loaded");
                        displayAppInstallAd(nativeAppInstallAd, adViewContainer1);
                    }
                })
                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd nativeContentAd) {
                        Log.e(TAG, "Content adloaded");
                        displayContentAd(nativeContentAd,adViewContainer1 );
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());


        //TODO: Change this test ad Unit id
        AdLoader adLoader2 = new AdLoader.Builder(getActivity(), "ca-app-pub-3940256099942544/2247696110")
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                        Log.e(TAG, "Install app loaded 2");
                        displayAppInstallAd(nativeAppInstallAd, adViewContainer2);
                    }
                })
                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd nativeContentAd) {
                        Log.e(TAG, "Content adloaded 2");
                        displayContentAd(nativeContentAd,adViewContainer2);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();
        adLoader2.loadAd(new AdRequest.Builder().build());


    }


    private void displayAppInstallAd(final NativeAppInstallAd appInstallAd, LinearLayout containerView) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.native_install_adview, null);
        final NativeAppInstallAdView adView = (NativeAppInstallAdView) view.findViewById(R.id.nativeAdView);

        final TextView contentHeadline = (TextView) adView.findViewById(R.id.contentHeadline);
        final ImageView imageView = (ImageView) adView.findViewById(R.id.imageView);
        final TextView advertiserView = (TextView) adView.findViewById(R.id.advertiser);
        final TextView adBody = (TextView) adView.findViewById(R.id.adBody);
        final TextView adPrice = (TextView) adView.findViewById(R.id.adPrice);
        final RatingBar ratingBar=(RatingBar)adView.findViewById(R.id.adRating);


        adView.setStarRatingView(ratingBar);
        adView.setPriceView(adPrice);
        adView.setImageView(imageView);
        adView.setHeadlineView(contentHeadline);
        adView.setBodyView(adBody);
        adView.setStoreView(advertiserView);

        adView.setNativeAd(appInstallAd);


        v.post(new Runnable() {
            @Override
            public void run() {
                contentHeadline.setText(appInstallAd.getHeadline());
                try {
                    imageView.setImageDrawable(appInstallAd.getIcon().getDrawable());
                }catch (NullPointerException e){
                    try{
                        imageView.setImageDrawable(appInstallAd.getImages().get(0).getDrawable());
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                advertiserView.setText(appInstallAd.getStore());
                adBody.setText(appInstallAd.getBody());
                adPrice.setText(appInstallAd.getPrice());
                ratingBar.setNumStars(5);
                ratingBar.setMax(5);
                ratingBar.setRating(Float.parseFloat(appInstallAd.getStarRating().toString()));
            }
        });
        containerView.addView(view);
    }

    private void displayContentAd(final NativeContentAd nativeContentAd, LinearLayout containerView) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.native_content_adview, null);
        final NativeContentAdView adView = (NativeContentAdView) view.findViewById(R.id.nativeContentAdView);

        final TextView contentHeadline = (TextView) adView.findViewById(R.id.contentHeadline);
        final ImageView imageView = (ImageView) adView.findViewById(R.id.imageView);
        final TextView advertiserView = (TextView) adView.findViewById(R.id.advertiser);
        final TextView adBody = (TextView) adView.findViewById(R.id.adBody);

        adView.setImageView(imageView);
        adView.setHeadlineView(contentHeadline);
        adView.setBodyView(adBody);
        adView.setAdvertiserView(advertiserView);

        adView.setNativeAd(nativeContentAd);
        v.post(new Runnable() {
            @Override
            public void run() {
                contentHeadline.setText(nativeContentAd.getHeadline());
                try {
                    imageView.setImageDrawable(nativeContentAd.getLogo().getDrawable());
                }catch (NullPointerException e){
                    try{
                        imageView.setImageDrawable(nativeContentAd.getImages().get(0).getDrawable());
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                advertiserView.setText(nativeContentAd.getAdvertiser());
                adBody.setText(nativeContentAd.getBody());
            }
        });
        containerView.addView(view);

    }

    String getMedicineDetailDisplayString(Medicine medicine, int viewIndex) {
        String a = getBreakfastTime(medicine.getBreakfast());
        String b = getLunchTime(medicine.getLunch());
        String c = getDinnerTime(medicine.getDinner());

        String s = "<b>" + medicine.getMedName() + "</b>" + "\n";
        if (a != null) {
            s += a + "\n";
        }
        if (b != null) {
            s += b + "\n";
        }
        if (c != null) {
            s += c + "\n";
        }
        return s;
    }

    void addNewItems() {
        if (isMenuOpen) {
            return;
        }
        isMenuOpen = true;
        Log.e(TAG, "fab doc creating");
        FloatingActionButton addDocFab = new FloatingActionButton(getActivity());
        addDocFab.setImageResource(R.drawable.ic_action_user);
        addDocFab.setClickable(true);
        addDocFab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.actionBackground)}));
        addDocFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "fab add doctor onclick");
                onFragmentInteractionListener.addDoctor();
            }
        });
        RelativeLayout.LayoutParams docFabLayoutParams = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.floatingActionButtonSize), (int) getResources().getDimension(R.dimen.floatingActionButtonSize));

        int bottom = 10 + (int) getResources().getDimension(R.dimen.floatingActionButtonSize) + 10 + 10;
        docFabLayoutParams.setMargins(10, 10, 10, bottom);
        docFabLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        docFabLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addDocFab.setLayoutParams(docFabLayoutParams);


        FloatingActionButton addMedFab = new FloatingActionButton(getActivity());
        Drawable drawable = getResources().getDrawable(R.drawable.ic_action_pill);
        try {
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        relativeLayout.addView(addDocFab);
        addMedFab.setImageDrawable(drawable);
        addMedFab.setClickable(true);
        addMedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentInteractionListener.addMedicine();
            }
        });
        addMedFab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.color_death)}));
        RelativeLayout.LayoutParams addMedLayoutParams = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.floatingActionButtonSize), (int) getResources().getDimension(R.dimen.floatingActionButtonSize));
        addMedLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addMedLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottom *= 2;
        bottom -= 10;
        addMedLayoutParams.setMargins(10, 10, 10, bottom);
        addMedFab.setLayoutParams(addMedLayoutParams);


        relativeLayout.addView(addMedFab);

        addDocFab.startAnimation(getShowAnimation(true));
        addMedFab.startAnimation(getShowAnimation(false));

    }

    private Animation getShowAnimation(boolean isFirst) {
        Log.e(TAG, "fab getting animation");
        int fromX = 20 + (int) getResources().getDimension(R.dimen.floatingActionButtonSize);
        TranslateAnimation animation = new TranslateAnimation(fromX, 0, 0, 0);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setDuration(getResources().getInteger(R.integer.fabAnimDuration));
        if (!isFirst) {
            animation.setStartOffset(getResources().getInteger(R.integer.fabAnimDuration) / 2);
        }
        return animation;
    }

    OnFragmentInteractionListener onFragmentInteractionListener;

    public interface OnFragmentInteractionListener {
        void addMedicine();
        void showNews();
        void addDoctor();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onFragmentInteractionListener = (OnFragmentInteractionListener) activity;
    }

    public void getDefaultTime() {
        SharedPreferences inputPref;
        inputPref = getActivity().getSharedPreferences(Constants.INPUT_SHARED_PREFS, Context.MODE_PRIVATE);

        bbh = inputPref.getInt(Constants.BEFORE_BREAKFAST_HOUR, 8);
        bbm = inputPref.getInt(Constants.BEFORE_BREAKFAST_MINUTE, 0);
        abh = inputPref.getInt(Constants.AFTER_BREAKFAST_HOUR, 10);
        abm = inputPref.getInt(Constants.AFTER_BREAKFAST_MINUTE, 0);

        blh = inputPref.getInt(Constants.BEFORE_LUNCH_HOUR, 12);
        blm = inputPref.getInt(Constants.BEFORE_LUNCH_MINUTE, 0);
        alh = inputPref.getInt(Constants.AFTER_LUNCH_HOUR, 14);
        alm = inputPref.getInt(Constants.AFTER_LUNCH_MINUTE, 0);

        bdh = inputPref.getInt(Constants.BEFORE_DINNER_HOUR, 20);
        bdm = inputPref.getInt(Constants.BEFORE_DINNER_MINUTE, 0);
        adh = inputPref.getInt(Constants.AFTER_DINNER_HOUR, 22);
        adm = inputPref.getInt(Constants.AFTER_DINNER_MINUTE, 0);

        is24hr = inputPref.getBoolean(Constants.IS_24_HOURS_FORMAT, false);
    }

    private String getBreakfastTime(String beforeOrAfter) {
        if (beforeOrAfter.equalsIgnoreCase("before")) {
            return TimingClass.getTime(bbh, bbm, is24hr);
        } else if (beforeOrAfter.equalsIgnoreCase("after")) {
            return TimingClass.getTime(abh, abm, is24hr);
        }
        return null;
    }

    private String getLunchTime(String beforeOrAfter) {
        if (beforeOrAfter.equalsIgnoreCase("before")) {
            return TimingClass.getTime(blh, blm, is24hr);
        } else if (beforeOrAfter.equalsIgnoreCase("after")) {
            return TimingClass.getTime(alh, alm, is24hr);
        }
        return null;
    }

    private String getDinnerTime(String beforeOrAfter) {
        if (beforeOrAfter.equalsIgnoreCase("before")) {
            return TimingClass.getTime(bdh, bdm, is24hr);
        } else if (beforeOrAfter.equalsIgnoreCase("after")) {
            return TimingClass.getTime(adh, adm, is24hr);
        }
        return null;
    }

    private final int BREAKFAST = 0;
    private final int LUNCH = 1;
    private final int DINNER = 2;

    Medicine findTimelyMedicine(ArrayList<Medicine> medicines, int bld, String beforeOrAfter) {
        for (Medicine medicine : medicines) {
            switch (bld) {
                case BREAKFAST:
                    if (medicine.getBreakfast().equalsIgnoreCase(beforeOrAfter)) {
                        return medicine;
                    }
                    break;
                case LUNCH:
                    if (medicine.getLunch().equalsIgnoreCase(beforeOrAfter)) {
                        return medicine;
                    }
                    break;
                case DINNER:
                    if (medicine.getDinner().equalsIgnoreCase(beforeOrAfter)) {
                        return medicine;
                    }
                    break;
            }
        }
        return null;
    }

}
