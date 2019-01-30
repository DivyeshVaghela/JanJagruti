package janjagruti.learning.com.janjagruti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity {

    public static final String TAG = ErrorActivity.class.getSimpleName();

    public static final String EXTRA_ERROR_MESSAGE = "errorMessage";
    public static final String EXTRA_REFRESH = "refresh";
    public static final String EXTRA_REFERESH_ACTIVITY = "refreshActivity";

    private TextView txtview_errorMessage;
    private Button btn_refresh;
    private Class refreshActivity;

    private boolean doRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        txtview_errorMessage = findViewById(R.id.txtview_errorMessage);
        btn_refresh = findViewById(R.id.btn_refresh);

        Intent comingIntent = getIntent();
        if (comingIntent != null){

            Log.d(TAG, "bundle not null");

            //check for message
            if (comingIntent.hasExtra(EXTRA_ERROR_MESSAGE) && !TextUtils.isEmpty(comingIntent.getStringExtra(EXTRA_ERROR_MESSAGE))){

                Log.d(TAG, "Setting the error text");
                txtview_errorMessage.setText(comingIntent.getStringExtra(EXTRA_ERROR_MESSAGE));
            }

            //check for refresh
            doRefresh = comingIntent.getBooleanExtra(EXTRA_REFRESH, false);
            refreshActivity = comingIntent.hasExtra(EXTRA_REFERESH_ACTIVITY) ? (Class) comingIntent.getSerializableExtra(EXTRA_REFERESH_ACTIVITY) : SplashActivity.class;
            if (doRefresh){
                btn_refresh.setVisibility(View.VISIBLE);
                btn_refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ErrorActivity.this, refreshActivity);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        } else{
            Log.d(TAG, "bundle null");
        }

    }
}
