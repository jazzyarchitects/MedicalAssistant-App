package architect.jazzy.medicinereminder.HelperClasses;

/**
 * Created by Jibin_ism on 13-Dec-15.
 */
public class BackendUrls {

    public static final String BASE_URL = "http://192.168.1.1";
    public static final int PORT = 3000;

//    public static final String API_ACCESS_POINT = BASE_URL + ":" + PORT + "/";
    public static final String API_ACCESS_POINT = "http://medicalassistant-remedyshare.herokuapp.com/";

    public static final String LOGIN = API_ACCESS_POINT + "user/login";
    public static final String SIGNUP = API_ACCESS_POINT + "user/signup";
    public static final String UPDATE_USER = API_ACCESS_POINT + "user";


    public static final String REMEDY_IMAGE = API_ACCESS_POINT+"remedy/image/";
}
