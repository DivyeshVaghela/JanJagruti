package janjagruti.learning.com.janjagruti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import janjagruti.learning.com.janjagruti.adapter.NoticeAdapter;
import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.Notice;
import janjagruti.learning.com.janjagruti.entity.NoticeList;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;

public class NoticeListActivity extends AppCompatActivity {

    private static final String TAG = NoticeListActivity.class.getSimpleName();

    private SessionManager sessionManager;

    private Toolbar toolbar;
    private RecyclerView rv_noticeList;
    private NoticeAdapter noticeAdapter;
    private LinearLayoutManager layoutManager;

    //private List<Notice> noticeList = new ArrayList<>();

    //notice related params
    private final String PARAM_INCLUDE_DETAILS = "details";
    private final String PARAM_DETAILS_LENGTH = "detailsLimit";
    private final String PARAM_PAGE = "page";
    private final String PARAM_NOTICE_PER_PAGE = "pageSize";

    private int totalNotice = 0;
    private boolean includeDetails = true;
    private int detailsLength = 40;
    private int noticePerPage = 5;
    private int currentPage = 0;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);

        sessionManager = new SessionManager(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv_noticeList = findViewById(R.id.rv_noticeList);
        noticeAdapter = new NoticeAdapter();
        noticeAdapter.addLoadingFooter();
        noticeAdapter.setNoticeClickListener(new NoticeAdapter.NoticeClickListener() {
            @Override
            public void onNoticeClicked(int noticeId) {
                Intent intent = new Intent(NoticeListActivity.this, NoticeDetailsActivity.class);
                intent.putExtra(NoticeDetailsActivity.EXTRA_NOTICE_ID, noticeId);
                startActivity(intent);
            }
        });
        rv_noticeList.setAdapter(noticeAdapter);
        layoutManager = new LinearLayoutManager(this);
        rv_noticeList.setLayoutManager(layoutManager);

        loadNotices();
        rv_noticeList.scrollToPosition(0);
        setScrollListener();
    }

    private void loadNotices(){

        isLoading = true;

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_INCLUDE_DETAILS, String.valueOf(includeDetails));
        params.put(PARAM_DETAILS_LENGTH, String.valueOf(detailsLength));
        //pagination
        params.put(PARAM_NOTICE_PER_PAGE, String.valueOf(noticePerPage));
        params.put(PARAM_PAGE, String.valueOf(++currentPage));

        final JsonObjectRequest noticeListRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.BASE_URL + ApiConfig.NOTICE_URI + VolleyUtil.prepareQueryString(params),
                null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(LoginActivity.TAG, response.toString());

                        NoticeList notices = JSONConverterUtil.fromJSONObject(response, NoticeList.class);
                        noticeAdapter.addAllItems(notices.getNotices());
                        totalNotice = notices.getTotalNotice();
                        if (totalNotice == noticeAdapter.getItemCount() - 1){
                            isLastPage = true;
                            noticeAdapter.removeLoadingFooter();
                        }

                        isLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        VolleyUtil.volleyErrorHandler(error, NoticeListActivity.this);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyUtil.getHeaders(sessionManager);
            }
        };
        AppController.getInstance().addToRequestQueue(noticeListRequest, TAG);
    }

    private void setScrollListener(){

        rv_noticeList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                Log.d(TAG, "visibleItemCount = " + visibleItemCount + ", totalItemCount = " + totalItemCount + ", firstVisibleItemPosition = " + firstVisibleItemPosition);

                if (!isLoading && !isLastPage){
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount){
                        loadNotices();
                    }
                }
            }
        });

    }
}
