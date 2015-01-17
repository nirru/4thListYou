package com.oxilo.listyou.login;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.listyou.listener.LoginClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oxilo.applistyou.R;
import com.oxilo.listyou.constant.AppConstant;
import com.util.Util;

import org.apache.http.Header;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class Login extends Activity {

    Dialog customDialog;
    Button login, retreive_butoon;
    ImageView dilog_cross_button;
    ImageView facebook, twitter, google;
    TextView forgotPassword;
    EditText dialog_email_address, email, password;
    AsyncHttpClient client;
    ProgressDialog progressDialog;

    String listYou_first_name, listYou_last_name, listYou_email,
            listYou_userid;

    String SNSTYPE;

    ImageView back_buttton;
    SocialAuthAdapter adapter;
    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // launchLastActivity();
        setContentView(R.layout.activity_login);
        setActonBAr();
        init();
    }

    private void launchLastActivity() {
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }

    private void setActonBAr() {
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.header);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
        // getActionBar().setIcon(R.drawable.side_drawer);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(
                        android.R.color.transparent)));
    }

    private void init() {
        regid = getIntent().getStringExtra(AppConstant.GCMSENDER);
        Log.e("DEVICE ID", regid);
        adapter = new SocialAuthAdapter(new ResponseListener());
        client = new AsyncHttpClient();
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Sign in ListYou...");

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        login = (Button) findViewById(R.id.login);
        facebook = (ImageView) findViewById(R.id.facebook);
        twitter = (ImageView) findViewById(R.id.twitter);
        google = (ImageView) findViewById(R.id.google);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        back_buttton = (ImageView) findViewById(R.id.back_arrow);

        back_buttton.setOnClickListener(backlistener);
        login.setOnClickListener(loginListener);
        facebook.setOnClickListener(loginListener);
        twitter.setOnClickListener(loginListener);
        google.setOnClickListener(loginListener);
        forgotPassword.setOnClickListener(loginListener);

    }

    View.OnClickListener backlistener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            finish();
        }
    };

    LoginClickListener loginListener = new LoginClickListener() {

        @Override
        public void signInClick(View view) {
            // TODO Auto-generated method stub
            signInListYou();
        }

        @Override
        public void facebookClick(View view) {
            facebookAuthorization();
        }

        @Override
        public void twitterClick(View view) {
            // TODO Auto-generated method stub
            twitterAuthorization();
        }

        @Override
        public void googleClick(View view) {
            // TODO Auto-generated method stub
            googleAuthorization();
        }

        @Override
        public void forgotPasswordClick(View view) {
            // TODO Auto-generated method stub
            showCustomDialog();
        }

    };

    protected void signInListYou() {
        if (!checkEmail())
            return;

        if (!checkEmailPattern())
            return;

        if (!checkPassword())
            return;

        else {
            startConnection();
        }

    }

    private boolean checkEmail() {

        if (email.getText().toString().equals("")) {
            showOKAleart("Message", "Email field is Blank");
            return false;
        }
        return true;
    }

    public boolean checkEmailPattern() {
        Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9+._%-+]{1,100}"
                + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,10}" + "(" + "."
                + "[a-zA-Z0-9][a-zA-Z0-9-]{0,20}" + ")+");
        if (!EMAIL_PATTERN.matcher(email.getText().toString()).matches()) {
            showOKAleart("Message", "Please enter the correct email");
            return false;
        }
        return true;
    }

    private boolean checkPassword() {

        if (password.getText().toString().equals("")) {
            showOKAleart("Message", "Password field is Blank");
            return false;
        }
        return true;
    }

    private void startConnection() {
        com.listyou.main.ConnectionDetector cd = new com.listyou.main.ConnectionDetector(
                getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            progressDialog.show();
            postRequestToServer();
        } else {
            showOKAleart("Connectivity", "You are not connected to internet");
        }
    }

    private void postRequestToServer() {
        RequestParams params = new RequestParams();
        params.put(AppConstant.LOGIN_EMAIL, email.getText().toString().trim());
        params.put(AppConstant.LOGIN_PASSWORD, password.getText().toString()
                .trim());
        params.put(AppConstant.OS_TYPE, "android");
        params.put(AppConstant.DEVICE_ID, regid.trim());
        client.post(AppConstant.URL_LOGIN_URL, params, responseHandler);

    }

    AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            try {
                String response = String.valueOf(new String(bytes, "UTF-8"));
                Log.e("NAME AND SURNAME", "" + response);
                progressDialog.dismiss();
                getResponseMessageToShowInAlert(response);
            } catch (UnsupportedEncodingException e1) {

            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(Login.this, Login.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(Login.this, Login.this.getString(R.string.error_title), Login.this.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }
    };

    protected void getResponseMessageToShowInAlert(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject.getString(AppConstant.LOGIN_MESSAGE);
            if (message.trim().equals(AppConstant.LOGIN_SUCCESSFULL_MESSAGE)) {
                String id = jsonObject.getString(AppConstant.LOGIN_ID);
                LoginSuccessfull(message, id);
            } else {
                showOKAleart("Message", message);
                return;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void showOKAleart(String title, String message) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle("Message");
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void LoginSuccessfull(String message, String id) {
        if (message.trim().equals(AppConstant.LOGIN_SUCCESSFULL_MESSAGE)) {
            Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
            Intent i = new Intent(Login.this, LoginProfileActivity.class);
            i.putExtra(AppConstant.LOGIN_ID, id);
            startActivity(i);
            finish();
        } else {
            showOKAleart("Message", message);
            return;
        }

    }

    protected void showCustomDialog() {

        forgotPassword.setClickable(false);
        customDialog = new Dialog(Login.this);

        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.forgotpass);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();

        initDialogElement();
    }

    private void initDialogElement() {

        retreive_butoon = (Button) customDialog.findViewById(R.id.retreive_btn);
        dilog_cross_button = (ImageView) customDialog
                .findViewById(R.id.cross_button);
        dialog_email_address = (EditText) customDialog
                .findViewById(R.id.frgtpass_et);

        retreive_butoon.setOnClickListener(retreiveListener);
        dilog_cross_button.setOnClickListener(crossListener);

        customDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        customDialog.cancel();
                        forgotPassword.setClickable(true);
                    }
                });
    }

    View.OnClickListener crossListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.cancel();
            forgotPassword.setClickable(true);
        }
    };

    View.OnClickListener retreiveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            progressDialog.show();
            resetPassword();
        }
    };

    private void resetPassword() {
        RequestParams params = new RequestParams();
        params.put(AppConstant.FORGOT_EMAIL, dialog_email_address.getText()
                .toString().trim());
        client.post(AppConstant.URL_FORGOT_PASSWORD_URL, params,
                forgotresponseHandler);
    }

    AsyncHttpResponseHandler forgotresponseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            try {
                String response = String.valueOf(new String(bytes, "UTF-8"));
                progressDialog.dismiss();
                receiveForgotMessage(response);
            } catch (UnsupportedEncodingException e1) {

            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(Login.this, Login.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(Login.this, Login.this.getString(R.string.error_title), Login.this.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }
    };

    protected void receiveForgotMessage(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject.getString(AppConstant.FORGOT_MESSAGE);
            linkSentSuccesfuly(message);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void linkSentSuccesfuly(String message) {
        if (message.trim().equals(
                AppConstant.FORGOT_PASSWORD_MESSAGE)) {
            showForgotAleart("Message", AppConstant.FORGOT_PASSWORD_SUCCESSFULL_MESSAGE);
        } else {
            showOKAleart("Message", message);
            return;
        }

    }

    @SuppressWarnings("deprecation")
    public void showForgotAleart(String title, String message) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle("Message");
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                alertDialog.dismiss();
                customDialog.cancel();
            }
        });
        alertDialog.show();
    }

    /**
     * ------------------------------Facebook Twiiter Google Authorization Code
     * START-------------
     */
    private void facebookAuthorization() {
        SNSTYPE = "facebook";
        if (isstartConnection()) {
            adapter.authorize(Login.this, Provider.FACEBOOK);
        } else {
            showOKAleart("Connectivity", "You are not connected to internet");
        }
    }

    private void twitterAuthorization() {
        SNSTYPE = "twitter";
        if (isstartConnection()) {
            adapter.authorize(Login.this, Provider.TWITTER);
        } else {
            showOKAleart("Connectivity", "You are not connected to internet");
        }
    }

    private void googleAuthorization() {
        SNSTYPE = "google";
        if (isstartConnection()) {
            adapter.addCallBack(Provider.GOOGLEPLUS, "https://localhost/oauth2callback");
            adapter.authorize(Login.this, Provider.GOOGLEPLUS);
        } else {
            showOKAleart("Connectivity", "You are not connected to internet");
        }
    }

    /**
     * ------------------------------Facebook Twiiter Google Authorization Code
     * END-------------
     */

    private boolean isstartConnection() {
        com.listyou.main.ConnectionDetector cd = new com.listyou.main.ConnectionDetector(
                getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent)
            return true;
        else
            return false;
    }

    /**
     * ---------------------------------------SocialAuth Handler
     * Code--------------------------------------------- We Receive the facebook
     * twitter google Response and get the user detail fiels
     */

    private final class ResponseListener implements DialogListener {
        public void onComplete(Bundle values) {
            Log.e("I GET THE RESPONSE", "YES");
            progressDialog.cancel();
            adapter.getUserProfileAsync(new ProfileDataListener());
        }

        @Override
        public void onBack() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(SocialAuthError arg0) {
            // TODO Auto-generated method stub

        }
    }

    /**
     * ---------------To Receive Profile Response after
     * Authencation-----------------------
     * ----------------------------------------------
     */
    private final class ProfileDataListener implements
            SocialAuthListener<Profile> {

        @Override
        public void onError(SocialAuthError arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onExecute(String arg0, Profile t) {
            // TODO Auto-generated method stub
            Log.e("Custom-UI", "Receiving Data");
            Profile profileMap = t;
            Log.e("Custom-UI",
                    "Validate ID         = " + profileMap.getValidatedId());
            Log.e("Custom-UI",
                    "First Name          = " + profileMap.getDisplayName());
            // Log.e("Custom-UI",
            // "Last Name           = " + profileMap.getLastName());
            // Log.e("Custom-UI", "Email               = " +
            // profileMap.getEmail());
            // Log.e("Custom-UI",
            // "Gender                   = " + profileMap.getGender());
            // Log.e("Custom-UI",
            // "Country                  = " + profileMap.getCountry());
            // Log.e("Custom-UI",
            // "Language                 = " + profileMap.getLanguage());
            // Log.e("Custom-UI",
            // "Location                 = " + profileMap.getLocation());
            // Log.e("Custom-UI",
            // "Profile Image URL  = " + profileMap.getProfileImageURL());

            listYou_userid = profileMap.getValidatedId();

            if (SNSTYPE.equals("twitter")) {
                listYou_first_name = profileMap.getDisplayName();
                listYou_last_name = "";
                listYou_email = "";
            } else {
                listYou_first_name = profileMap.getFirstName().trim();
                listYou_last_name = profileMap.getLastName().trim();
                listYou_email = profileMap.getEmail().trim();
            }
            saveSNSDetailTOServer();
        }
    }

    private void saveSNSDetailTOServer() {
        progressDialog.show();
        RequestParams params = new RequestParams();
        params.put(AppConstant.REG_LOGIN_TYPE, SNSTYPE);
        params.put(AppConstant.REG_FIRST_NAME, listYou_first_name);
        params.put(AppConstant.REG_LAST_NAME, listYou_last_name);
        params.put(AppConstant.REG_EMAIL, listYou_email);
        params.put(AppConstant.REG_SNS_TOKEN, listYou_userid);
        client.post(AppConstant.SNS_REGISTRATION_URL, params,
                snsresponseHandler);
    }

    AsyncHttpResponseHandler snsresponseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            try {
                String response = String.valueOf(new String(bytes, "UTF-8"));
                progressDialog.dismiss();
                isAlreadyRegisteredOrNot(response);
            } catch (UnsupportedEncodingException e1) {

            }

        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(Login.this, Login.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(Login.this, Login.this.getString(R.string.error_title), Login.this.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }
    };

    private void isAlreadyRegisteredOrNot(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject.getString(AppConstant.REG_MESSAGE);
            Log.e("YEYYYYYYYYYYYY", "" + message);
            if (message.trim().equals(AppConstant.REG_SNS_SUCCESSFULL_MESSAGE)
                    || message.trim().equals(AppConstant.REG_SNS_ALREADY_EXIST)) {
                Log.e("ELSEEEEEEE", "" + "IF");
                String id = jsonObject.getString(AppConstant.REG_ID);
                showOKAleartOnSuccesFull("Message", message, id,
                        "SNS_REGISTRATION");
            } else {
                Log.e("IFIFIFIFIF", "" + "IF");
                showOKAleart("Message", message);
                return;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void showOKAleartOnSuccesFull(String title, final String message,
                                         final String userid, String reg_Type) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle("Message");
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                alertDialog.dismiss();
                Log.e("USER IDDDDDDDDNADQADNAD", userid);
                goToEditProfileScreen(userid);
            }
        });
        alertDialog.show();
    }

    private void goToEditProfileScreen(String userID) {
        Intent i = new Intent(Login.this, LoginProfileActivity.class);
        i.putExtra(AppConstant.REG_FIRST_NAME, "" + listYou_first_name);
        i.putExtra(AppConstant.REG_LAST_NAME, "" + listYou_last_name);
        i.putExtra(AppConstant.REG_EMAIL, "" + listYou_email);
        i.putExtra(AppConstant.REG_ID, "" + userID);
        startActivity(i);
        finish();
    }

}
