package com.oxilo.applistyou;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.landing.screen.MainLandingActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.response.User;
import com.util.Util;

public class StartUpActivity extends Activity {

    int pref_screen_id;
    SharedPreferences sharedPreferences;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = this.getSharedPreferences("com.oxilo.listyou_app_country_code",
                SplashActivity.MODE_PRIVATE);
        goToScreen();
    }

    private void goToScreen() {
        Intent intent = new Intent();
        intent.setClass(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private void decideScreen() {
        pref_screen_id = Util.getScreennavigation(StartUpActivity.this);
        id = sharedPreferences.getString(AppConstant.REG_ID, "");
        if (pref_screen_id == 2) {
            User user = Util.saveParcel(Util.readFromPrefs(StartUpActivity.this), id);
            Intent i = new Intent(StartUpActivity.this, MainLandingActivity.class);
            i.putExtra(AppConstant.USER, user);
            startActivity(i);
            finish();
        } else {
            goToScreen();
        }
    }


}