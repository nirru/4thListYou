package com.oxilo.listyou.register;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.listyou.listener.RegisterClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
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

public class RegisterActivity extends Activity {

	Button create_account;
	EditText firstName, lastName, email, confirmEmail, password;
	AsyncHttpClient client;
	ProgressDialog progressDialog;
	ImageView back_buttton;
	SocialAuthAdapter adapter;
	ImageView facebook_button, twitter_button, google_button;
	String listYou_first_name, listYou_last_name, listYou_email,
			listYou_userid;
	String SNSTYPE;
	RelativeLayout relativeWeb;
    String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// launchLastActivity();
		setContentView(R.layout.activity_register);
		setActonBAr();
		init();
		setCustomFont();
	}

	private void setActonBAr() {
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.header);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));
	}

	private void setCustomFont() {
		firstName.setTypeface(SplashActivity.mpRegular);
		lastName.setTypeface(SplashActivity.mpRegular);
		email.setTypeface(SplashActivity.mpRegular);
		confirmEmail.setTypeface(SplashActivity.mpRegular);
		password.setTypeface(SplashActivity.mpRegular);
	}

	private void init() {
        regid = getIntent().getStringExtra(AppConstant.GCMSENDER);
        Log.e("<<<DEVICE ID in REGISTRATION>>>" , regid);

		adapter = new SocialAuthAdapter(new ResponseListener());
		progressDialog = new ProgressDialog(RegisterActivity.this);
		progressDialog.setMessage("Registering with ListYou...");
		client = new AsyncHttpClient();

		firstName = (EditText) findViewById(R.id.first_name);
		lastName = (EditText) findViewById(R.id.last_name);
		email = (EditText) findViewById(R.id.email);
		confirmEmail = (EditText) findViewById(R.id.confirm_email);
		password = (EditText) findViewById(R.id.password);
		relativeWeb = (RelativeLayout) findViewById(R.id.relative_web);

		facebook_button = (ImageView) findViewById(R.id.facebook);
		twitter_button = (ImageView) findViewById(R.id.twitter);
		google_button = (ImageView) findViewById(R.id.google);

		back_buttton = (ImageView) findViewById(R.id.back_arrow);
		create_account = (Button) findViewById(R.id.create_account);

		create_account.setOnClickListener(registerClickListener);
		back_buttton.setOnClickListener(registerClickListener);
		facebook_button.setOnClickListener(registerClickListener);
		twitter_button.setOnClickListener(registerClickListener);
		google_button.setOnClickListener(registerClickListener);
		relativeWeb.setOnClickListener(registerClickListener);
	}

	private boolean checkFirstName() {

		if (firstName.getText().toString().equals("")) {
			showOKAleart("Message", "First Name field is Blank");
			return false;
		}
		return true;
	}

	private boolean checkLastName() {

		if (lastName.getText().toString().equals("")) {
			showOKAleart("Message", "Surname Name field is Blank");
			return false;
		}
		return true;
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

	private boolean checkConfirmEmail() {

		if (confirmEmail.getText().toString().equals("")) {
			showOKAleart("Message", "Confirm Email field is Blank");
			return false;
		}
		return true;
	}

	public boolean checkConfirmEmailPattern() {
		if (!confirmEmail.getText().toString()
				.equals(confirmEmail.getText().toString())) {
			showOKAleart("Message",
					"Email And Confirm Email Field Are Not Same");
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

	RegisterClickListener registerClickListener = new RegisterClickListener() {

		@Override
		public void createAccountBtnClick(View view) {
			// TODO Auto-generated method stub
			createAnAccount();
		}

		@Override
		public void facebbokBtnClick(View view) {
			// TODO Auto-generated method stub
			facebookAuthorization();
		}

		@Override
		public void twitterBtnClick(View view) {
			// TODO Auto-generated method stub
			twitterAuthorization();
		}

		@Override
		public void googleBtnClick(View view) {
			// TODO Auto-generated method stub
			googleAuthorization();
		}

		@Override
		public void backBtnClick(View view) {
			// TODO Auto-generated method stub
			finish();
		}

		@Override
		public void onrelativeWeb(View view) {
			// TODO Auto-generated method stub
			startActivity(new Intent(RegisterActivity.this, TermServices.class));
			overridePendingTransition(R.anim.animation_enter,
					R.anim.animation_leave);
		}
	};

	protected void createAnAccount() {

		if (!checkFirstName())
			return;

		if (!checkLastName())
			return;

		if (!checkEmail())
			return;

		if (!checkEmailPattern())
			return;

		if (!checkConfirmEmail())
			return;

		if (!checkPassword())
			return;

		else {
			listYou_first_name = firstName.getText().toString().trim();
			listYou_last_name = lastName.getText().toString().trim();
			listYou_email = email.getText().toString().trim();
			Log.e("FIRST NAME", "" + listYou_first_name);
			Log.e("LAST NAME", "" + listYou_last_name);
			Log.e("EMAIL NAME", "" + listYou_email);
			savingListYouRegistrationDetails();
		}
	}

	private void savingListYouRegistrationDetails() {
		if (isstartConnection()) {
			progressDialog.show();
			postRequestToServer();
		} else {
			showOKAleart("Connectivity", "You are not connected to internet");
		}

	}

	/**
	 * ------------------------------Facebook Twiiter Google Authorization Code
	 * START-------------
	 **/
	private void facebookAuthorization() {
		SNSTYPE = "facebook";
		if (isstartConnection()) {
			adapter.authorize(RegisterActivity.this, Provider.FACEBOOK);
		} else {
			showOKAleart("Connectivity", "You are not connected to internet");
		}
	}

	private void twitterAuthorization() {
		SNSTYPE = "twitter";
		if (isstartConnection()) {
			adapter.authorize(RegisterActivity.this, Provider.TWITTER);
		} else {
			showOKAleart("Connectivity", "You are not connected to internet");
		}
	}

	private void googleAuthorization() {
		SNSTYPE = "google";
		if (isstartConnection()) {
			adapter.addCallBack(Provider.GOOGLEPLUS, "https://localhost/oauth2callback");
			adapter.authorize(RegisterActivity.this, Provider.GOOGLEPLUS);
		} else {
			showOKAleart("Connectivity", "You are not connected to internet");
		}
	}

	/**
	 * ------------------------------Facebook Twiiter Google Authorization Code
	 * END-------------
	 **/

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

	private void postRequestToServer() {
		RequestParams params = new RequestParams();
		params.put(AppConstant.REG_LOGIN_TYPE, AppConstant.REG_TYPE);
		params.put(AppConstant.REG_FIRST_NAME, listYou_first_name);
		params.put(AppConstant.REG_LAST_NAME, listYou_last_name);
		params.put(AppConstant.REG_EMAIL, listYou_email);
		params.put(AppConstant.REG_PASSWORD, password.getText().toString()
				.trim());
		client.post(AppConstant.URL_REGISTRATION_URL, params, responseHandler);
	}

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            try {
                String response = String.valueOf(new String(bytes, "UTF-8"));
                progressDialog.dismiss();
                getResponseMessageToShowInAlert(response);
            } catch (UnsupportedEncodingException e1) {

            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(RegisterActivity.this, RegisterActivity.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(RegisterActivity.this, RegisterActivity.this.getString(R.string.error_title), RegisterActivity.this.getString(R.string.registration_response_error));
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
			String message = jsonObject.getString(AppConstant.REG_MESSAGE);

			Log.e("RESPONSE  ", "" + message);
			if (!message.trim().equals(AppConstant.REG_SUCCESSFULL_MESSAGE)) {
				showOKAleart("Message", message);
				Log.e("I AM IN IF", "" + message);
				return;
			} else {
				Log.e("I AM IN else", "" + message);
				String id = jsonObject.getString(AppConstant.REG_ID);
				showOKAleartOnSuccesFull("Message", message, id,
						"LISTYOU_REGISTARION");
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
		if (reg_Type.equals("LISTYOU_REGISTARION")) {
			alertDialog.setMessage(getResources().getString(R.string.reg_message));
		} else {
			alertDialog.setMessage(message);
		}
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				Log.e("tgrtrtr AYA", message);
				goToEditProfileScreen(userid);
			}
		});
		alertDialog.show();
	}

	/**
	 * ---------------------------------------SocialAuth Handler
	 * Code--------------------------------------------- We Receive the facebook
	 * twitter google Response and get the user detail fiels
	 **/

	private final class ResponseListener implements DialogListener {
		public void onComplete(Bundle values) {
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
	 **/
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
			// Log.e("Custom-UI",
			// "Validate ID         = " + profileMap.getValidatedId());
			// Log.e("Custom-UI",
			// "First Name          = " + profileMap.getFirstName());
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
                Util.showOKAleart(RegisterActivity.this, RegisterActivity.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(RegisterActivity.this, RegisterActivity.this.getString(R.string.error_title), RegisterActivity.this.getString(R.string.registration_response_error));
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
			if (!message.trim().equals(AppConstant.REG_SNS_SUCCESSFULL_MESSAGE)) {
				showOKAleart("Message", message);
				return;
			} else {
				String id = jsonObject.getString(AppConstant.REG_ID);
				showOKAleartOnSuccesFull("Message", message, id,
						"SNS_REGISTRATION");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void goToEditProfileScreen(String userID) {
		Intent i = new Intent(RegisterActivity.this, EditProfileActivity.class);
		i.putExtra(AppConstant.REG_FIRST_NAME, "" + listYou_first_name);
		i.putExtra(AppConstant.REG_LAST_NAME, "" + listYou_last_name);
		i.putExtra(AppConstant.REG_EMAIL, "" + listYou_email);
		i.putExtra(AppConstant.REG_ID, "" + userID);
        i.putExtra(AppConstant.GCMSENDER, regid);
		startActivity(i);
		finish();
	}

	
}
