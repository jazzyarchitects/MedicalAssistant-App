package architect.jazzy.medicinereminder.HelperClasses;

/**
 * Created by Jibin_ism on 13-Dec-15.
 */
public class BackendUrls {

//    public static final String BASE_URL = "http://192.168.1.1";
//    public static final int PORT = 3000;

    //    public static final String API_ACCESS_POINT = BASE_URL + ":" + PORT + "/";
    public static final String BASE_URL="http://medicalassistant-jazzyarchitect.rhcloud.com/";
    public static final String API_ACCESS_POINT = BASE_URL+"api/";

    public static final String LOGIN = API_ACCESS_POINT + "user/login";
    public static final String SIGNUP = API_ACCESS_POINT + "user/signup";
    public static final String UPDATE_USER = API_ACCESS_POINT + "user";


    private static final String REMEDY = "remedy/";
    private static final String REMEDY_FEED = API_ACCESS_POINT + REMEDY + "/all/";

    public static String getRemedyFeed(int page) {
        return REMEDY_FEED + page;
    }

    public static String getRemedyFeed() {
        return REMEDY_FEED;
    }





    public static String getUpvoteUrl(String id) {
        return API_ACCESS_POINT + REMEDY + id + "/upvote";
    }

    public static String getDownvoteUrl(String id) {
        return API_ACCESS_POINT + REMEDY + id + "/downvote";
    }

    public static String getBookmarkUrl(String id){
        return API_ACCESS_POINT+REMEDY+id+"/bookmark";
    }

    public static String getRemedy(String id) {
        return REMEDY + id;
    }

    public static String getRemedyImage(String filename){
        return BASE_URL+"images/remedy/"+filename;
    }

    public static String getRemedyDetail(String remedyId){
        return API_ACCESS_POINT+"remedy/"+remedyId;
    }
}
