package com.oxilo.showprofile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.landing.screen.MainLandingActivity;
import com.listyou.listener.ShowProfileClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.response.User;
import com.util.Util;

import org.apache.http.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ShowProfileActivity extends Activity {

    TextView user_profile_name, user_name_in_other_lang, designation,
            companyName, email, website, address, city, countrty, mobile,
            telephone, fax, skype, userEmail, userWebsite, userAddress,
            usercity, usercountry, usermobile, usertelephone, userfax,
            userskype, usercompany;


    String id;

    Dialog customDialog;
    AsyncHttpClient client;
    ProgressDialog progressDialog;
    ImageView user_proile_pic, user_company_logo, share_button,
            myBussinessCardButton, user_QR_Image, start_button;
    ImageView back_buttton;
    Button retreive_butoon;
    ImageView dilog_cross_button;

    DisplayImageOptions options;
    public ImageLoader imageLoader;
    AnimateFirstDisplayListener animateFirstDisplayListener;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        setActonBAr();
        init();
        setCustomFont();
        getThumbnail("qrImagePic.png", user_QR_Image);
        if (Util.getScreennavigation(ShowProfileActivity.this) == 1) {
            Log.e("Util.getScreennavigation TRUE" , "" + Util.getScreennavigation(ShowProfileActivity.this));
            user = getIntent().getParcelableExtra(AppConstant.USER);
            setDetails();
        } else {
            Log.e("Util.getScreennavigation FALSE" , "" + Util.getScreennavigation(ShowProfileActivity.this));
            fetchDataDromServer();
        }
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

    private void init() {
        animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader = ImageLoader.getInstance();
        ShowProfileActivity.this.imageLoader.init(ImageLoaderConfiguration
                .createDefault(getBaseContext()));
        options = new DisplayImageOptions.Builder().showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(null).cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(0)).build();
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "com.oxilo.listyou_app_country_code",
                ShowProfileActivity.MODE_PRIVATE);
        id = sharedPreferences.getString(AppConstant.REG_ID, "");
        client = new AsyncHttpClient();
        progressDialog = new ProgressDialog(ShowProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);

        myBussinessCardButton = (ImageView) findViewById(R.id.original_buss_card_button);
        start_button = (ImageView) findViewById(R.id.btn_start);
        user_company_logo = (ImageView) findViewById(R.id.comapny_logo);
        user_proile_pic = (ImageView) findViewById(R.id.user_profile_pic);
        user_profile_name = (TextView) findViewById(R.id.profile_name);
        user_QR_Image = (ImageView) findViewById(R.id.imageView1);
        user_name_in_other_lang = (TextView) findViewById(R.id.name_in_another_language);
        designation = (TextView) findViewById(R.id.designation);
        companyName = (TextView) findViewById(R.id.comapany_name);
        email = (TextView) findViewById(R.id.show_user_profile_email);
        website = (TextView) findViewById(R.id.show_user_profile_website);
        address = (TextView) findViewById(R.id.show_user_profile_address);
        city = (TextView) findViewById(R.id.show_user_profile_city);
        countrty = (TextView) findViewById(R.id.show_user_profile_country);
        mobile = (TextView) findViewById(R.id.show_user_profile_mobile);
        telephone = (TextView) findViewById(R.id.show_user_profile_telephone);
        fax = (TextView) findViewById(R.id.show_user_profile_fax);
        skype = (TextView) findViewById(R.id.show_user_profile_skype);
        usercompany = (TextView) findViewById(R.id.comapany_name_id);

        share_button = (ImageView) findViewById(R.id.share_button);
        userEmail = (TextView) findViewById(R.id.show_user_profile_email_id);
        userWebsite = (TextView) findViewById(R.id.show_user_profile_website_id);
        userAddress = (TextView) findViewById(R.id.show_user_profile_address_id);
        usercity = (TextView) findViewById(R.id.show_user_profile_city_id);
        usercountry = (TextView) findViewById(R.id.show_user_profile_country_id);
        usermobile = (TextView) findViewById(R.id.show_user_profile_mobile_id);
        usertelephone = (TextView) findViewById(R.id.show_user_profile_telephone_id);
        userfax = (TextView) findViewById(R.id.show_user_profile_fax_id);
        userskype = (TextView) findViewById(R.id.show_user_profile_skype_id);

        back_buttton = (ImageView) findViewById(R.id.back_arrow);
        back_buttton.setOnClickListener(showProfileClickListener);
        share_button.setOnClickListener(showProfileClickListener);
        myBussinessCardButton.setOnClickListener(showProfileClickListener);
        start_button.setOnClickListener(showProfileClickListener);

        user_proile_pic.setImageDrawable(null);
        user_company_logo.setImageDrawable(null);
        user_company_logo.setVisibility(View.INVISIBLE);
    }

    ShowProfileClickListener showProfileClickListener = new ShowProfileClickListener() {

        @Override
        public void startBtnClick(View view) {
            // TODO Auto-generated method stub
            congratsPopUp();
        }

        @Override
        public void shareBtnClick(View view) {
            // TODO Auto-generated method stub
            share();
        }

        @Override
        public void bussinessCardClick(View view) {
            // TODO Auto-generated method stub
            showMyCard();
        }

        @Override
        public void backBtnClick(View view) {
            // TODO Auto-generated method stub
            finish();
        }
    };

    private void showMyCard() {
        Intent i = new Intent(ShowProfileActivity.this,
                BussinessCardScreen.class);
        i.putExtra(AppConstant.USER, user);
        i.putExtra(AppConstant.EDIT_PROFILE_BUSSINESS_LOGO_PIC, id);
        startActivity(i);
//		finish();
    }

    private void congratsPopUp() {

        customDialog = new Dialog(ShowProfileActivity.this);

        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.lets_start_popup);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();

        initDialogElement();
    }

    private void initDialogElement() {

        TextView congratulation = (TextView) customDialog
                .findViewById(R.id.forgot);
        TextView label_one = (TextView) customDialog
                .findViewById(R.id.lets_start_label_one);
        TextView label_two = (TextView) customDialog
                .findViewById(R.id.lets_start_label_two);
        TextView label_three = (TextView) customDialog
                .findViewById(R.id.lets_start_label_three);
        congratulation.setTypeface(SplashActivity.mpBold);
        label_one.setTypeface(SplashActivity.mpRegular);
        label_two.setTypeface(SplashActivity.mpRegular);
        label_three.setTypeface(SplashActivity.mpRegular);

        retreive_butoon = (Button) customDialog.findViewById(R.id.retreive_btn);
        dilog_cross_button = (ImageView) customDialog
                .findViewById(R.id.cross_button);
        retreive_butoon.setOnClickListener(retreiveListener);
        dilog_cross_button.setOnClickListener(crossListener);

        customDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        customDialog.cancel();
                    }
                });
    }

    View.OnClickListener crossListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.cancel();
        }
    };

    View.OnClickListener retreiveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.cancel();
            Util.setScreenNavigation(ShowProfileActivity.this, 2);
            Intent i = new Intent(ShowProfileActivity.this, MainLandingActivity.class);
            i.putExtra(AppConstant.USER, user);
            startActivity(i);

        }
    };

    private void setCustomFont() {

        user_profile_name.setTypeface(SplashActivity.mpBold);
        user_name_in_other_lang.setTypeface(SplashActivity.mpRegular);
        designation.setTypeface(SplashActivity.mpRegular);
        companyName.setTypeface(SplashActivity.mpRegular);
        email.setTypeface(SplashActivity.mpBold);
        website.setTypeface(SplashActivity.mpBold);
        address.setTypeface(SplashActivity.mpBold);
        city.setTypeface(SplashActivity.mpBold);
        countrty.setTypeface(SplashActivity.mpBold);
        mobile.setTypeface(SplashActivity.mpBold);
        telephone.setTypeface(SplashActivity.mpBold);
        fax.setTypeface(SplashActivity.mpBold);
        skype.setTypeface(SplashActivity.mpBold);
        usercompany.setTypeface(SplashActivity.mpBold);

        userEmail.setTypeface(SplashActivity.mpRegular);
        userWebsite.setTypeface(SplashActivity.mpRegular);
        userAddress.setTypeface(SplashActivity.mpRegular);
        usercity.setTypeface(SplashActivity.mpRegular);
        usercountry.setTypeface(SplashActivity.mpRegular);
        usermobile.setTypeface(SplashActivity.mpRegular);
        usertelephone.setTypeface(SplashActivity.mpRegular);
        userfax.setTypeface(SplashActivity.mpRegular);
        userskype.setTypeface(SplashActivity.mpRegular);
    }

    private void fetchDataDromServer() {
        progressDialog.show();
        RequestParams params = new RequestParams();
        params.put(AppConstant.EDIT_PROFILE_LOGIN_ID, id);
        client.post(AppConstant.URL_SHOW_PROFILE_URL, params, responseHandler);
    }

    AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            try {
                String response = String.valueOf(new String(bytes, "UTF-8"));
                setUserDetailToField(response);
            } catch (UnsupportedEncodingException e1) {

            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(ShowProfileActivity.this, ShowProfileActivity.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(ShowProfileActivity.this, ShowProfileActivity.this.getString(R.string.error_title), ShowProfileActivity.this.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }
    };


    public void setUserDetailToField(String response) {
        Util.setScreenNavigation(ShowProfileActivity.this, 1);
        Util.writeToPrefrefs(ShowProfileActivity.this, response);
        user = Util.saveParcel(response, id);
        setDetails();
    }

    private void setProfilePic() {
        imageLoader.displayImage(user.qrcodeUrl, user_QR_Image,
                options, animateFirstDisplayListener);
        if (user.userPicUrl.equals("")
                || user.userPicUrl == null
                || user.userPicUrl == "null") {
            user_proile_pic.setVisibility(View.INVISIBLE);
        } else {
            imageLoader.displayImage(user.userPicUrl, user_proile_pic,
                    options, animateFirstDisplayListener);
        }
    }

    private void setCompanyPic() {
        if (user.companyPicUrl.equals("") || user.companyPicUrl == null
                || user.companyPicUrl == "null") {
            user_company_logo.setVisibility(View.INVISIBLE);
        } else {
            imageLoader.displayImage(user.companyPicUrl, user_company_logo,
                    options, animateFirstDisplayListener);
        }
    }

    private void setName() {
        user_profile_name.setText(user.firstName + " " + user.lastName);
    }

    private void setNameInOtherLang() {
        if (user.firstNameInOtherLang.equals("") || user.firstNameInOtherLang == null) {
            user_name_in_other_lang.setVisibility(View.INVISIBLE);
        } else if (user.lastNameInOtherlang.equals("") || user.lastNameInOtherlang == null) {
            user_name_in_other_lang.setVisibility(View.INVISIBLE);
        } else {
            user_name_in_other_lang.setText("(" + user.firstNameInOtherLang + " " + user.lastNameInOtherlang
                    + ")");
        }
    }

    private void setDesignation() {
        if (user.designation.equals("") || user.designation == null)
            designation.setHint(getResources().getString(R.string.hint_title));
        else
            designation.setText(user.designation);
    }

    private void setCompanyName() {
        if (user.comapnyName.equals("") || user.comapnyName == null)
            companyName.setHint(getResources().getString(R.string.hint_company_name));
        else
            companyName.setText(user.comapnyName);
    }

    private void setCompanyEmailAddress() {
        if (user.companyEmailAddress.equals("") || user.companyEmailAddress == null)
            userEmail.setHint(getResources().getString(R.string.hint_company_email));
        else
            userEmail.setText(user.companyEmailAddress);
    }

    private void setUserWebsite() {
        if (user.website.equals("") || user.website == null)
            userWebsite.setHint(getResources().getString(R.string.hint_website));
        else
            userWebsite.setText(user.website);
    }

    private void setUserAddress() {
        if (user.address.equals("") || user.address == null)
            userAddress.setHint(getResources().getString(R.string.hint_address));
        else
            userAddress.setText(user.address);
    }

    private void setUserCity() {
        if (user.city.equals("") || user.city == null)
            usercity.setHint(getResources().getString(R.string.hint_city));
        else
            usercity.setText(user.city);
    }

    private void setUserCountry() {
        if (user.country.equals("") || user.country == null)
            usercountry.setHint(getResources().getString(R.string.hint_country));
        else
            usercountry.setText(user.country);
    }

    private void setMobileNumber() {
        if (user.mobile.equals("") || user.mobile == null)
            usermobile.setHint(getResources().getString(R.string.hint_mobile_number));
        else
            usermobile.setText("+" + user.countryCode + "-" + user.mobile);
    }

    private void setTelephoneNumber() {
        if (user.telephone.equals("") || user.telephone == null)
            usertelephone.setHint(getResources().getString(R.string.hint_phone_number));
        else
            usertelephone.setText("+" + user.countryCode + "-" + user.telephone);
    }

    private void setUserFax() {
        if (user.fax.equals("") || user.fax == null)
            userfax.setHint(getResources().getString(R.string.hint_fax_number));
        else
            userfax.setText("+" + user.countryCode + "-" + user.fax);
    }

    private void setSkype() {
        if (user.listyouid.equals("") || user.listyouid == null)
            userskype.setHint(getResources().getString(R.string.hint_list_you_id));
        else
            userskype.setText(user.listyouid);
    }

    private void setDetails() {
        setProfilePic();
        setCompanyPic();
        setName();
        setNameInOtherLang();
        setDesignation();
        setCompanyName();
        setCompanyEmailAddress();
        setUserWebsite();
        setUserAddress();
        setUserCity();
        setUserCountry();
        setMobileNumber();
        setUserFax();
        setTelephoneNumber();
        setSkype();
    }

    private void share() {
        Uri uri = Uri.fromFile(new File(Util.getPathOfFile("QrCode")));
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, "");
        email.putExtra(Intent.EXTRA_SUBJECT, "ListYou");
        email.putExtra(Intent.EXTRA_TEXT, ShowProfileActivity.this.getResources().getString(R.string.sms_text) + "\n" + user.qrcodeUrl);
        email.putExtra(Intent.EXTRA_STREAM, uri);

//        email.setType("message/rfc822");
        email.setType("image/*");
//        email.setType("application/jpg");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    private class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
//				Log.e("YES MAI HOO DON", "  " + "AYA DON");
                ImageView imageView = (ImageView) view;
                imageView.setVisibility(View.VISIBLE);
                // ImageView imageView = (ImageView) view;
                // boolean firstDisplay = !displayedImages.contains(imageUri);
                // if (firstDisplay) {
                // FadeInBitmapDisplayer.animate(imageView, 500);
                // displayedImages.add(imageUri);
                // }

            }
            progressDialog.dismiss();
        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
            // TODO Auto-generated method stub
            super.onLoadingFailed(imageUri, view, failReason);
//			Log.e("LOADING FAILED", "  " + "AYA DON");
            progressDialog.dismiss();
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            // TODO Auto-generated method stub
            super.onLoadingCancelled(imageUri, view);
//			Log.e("LOADING Cancelled", "  " + "AYA DON");
            progressDialog.dismiss();
        }

    }

    private void putValueInPrefs() {
        SharedPreferences.Editor prefs = ShowProfileActivity.this
                .getSharedPreferences("com.oxilo.listyou_app_country_code",
                        Context.MODE_PRIVATE).edit();
        prefs.putInt(AppConstant.PREFS_SCREEN_ID, 1);
        prefs.commit();
    }

    public Bitmap getThumbnail(String filename, ImageView imageView) {
        Bitmap thumbnail = null;
        try {
            FileInputStream fi = new FileInputStream(new File(
                    ShowProfileActivity.this.getFilesDir(), "/Images/" + "qrimages" + "/"
                    + "ThemeImages/" + filename));
            thumbnail = BitmapFactory.decodeStream(fi);
            Drawable d = new BitmapDrawable(getResources(), thumbnail);
            imageView.setImageBitmap(thumbnail);
            // linearLayout.setBackgroundDrawable(d);
        } catch (Exception ex) {
            Log.e("getThumbnail() on internal storage", ex.getMessage());
        }
        return thumbnail;
    }
}
