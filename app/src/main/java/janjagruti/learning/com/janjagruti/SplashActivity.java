package janjagruti.learning.com.janjagruti;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Space;

import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.util.GeneralUtil;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(getApplicationContext());

        new CheckServerConnection().execute();
    }

    public class CheckServerConnection extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {

            if (!GeneralUtil.isNetworkOn(SplashActivity.this)){
                Intent intent = new Intent(SplashActivity.this, ErrorActivity.class);
                intent.putExtra(ErrorActivity.EXTRA_ERROR_MESSAGE, getString(R.string.no_network));
                intent.putExtra(ErrorActivity.EXTRA_REFRESH, true);
                intent.putExtra(ErrorActivity.EXTRA_REFERESH_ACTIVITY, SplashActivity.class);
                startActivity(intent);
				finish();
                return false;
            }
            else if (!GeneralUtil.isServerReachable()){
                Intent intent = new Intent(SplashActivity.this, ErrorActivity.class);
                intent.putExtra(ErrorActivity.EXTRA_ERROR_MESSAGE, getString(R.string.server_not_reachable));
                intent.putExtra(ErrorActivity.EXTRA_REFRESH, true);
                intent.putExtra(ErrorActivity.EXTRA_REFERESH_ACTIVITY, SplashActivity.class);
                startActivity(intent);
				finish();
                return false;
            }
            else if (sessionManager.isTokenExpired()){
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result){
                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                redirect();
                            }
                        }, 2000);
            }
        }
    }

    private void redirect() {

        Class redirectToClazz = null;

        if (sessionManager.isLoggedIn()){
            redirectToClazz = DashboardActivity.class;
        } else {
            redirectToClazz = LoginActivity.class;
        }

        Intent intent = new Intent(this, redirectToClazz);
        startActivity(intent);
        finish();
    }
}
