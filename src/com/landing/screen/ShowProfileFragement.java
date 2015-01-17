package com.landing.screen;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.listyou.listener.ShowProfileClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.oxilo.showprofile.BussinessCardScreen;
import com.response.User;
import com.util.Freinds;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ShowProfileFragement extends Fragment {

    TextView user_profile_name, user_name_in_other_lang, designation,
            companyName, email, website, address, city, countrty, mobile,
            telephone, fax, skype, userEmail, userWebsite, userAddress,
            usercity, usercountry, usermobile, usertelephone, userfax,
            userskype, usercompany;

    String fname, lname, fname_other, lname_other, com_designation,
            company_name, company_email, company_another_email, company_website, company_address,
            company_city, company_country, country_code, company_mobile,
            company_telephone, company_fax, company_skype,
            company_user_profile_pic, company_user_logo_pic,
            company_user_bussiness_card_pic, qr_image_url;

    String id;

    Dialog customDialog;
    AsyncHttpClient client;
    ProgressDialog progressDialog;
    ImageView user_proile_pic, user_company_logo, share_button,
            myBussinessCardButton, start_button, user_QR_Image;
    //    ImageView back_buttton;
    Button retreive_butoon;
    ImageView dilog_cross_button;

    DisplayImageOptions options;
    public ImageLoader imageLoader;
    AnimateFirstDisplayListener animateFirstDisplayListener;
    Context context;
    private User user;

    public ShowProfileFragement(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_show_profile, container,
                false);
        init(rootView);
        setCustomFont();
        setDetails();
        return rootView;
    }


    private void init(View root) {
        animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader = ImageLoader.getInstance();
        ShowProfileFragement.this.imageLoader.init(ImageLoaderConfiguration
                .createDefault(context));
        options = new DisplayImageOptions.Builder().showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(null).cacheInMemory(false).cacheOnDisk(false)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(0)).build();
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "com.oxilo.listyou_app_country_code",
                Context.MODE_PRIVATE);
        id = sharedPreferences.getString(AppConstant.REG_ID, "");
        client = new AsyncHttpClient();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);

        myBussinessCardButton = (ImageView) root.findViewById(R.id.original_buss_card_button);
        start_button = (ImageView) root.findViewById(R.id.btn_start);
        user_QR_Image = (ImageView) root.findViewById(R.id.imageView1);
        user_company_logo = (ImageView) root.findViewById(R.id.comapny_logo);
        user_proile_pic = (ImageView) root.findViewById(R.id.user_profile_pic);
        user_profile_name = (TextView) root.findViewById(R.id.profile_name);
        user_name_in_other_lang = (TextView) root.findViewById(R.id.name_in_another_language);
        designation = (TextView) root.findViewById(R.id.designation);
        companyName = (TextView) root.findViewById(R.id.comapany_name);
        email = (TextView) root.findViewById(R.id.show_user_profile_email);
        website = (TextView) root.findViewById(R.id.show_user_profile_website);
        address = (TextView) root.findViewById(R.id.show_user_profile_address);
        city = (TextView) root.findViewById(R.id.show_user_profile_city);
        countrty = (TextView) root.findViewById(R.id.show_user_profile_country);
        mobile = (TextView) root.findViewById(R.id.show_user_profile_mobile);
        telephone = (TextView) root.findViewById(R.id.show_user_profile_telephone);
        fax = (TextView) root.findViewById(R.id.show_user_profile_fax);
        skype = (TextView) root.findViewById(R.id.show_user_profile_skype);
        usercompany = (TextView) root.findViewById(R.id.comapany_name_id);

        share_button = (ImageView) root.findViewById(R.id.share_button);
        userEmail = (TextView) root.findViewById(R.id.show_user_profile_email_id);
        userWebsite = (TextView) root.findViewById(R.id.show_user_profile_website_id);
        userAddress = (TextView) root.findViewById(R.id.show_user_profile_address_id);
        usercity = (TextView) root.findViewById(R.id.show_user_profile_city_id);
        usercountry = (TextView) root.findViewById(R.id.show_user_profile_country_id);
        usermobile = (TextView) root.findViewById(R.id.show_user_profile_mobile_id);
        usertelephone = (TextView) root.findViewById(R.id.show_user_profile_telephone_id);
        userfax = (TextView) root.findViewById(R.id.show_user_profile_fax_id);
        userskype = (TextView) root.findViewById(R.id.show_user_profile_skype_id);

//        back_buttton = (ImageView) root.findViewById(R.id.back_arrow);
//        back_buttton.setOnClickListener(showProfileClickListener);
        share_button.setOnClickListener(showProfileClickListener);
//        myBussinessCardButton.setOnClickListener(showProfileClickListener);
//        start_button.setOnClickListener(showProfileClickListener);

        user_proile_pic.setImageDrawable(null);
        user_company_logo.setImageDrawable(null);
        user_company_logo.setVisibility(View.INVISIBLE);

        myBussinessCardButton.setVisibility(View.GONE);
        start_button.setVisibility(View.GONE);
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
//            finish();
        }
    };

    private void showMyCard() {
        Intent i = new Intent(context,
                BussinessCardScreen.class);
        i.putExtra(AppConstant.EDIT_PROFILE_BUSSINESS_LOGO_PIC, id);
        startActivity(i);
//		finish();
    }

    private void congratsPopUp() {

        customDialog = new Dialog(context);

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

    OnClickListener crossListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.cancel();
        }
    };

    OnClickListener retreiveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, MainLandingActivity.class);
            startActivity(i);
            customDialog.cancel();
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
        else{
            userEmail.setText(user.companyEmailAddress);
        }
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

    private void share() {
        Freinds.share(context, user, "QrCode");
    }

    private class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                imageView.setVisibility(View.VISIBLE);
            }
            progressDialog.dismiss();
        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
            // TODO Auto-generated method stub
            super.onLoadingFailed(imageUri, view, failReason);
            progressDialog.dismiss();
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            // TODO Auto-generated method stub
            super.onLoadingCancelled(imageUri, view);
            progressDialog.dismiss();
        }

    }
}
