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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.User;
import janjagruti.learning.com.janjagruti.entity.support.AuthResponse;
import janjagruti.learning.com.janjagruti.util.ComponentUtil;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;
import janjagruti.learning.com.janjagruti.validation.StringValidation;

public class LoginActivity extends AppCompatActivity {

    //LoginActivity.class.getSimpleName()
    public static String TAG = "LoginAttempt";

    private SessionManager sessionManager;

    private TextInputLayout txtInputEmail, txtInputPassword;
    private Button btnRegisterLink, btnLogin;
    private TextView txtview_ErrorMessage;

    private Dialog progressDialog;
    private boolean isSubmitionInitiated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());

        //Log.d(TAG, sessionManager.getUserProfile().toString());

        txtview_ErrorMessage = findViewById(R.id.txtview_ErrorMessage);

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

        txtInputPassword = findViewById(R.id.txtInputPassword);
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

        btnRegisterLink = findViewById(R.id.btnRegisterLink);
        btnRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSubmitionInitiated = true;
                if (!isValid())
                    return;
                authenticate(
                        txtInputEmail.getEditText().getText().toString().trim(),
                        txtInputPassword.getEditText().getText().toString().trim()
                );
            }
        });

        progressDialog = ComponentUtil.getProgressDialog(this, null);
    }

    //perform validation for login form
    private boolean isValid(){

        return (isValidEmail() & isValidPassword());
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

    //password validation
    private boolean isValidPassword() {
        boolean isValid = true;

        String password = txtInputPassword.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            txtInputPassword.setError(getString(R.string.empty_field_message));
            isValid = false;
        }
        else {
            txtInputPassword.setError(null);
        }
        return isValid;
    }


    private void authenticate(String email, String password){

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        JSONObject credentials = JSONConverterUtil.toJSONObject(user);

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST,
                ApiConfig.BASE_URL + ApiConfig.AUTHENTICATE_URI,
                credentials,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        AuthResponse authResponse = JSONConverterUtil.fromJSONObject(response, AuthResponse.class);

                        if (!authResponse.isSuccess()){
                            loginUnsuccessful(getString(R.string.invalid_credential));
                            return;
                        }

                        loginSuccessful(authResponse);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = VolleyUtil.volleyErrorHandler(error, LoginActivity.this, false);
                        loginUnsuccessful(errorMessage);
                    }
                });
        AppController.getInstance().addToRequestQueue(loginRequest, TAG);

        processStarted();
    }

    private void processStarted(){
        progressDialog.show();
    }

    private void processEnded(){
        progressDialog.hide();
    }

    private void loginSuccessful(AuthResponse authResponse){


        sessionManager.loggedIn(authResponse.getToken(), authResponse.getIssuedAt(), authResponse.getExpire(), authResponse.getUserProfile());

        processEnded();
        goToDashboardActivity();
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.login_successful))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        dialogInterface.cancel();
                        goToDashboardActivity();
                    }
                });
        builder.create().show();*/
    }

    private void goToDashboardActivity(){
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginUnsuccessful(String reason){

        processEnded();

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
