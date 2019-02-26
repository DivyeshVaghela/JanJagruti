package janjagruti.learning.com.janjagruti;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.Map;

import janjagruti.learning.com.janjagruti.adapter.SubjectAdapter;
import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.EntityList;
import janjagruti.learning.com.janjagruti.entity.Subject;
import janjagruti.learning.com.janjagruti.entity.User;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = DashboardActivity.class.getSimpleName();

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView drawerNavigation;
    private ActionBarDrawerToggle drawerToggle;

    private ConstraintLayout wrapper_userIdentity;
    private TextView drawerHearder_username, drawerHeader_email;

    private RecyclerView rv_subjectList;
    private SubjectAdapter subjectAdapter;
    private CardView card_loading;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.dashboard);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerNavigation = findViewById(R.id.drawerNavigation);

        View headerView = drawerNavigation.getHeaderView(0);

        /**
         * NavigationDrawer Header
         */
        User userProfile = sessionManager.getUserProfile();
        drawerHearder_username = headerView.findViewById(R.id.drawerHearder_username);
        drawerHearder_username.setText(userProfile.getUsername());
        drawerHeader_email = headerView.findViewById(R.id.drawerHeader_email);
        drawerHeader_email.setText(userProfile.getEmail());

        wrapper_userIdentity = headerView.findViewById(R.id.wrapper_userIdentity);
        wrapper_userIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                goToUserProfile();
            }
        });
        if (sessionManager.isPremiumUser())
            wrapper_userIdentity.setBackground(ContextCompat.getDrawable(this, R.drawable.success_gradient));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerNavigation.setNavigationItemSelectedListener(this);

        card_loading = findViewById(R.id.card_loading);

        rv_subjectList = findViewById(R.id.rv_subjectList);
        subjectAdapter = new SubjectAdapter();
        subjectAdapter.setSubjectClickListener(new SubjectAdapter.SubjectClickListener() {
            @Override
            public void onSubjectClicked(int subjectId, String subjectTitle) {
                Intent intent = new Intent(DashboardActivity.this, SubjectActivity.class);
                intent.putExtra(SubjectActivity.EXTRA_SUBJECT_ID, subjectId);
                intent.putExtra(SubjectActivity.EXTRA_SUBJECT_TITLE, subjectTitle);
                startActivity(intent);
            }
        });
        rv_subjectList.setAdapter(subjectAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_subjectList.setLayoutManager(linearLayoutManager);

        loadSubjectList();
    }

    private void loadSubjectList(){

        final JsonObjectRequest subjectListRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.BASE_URL + ApiConfig.SUBJECT_URI,
                null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        EntityList<Subject> entityList = JSONConverterUtil.fromJSONObject(response, TypeToken.getParameterized(EntityList.class, Subject.class).getType());
                        subjectAdapter.addAllItems(entityList.getList());
                        card_loading.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyUtil.volleyErrorHandler(error, DashboardActivity.this, true);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyUtil.getHeaders(sessionManager);
            }
        };
        AppController.getInstance().addToRequestQueue(subjectListRequest, TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch (item.getItemId()){
            case R.id.action_notice: {
                Intent intent = new Intent(DashboardActivity.this, NoticeListActivity.class);
                startActivity(intent);
                return true;
            }

            default:
                return true;
        }
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_logout: {
                sessionManager.loggedOut();
                goToLoginActivity();
                return true;
            }

            case R.id.nav_changePassword: {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(DashboardActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                return true;
            }

            default:
                return true;
        }
    }

    private void goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToUserProfile(){
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.dashboard_action_menu, menu);
        return true;
    }
}
