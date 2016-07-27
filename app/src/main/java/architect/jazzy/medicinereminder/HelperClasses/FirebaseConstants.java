package architect.jazzy.medicinereminder.HelperClasses;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Jibin_ism on 22-May-16.
 */
public class FirebaseConstants {
    public static class Analytics{

        public static FirebaseAnalytics logCurrentScreen(Context context, String screenName){
            FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_SCREEN_NAME, screenName);
            analytics.logEvent(EVENT_SCREEN_NAME, bundle);
            return analytics;
        }

        public static final String BUNDLE_SCREEN_NAME = "ScreenName";
        public static final String BUNDLE_LOGIN_ALERT_SHOWN = "LoginAlert-Shown";
        public static final String BUNDLE_LOGIN_ALERT_ACTION = "LoginAlert-Action";
        public static final String BUNDLE_LOGIN_ALERT_ACTION_METHOD = "LoginAlert-DismissMethod";



        public static final String EVENT_SCREEN_NAME = "Screen";
        public static final String EVENT_TIME_SETTINGS = "TimeSettings";
        public static final String EVENT_LOGIN_ALERT = "LoginAlert";
    }

    public static class RemoteConfig{
        public static final String PREFERENCE_NAME = "FirebaseRemoteConfigPrefs";
        public static final String USER_LOGIN_ENABLED="userLoginEnabled";
        public static final String REMEDY_VOTE_ENABLED="isRemedyVoteEnabled";
        public static final String REMEDY_COMMENT_ENABLED="isRemedyCommentEnabled";
        public static final String REMEDY_ENABLED="isRemedyEnabled";
    }
}
