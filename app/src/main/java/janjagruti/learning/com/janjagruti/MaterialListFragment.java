package janjagruti.learning.com.janjagruti;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import janjagruti.learning.com.janjagruti.adapter.MaterialAdapter;
import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.EntityList;
import janjagruti.learning.com.janjagruti.entity.Material;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class MaterialListFragment extends Fragment {

    public static final String TAG = MaterialListFragment.class.getSimpleName();

    public static final String EXTRA_SUBJECT_ID = "subjectId";
    private int subjectId = 0;

    private CardView card_no_content;

    private RecyclerView rv_subjectMaterialList;
    private LinearLayoutManager linearLayoutManager;
    private MaterialAdapter materialAdapter;

    //query params
    private final String PARAM_INCLUDE_DETAILS = "details";
    private final String PARAM_INCLUDE_TYPE = "type";
    private final String PARAM_INCLUDE_SUBJECT = "subject";
    private final String PARAM_DETAILS_LENGTH = "detailsLimit";
    private final String PARAM_PAGE = "page";
    private final String PARAM_PAGE_SIZE = "pageSize";

    private int totalMaterial = 0;
    private boolean includeDetails = false;
    private boolean includeType = true;
    private boolean includeSubject = false;
    private int detailsLength = 0;
    private int pageSize = 2;
    private int currentPage = 0;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private SessionManager sessionManager;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_material_list, container, false);

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        Bundle argumentsBundle = this.getArguments();
        if (argumentsBundle != null){
            subjectId = argumentsBundle.getInt(EXTRA_SUBJECT_ID, 0);
        }

        card_no_content = view.findViewById(R.id.card_no_content);

        rv_subjectMaterialList = view.findViewById(R.id.rv_subjectMaterialList);
        materialAdapter = new MaterialAdapter();
        materialAdapter.setMaterialClickInterface(new MaterialAdapter.MaterialClickInterface() {
            @Override
            public void onMaterialClicked(int materialId, String materialTitle) {
                Intent intent = new Intent(getActivity(), MaterialDetailsActivity.class);
                intent.putExtra(MaterialDetailsActivity.EXTRA_MATERIAL_ID, materialId);
                intent.putExtra(MaterialDetailsActivity.EXTRA_MATERIAL_TITLE, materialTitle);
                startActivity(intent);
            }
        });
        materialAdapter.addLoadingFooter();
        rv_subjectMaterialList.setAdapter(materialAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_subjectMaterialList.setLayoutManager(linearLayoutManager);

        loadMaterials();
        rv_subjectMaterialList.scrollToPosition(0);
        setScrollListener();

        return view;
    }

    private void loadMaterials(){

        isLoading = true;

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(PARAM_INCLUDE_DETAILS, String.valueOf(includeDetails));
        queryParams.put(PARAM_PAGE_SIZE, String.valueOf(pageSize));
        queryParams.put(PARAM_PAGE, String.valueOf(++currentPage));
        queryParams.put(PARAM_INCLUDE_TYPE, String.valueOf(includeType));
        queryParams.put(PARAM_INCLUDE_SUBJECT, String.valueOf(includeSubject));

        final JsonObjectRequest materialListRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.BASE_URL + ApiConfig.SUBJECT_URI + "/" + subjectId + "/materials" + VolleyUtil.prepareQueryString(queryParams),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, response.toString());

                        EntityList<Material> entityList = JSONConverterUtil.fromJSONObject(response, TypeToken.getParameterized(EntityList.class, Material.class).getType());
                        materialAdapter.addAllItems(entityList.getList());
                        totalMaterial = entityList.getTotal();
                        if (totalMaterial==0){
                            card_no_content.setVisibility(View.VISIBLE);
                            rv_subjectMaterialList.setVisibility(View.GONE);
                        }
                        if (totalMaterial==0 || totalMaterial == materialAdapter.getItemCount() - 1) {
                            isLastPage = true;
                            materialAdapter.removeLoadingFooter();
                        }

                        isLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyUtil.volleyErrorHandler(error, getActivity());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyUtil.getHeaders(sessionManager);
            }
        };

        AppController.getInstance().addToRequestQueue(materialListRequest, TAG);
    }

    private void setScrollListener(){

        rv_subjectMaterialList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPostion = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage){
                    if ((visibleItemCount + firstVisibleItemPostion) >= totalItemCount){
                        loadMaterials();
                    }
                }
            }
        });
    }
}
