package janjagruti.learning.com.janjagruti.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;

import janjagruti.learning.com.janjagruti.config.ApiConfig;

public class GeneralUtil {

    public static final String TAG = GeneralUtil.class.getSimpleName();

    public static SimpleDateFormat getDateFormat(){
        return new SimpleDateFormat("dd MMM, yyyy (hh:mm a)");
    }

    //run on background thread
    public static boolean isServerReachable(){
        try {
            Socket socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress(ApiConfig.SERVER_IP, ApiConfig.SERVER_PORT);
            socket.connect(socketAddress, 4000);
            socket.close();

            return true;
        } catch (IOException e) {

            Log.d(TAG, "Server connection IOException");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isNetworkOn(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return  (networkInfo != null && networkInfo.isConnected());
    }
}
