package architect.jazzy.medicinereminder.RemedySharing.HelperClasses;

/**
 * Created by Jibin_ism on 13-Dec-15.
 */
public class BackendUrls {

//    public static final String BASE_URL = "http://192.168.1.1";
//    public static final int PORT = 3000;

  //    public static final String API_ACCESS_POINT = BASE_URL + ":" + PORT + "/";
  public static final String BASE_URL = "http://medicalassistant-jazzyarchitect.rhcloud.com/";
  public static final String API_ACCESS_POINT = BASE_URL + "api";

  // ------------- User Routes ---------------- //

  public static final String USER_ACCESS_POINT = API_ACCESS_POINT + "/user";

  public static final String LOGIN = USER_ACCESS_POINT + "/login";
  public static final String SIGNUP = USER_ACCESS_POINT + "/signup";
  public static final String LOGIN_GOOGLE = LOGIN + "/google";
  public static final String LOGIN_FACEBOOK = LOGIN + "/fb";
  public static final String GET_SELF_DATA = USER_ACCESS_POINT + "/";
  public static final String UPDATE_USER = USER_ACCESS_POINT + "/";
  public static final String DELETE_ACCOUNT = USER_ACCESS_POINT + "/";
  public static final String LOGOUT = USER_ACCESS_POINT + "/logout";
  public static final String COMMENT_ACCESS_POINT = API_ACCESS_POINT + "/comments";
  private static final String REMEDY_ACCESS_POINT = API_ACCESS_POINT + "/remedy";
  public static final String NEW_REMEDY = REMEDY_ACCESS_POINT + "/";
  private static final String REMEDY_FEED = REMEDY_ACCESS_POINT + "/all/";
  public static String UPLOAD_APP_DATA = USER_ACCESS_POINT + "/app/uploadApp";
  public static String DOWNLOAD_APP_DATA = USER_ACCESS_POINT + "/app/downloadApp";

  // -----------Remedy Routes------------ //

  public static String getSelfRemedies(int page) {
    return USER_ACCESS_POINT + "/remedy/" + page;
  }

  public static String getUserDetails(String userId) {
    return USER_ACCESS_POINT + "/" + userId;
  }

  public static String getUserRemedies(String userId, int page) {
    return USER_ACCESS_POINT + "/" + userId + "/remedy/" + page;
  }

  public static String getUserRemedies(String userId) {
    return getUserRemedies(userId, 1);
  }

  public static String getRemedyFeed(int page) {
    return REMEDY_FEED + page;
  }

  public static String getRemedyFeed() {
    return getRemedyFeed(1);
  }

  public static String getRemedyDetails(String remedyId) {
    return REMEDY_ACCESS_POINT + "/" + remedyId;
  }

  public static String getRemedyComments(String remedyId) {
    return REMEDY_ACCESS_POINT + "/" + remedyId + "/comments";
  }

  public static String updateRemedy(String remedyId) {
    return REMEDY_ACCESS_POINT + "/" + remedyId;
  }

  public static String getUpvoteUrl(String id) {
    return REMEDY_ACCESS_POINT + "/" + id + "/upvote";
  }

  public static String getDownvoteUrl(String id) {
    return REMEDY_ACCESS_POINT + "/" + id + "/downvote";
  }

  public static String getBookmarkUrl(String id) {
    return REMEDY_ACCESS_POINT + "/" + id + "/bookmark";
  }

  public static String remedyComments(String remedyId) {
    return REMEDY_ACCESS_POINT + "/" + remedyId + "/comment";
  }

  public static String registerRemedyView(String remedyId) {
    return REMEDY_ACCESS_POINT + "/view/" + remedyId;
  }

  public static String getRemedyImage(String filename) {
    return BASE_URL + "images/remedy/" + filename;
  }

  // -----------------Comment Routes------------- //

  public static String getRemedyDetail(String remedyId) {
    return REMEDY_ACCESS_POINT + "/" + remedyId;
  }

  public static String deleteCommene(String commentId) {
    return COMMENT_ACCESS_POINT + "/" + commentId;   //DELETE REQUEST
  }
}
