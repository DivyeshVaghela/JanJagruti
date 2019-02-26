package janjagruti.learning.com.janjagruti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.Package;
import janjagruti.learning.com.janjagruti.entity.support.AuthResponse;
import janjagruti.learning.com.janjagruti.entity.support.PackagePurchaseInitResponse;
import janjagruti.learning.com.janjagruti.util.ComponentUtil;
import janjagruti.learning.com.janjagruti.util.JSONConverterUtil;
import janjagruti.learning.com.janjagruti.util.VolleyUtil;

public class PackageDetailsActivity extends AppCompatActivity {

    public static final String TAG = PackageDetailsActivity.class.getSimpleName();

    public static final String EXTRA_PACKAGE_ID = "packageId";
    public static final String EXTRA_PACKAGE_TITLE = "packageTitle";

    private int packageId = 0;
    private String packageTitle;

    private Toolbar toolbar;
    private CardView card_loading, card_packageDetails;

    private TextView txtview_title, txtview_subtitle, txtview_details, txtview_rateLine;
    private Button btn_purchaseNow;

    private SessionManager sessionManager;
    private PackagePurchaseInitResponse packagePurchaseInitResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);

        Intent comingIntent = getIntent();
        if (comingIntent != null) {
            packageId = comingIntent.getIntExtra(EXTRA_PACKAGE_ID, 0);
            packageTitle = comingIntent.getStringExtra(EXTRA_PACKAGE_TITLE);
        }

        if (packageId == 0){
            finish();
            return;
        }

        sessionManager = new SessionManager(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(TextUtils.isEmpty(packageTitle) ? "Package Details" : packageTitle);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        txtview_title = findViewById(R.id.txtview_title);
        txtview_subtitle = findViewById(R.id.txtview_subtitle);
        txtview_details = findViewById(R.id.txtview_details);
        txtview_rateLine = findViewById(R.id.txtview_rateLine);
        btn_purchaseNow = findViewById(R.id.btn_purchaseNow);

        card_loading = findViewById(R.id.card_loading);
        card_packageDetails = findViewById(R.id.card_packageDetails);

        loadPackageDetails();

        //start payment process
        btn_purchaseNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPaymentProcess();
            }
        });
    }

    private void loadPackageDetails(){

        Map<String, String> query = new HashMap<>();
        query.put("creator", String.valueOf(false));
        query.put("details", String.valueOf(true));

        JsonObjectRequest packageDetailsRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.BASE_URL + ApiConfig.PACKAGE_URI + "/" + packageId + VolleyUtil.prepareQueryString(query),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Package aPackage = JSONConverterUtil.fromJSONObject(response, Package.class);

                        txtview_title.setText(aPackage.getTitle());
                        txtview_subtitle.setText(aPackage.getSubtitle());
                        txtview_details.setText(aPackage.getDetails());
                        txtview_rateLine.setText(String.format(getResources().getString(R.string.rate_format), aPackage.getRate(), aPackage.getDuration(), aPackage.getDurationScale()));

                        card_loading.setVisibility(View.GONE);
                        card_packageDetails.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyUtil.volleyErrorHandler(error, PackageDetailsActivity.this, true);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyUtil.getHeaders(sessionManager);
            }
        };
        AppController.getInstance().addToRequestQueue(packageDetailsRequest, TAG);

    }

    private void startPaymentProcess(){

        JSONObject data = new JSONObject();
        try {
            data.put("package", packageId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest packagePurchaseRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.BASE_URL + ApiConfig.PACKAGE_PURCHASE,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("PaymentInProgress", response.toString());
                        packagePurchaseInitResponse = JSONConverterUtil.fromJSONObject(response, PackagePurchaseInitResponse.class);
                        startPayUMoneyIntegration();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("PaymentInProgress", "Volle Error");
                        VolleyUtil.volleyErrorHandler(error, PackageDetailsActivity.this, true);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyUtil.getHeaders(sessionManager);
            }
        };
        AppController.getInstance().addToRequestQueue(packagePurchaseRequest, TAG);
    }

    private void startPayUMoneyIntegration(){

        if (packagePurchaseInitResponse != null){

            Log.d("PaymentInProgress", "Initiating the PayUMoney");

            PayUmoneySdkInitializer.PaymentParam paymentParam = null;

            /**
             * Payment related parameters
             */
            PayUmoneySdkInitializer.PaymentParam.Builder paymentParamBuilder = new PayUmoneySdkInitializer.PaymentParam.Builder();
            Log.d("PaymentInProgress", String.format("%.2f", packagePurchaseInitResponse.getProduct().getRate()));
            paymentParamBuilder
                    .setAmount(String.format("%.2f", packagePurchaseInitResponse.getProduct().getRate()))
                    .setTxnId(packagePurchaseInitResponse.getTxnId())
                    .setPhone(packagePurchaseInitResponse.getCustomer().getMobileNo())
                    .setProductName(packagePurchaseInitResponse.getProduct().getTitle())
                    .setFirstName(packagePurchaseInitResponse.getCustomer().getUsername())
                    .setEmail(packagePurchaseInitResponse.getCustomer().getEmail())
                    .setsUrl(ApiConfig.BASE_URL + ApiConfig.PAYMENT_SUCCESS)
                    .setfUrl(ApiConfig.BASE_URL + ApiConfig.PAYMENT_FAILURE)
                    .setUdf1(String.valueOf(packagePurchaseInitResponse.getProduct().getId()))
                    .setUdf2(String.valueOf(packagePurchaseInitResponse.getCustomer().getId()))
                    .setIsDebug(true)
                    //TODO: change the KEY or get it from server and set here
                    .setKey(packagePurchaseInitResponse.getKey())
                    //TODO: change the MERCHANT ID or get it from server and set here
                    .setMerchantId(packagePurchaseInitResponse.getMerchantId());

            try {
                paymentParam = paymentParamBuilder.build();

                Log.d("PaymentInProgress", "paymentParam built");

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("PaymentInProgress", "Error : " + e.getMessage());
                return;
            }

            if (paymentParam != null){

                Log.d("PaymentInProgress", "All Done");

                //TODO: fetch the merchant HASH from server and set it here
                paymentParam.setMerchantHash(packagePurchaseInitResponse.getHash());
                //Start the transaction
                PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, PackageDetailsActivity.this, R.style.AppTheme_default, false);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("PaymentInProgress", "request code " + requestCode + " resultcode " + resultCode);

        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT){

            if (resultCode == RESULT_CANCELED){
                sendPaymentStatusRequest("canceled");
            }

            if (resultCode == RESULT_OK && data != null){
                TransactionResponse transactionResponse = data.getParcelableExtra( PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE );
                if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                    Log.d("PaymentInProgress", "transactionResponse.getTransactionStatus() = " + transactionResponse.getTransactionStatus());

                    if(transactionResponse.getTransactionStatus().equals( TransactionResponse.TransactionStatus.SUCCESSFUL )) {
                        //Success Transaction
                        refreshApiToken();

                    } else {
                        //Failure Transaction
                        ComponentUtil.showOkayAlertDialog(PackageDetailsActivity.this, "Payment Failed");
                    }

                    // Response from Payumoney
                    String payuResponse = transactionResponse.getPayuResponse();
                    Log.d("PaymentInProgress", "payuResponse = " + payuResponse);

                    // Response from SURl and FURL
                    //String merchantResponse = transactionResponse.getTransactionDetails();
                } else {
                    Log.d("PaymentInProgress", "transactionResponse is null");
                }
            }
        }
    }

    //Will be called mannually in case of User Canceled payment
    private void sendPaymentStatusRequest(String status){

        JSONObject data = new JSONObject();
        try {
            if (packagePurchaseInitResponse.getTxnId() == null)
                return;
            data.put("txnid", packagePurchaseInitResponse.getTxnId());
            data.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest paymentStatusRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.BASE_URL + ApiConfig.PAYMENT_FAILURE,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Payment status changed successfully
                        Toast.makeText(PackageDetailsActivity.this, "Payment Canceled", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyUtil.volleyErrorHandler(error, PackageDetailsActivity.this, true);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyUtil.getHeaders(sessionManager);
            }
        };
        AppController.getInstance().addToRequestQueue(paymentStatusRequest, TAG);
    }

    private void refreshApiToken(){

        JsonObjectRequest tokenRefreshRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.BASE_URL + ApiConfig.REFRESH_TOKEN_URI,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        AuthResponse authResponse = JSONConverterUtil.fromJSONObject(response, AuthResponse.class);
                        if (authResponse.isSuccess()){
                            sessionManager.loggedIn(authResponse.getToken(), authResponse.getIssuedAt(), authResponse.getExpire(), authResponse.getUserProfile());
                            Intent intent = new Intent(PackageDetailsActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyUtil.volleyErrorHandler(error, PackageDetailsActivity.this, true);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return VolleyUtil.getHeaders(sessionManager);
            }
        };
        AppController.getInstance().addToRequestQueue(tokenRefreshRequest, TAG);
    }

}
