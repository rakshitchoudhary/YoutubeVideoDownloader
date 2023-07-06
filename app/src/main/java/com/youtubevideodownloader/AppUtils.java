package com.youtubevideodownloader;


import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

/**
 * @author Vaibhav
 */
public class AppUtils {

    public static AlertDialog d;
    private ProgressDialog progressBar;

    public static boolean isEmpty(@NonNull EditText etText) {
        return (etText.getText().toString().trim().length() != 0)
                && etText.getText().toString() != null
                && !etText.getText().toString().equals("null");
    }

    // Method for checking if wifi is connected
    public static boolean isConnectedWifi(@NonNull Context context) {
        ConnectivityManager mManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mManager.getActiveNetworkInfo();
        return (info != null
                && info.isConnected()
                && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    // validate email address
    public static boolean isEmailValid(@NonNull EditText email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
    }

    public static String replaceString(String string) {
        return string.replaceAll("[^A-Za-z0-9 ]","");// removing all special character.
    }

    // Show internet not connected message to user
    public static void noInternetDialog(@NonNull Context ctx) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setMessage("No internet found");
        dialog.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // dialog.show();
        d = dialog.create();
        d.show();
    }

    // check for internet connectivity
    public static boolean checkInternetConnection(@NonNull Context ctx) {
        ConnectivityManager mManager =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mManager.getActiveNetworkInfo();
        return (mNetworkInfo != null) && (mNetworkInfo.isConnected());
    }

    // get current app version
    public static int getAppVersion(@NonNull Context context) {
        try {
            PackageInfo packageInfo =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    // get device token
    public static String getDeviceToken(@NonNull Context ctx) {
        String deviceId = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
        return deviceId;
    }

    // check if text entered is valid
    public static boolean valid(@Nullable String text) {
        return text != null && !text.trim().equals("");
    }

    // convert normal text to base64 string
    @Nullable
    public static String getBase64String(String pathToImage) {
        if (valid(pathToImage)) {
            try {
                Bitmap bm = BitmapFactory.decodeFile(pathToImage);

                // Bitmap scaledBitmap = scaleDown(bm, 200, true);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // btm
                // is
                // the
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                return encodedImage;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // check if email is valid
    public static final boolean isValidEmail(@Nullable CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    // check if mobile number is valid
    public static boolean isValidMobile(@NonNull String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 9 || phone.length() > 14) {
                // if(phone.length() != 10) {
                check = false;
                // txtPhone.setError("Not Valid Number");
            } else {
                check = android.util.Patterns.PHONE.matcher(phone).matches();
            }
        } else {
            check = false;
        }
        return check;
    }

    // check if mobile number is valid
    public static boolean isRegValidMobile(@NonNull String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 8 || phone.length() > 14) {
                // if(phone.length() != 10) {
                check = false;
                // txtPhone.setError("Not Valid Number");
            } else {
                check = android.util.Patterns.PHONE.matcher(phone).matches();
            }
        } else {
            check = false;
        }
        return check;
    }

} // AppUtils
