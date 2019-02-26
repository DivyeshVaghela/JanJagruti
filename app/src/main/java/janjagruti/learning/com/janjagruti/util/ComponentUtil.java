package janjagruti.learning.com.janjagruti.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import janjagruti.learning.com.janjagruti.R;

public class ComponentUtil {

    public static Dialog getProgressDialog(Context context, @Nullable String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.progress, null);

        if (message != null) {
            TextView txtview_progressMessage = view.findViewById(R.id.txtview_progressMessage);
            txtview_progressMessage.setText(message);
        }

        builder
                .setView(view)
                .setCancelable(false);
        return builder.create();
    }

    public static void showOkayAlertDialog(Context context, @Nullable String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
    }
}
