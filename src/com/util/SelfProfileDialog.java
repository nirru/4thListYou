package com.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asyn.RequestHandler;
import com.asyn.RequestListener;
import com.holder.GroupItem;
import com.landing.screen.LoginProfileFragement;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.response.User;
import com.response.UserFriends;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by C-ShellWin on 12/19/2014.
 */
public class SelfProfileDialog {
    private static SelfProfileDialog instance;
    private Dialog customDialog;
    private Context mContext;
    private TextView  save_status;
    private EditText type_your_status;
    private ImageView dilog_cross_button;
    private ImageView mUserImageView;
    private TextView mUserName, mUserDesignation, mUserCompanyName, mMe;
    private TextView share_my_contact,account_seeting,profile, profile_status,status, added_user_date_info;
    private ProgressBar spinner1;
    private RelativeLayout mChatToUser, mDeleteUser, mShareDetail;
    private ImageView friends_qr_code;
    private UserFriends userFriends = null;
    private User user;
    private List<GroupItem> items;
    GroupItem item;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private AnimateFirstDisplayListener animateFirstDisplayListener;
    private boolean isNoteSaved = false;
    private String userStatus;

    public static SelfProfileDialog getInstance() {
        if (instance == null) {
            instance = new SelfProfileDialog();
        }
        return instance;
    }

    public void createDialog(Context mContext,User user) {
        this.mContext = mContext;
        this.user = user;
        customDialog = new Dialog(mContext);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        customDialog.setContentView(R.layout.self_profile_popup);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
        init();
        getSavedStatus();
        setCustomFont();
    }

    private void init() {
        animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));

        options = new DisplayImageOptions.Builder().showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(null).cacheInMemory(false).cacheOnDisk(false)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(0)).build();

        items = new ArrayList<GroupItem>();
        item = new GroupItem();
        spinner1 = (ProgressBar) customDialog.findViewById(R.id.progress_bar_one);
        friends_qr_code = (ImageView) customDialog.findViewById(R.id.friends_qr_code);
        mChatToUser = (RelativeLayout) customDialog.findViewById(R.id.rel_layout_left);
        mDeleteUser = (RelativeLayout) customDialog.findViewById(R.id.rel_layout_right);
        mShareDetail = (RelativeLayout) customDialog.findViewById(R.id.share_relative);
        mUserImageView = (ImageView) customDialog.findViewById(R.id.user_profile_pic);
        mUserName = (TextView) customDialog.findViewById(R.id.profile_name);
        added_user_date_info = (TextView)customDialog.findViewById(R.id.added_user_date_info);
        mUserDesignation = (TextView) customDialog.findViewById(R.id.designation);
        mUserCompanyName = (TextView) customDialog.findViewById(R.id.company);
        mMe = (TextView)customDialog.findViewById(R.id.me);
        share_my_contact = (TextView)customDialog.findViewById(R.id.share_my_contact);
        account_seeting = (TextView)customDialog.findViewById(R.id.account_seeting);
        profile = (TextView)customDialog.findViewById(R.id.profile);
        profile_status = (TextView)customDialog.findViewById(R.id.status);
        status = (TextView)customDialog.findViewById(R.id.profile_status);
        type_your_status = (EditText)customDialog.findViewById(R.id.type_your_status);
        save_status = (TextView)customDialog.findViewById(R.id.save_status);
        dilog_cross_button = (ImageView) customDialog.findViewById(R.id.cross_button);
        mUserImageView.setVisibility(View.INVISIBLE);

        imageLoader.displayImage(user.userPicUrl, mUserImageView,
                options, animateFirstDisplayListener, progressListener);


        mUserName.setText(user.firstName + " " + user.lastName);
        mUserCompanyName.setText(user.comapnyName);
        mMe.setText("(" + "Me" + ")");
        added_user_date_info.setText("Added to my list on" + " " + user.listyou_date);

        mChatToUser.setOnClickListener(l);
        mDeleteUser.setOnClickListener(l);
        mShareDetail.setOnClickListener(l);
        save_status.setOnClickListener(l);
        dilog_cross_button.setOnClickListener(l);
    }

    private void setCustomFont(){
        status.setTypeface(SplashActivity.mpRegular);
        type_your_status.setTypeface(SplashActivity.mpRegular);
        mUserName.setTypeface(SplashActivity.mpRegular);
        mUserCompanyName.setTypeface(SplashActivity.mpRegular);
        mMe.setTypeface(SplashActivity.mpRegular);
        share_my_contact.setTypeface(SplashActivity.mpRegular);
        account_seeting.setTypeface(SplashActivity.mpRegular);
        profile.setTypeface(SplashActivity.mpRegular);
        profile_status.setTypeface(SplashActivity.mpRegular);
    }

    public void getSavedStatus(){
        Freinds freinds = Freinds.getInstance();
        freinds.getUserStatus(mContext,user.id,new RequestListener() {
            @Override
            public void onSuccess(String result) {
                Log.e("MY <<<????>>>USER STATUS", "" + result);
                userStatus = result;
                writeStatus();
            }
        });
    }

    public void writeStatus() {
        if (userStatus == null || userStatus.equals("")
                || userStatus.equals("null")){
            profile_status.setHint(mContext.getResources().getString(R.string.type_your_status));
        }
        else{
            profile_status.setText(userStatus);
        }
    }

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.rel_layout_left:
                   share();
                    break;
                case R.id.share_relative:
                    accoutSetting();
                    break;
                case R.id.rel_layout_right:
                    editprofile();
                    break;
                case R.id.cross_button:
                    shutTheDialog();
                    break;
                case R.id.save_status:
                   saveStaus();
                    break;
            }
        }
    };



    private void share() {
        customDialog.cancel();
        Freinds.share(mContext, user, "QrCode");
    }

    private void editprofile() {
        customDialog.cancel();
        Fragment fragment = new LoginProfileFragement(mContext, user);
        android.app.FragmentManager fragmentManager = ((Activity) mContext).getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).addToBackStack(null).commit();
    }

    private void accoutSetting(){
        customDialog.cancel();
    }

    private void shutTheDialog() {
        customDialog.cancel();
    }


   private void saveStaus() {
       customDialog.cancel();
       final ProgressDialog progressDialog = new ProgressDialog(mContext);
       progressDialog.setMessage("Loading...");
       progressDialog.setCanceledOnTouchOutside(false);
//       progressDialog.show();
       RequestHandler requestHandler = RequestHandler.getInstance();
       RequestParams params = new RequestParams();
       params.put(AppConstant.SENDER_UID, user.id);
       params.put(AppConstant.SELF_STATUS, type_your_status.getText().toString());
       requestHandler.makePostRequest(mContext, params, AppConstant.UPDATE_USER_SATUS, new RequestListener() {
           @Override
           public void onSuccess(String result) {
               progressDialog.cancel();
               Object json = null;
               try {
                   json = new JSONTokener(result).nextValue();
                   if (json instanceof JSONObject) {
                       JSONObject jsonObject = (JSONObject) json;
                       if (jsonObject.getString("message").equals("UPDATED")){
                           Toast.makeText(mContext,"Status saved succesfully", Toast.LENGTH_SHORT).show();
                       }
                       else {
                           Util.showOKAleart(mContext,mContext.getResources().getString(R.string.message_error),"Error occured during save , please try again");
                       }
                   } else if (json instanceof JSONArray) {
                       JSONArray jsonArray = (JSONArray) json;
                   }
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       });
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
        }

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
//				Log.e("YES MAI HOO DON", "  " + "AYA DON");
                ImageView imageView = (ImageView) view;
                imageView.setVisibility(View.VISIBLE);
            }
            spinner1.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
            // TODO Auto-generated method stub
            super.onLoadingFailed(imageUri, view, failReason);
            spinner1.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            // TODO Auto-generated method stub
            super.onLoadingCancelled(imageUri, view);
            spinner1.setVisibility(View.GONE);
        }

    }

    ImageLoadingProgressListener progressListener = new ImageLoadingProgressListener() {

        @Override
        public void onProgressUpdate(String arg0, View view, int current, int total) {
            // TODO Auto-generated method stub

            ImageView imageView = (ImageView) view;
            spinner1.setProgress(Math.round(100.0f * current / total));
        }
    };
}
