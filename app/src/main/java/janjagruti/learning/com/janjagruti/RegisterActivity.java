package janjagruti.learning.com.janjagruti;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.entity.User;
import janjagruti.learning.com.janjagruti.entity.support.ApiResponse;
import janjagruti.learning.com.janjagruti.util.ComponentUtil;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;
import janjagruti.learning.com.janjagruti.validation.StringValidation;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = RegisterActivity.class.getSimpleName();

    private TextInputLayout txtInputEmail, txtInputMobileNo, txtInputUsername, txtInputPassword, txtInputConfirmPassword;
    private Button btnLoginLink, btnRegister;
    private TextView txtview_errorMessage;

    private Dialog progressDialog;
    private boolean isSubmitionInitiated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = ComponentUtil.getProgressDialog(this, null);

        txtview_errorMessage = findViewById(R.id.txtview_errorMessage);

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

        txtInputMobileNo = findViewById(R.id.txtInputMobileNo);
        txtInputMobileNo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isSubmitionInitiated)
                    isValidMobileNo();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        txtInputUsername = findViewById(R.id.txtInputUsername);
        txtInputUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isSubmitionInitiated)
                    isValidUsername();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        txtInputPassword = findViewById(R.id.txtInputCurrentPassword);
        txtInputPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isSubmitionInitiated)
                    isValidPassword();
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

        btnLoginLink = findViewById(R.id.btnLoginLink);
        btnLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginActivity();
            }
        });

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isSubmitionInitiated = true;
                if (!isValid())
                    return;

                try {
                    register();
                } catch (JSONException e) {
                    e.printStackTrace();
                    registrationUnsuccessful(getString(R.string.something_wrong));
                }
            }
        });
    }

    private void goToLoginActivity(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //validate the registration form
    private boolean isValid(){

        return (isValidEmail() & isValidUsername() & isValidMobileNo() & isValidPassword() & isValidConfirmPassword());
    }

    //email validation
    private boolean isValidEmail(){
        boolean isValid = true;

        String email = txtInputEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()){
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

    //username validation
    private boolean isValidUsername(){
        boolean isValid = true;

        String username = txtInputUsername.getEditText().getText().toString().trim();
        if (username.isEmpty()){
            txtInputUsername.setError(getString(R.string.empty_field_message));
            isValid = false;
        }
        else {
            txtInputUsername.setError(null);
        }

        return isValid;
    }

    //mobile no validation
    private boolean isValidMobileNo(){
        boolean isValid = true;

        String mobileNo = txtInputMobileNo.getEditText().getText().toString().trim();
        if (mobileNo.isEmpty()){
            txtInputMobileNo.setError(getString(R.string.empty_field_message));
            isValid = false;
        }
        else if (!StringValidation.isValidMobileNo(mobileNo)){
            txtInputMobileNo.setError(getString(R.string.invalid_mobile_no));
            isValid = false;
        }
        else {
            txtInputMobileNo.setError(null);
        }

        return isValid;
    }

    //password validation
    private boolean isValidPassword(){
        boolean isValid = true;

        String password = txtInputPassword.getEditText().getText().toString().trim();
        if (password.isEmpty()){
            txtInputPassword.setError(getString(R.string.empty_field_message));
            isValid = false;
        }
        else {
            txtInputPassword.setError(null);
        }

        return isValid;
    }

    //confirm password validation
    private boolean isValidConfirmPassword(){
        boolean isValid = true;

        String confirmPassword = txtInputConfirmPassword.getEditText().getText().toString().trim();
        String password = txtInputPassword.getEditText().getText().toString().trim();

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

    private void register() throws JSONException {

        String email = txtInputEmail.getEditText().getText().toString().trim();
        String mobileNo = txtInputMobileNo.getEditText().getText().toString().trim();
        String username = txtInputUsername.getEditText().getText().toString().trim();
        String password = txtInputPassword.getEditText().getText().toString().trim();

        User user = new User(username, email, mobileNo, password);

        JSONObject data = JSONConverterUtil.toJSONObject(user);

        JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.POST,
                ApiConfig.BASE_URL + ApiConfig.REGISTER_URI,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        ApiResponse apiResponse = JSONConverterUtil.fromJSONObject(response, ApiResponse.class);

                        if (apiResponse == null){
                            registrationUnsuccessful(getString(R.string.something_wrong));
                            return;
                        }
                        if (!apiResponse.isSuccess()){

                            Map<String, String> fieldErrors = apiResponse.getFieldErrors();
                            if (fieldErrors.containsKey("email")){
                                txtInputEmail.setError(fieldErrors.get("email"));
                            }
                            if (fieldErrors.containsKey("username")){
                                txtInputUsername.setError(fieldErrors.get("username"));
                            }
                            if (fieldErrors.containsKey("mobileNo")){
                                txtInputMobileNo.setError(fieldErrors.get("mobileNo"));
                            }
                            if (fieldErrors.containsKey("password")){
                                txtInputPassword.setError(fieldErrors.get("password"));
                            }

                            registrationUnsuccessful(apiResponse.getMessage());
                            return;
                        }

                        registrationSuccessful();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = VolleyUtil.volleyErrorHandler(error, RegisterActivity.this, false);
                        registrationUnsuccessful(errorMessage);
                    }
                });
        registerRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int)TimeUnit.SECONDS.toMillis(10),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        AppController.getInstance().addToRequestQueue(registerRequest, TAG);
        processStarted();
    }

    private void processStarted(){
        progressDialog.show();
    }

    private void processEnded(){
        progressDialog.hide();
    }

    private void registrationSuccessful(){

        processEnded();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.registration_successful))
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

    private void registrationUnsuccessful(String reason){

        processEnded();

        if (reason != null && !reason.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(reason)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            dialogInterface.cancel();
                        }
                    });
            builder.create().show();
        }
    }
}
