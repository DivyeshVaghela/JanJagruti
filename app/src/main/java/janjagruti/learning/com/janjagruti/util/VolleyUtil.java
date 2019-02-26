package janjagruti.learning.com.janjagruti.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import janjagruti.learning.com.janjagruti.ErrorActivity;
import janjagruti.learning.com.janjagruti.LoginActivity;
import janjagruti.learning.com.janjagruti.R;
import janjagruti.learning.com.janjagruti.config.ApiConfig;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.config.SessionManager;
import janjagruti.learning.com.janjagruti.entity.support.ApiErrorResponse;

public class VolleyUtil {

    public static Map<String, String> getHeaders(SessionManager sessionManager){
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", ApiConfig.AUTH_TYPE + " " + sessionManager.getSharedPreferences().getString(SessionManager.KEY_API_TOKEN, null));
        return params;
    }

    public static String prepareQueryString(Map<String, String> queryParams){

        boolean isFirst = true;
        StringBuilder sb = new StringBuilder("?");
        for (Map.Entry<String,String> entry : queryParams.entrySet()){
            if (isFirst){
                isFirst=false;
            } else {
                sb.append("&");
            }
            sb.append(entry.getKey() + "=" + entry.getValue());
        }
        return sb.toString();
    }

    public static String volleyErrorHandler(VolleyError volleyError, final Activity activity, boolean takeActions, @Nullable Integer except, @Nullable Class exceptClass){

        boolean isActionTaken = false;

        if (volleyError.getMessage() != null)
            Log.d("PaymentInProgress", volleyError.getMessage());
        volleyError.printStackTrace();

        String errorMessage = "";

        if (volleyError instanceof NoConnectionError || volleyError instanceof TimeoutError){

            if (volleyError instanceof NoConnectionError){
                errorMessage = activity.getString(R.string.no_network);
            } else {
                errorMessage = activity.getString(R.string.server_not_reachable);
            }

            if (takeActions && (exceptClass == null || (exceptClass!= null && exceptClass != NoConnectionError.class && exceptClass != TimeoutError.class))) {
                Intent intent = new Intent(activity, ErrorActivity.class);
                intent.putExtra(ErrorActivity.EXTRA_ERROR_MESSAGE, errorMessage);
                intent.putExtra(ErrorActivity.EXTRA_REFRESH, true);
                intent.putExtra(ErrorActivity.EXTRA_REFERESH_ACTIVITY, activity.getClass());
                activity.startActivity(intent);
                activity.finish();

                isActionTaken = true;
            }
        } else if (volleyError instanceof AuthFailureError){
            errorMessage = AppController.getInstance().getString(R.string.invalid_credential);

            if (takeActions && (exceptClass == null || (exceptClass != null && exceptClass != AuthFailureError.class))){
                handleAuthenticationFailed(activity);
                /*AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(activity.getString(R.string.session_expired))
                        .setCancelable(false)
                        .setPositiveButton(activity.getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int id) {
                                dialogInterface.cancel();

                                Intent intent = new Intent(activity, LoginActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        });
                builder.create().show();*/

                isActionTaken = true;
            }
        }
        else if (volleyError.networkResponse != null && volleyError.networkResponse.data != null){

            switch (volleyError.networkResponse.statusCode){

                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    errorMessage = AppController.getInstance().getString(R.string.unauthorized);
                    isActionTaken = true;
                    break;

                case HttpURLConnection.HTTP_NOT_FOUND:
                    ApiErrorResponse apiErrorResponse = JSONConverterUtil.fromJSONObject(new String(volleyError.networkResponse.data), ApiErrorResponse.class);
                    errorMessage = apiErrorResponse.getMessage();
                    if (except == null || (except != null && except != HttpURLConnection.HTTP_NOT_FOUND))
                        handleNotFound(activity, errorMessage);
                    isActionTaken = true;
                    break;

                case HttpURLConnection.HTTP_BAD_REQUEST:
                    ApiErrorResponse badRequestResponse = JSONConverterUtil.fromJSONObject(new String(volleyError.networkResponse.data), ApiErrorResponse.class);
                    errorMessage = badRequestResponse.getMessage();
                    if (except == null || (except != null && except != HttpURLConnection.HTTP_BAD_REQUEST))
                        handleBadRequest(activity, errorMessage);
                    isActionTaken = true;
                    break;

            }

            if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                errorMessage = AppController.getInstance().getString(R.string.unauthorized);
            } else if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND){
                ApiErrorResponse apiErrorResponse = JSONConverterUtil.fromJSONObject(new String(volleyError.networkResponse.data), ApiErrorResponse.class);
                errorMessage = apiErrorResponse.getMessage();
                if (takeActions && (except == null || (except != null && except != HttpURLConnection.HTTP_NOT_FOUND))) {
                    handleNotFound(activity, errorMessage);
                }
                isActionTaken = true;
            }
        } else {
            errorMessage = "Something unexpected happened";
        }

        if (!isActionTaken) {
            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
        }

        return errorMessage;
    }

    public static String volleyErrorHandler(VolleyError volleyError, final Activity activity, boolean takeActions){

        return volleyErrorHandler(volleyError, activity, takeActions, null, null);

        /*boolean isActionTaken = false;

        volleyError.printStackTrace();
        Log.d("LoginAttempt", volleyError.getClass().getName());

        String errorMessage = "";

        if (volleyError instanceof NoConnectionError || volleyError instanceof TimeoutError){

            if (volleyError instanceof NoConnectionError){
                errorMessage = activity.getString(R.string.no_network);
            } else {
                errorMessage = activity.getString(R.string.server_not_reachable);
            }

            if (takeActions) {
                Intent intent = new Intent(activity, ErrorActivity.class);
                intent.putExtra(ErrorActivity.EXTRA_ERROR_MESSAGE, errorMessage);
                intent.putExtra(ErrorActivity.EXTRA_REFRESH, true);
                intent.putExtra(ErrorActivity.EXTRA_REFERESH_ACTIVITY, activity.getClass());
                activity.startActivity(intent);
                activity.finish();

                isActionTaken = true;
            }
        } else if (volleyError instanceof AuthFailureError){
            errorMessage = AppController.getInstance().getString(R.string.invalid_credential);

            if (takeActions){
                handleAuthenticationFailed(activity);
                *//*AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(activity.getString(R.string.session_expired))
                        .setCancelable(false)
                        .setPositiveButton(activity.getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int id) {
                                dialogInterface.cancel();

                                Intent intent = new Intent(activity, LoginActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        });
                builder.create().show();*//*

                isActionTaken = true;
            }
        }
        else if (volleyError.networkResponse != null && volleyError.networkResponse.data != null){

            switch (volleyError.networkResponse.statusCode){

                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    errorMessage = AppController.getInstance().getString(R.string.unauthorized);
                    isActionTaken = true;
                    break;

                case HttpURLConnection.HTTP_NOT_FOUND:
                    ApiErrorResponse apiErrorResponse = JSONConverterUtil.fromJSONObject(new String(volleyError.networkResponse.data), ApiErrorResponse.class);
                    errorMessage = apiErrorResponse.getMessage();
                    if (takeActions)
                        handleNotFound(activity, errorMessage);
                    isActionTaken = true;
                    break;

                case HttpURLConnection.HTTP_BAD_REQUEST:
                    ApiErrorResponse basRequestResponse = JSONConverterUtil.fromJSONObject(new String(volleyError.networkResponse.data), ApiErrorResponse.class);
                    errorMessage = basRequestResponse.getMessage();
                    handleBadRequest(activity, errorMessage);
                    isActionTaken = true;
                    break;

            }

            if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                errorMessage = AppController.getInstance().getString(R.string.unauthorized);
            } else if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND){
                ApiErrorResponse apiErrorResponse = JSONConverterUtil.fromJSONObject(new String(volleyError.networkResponse.data), ApiErrorResponse.class);
                errorMessage = apiErrorResponse.getMessage();
                if (takeActions) {
                    handleNotFound(activity, errorMessage);
                }
                isActionTaken = true;
            }
        } else {
            errorMessage = "Something unexpected happened";
        }

        if (!isActionTaken) {
            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
        }

        return errorMessage;*/
    }

    public static void handleBadRequest(final Activity activity, @Nullable String message){
        showErrorAlert(activity, message == null ? activity.getString(R.string.something_wrong) : message);
    }

    public static void handleAuthenticationFailed(final Activity activity){
        showErrorAlert(activity, activity.getString(R.string.session_expired),true, LoginActivity.class, false);
    }

    public static void handleNotFound(final Activity activity, @Nullable String message){
        showErrorAlert(activity, message != null ? message : activity.getString(R.string.not_found), false, LoginActivity.class, true);
    }

    public static void showErrorAlert(final Activity activity, String message){
        showErrorAlert(activity, message, false, null, false);
    }

    public static void showErrorAlert(final Activity activity, String message, final boolean doRedirect, @Nullable final Class redirectTo, final boolean doFinish){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        dialogInterface.cancel();

                        if (doRedirect){
                            Intent intent = new Intent(activity, redirectTo);
                            activity.startActivity(intent);
                            if (doFinish)
                                activity.finish();
                        }
                    }
                });
        builder.create().show();
    }

    public static String volleyErrorHandler(VolleyError volleyError, Activity activity){
        return volleyErrorHandler(volleyError, activity, true);
    }

}