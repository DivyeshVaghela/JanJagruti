package janjagruti.learning.com.janjagruti;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.User;
import janjagruti.learning.com.janjagruti.entity.UserPackage;
import janjagruti.learning.com.janjagruti.util.GeneralUtil;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;

public class UserProfileActivity extends AppCompatActivity {

    public static final String TAG = UserProfileActivity.class.getSimpleName();

    private Toolbar toolbar;
    private ActionBar supportActionBar;

    private CardView card_package, card_packageLoading, card_goPremium;

    private TextView txtview_username, txtview_email, txtview_mobileno, txtview_createdDateTime;
    private TextView txtview_packageName, txtview_subtitle, txtview_packageEnrollment,
            txtview_enrollmentDate, txtview_enrollmentTime, txtview_packageExpiry, txtview_expiryDate, txtview_expiryTime;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        sessionManager = new SessionManager(getApplicationContext());
        User userProfile = sessionManager.getUserProfile();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setTitle(userProfile.getUsername());

        txtview_username = findViewById(R.id.txtview_username);
        txtview_username.setText(userProfile.getUsername());

        txtview_email = findViewById(R.id.txtview_email);
        txtview_email.setText(userProfile.getEmail());

        txtview_mobileno = findViewById(R.id.txtview_mobileno);
        txtview_mobileno.setText(userProfile.getMobileNo());

        txtview_createdDateTime = findViewById(R.id.txtview_createdDateTime);
        txtview_createdDateTime.setText(GeneralUtil.getDateFormat().format(userProfile.getCreatedAt()));

        card_package = findViewById(R.id.card_package);
        card_packageLoading = findViewById(R.id.card_packageLoading);
        card_goPremium = findViewById(R.id.card_goPremium);

        loadActivePackage();
    }

    private void loadActivePackage(){

        JsonObjectRequest activePackageRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.BASE_URL + ApiConfig.USER_URI + "/" + sessionManager.getUserProfile().getId() + "/package",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        UserPackage userPackage = JSONConverterUtil.fromJSONObject(response, UserPackage.class);

                        txtview_packageName = findViewById(R.id.txtview_packageName);
                        txtview_packageName.setText(userPackage.getaPackage().getTitle());

                        txtview_subtitle = findViewById(R.id.txtview_subtitle);
                        txtview_subtitle.setText(userPackage.getaPackage().getSubtitle());

                        txtview_enrollmentDate = findViewById(R.id.txtview_enrollmentDate);
                        txtview_enrollmentDate.setText(GeneralUtil.getOnlyDateFormat().format(userPackage.getValidityStart()));
                        txtview_enrollmentTime = findViewById(R.id.txtview_enrollmentTime);
                        txtview_enrollmentTime.setText(GeneralUtil.getOnlyTimeFormat().format(userPackage.getValidityStart()));

                        txtview_expiryDate = findViewById(R.id.txtview_expiryDate);
                        txtview_expiryDate.setText(GeneralUtil.getOnlyDateFormat().format(userPackage.getValidityEnd()));
                        txtview_expiryTime = findViewById(R.id.txtview_expiryTime);
                        txtview_expiryTime.setText(GeneralUtil.getOnlyTimeFormat().format(userPackage.getValidityEnd()));

                        card_package.setVisibility(View.VISIBLE);
                        card_packageLoading.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                            card_goPremium.setVisibility(View.VISIBLE);

                            Button btn_goPremium = findViewById(R.id.btn_goPremium);
                            btn_goPremium.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(UserProfileActivity.this, PackageListActivity.class);
                                    startActivity(intent);
                                }
                            });

                        }
                        VolleyUtil.volleyErrorHandler(error, UserProfileActivity.this, true, HttpURLConnection.HTTP_NOT_FOUND, null);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyUtil.getHeaders(sessionManager);
            }
        };
        AppController.getInstance().addToRequestQueue(activePackageRequest , TAG);
    }
}
