package janjagruti.learning.com.janjagruti.config;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.TimeUnit;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    public static final String VERSION = "1.0.0";

    private static AppController _instance;

    private RequestQueue _requestQueue;
    private ImageLoader _imageLoader;

    public static synchronized AppController getInstance(){
        return _instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        _instance = this;
    }

    public RequestQueue getRequestQueue(){
        if (_requestQueue == null){
            _requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return _requestQueue;
    }

    public ImageLoader getImageLoader(){
        getRequestQueue();
        if (_imageLoader == null){
            _imageLoader = new ImageLoader(_requestQueue, new LruBitmapCache());
        }
        return _imageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        addToRequestQueue(req);
    }

    public <T> void addToRequestQueue(Request<T> req){
        if (req.getTag() == null)
            req.setTag(TAG);
        if (req.getRetryPolicy() == null){
            req.setRetryPolicy(new DefaultRetryPolicy(
                    (int)TimeUnit.SECONDS.toMillis(4),
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
        }
        getRequestQueue().add(req);
    }

    public void cancelPandingRequests(Object tag){
        if (_requestQueue != null){
            _requestQueue.cancelAll(tag);
        }
    }
}
