package com.oxilo.applistyou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.landing.screen.MainLandingActivity;
import com.listyou.listener.SplashClickListener;
import com.oxilo.listyou.constant.AppConstant;
import com.oxilo.listyou.login.Login;
import com.oxilo.listyou.register.RegisterActivity;
import com.oxilo.showprofile.ShowProfileActivity;
import com.response.User;
import com.util.Util;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class SplashActivity extends BaseSampleActivity {

    Button register_btn, login_btn;
    int deviceHeight = 0, deviceWidth = 0;

    SharedPreferences  sharedPreferences;
    public static Typeface mpRegular, mpBold;
    String id;
    int pref_screen_id;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "471885991287";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        init();
        decideScreen();
    }

    private void init() {
        getActionBar().hide();
        context = getApplicationContext();
        mpRegular = Typeface.createFromAsset(getAssets(),
                "fonts/GOTHIC.TTF");
        mpBold = Typeface.createFromAsset(getAssets(),
                "fonts/GOTHICB.TTF");
        WindowManager wMgr = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        deviceHeight = wMgr.getDefaultDisplay().getHeight() - 25;
        deviceWidth = wMgr.getDefaultDisplay().getWidth();

        sharedPreferences = this.getSharedPreferences("com.oxilo.listyou_app_country_code",
                SplashActivity.MODE_PRIVATE);

        id = sharedPreferences.getString(AppConstant.REG_ID, "");
        pref_screen_id = Util.getScreennavigation(SplashActivity.this);
        Log.e("ID IS BLANK OR NOT", "" + pref_screen_id);
        register_btn = (Button) findViewById(R.id.splash_register_btn);
        login_btn = (Button) findViewById(R.id.splash_login_btn);

        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        ((CirclePageIndicator) mIndicator).setFillColor(getResources()
                .getColor(R.color.list_you_register_background));
        ((CirclePageIndicator) mIndicator).setPageColor(Color.WHITE);
        ((CirclePageIndicator) mIndicator)
                .setRadius((int) (.02f * deviceWidth));
        mIndicator.setViewPager(mPager);
        mIndicator.setCurrentItem(0);

        register_btn.setOnClickListener(splashListener);
        login_btn.setOnClickListener(splashListener);
    }


    SplashClickListener splashListener = new SplashClickListener() {

        @Override
        public void registerBtnClick(View view) {
            // TODO Auto-generated method stub
            isRegisteredOnGCM(2);
//			finish();
        }

        @Override
        public void loginBtnClk(View view) {
            // TODO Auto-generated method stub
            isRegisteredOnGCM(1);
        }
    };

    private void register() {
        Intent i = new Intent(SplashActivity.this, RegisterActivity.class);
        i.putExtra(AppConstant.GCMSENDER, regid);
        startActivity(i);
        overridePendingTransition(R.anim.animation_enter,
                R.anim.animation_leave);
    }

    private void login() {
        if (pref_screen_id == 0) {
            Log.e("Value OF REG ID", regid);
            Intent i = new Intent(SplashActivity.this, Login.class);
            i.putExtra(AppConstant.GCMSENDER, regid);
            startActivity(i);
            overridePendingTransition(R.anim.animation_enter,
                    R.anim.animation_leave);

        } else if (pref_screen_id == 1) {
            putValueInPrefs();
            User user = Util.saveParcel(Util.readFromPrefs(SplashActivity.this), id);
            Intent i = new Intent(SplashActivity.this,
                    ShowProfileActivity.class);
            i.putExtra(AppConstant.USER, user);
            startActivity(i);
            overridePendingTransition(R.anim.animation_enter,
                    R.anim.animation_leave);
        } else if (pref_screen_id == 2) {
            User user = Util.saveParcel(Util.readFromPrefs(SplashActivity.this), id);
            Intent i = new Intent(SplashActivity.this, MainLandingActivity.class);
            i.putExtra(AppConstant.USER, user);
            startActivity(i);
            finish();
        }
    }

    private void decideScreen() {
        if (pref_screen_id == 2) {
            User user = Util.saveParcel(Util.readFromPrefs(SplashActivity.this), id);
            Intent i = new Intent(SplashActivity.this, MainLandingActivity.class);
            i.putExtra(AppConstant.USER, user);
            startActivity(i);
            finish();
        } else {
            setContentView(R.layout.activity_splash);
            init();
        }
    }


    private void putValueInPrefs() {
        SharedPreferences.Editor prefs = SplashActivity.this
                .getSharedPreferences("com.oxilo.listyou_app_country_code",
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.REG_ID, id);
        prefs.commit();
    }

    private void isRegisteredOnGCM(int k) {
//        if (checkPlayServices()) {
        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);
        Log.e("REGISTER ID IN BACKGROUND", " " + regid);
        if (regid.isEmpty()) {
            registerInBackground(k);
        } else {
            if (k == 1)
                login();
            else
                register();
        }
//        }
//    else {
//            Log.i(TAG, "No valid Google Play Services APK found.");
//        }
    }

    /** Push Notification Code **/

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(
                SplashActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */

    private void registerInBackground(final int k) {

        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... params) {
                // TODO Auto-generated method stub
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    Log.e("ID RECEIVED IN BACK GROUND", "" + msg);
                    // You should send the registration ID to your server over
                    // HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your
                    // app.
                    // The request to your server should be authenticated if
                    // your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the
                    // device
                    // will send upstream messages to a server that echo back
                    // the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (k == 1)
                    login();
                else
                    register();
            }

        }.execute(null, null, null);

    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use
     * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
     * since the device sends upstream messages to a server that echoes back the
     * message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

}