package com.runtracker.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import java.util.Map;

/**
 * Helper methods for creating dialogs easily
 */

public class HelperMethods {

    public void createOkayAlert(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Ok",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void createYesNoAlert(String message,
                                 Context context,
                                 DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Yes", onClickListener)
                .setNegativeButton("No", onClickListener)
                .show();
    }

    public Map<String, Claim> verifyJwtAndGetClaims(String token) {
        JWT jwt = new JWT(token);
        return jwt.getClaims();
    }
}
