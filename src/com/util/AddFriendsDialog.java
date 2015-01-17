package com.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asyn.RequestHandler;
import com.asyn.RequestListener;
import com.holder.ChildItem;
import com.holder.GroupItem;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by C-ShellWin on 12/15/2014.
 */
public class AddFriendsDialog {
    private static AddFriendsDialog instance;
    private Dialog customDialog;
    private Button retreive_butoon;
    private ImageView dilog_cross_button;
    private Context context;
    private User user;
    private ImageView mUserImageView, mUserCompanyImageView;
    private TextView mUserName, mUserDesignation, mUserCompanyName;
    private ProgressBar spinner1;
    private String listYouID;
    private ProgressDialog progressDialog;
    private List<GroupItem> items;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private AnimateFirstDisplayListener animateFirstDisplayListener;

    public static AddFriendsDialog getInstance() {
        if (instance == null) {
            instance = new AddFriendsDialog();
        }
        return instance;
    }

    public void createAddFriendsDialog(Context context) {
        this.context = context;
        animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(context));
        options = new DisplayImageOptions.Builder().showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(null).cacheInMemory(false).cacheOnDisk(false)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(0)).build();

        customDialog = new Dialog(context);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.add_as_friend_popup);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
        init();
    }

    private void init() {
        spinner1 = (ProgressBar) customDialog.findViewById(R.id.progress_bar_one);
        mUserImageView = (ImageView) customDialog.findViewById(R.id.user_profile_pic);
        mUserName = (TextView) customDialog.findViewById(R.id.profile_name);
        mUserDesignation = (TextView) customDialog.findViewById(R.id.designation);
        mUserCompanyName = (TextView) customDialog.findViewById(R.id.company);
        retreive_butoon = (Button) customDialog.findViewById(R.id.retreive_btn);
        mUserImageView.setVisibility(View.INVISIBLE);
        dilog_cross_button = (ImageView) customDialog
                .findViewById(R.id.cross_button);
        retreive_butoon.setOnClickListener(retreiveListener);
        dilog_cross_button.setOnClickListener(crossListener);
    }

    public void searchUser(final Context mContext, final User user, final String listYouID) {
       this.context = mContext;
        this.user = user;
        items = new ArrayList<GroupItem>();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.LIST_YOU_ID, listYouID.trim());
        requestHandler.makePostRequest(mContext, params, AppConstant.SEARCH_LISTYOU_USER, new RequestListener() {
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                GroupItem item = new GroupItem();
                Object json = null;
                try {
                    json = new JSONTokener(message).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        item.title = "Received Request" + "(" + 0 + ")";
                        showOKAleart(mContext.getResources().getString(R.string.message_error), mContext.getResources().getString(R.string.no_user_found));
                    } else if (json instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) json;
                        try {
                            jsonArray = new JSONArray(message.toString().trim());
                            item.title = "Received Request" + "(" + jsonArray.length() + ")";
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ChildItem child = new ChildItem();
                                child.remoteUserId = jsonObject.getString(AppConstant.SENDER_UID);
                                child.mUserName = jsonObject
                                        .getString(AppConstant.RECEIVER_USERNAME);
                                child.mCompanyName = jsonObject
                                        .getString(AppConstant.RECEIVER_COMPANY_NAME);
                                child.mUserPicUrl = jsonObject
                                        .getString(AppConstant.RECEIVER_PROFILE_PIC_URL);
                                item.items.add(child);
                            }
                            items.add(item);
                            createAddFriendsDialog(mContext);
                            populateData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void populateData() {
        if (items.size() == 0 || items == null) {
            showOKAleart(context.getResources().getString(R.string.message_error), context.getResources().getString(R.string.no_user_found));
        } else {
            ChildItem item = null;
            for (int i = 0; i < items.size(); i++) {
                item = getChild(0, i);
            }
            imageLoader.displayImage(item.mUserPicUrl, mUserImageView,
                    options, animateFirstDisplayListener, progressListener);
            mUserName.setText(item.mUserName);
            if (item.mCompanyName == null || item.mCompanyName.equals("")
                    || item.mCompanyName.equals("null")) {
            } else {
                mUserCompanyName.setText(item.mCompanyName);
            }
        }
    }

    public void showOKAleart(String title, String message) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
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

    private ChildItem getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.get(childPosition);
    }

    View.OnClickListener crossListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.dismiss();
        }
    };

    View.OnClickListener retreiveListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.cancel();
            progressDialog.show();
            Freinds freinds = Freinds.getInstance();
            ChildItem item = null;
            for (int i = 0; i < items.size(); i++) {
                item = getChild(0, i);
            }
            freinds.SendFriendRequest(context, user.id, item.remoteUserId, item.mUserName, new RequestListener() {
                @Override
                public void onSuccess(String result) {
                    progressDialog.dismiss();
                }
            });
        }
    };


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
