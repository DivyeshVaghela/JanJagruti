package janjagruti.learning.com.janjagruti.config;

public class ApiConfig {

    //TODO: Change the API Server IP and PORT
    public static final String SERVER_IP = "192.168.1.102";
    public static final int SERVER_PORT = 3000;
    public static final String PROTOCOL_HTTP = "http";

    //Server base location
    public static final String BASE_URL = PROTOCOL_HTTP + "://" + SERVER_IP + ":" + SERVER_PORT;

    public static String USER_URI = "/user";

    //User (security) related end points
    public static String AUTHENTICATE_URI = USER_URI + "/auth";
    public static String REFRESH_TOKEN_URI = USER_URI + "/auth/refresh";
    public static String REGISTER_URI = USER_URI + "/register";
    public static String FORGOT_PASSWORD_URI = USER_URI + "/forgot-password";
    public static String CHANGE_PASSWORD_URI = USER_URI + "/change-password";

    public static String PACKAGE_PURCHASE = USER_URI + "/package-purchase";
    public static String PAYMENT_SUCCESS = USER_URI + "/package-purchase/success";
    public static String PAYMENT_FAILURE = USER_URI + "/package-purchase/failure";

    //Notice-Notification and other service end points
    public static String NOTICE_URI = "/notice";
    public static String SUBJECT_URI = "/subject";
    public static String MATERIAL_URI = "/material";
    public static String PACKAGE_URI = "/package";

    public static String AUTH_TYPE = "jwt";
}
