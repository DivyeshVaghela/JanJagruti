package janjagruti.learning.com.janjagruti.config;

public class ApiConfig {

    //TODO: Change the API Server IP and PORT
    public static final String SERVER_IP = "192.168.1.102";
    public static final int SERVER_PORT = 3000;
    public static final String PROTOCOL_HTTP = "http";

    //Server base location
    public static final String BASE_URL = PROTOCOL_HTTP + "://" + SERVER_IP + ":" + SERVER_PORT;

    //User (security) related end points
    public static String AUTHENTICATE_URI = "/user/auth";
    public static String REGISTER_URI = "/user/register";
    public static String FORGOT_PASSWORD_URI = "/user/forgot-password";
    public static String CHANGE_PASSWORD_URI = "/user/change-password";

    //Notice-Notification and other service end points
    public static String NOTICE_URI = "/notice";
    public static String SUBJECT_URI = "/subject";
    public static String MATERIAL_URI = "/material";

    public static String AUTH_TYPE = "jwt";
}
