package janjagruti.learning.com.janjagruti;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.Material;
import janjagruti.learning.com.janjagruti.util.GeneralUtil;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;

public class MaterialDetailsActivity extends AppCompatActivity {

    public static final String TAG = MaterialDetailsActivity.class.getSimpleName();

    public static final String EXTRA_MATERIAL_ID = "materialId";
    public static final String EXTRA_MATERIAL_TITLE = "materialTitle";

    private Toolbar toolbar;
    private CardView card_noticeDetails, card_loading;
    private NetworkImageView img_materialLogo;
    private TextView txtview_title, txtview_subtitle, txtview_type, txtview_subject, txtview_postedBy, txtview_postedDateTime, txtview_details;
    private Button btn_launch, btn_goPremium;

    private int materialId = 0;
    private String materialTitle;

    //query params
    private final String PARAM_INCLUDE_DETAILS = "details";
    private final String PARAM_INCLUDE_SUBJECT = "subject";
    private final String PARAM_INCLUDE_TYPE = "type";
    private final String PARAM_INCLUDE_CREATOR = "creator";

    private boolean includeDetails = true;
    private boolean includeSubject = true;
    private boolean includeType = true;
    private boolean includeCreator = true;

    private SessionManager sessionManager;
    private ActionBar supportActionBar;

    private Intent materialLaunchIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_details);

        sessionManager = new SessionManager(this);

        Intent comingIntent = getIntent();
        if (comingIntent != null){
            materialId = comingIntent.getIntExtra(EXTRA_MATERIAL_ID, 0);
            materialTitle = comingIntent.getStringExtra(EXTRA_MATERIAL_TITLE);
        }

        if (materialId == 0) {
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setTitle(TextUtils.isEmpty(materialTitle) ? "Material" : materialTitle);

        card_loading = findViewById(R.id.card_loading);
        card_noticeDetails = findViewById(R.id.card_noticeDetails);

        img_materialLogo = findViewById(R.id.img_materialLogo);
        txtview_title = findViewById(R.id.txtview_title);
        txtview_subtitle = findViewById(R.id.txtview_subtitle);
        txtview_type = findViewById(R.id.txtview_type);
        txtview_subject = findViewById(R.id.txtview_subject);
        txtview_postedBy = findViewById(R.id.txtview_postedBy);
        txtview_postedDateTime = findViewById(R.id.txtview_postedDateTime);
        txtview_details = findViewById(R.id.txtview_details);

        btn_launch = findViewById(R.id.btn_launch);
        btn_goPremium = findViewById(R.id.btn_goPremium);

        loadMaterialDetails();
    }

    private void loadMaterialDetails(){

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(PARAM_INCLUDE_DETAILS, String.valueOf(includeDetails));
        queryParams.put(PARAM_INCLUDE_SUBJECT, String.valueOf(includeSubject));
        queryParams.put(PARAM_INCLUDE_TYPE, String.valueOf(includeType));
        queryParams.put(PARAM_INCLUDE_CREATOR, String.valueOf(includeCreator));

        JsonObjectRequest materialDetailsRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.BASE_URL + ApiConfig.MATERIAL_URI + "/" + materialId + VolleyUtil.prepareQueryString(queryParams),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Material material = JSONConverterUtil.fromJSONObject(response, Material.class);

                        img_materialLogo.setImageUrl(material.getType().getLogoUrl(), AppController.getInstance().getImageLoader());
                        img_materialLogo.setContentDescription(material.getTitle());

                        txtview_title.setText(material.getTitle());
                        txtview_subtitle.setText(material.getSubtitle());
                        txtview_details.setText(material.getDetails());
                        txtview_type.setText(material.getType().getName());
                        txtview_subject.setText(material.getSubject().getTitle());
                        txtview_postedBy.setText(material.getCreatedBy().getUsername() + " (" + material.getCreatedBy().getEmail() + ")");
                        txtview_postedDateTime.setText(GeneralUtil.getDateFormat().format(material.getCreatedAt()));

                        card_loading.setVisibility(View.GONE);
                        card_noticeDetails.setVisibility(View.VISIBLE);

                        if (!material.isPremium() || (material.isPremium() && sessionManager.isPremiumUser())){
                            materialLaunchIntent = new Intent(MaterialDetailsActivity.this, MaterialActivity.class);
                            materialLaunchIntent.putExtra(MaterialActivity.EXTRA_MATERIAL_ID, material.getId());
                            materialLaunchIntent.putExtra(MaterialActivity.EXTRA_MATERIAL_TITLE, material.getTitle());
                            materialLaunchIntent.putExtra(MaterialActivity.EXTRA_MATERIAL_PATH, material.getPathUrl());
                            materialLaunchIntent.putExtra(MaterialActivity.EXTRA_MATERIAL_TYPE, material.getType().getValue());

                            btn_launch.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(materialLaunchIntent);
                                }
                            });
                            btn_launch.setVisibility(View.VISIBLE);
                        } else {
                            btn_goPremium.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(MaterialDetailsActivity.this, PackageListActivity.class);
                                    startActivity(intent);
                                }
                            });
                            btn_goPremium.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyUtil.volleyErrorHandler(error, MaterialDetailsActivity.this, true);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyUtil.getHeaders(sessionManager);
            }
        };
        AppController.getInstance().addToRequestQueue(materialDetailsRequest, TAG);
    }
}
