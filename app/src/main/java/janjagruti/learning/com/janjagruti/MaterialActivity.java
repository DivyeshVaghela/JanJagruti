package janjagruti.learning.com.janjagruti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;

public class MaterialActivity extends AppCompatActivity {

    public static final String TAG = MaterialActivity.class.getSimpleName();

    public static final String EXTRA_MATERIAL_ID = "materialId";
    public static final String EXTRA_MATERIAL_TITLE = "materialTitle";
    public static final String EXTRA_MATERIAL_PATH = "materialPath";
    public static final String EXTRA_MATERIAL_TYPE = "materialType";

    private int materialId = 0;
    private String materialTitle;
    private String materialPath;
    private String materialType;

    private SessionManager sessionManager;

    private Toolbar toolbar;
    private PDFView pdfview_material;
    private CardView card_loading;

    private final List<String> supportedTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        supportedTypes.add("application/pdf");

        Intent comingIntent = getIntent();
        if (comingIntent != null){
            materialId = comingIntent.getIntExtra(EXTRA_MATERIAL_ID, 0);
            materialTitle = comingIntent.getStringExtra(EXTRA_MATERIAL_TITLE);
            materialPath = comingIntent.getStringExtra(EXTRA_MATERIAL_PATH);
            materialType = comingIntent.getStringExtra(EXTRA_MATERIAL_TYPE);
        }

        boolean doProceed = true;

        if (materialId == 0 || TextUtils.isEmpty(materialPath) || TextUtils.isEmpty(materialType)) {
            finish();
            return;
        } else if (!supportedTypes.contains(materialType)){
            doProceed = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(this.getString(R.string.material_type_not_supported))
                    .setCancelable(false)
                    .setPositiveButton(this.getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            dialogInterface.cancel();
                            finish();
                            return;
                        }
                    });
            builder.create().show();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(TextUtils.isEmpty(materialTitle) ? getString(R.string.material) : materialTitle);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());

        card_loading = findViewById(R.id.card_loading);
        pdfview_material = findViewById(R.id.pdfview_material);

        if (doProceed) {
            new PDFStreamReaders().execute(materialPath);
        }
    }

    public class PDFStreamReaders extends AsyncTask<String, Void, InputStream>{

        int responseCode = 0;
        String responseMessage = "";

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Authorization", ApiConfig.AUTH_TYPE + " " + sessionManager.getSharedPreferences().getString(SessionManager.KEY_API_TOKEN, ""));

                responseCode = httpURLConnection.getResponseCode();
                responseMessage = httpURLConnection.getResponseMessage();
                if (responseCode == HttpURLConnection.HTTP_OK){
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }

            } catch (java.io.IOException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {

            if (inputStream == null){

                Log.d("ResponseMessage", String.valueOf(responseCode) + " " + responseMessage);
                if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED || responseCode == HttpURLConnection.HTTP_FORBIDDEN){
                    VolleyUtil.handleAuthenticationFailed(MaterialActivity.this);
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MaterialActivity.this);
                    builder.setMessage(MaterialActivity.this.getString(R.string.something_wrong))
                            .setCancelable(false)
                            .setPositiveButton(MaterialActivity.this.getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int id) {
                                    dialogInterface.cancel();
                                    MaterialActivity.this.finish();
                                }
                            });
                    builder.create().show();
                }

            } else{
                pdfview_material.fromStream(inputStream).load();
                pdfview_material.setVisibility(View.VISIBLE);
                card_loading.setVisibility(View.GONE);
            }
        }
    }
}
