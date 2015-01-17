package com.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asyn.RequestHandler;
import com.asyn.RequestListener;
import com.holder.ChildItem;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by C-ShellWin on 1/10/2015.
 */
public class ReportUserPermanentDialog {

    private static ReportUserPermanentDialog instance;
    private Context mContext;
    private User user;
    private ChildItem childItem;
    private TextView reportHeader, profileName, companyName, submitReport,profile_status;
    private EditText type_report;
    private Dialog customDialog;
    private ImageView dialog_cross_button, user_profile_pic;
    private ProgressBar spinner1;


    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private AnimateFirstDisplayListener animateFirstDisplayListener;

    private TextView header, reportUser, deleteUser, manageGroups;

    public static ReportUserPermanentDialog getInstance() {
        if (instance == null) {
            instance = new ReportUserPermanentDialog();
        }
        return instance;
    }

    public void createDialog(Context mContext, User user, ChildItem childItem) {
        this.mContext = mContext;
        this.user = user;
        this.childItem = childItem;
        customDialog = new Dialog(mContext);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.report_user_permanentely);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
        init();
        setCustomFont();
    }

    private void setCustomFont() {
        profile_status.setTypeface(SplashActivity.mpBold);
        reportHeader.setTypeface(SplashActivity.mpBold);
        profileName.setTypeface(SplashActivity.mpRegular);
        companyName.setTypeface(SplashActivity.mpRegular);
        type_report.setTypeface(SplashActivity.mpRegular);
        submitReport.setTypeface(SplashActivity.mpBold);
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

        user_profile_pic = (ImageView) customDialog.findViewById(R.id.user_profile_pic);
        spinner1 = (ProgressBar) customDialog.findViewById(R.id.progress_bar_one);
        dialog_cross_button = (ImageView) customDialog.findViewById(R.id.cross_button);
        profile_status = (TextView) customDialog.findViewById(R.id.profile_status);
        reportHeader = (TextView) customDialog.findViewById(R.id.textView1);
        profileName = (TextView) customDialog.findViewById(R.id.profile_name);
        companyName = (TextView) customDialog.findViewById(R.id.company);
        type_report = (EditText) customDialog.findViewById(R.id.type_your_status);
        submitReport = (TextView) customDialog.findViewById(R.id.save_status);

        submitReport.setOnClickListener(l);
        dialog_cross_button.setOnClickListener(l);

        profileName.setText(childItem.mUserName);
        companyName.setText(childItem.mCompanyName);
        imageLoader.displayImage(childItem.mUserPicUrl, user_profile_pic,
                options, animateFirstDisplayListener, progressListener);
    }


    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (type_report.getText().toString().equals("")) {
                Util.showOKAleart(mContext, "Message", "Please specify the reason to report");
                return;
            } else {
                customDialog.cancel();
                reportUser();
            }
        }
    };

    private void reportUser() {
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        Log.e("SENDER UID", "" + user.id);
        Log.e("SENDER UID", "" + childItem.remoteUserId);
        params.put(AppConstant.SENDER_UID, user.id);
        params.put(AppConstant.RECEIVER_UID, childItem.remoteUserId);
        params.put(AppConstant.REPORT_MEMBER_MESSAGE, type_report.getText().toString().trim());
        requestHandler.makePostRequest(mContext, params, AppConstant.REPORT_USER, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject) {
                        Log.e("I AIM CSDSD", "" + result);
                        JSONObject jsonObject = (JSONObject) json;
                        if (jsonObject.getString("message").trim().equals("SUCCESS")) {
                            Log.e("DELEYED SUCCESS", "YES");
                            Toast.makeText(mContext, "Ypu have successfully report " + childItem.mUserName + ". Now admin will take imaddiate action against hom", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.getString("message").trim().equals("ALREADY_FOUND")) {
                            Toast.makeText(mContext, "You have already reported " + childItem.mUserName + ".", Toast.LENGTH_SHORT).show();

                        } else {
                            Util.showOKAleart(mContext, "Message", "Error Occured");
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
