package com.landing.screen;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapter.ContactDetailAdapter;
import com.holder.GroupItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.response.UserFriends;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by C-ShellWin on 12/20/2014.
 */
public class ContactDetailFragement extends Fragment {
    private Context mContext;
    private GroupItem item;
    private UserFriends user;

    /**
     * ----------------------Declaration of Widget Variable-------------------------
     */

    private ImageView user_QR_Image, user_proile_pic, back_arrow;
    private TextView user_profile_name, companyName, designation, userEmail, userWebsite, userAddress, usercity, usercountry, usermobile, usertelephone, userfax, userskype;
    private TextView email, website, address, city, countrty, mobile, telephone, fax, skype;
    private TextView personal_detail, personal_status, common_group, contact_details;
    private ListView listView;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private AnimateFirstDisplayListener animateFirstDisplayListener;

    private ProgressBar spinner1, spinner2;

    public ContactDetailFragement(Context mContext, GroupItem item, UserFriends user) {
        this.mContext = mContext;
        this.item = item;
        this.user = user;
        Log.e("THIS>ITEM COUNT","" + item.items.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_detail, container,
                false);
        init(rootView);
        setCustomFont();
        setDetails();
        setAdapterToList();
        return rootView;
    }

    private void init(View root) {
        animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(null)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(0)).build();

        spinner1 = (ProgressBar)root. findViewById(R.id.progressBar1);
        spinner2 = (ProgressBar)root. findViewById(R.id.progressBar2);


        listView = (ListView) root.findViewById(R.id.lvExp);

        user_proile_pic = (ImageView)root.findViewById(R.id.user_profile_pic);
        user_QR_Image = (ImageView)root.findViewById(R.id.image_qr);
        back_arrow = (ImageView)root.findViewById(R.id.back_arrow);

        personal_detail = (TextView)root.findViewById(R.id.personal_details);
        personal_status = (TextView)root.findViewById(R.id.personal_status);
        common_group = (TextView)root.findViewById(R.id.common_group);

        contact_details = (TextView)root.findViewById(R.id.contact_detail);
        user_profile_name = (TextView) root.findViewById(R.id.user_profile_name);
        designation = (TextView) root.findViewById(R.id.designation);
        companyName = (TextView) root.findViewById(R.id.comapany_name);

        userEmail = (TextView) root.findViewById(R.id.show_user_profile_email_id);
        userWebsite = (TextView) root.findViewById(R.id.show_user_profile_website_id);
        userAddress = (TextView) root.findViewById(R.id.show_user_profile_address_id);
        usercity = (TextView) root.findViewById(R.id.show_user_profile_city_id);
        usercountry = (TextView) root.findViewById(R.id.show_user_profile_country_id);
        usermobile = (TextView) root.findViewById(R.id.show_user_profile_mobile_id);
        usertelephone = (TextView) root.findViewById(R.id.show_user_profile_telephone_id);
        userfax = (TextView) root.findViewById(R.id.show_user_profile_fax_id);
        userskype = (TextView) root.findViewById(R.id.show_user_profile_skype_id);

        email = (TextView) root.findViewById(R.id.show_user_profile_email);
        website = (TextView) root.findViewById(R.id.show_user_profile_website);
        address = (TextView) root.findViewById(R.id.show_user_profile_address);
        city = (TextView) root.findViewById(R.id.show_user_profile_city);
        countrty = (TextView) root.findViewById(R.id.show_user_profile_country);
        mobile = (TextView) root.findViewById(R.id.show_user_profile_mobile);
        telephone = (TextView) root.findViewById(R.id.show_user_profile_telephone);
        fax = (TextView) root.findViewById(R.id.show_user_profile_fax);
        skype = (TextView) root.findViewById(R.id.show_user_profile_skype);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((Activity) mContext).getFragmentManager();
                fm.popBackStack();
            }
        });

    }

    private void setCustomFont() {
        contact_details.setTypeface(SplashActivity.mpBold);
        user_profile_name.setTypeface(SplashActivity.mpBold);
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

        personal_detail.setTypeface(SplashActivity.mpBold);
        personal_status.setTypeface(SplashActivity.mpBold);
        common_group.setTypeface(SplashActivity.mpBold);

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
        setQrImage();
        setName();
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
        Log.e("USER QR CODE", "" + user.userPicUrl);
        if (user.userPicUrl.equals("")
                || user.userPicUrl == null
                || user.userPicUrl == "null") {
            user_proile_pic.setVisibility(View.INVISIBLE);
        } else {
            imageLoader.displayImage(user.userPicUrl, user_proile_pic,
                    options, animateFirstDisplayListener);
        }
    }

    private void setQrImage() {
        if (user.qrcodeUrl.equals("")
                || user.qrcodeUrl == null
                || user.qrcodeUrl == "null") {
            user_proile_pic.setVisibility(View.INVISIBLE);
        } else {
            imageLoader.displayImage(user.qrcodeUrl, user_QR_Image,
                    options, animateFirstDisplayListener);
        }
    }

    private void setName() {
        user_profile_name.setText(user.firstName + " " + user.lastName);
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
            usermobile.setText(user.mobile);
    }

    private void setTelephoneNumber() {
        if (user.telephone.equals("") || user.telephone == null)
            usertelephone.setHint(getResources().getString(R.string.hint_phone_number));
        else
            usertelephone.setText(user.telephone);
    }

    private void setUserFax() {
        if (user.fax.equals("") || user.fax == null)
            userfax.setHint(getResources().getString(R.string.hint_fax_number));
        else
            userfax.setText(user.fax);
    }

    private void setSkype() {
        if (user.listyouid.equals("") || user.listyouid == null)
            userskype.setHint(getResources().getString(R.string.hint_list_you_id));
        else
            userskype.setText(user.listyouid);
    }

    private void setAdapterToList() {
        ContactDetailAdapter adapter = new ContactDetailAdapter(mContext, item, user);
        listView.setAdapter(adapter);
    }


    private class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());


        @Override
        public void onLoadingStarted(String imageUri, View view) {
            // TODO Auto-generated method stub
            super.onLoadingStarted(imageUri, view);
            spinner1.setProgress(0);
            spinner2.setProgress(0);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                int tag = Integer.parseInt(imageView.getTag().toString());
                if (tag == 1) {
                    spinner1.setVisibility(View.GONE);

                } else if (tag == 2) {
                    spinner2.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
            // TODO Auto-generated method stub
            super.onLoadingFailed(imageUri, view, failReason);
            String message = null;
            switch (failReason.getType()) {
                case IO_ERROR:
                    message = "Input/Output error";
                    break;
                case DECODING_ERROR:
                    message = "Image can't be decoded";
                    break;
                case NETWORK_DENIED:
                    message = "Downloads are denied";
                    break;
                case OUT_OF_MEMORY:
                    message = "Out Of Memory error";
                    break;
                case UNKNOWN:
                    message = "Unknown error";
                    break;
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            // TODO Auto-generated method stub
            super.onLoadingCancelled(imageUri, view);

            spinner1.setVisibility(View.GONE);
            spinner2.setVisibility(View.GONE);
        }

    }

    ImageLoadingProgressListener progressListener = new ImageLoadingProgressListener() {

        @Override
        public void onProgressUpdate(String arg0, View view, int current, int total) {
            // TODO Auto-generated method stub

            ImageView imageView = (ImageView) view;
            int tag = Integer.parseInt(imageView.getTag().toString());
            if (tag == 1) {
                spinner1.setProgress(Math.round(100.0f * current / total));
            } else if (tag == 2) {
                spinner2.setProgress(Math.round(100.0f * current / total));
            }
        }
    };
}
