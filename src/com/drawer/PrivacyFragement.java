package com.drawer;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.PrivacyAdapter;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.landing.screen.LoginProfileFragement;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.response.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by C-ShellWin on 12/28/2014.
 */
public class PrivacyFragement extends Fragment {

    private GroupItem groupItem;

    private Context mContext;
    private User user;
    private PrivacyAdapter adpapter;
    private ListView listView;
    private ImageView userProfilePic;
    private Button delete_button;
    private TextView userName, userDesignation, userCompanyName, privacy;
    DisplayImageOptions options;
    public ImageLoader imageLoader;
    AnimateFirstDisplayListener animateFirstDisplayListener;

    public PrivacyFragement(Context mContext, User user) {
        this.mContext = mContext;
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_privacy_setting, container,
                false);
        init(rootView);
        setDetails();
        preareList();
        setListviewToAdapter();
        return rootView;
    }

    private void init(View root) {
        animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));

        options = new DisplayImageOptions.Builder().showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(null).cacheInMemory(false).cacheOnDisk(false)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(0)).build();

        delete_button = (Button) root.findViewById(R.id.imageView6);
        groupItem = new GroupItem();
        listView = (ListView) root.findViewById(R.id.lvExp);
        userName = (TextView) root.findViewById(R.id.user_profile_name);
        userDesignation = (TextView) root.findViewById(R.id.designation);
        userCompanyName = (TextView) root.findViewById(R.id.company);
        privacy = (TextView) root.findViewById(R.id.priacy);
        userProfilePic = (ImageView) root.findViewById(R.id.imageView);

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfile();
            }
        });
    }


    private void preareList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(mContext.getResources().getString(R.string.privacy_search_by_name));
        list.add(mContext.getResources().getString(R.string.privacy_auto_accept_friend_request));
        list.add(mContext.getResources().getString(R.string.privacy_qr_code));
        list.add(mContext.getResources().getString(R.string.privacy_show_my_photo));
        list.add(mContext.getResources().getString(R.string.privacy_chat));
        list.add(mContext.getResources().getString(R.string.privacy_message_alert));
        list.add(mContext.getResources().getString(R.string.privacy_notification_alert));

        for (int i = 0; i < list.size(); i++) {
            ChildItem childItem = new ChildItem();
            childItem.mCompanyName = list.get(i).toString();
            groupItem.items.add(childItem);
        }
    }

    private void setListviewToAdapter() {
        if (groupItem != null) {
            adpapter = new PrivacyAdapter(mContext, groupItem, user);
            listView.setAdapter(adpapter);
        } else {
            Toast.makeText(mContext, "No Friend to add to group, please first add friends first", Toast.LENGTH_LONG).show();
        }
    }


    private void setDetails() {
        if (user.userPicUrl.equals("")
                || user.userPicUrl == null
                || user.userPicUrl == "null") {
//			Log.e("I AI IN IF CONDITION", "YES");
            userProfilePic.setVisibility(View.INVISIBLE);
        } else {
            imageLoader.displayImage(user.userPicUrl, userProfilePic,
                    options, animateFirstDisplayListener);
        }

        userName.setTypeface(SplashActivity.mpRegular);
        userDesignation.setTypeface(SplashActivity.mpRegular);
        userCompanyName.setTypeface(SplashActivity.mpRegular);
        privacy.setTypeface(SplashActivity.mpRegular);

        userName.setText(user.firstName + " " + user.lastName);
        userDesignation.setText(user.designation);
        userCompanyName.setText(user.comapnyName);
    }

    private void showEditProfile() {
        Fragment fragment = new LoginProfileFragement(mContext, user);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.content_frame, fragment).addToBackStack(null);
        transaction.commit();
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
            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
            // TODO Auto-generated method stub
            super.onLoadingFailed(imageUri, view, failReason);
//			Log.e("LOADING FAILED", "  " + "AYA DON");
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            // TODO Auto-generated method stub
            super.onLoadingCancelled(imageUri, view);
//			Log.e("LOADING Cancelled", "  " + "AYA DON");
        }
    }

}
