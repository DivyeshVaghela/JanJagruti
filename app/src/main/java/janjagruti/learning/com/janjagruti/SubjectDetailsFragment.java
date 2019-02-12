package janjagruti.learning.com.janjagruti;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import java.util.Map;

import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.Subject;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectDetailsFragment extends Fragment {

    public static final String TAG = SubjectDetailsFragment.class.getSimpleName();

    public static final String EXTRA_SUBJECT_ID = "subjectId";
    private int subjectId = 0;

    private CardView card_loading, wrapper_card;
    private TextView txtview_title, txtview_subtitle, txtview_details;
    private NetworkImageView img_subjectLogo;

    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject_details, container, false);

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        Bundle argumentsBundle = this.getArguments();
        if (argumentsBundle != null){
            subjectId = argumentsBundle.getInt(EXTRA_SUBJECT_ID, 0);
        }

        card_loading = view.findViewById(R.id.card_loading);
        wrapper_card = view.findViewById(R.id.wrapper_card);

        img_subjectLogo = view.findViewById(R.id.img_subjectLogo);
        txtview_title = view.findViewById(R.id.txtview_title);
        txtview_subtitle = view.findViewById(R.id.txtview_subtitle);
        txtview_details = view.findViewById(R.id.txtview_details);

        if (subjectId != 0){

            JsonObjectRequest subjectDetailsRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    ApiConfig.BASE_URL + ApiConfig.SUBJECT_URI + "/" + subjectId,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Subject subject = JSONConverterUtil.fromJSONObject(response, Subject.class);

                            txtview_title.setText(subject.getTitle());
                            txtview_subtitle.setText(subject.getSubtitle());
                            txtview_details.setText(subject.getDetails());

                            img_subjectLogo.setImageUrl(subject.getLogoUrl(), AppController.getInstance().getImageLoader());
                            img_subjectLogo.setContentDescription(subject.getTitle());

                            wrapper_card.setVisibility(View.VISIBLE);
                            card_loading.setVisibility(View.INVISIBLE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyUtil.volleyErrorHandler(error, getActivity(), true);
                        }
                    }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return VolleyUtil.getHeaders(sessionManager);
                }
            };
            AppController.getInstance().addToRequestQueue(subjectDetailsRequest, TAG);

        }

        return view;
    }

}
