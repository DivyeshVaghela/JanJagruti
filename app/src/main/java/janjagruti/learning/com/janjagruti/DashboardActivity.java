package janjagruti.learning.com.janjagruti;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.User;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView drawerNavigation;
    private ActionBarDrawerToggle drawerToggle;

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

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerNavigation.setNavigationItemSelectedListener(this);

        /*TextView textView = findViewById(R.id.textView);
        User userProfile = sessionManager.getUserProfile();
        textView.setText(userProfile.getUsername() + " : " + userProfile.getEmail());*/
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

            default:
                return true;
        }
    }

    private void goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.dashboard_action_menu, menu);
        return true;
    }
}
