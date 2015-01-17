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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asyn.RequestHandler;
import com.asyn.RequestListener;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.landing.screen.ContactDetailFragement;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxilo.applistyou.R;
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
public class RecentTapDialog {
    private static RecentTapDialog instance;
    private Dialog customDialog;
    private Context mContext;
    private ChildItem childItem;
    private TextView retreive_butoon, add_note;
    private ImageView dilog_cross_button;
    private ImageView mUserImageView;
    private TextView mUserName, mUserDesignation, mUserCompanyName;
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

    public static RecentTapDialog getInstance() {
        if (instance == null) {
            instance = new RecentTapDialog();
        }
        return instance;
    }

    public void createDialog(ChildItem childItem, User user, Context mContext) {
        this.childItem = childItem;
        this.mContext = mContext;
        this.user = user;
        customDialog = new Dialog(mContext);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.recently_added_friends_popup);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
        init();
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
        mUserDesignation = (TextView) customDialog.findViewById(R.id.designation);
        mUserCompanyName = (TextView) customDialog.findViewById(R.id.company);
        retreive_butoon = (TextView) customDialog.findViewById(R.id.retreive_btn);
        add_note = (TextView) customDialog.findViewById(R.id.add_note);
        dilog_cross_button = (ImageView) customDialog.findViewById(R.id.cross_button);
        mUserImageView.setVisibility(View.INVISIBLE);
        add_note.setVisibility(View.GONE);

        imageLoader.displayImage(childItem.mUserPicUrl, mUserImageView,
                options, animateFirstDisplayListener, progressListener);

        imageLoader.displayImage(childItem.qr_image_link, friends_qr_code,
                options, animateFirstDisplayListener, progressListener);

        mUserName.setText(childItem.mUserName);
        mUserCompanyName.setText(childItem.mCompanyName);

        mChatToUser.setOnClickListener(l);
        mDeleteUser.setOnClickListener(l);
        mShareDetail.setOnClickListener(l);
        retreive_butoon.setOnClickListener(l);
        dilog_cross_button.setOnClickListener(l);
    }

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.rel_layout_left:
                    chat();
                    break;
                case R.id.share_relative:
                    share();
                    break;
                case R.id.rel_layout_right:
                    profile();
                    break;
                case R.id.cross_button:
                    shutTheDialog();
                    break;
                case R.id.retreive_btn:
                    showNote();
                    break;
            }
        }
    };

    private void chat() {
        customDialog.cancel();
    }

    private void share() {
        customDialog.cancel();
        Freinds.shareFriendsQr(mContext, childItem.qr_image_link, childItem.mUserName);
    }

    private void profile() {
        customDialog.cancel();
        showContactDetails();
    }

    private void shutTheDialog() {
        customDialog.cancel();
    }

    private void showNote() {
        if (!isNoteSaved) {
            retreive_butoon.setBackground(mContext.getResources().getDrawable(R.drawable.round_corner_edittext_selected));
            retreive_butoon.setTextColor(Color.WHITE);
            add_note.setVisibility(View.VISIBLE);
            isNoteSaved = true;
        } else {
            retreive_butoon.setBackground(mContext.getResources().getDrawable(R.drawable.round_corner_edittext));
            retreive_butoon.setTextColor(Color.parseColor("#242c3d"));
            add_note.setVisibility(View.GONE);
            customDialog.cancel();
            isNoteSaved = false;
        }
    }

    private void showContactDetails() {
        Log.e("<><><><><><>BEFORE CLICK", childItem.remoteUserId);
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, childItem.remoteUserId);
        requestHandler.makePostRequest(mContext, params, AppConstant.URL_SHOW_PROFILE_URL, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                userFriends = Util.saveParcelForUserFirnds(result, childItem.remoteUserId);
                Log.e("<><><><><><>USERFRIENDID", userFriends.id);
                getCommonGroup(progressDialog);
            }
        });
    }

    private void getCommonGroup(final ProgressDialog progressDialog) {
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, user.id);
        params.put(AppConstant.RECEIVER_UID, userFriends.id);
        requestHandler.makePostRequest(mContext, params, AppConstant.GET_COMMON_GROUP, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        item.title = "No common group";
                        items.add(item);
                    } else if (json instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) json;
                        getList(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startFragementContactDetail();
            }
        });
    }

    private void getList(String message) {
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(message.toString().trim());
            item.title = "Members" + "(" + jsonArray.length() + ")";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ChildItem child = new ChildItem();
                child.mCompanyName = jsonObject
                        .getString(AppConstant.NAME_SET_TO_GROUP);
                child.groupMemberCount = jsonObject
                        .getString(AppConstant.GROUP_MEMBER_COUNT);
                item.items.add(child);
            }

            items.add(item);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startFragementContactDetail() {
        Fragment fragment = new ContactDetailFragement(mContext, item, userFriends);
        android.app.FragmentManager fragmentManager = ((Activity) mContext).getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).addToBackStack(null).commit();
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
                int tag = Integer.parseInt(imageView.getTag().toString());
                imageView.setVisibility(View.VISIBLE);

                if (tag == 1) {

                }
                if (tag == 2) {
                    Util.saveImageToExternalStorage(mContext, loadedImage, childItem.mUserName);
                }
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
