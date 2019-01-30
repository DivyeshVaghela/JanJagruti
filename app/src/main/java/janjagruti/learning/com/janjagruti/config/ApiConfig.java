package janjagruti.learning.com.janjagruti.config;

public class ApiConfig {

//    public static final String SERVER_IP = "192.168.43.33";
    public static final String SERVER_IP = "192.168.1.102";
    public static final int SERVER_PORT = 3000;
    public static final String PROTOCOL_HTTP = "http";

    //Server base location
    public static final String BASE_URL = PROTOCOL_HTTP + "://" + SERVER_IP + ":" + SERVER_PORT;

    //Login URL Part
    public static String AUTHENTICATE_URI = "/user/auth";
    //Register URL Part
    public static String REGISTER_URI = "/user/register";

    //Notice-Notification
    public static String NOTICE_URI = "/notice";

    public static String AUTH_TYPE = "jwt";
}
