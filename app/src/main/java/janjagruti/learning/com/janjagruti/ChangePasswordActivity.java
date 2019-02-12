package janjagruti.learning.com.janjagruti;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.support.ApiResponse;
import janjagruti.learning.com.janjagruti.util.ComponentUtil;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;

public class ChangePasswordActivity extends AppCompatActivity {

    public static final String TAG = ChangePasswordActivity.class.getSimpleName();

    private Toolbar toolbar;
    private ActionBar supportActionBar;

    private TextInputLayout txtInputCurrentPassword, txtInputNewPassword, txtInputConfirmPassword;
    private Button btnChangePassword, btnCancel;

    private Dialog progressDialog;
    private boolean isSubmitionInitiated = false;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        sessionManager = new SessionManager(getApplicationContext());
        progressDialog = ComponentUtil.getProgressDialog(this, null);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setTitle(R.string.change_password);

        txtInputCurrentPassword = findViewById(R.id.txtInputCurrentPassword);
        txtInputCurrentPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isSubmitionInitiated)
                    isValidCurrentPassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        txtInputNewPassword = findViewById(R.id.txtInputNewPassword);
        txtInputNewPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isSubmitionInitiated)
                    isValidNewPassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        txtInputConfirmPassword = findViewById(R.id.txtInputConfirmPassword);
        txtInputConfirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isSubmitionInitiated)
                    isValidConfirmPassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSubmitionInitiated = true;
                if (!isValid())
                    return;

                chnagePassword(
                        sessionManager.getUserProfile().getEmail(),
                        txtInputCurrentPassword.getEditText().getText().toString().trim(),
                        txtInputNewPassword.getEditText().getText().toString().trim()
                );
            }
        });

        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void chnagePassword(String email, String currentPassword, String newPassword) {

        progressDialog.show();

        JSONObject data = new JSONObject();
        try {
            data.put("email", email);
            data.put("currentPassword", currentPassword);
            data.put("newPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest changePasswordRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.BASE_URL + ApiConfig.CHANGE_PASSWORD_URI,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.hide();

                        final ApiResponse apiResponse = JSONConverterUtil.fromJSONObject(response, ApiResponse.class);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                        builder.setCancelable(false)
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int id) {
                                        dialogInterface.cancel();
                                        if (apiResponse.isSuccess()){
                                            sessionManager.loggedOut();
                                            goToLoginActivity();
                                        }
                                    }
                                });
                        if (apiResponse.isSuccess()){
                            builder.setMessage(getString(R.string.password_change_success));
                        } else {
                            builder.setMessage(apiResponse.getMessage());
                        }
                        builder.create().show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyUtil.volleyErrorHandler(error, ChangePasswordActivity.this, true);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyUtil.getHeaders(sessionManager);
            }
        };
        AppController.getInstance().addToRequestQueue(changePasswordRequest, TAG);
    }

    //validation for complete form
    private boolean isValid(){
        return (isValidCurrentPassword() & isValidNewPassword() & isValidConfirmPassword());
    }

    //current password validation
    private boolean isValidCurrentPassword(){
        boolean isValid = true;

        String password = txtInputCurrentPassword.getEditText().getText().toString().trim();
        if (password.isEmpty()){
            txtInputCurrentPassword.setError(getString(R.string.empty_field_message));
            isValid = false;
        }
        else {
            txtInputCurrentPassword.setError(null);
        }

        return isValid;
    }

    //new password validation
    private boolean isValidNewPassword(){
        boolean isValid = true;

        String password = txtInputNewPassword.getEditText().getText().toString().trim();
        if (password.isEmpty()){
            txtInputNewPassword.setError(getString(R.string.empty_field_message));
            isValid = false;
        }
        else {
            txtInputNewPassword.setError(null);
        }

        return isValid;
    }

    //confirm password validation
    private boolean isValidConfirmPassword(){
        boolean isValid = true;

        String confirmPassword = txtInputConfirmPassword.getEditText().getText().toString().trim();
        String password = txtInputNewPassword.getEditText().getText().toString().trim();

        if (confirmPassword.isEmpty()){
            txtInputConfirmPassword.setError(getString(R.string.empty_field_message));
            isValid = false;
        }
        else if (!password.equals(confirmPassword)){
            txtInputConfirmPassword.setError(getString(R.string.confirm_password_mismatch));
            isValid = false;
        }
        else {
            txtInputConfirmPassword.setError(null);
        }

        return isValid;
    }

    private void goToLoginActivity(){
        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
