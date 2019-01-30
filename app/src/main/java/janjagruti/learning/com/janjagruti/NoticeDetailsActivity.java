package janjagruti.learning.com.janjagruti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Map;

import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.Notice;
import janjagruti.learning.com.janjagruti.util.GeneralUtil;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;

public class NoticeDetailsActivity extends AppCompatActivity {

    public static final String TAG = NoticeDetailsActivity.class.getSimpleName();

    public static final String EXTRA_NOTICE_ID = "noticeId";
    private int noticeId = 0;

    private Toolbar toolbar;
    private TextView txtview_title, txtview_postedBy, txtview_postedDateTime, txtview_details;
    private CardView card_loading, card_noticeDetails;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_details);

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(EXTRA_NOTICE_ID)){
            finish();
        } else {
            noticeId = intent.getIntExtra(EXTRA_NOTICE_ID, 0);
        }

        sessionManager = new SessionManager(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        card_loading = findViewById(R.id.card_loading);
        card_noticeDetails = findViewById(R.id.card_noticeDetails);

        txtview_title = findViewById(R.id.txtview_title);
        txtview_postedBy = findViewById(R.id.txtview_postedBy);
        txtview_postedDateTime = findViewById(R.id.txtview_postedDateTime);
        txtview_details = findViewById(R.id.txtview_details);

        JsonObjectRequest noticeDetailsRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.BASE_URL + ApiConfig.NOTICE_URI + "/" + noticeId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Notice notice = JSONConverterUtil.fromJSONObject(response, Notice.class);
                        txtview_title.setText(notice.getTitle());
                        txtview_details.setText(notice.getDetails());
                        txtview_postedBy.setText(notice.getPostedBy().getUsername());
                        txtview_postedDateTime.setText(GeneralUtil.getDateFormat().format(notice.getPostedDateTime()));

                        card_noticeDetails.setVisibility(View.VISIBLE);
                        card_loading.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND){
                            Toast.makeText(NoticeDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            //finish();
                        }
                        VolleyUtil.volleyErrorHandler(error, NoticeDetailsActivity.this, true);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyUtil.getHeaders(sessionManager);
            }
        };

        AppController.getInstance().addToRequestQueue(noticeDetailsRequest, TAG);
    }
}
