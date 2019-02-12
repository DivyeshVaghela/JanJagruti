package janjagruti.learning.com.janjagruti;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.entity.support.ApiResponse;
import janjagruti.learning.com.janjagruti.util.ComponentUtil;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;
import janjagruti.learning.com.janjagruti.validation.StringValidation;

public class ForgotPasswordActivity extends AppCompatActivity {

    public static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    private TextInputLayout txtInputEmail;
    private Button btnSendMail, btnBack;

    private Dialog progressDialog;
    private boolean isSubmitionInitiated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        progressDialog = ComponentUtil.getProgressDialog(this, null);

        txtInputEmail = findViewById(R.id.txtInputEmail);
        txtInputEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isSubmitionInitiated)
                    isValidEmail();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnSendMail = findViewById(R.id.btnSendMail);
        btnSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSubmitionInitiated = true;

                if (!isValidEmail())
                    return;
                forgotPassword(txtInputEmail.getEditText().getText().toString().trim());
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginActivity();
            }
        });
    }

    private void forgotPassword(String email) {

        progressDialog.show();

        JSONObject data = new JSONObject();
        try {
            data.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest forgotPasswordRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.BASE_URL + ApiConfig.FORGOT_PASSWORD_URI,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.hide();

                        final ApiResponse apiResponse = JSONConverterUtil.fromJSONObject(response, ApiResponse.class);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                        builder.setMessage(apiResponse.getMessage())
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int id) {
                                        dialogInterface.cancel();
                                        goToLoginActivity();
                                    }
                                });
                        builder.create().show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        VolleyUtil.volleyErrorHandler(error, ForgotPasswordActivity.this, true);
                    }
                }
        );
        forgotPasswordRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int)TimeUnit.SECONDS.toMillis(10),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        AppController.getInstance().addToRequestQueue(forgotPasswordRequest, TAG);
    }

    //email validation
    private boolean isValidEmail() {
        boolean isValid = true;

        String email = txtInputEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            txtInputEmail.setError(getString(R.string.empty_field_message));
            isValid = false;
        }
        else if (!StringValidation.isValidEmailAddress(email)){
            txtInputEmail.setError(getString(R.string.invalid_email_address));
            isValid = false;
        }
        else {
            txtInputEmail.setError(null);
        }
        return isValid;
    }

    private void goToLoginActivity(){
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
