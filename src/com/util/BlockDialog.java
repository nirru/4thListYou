package com.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asyn.RequestHandler;
import com.asyn.RequestListener;
import com.landing.screen.ContactListingfragement;
import com.loopj.android.http.RequestParams;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.response.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by C-ShellWin on 12/31/2014.
 */
public class BlockDialog {
    private static BlockDialog instance;
    private Context mContext;
    private String memberList;
    private User user;
    private Dialog customDialog;
    private TextView title, lets_start_label_one, yes, no;
    private ProgressDialog progressDialog;
    private Spinner spinner_system_defined_group;
    private int spinnerSelectedItem;
    private ImageView dilog_cross_button;
    private TextView mAddListCount;
    String memberName = "";

    public static BlockDialog getInstance() {
        if (instance == null) {
            instance = new BlockDialog();
        }
        return instance;
    }

    public void createDialog(Context mContext, String memberList, String memberName, User user, TextView mAddListCount) {
        this.mContext = mContext;
        this.memberList = memberList;
        this.memberName = memberName;
        Log.e("MEMBER NAME IN LIST>>>>", "" + memberName);
        this.user = user;
        this.mAddListCount = mAddListCount;
        customDialog = new Dialog(mContext);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.delete_user_popup);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
        init();
        setCustomFont();
    }

    private void init() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        spinner_system_defined_group = (Spinner) customDialog.findViewById(R.id.spinner_system_defined_group);
        dilog_cross_button = (ImageView) customDialog
                .findViewById(R.id.cross_button);
        title = (TextView) customDialog.findViewById(R.id.forgot);
        lets_start_label_one = (TextView) customDialog.findViewById(R.id.lets_start_label_one);

        yes = (TextView) customDialog.findViewById(R.id.yes);
        no = (TextView) customDialog.findViewById(R.id.no);
        yes.setOnClickListener(l);
        dilog_cross_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.cancel();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.cancel();
            }
        });
    }

    private void setCustomFont() {
        lets_start_label_one.setText("Deleting" + " " + memberName + " won’t let you chat with " + memberName + " anymore and your previous chat with " + memberName + " will be deleted.Are you sure you want to delete " + memberName + " from your freind list?");
        title.setTypeface(SplashActivity.mpBold);
        lets_start_label_one.setTypeface(SplashActivity.mpRegular);
        yes.setTypeface(SplashActivity.mpRegular);
        no.setTypeface(SplashActivity.mpRegular);
    }

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (memberList != null) {
                if (memberList.trim().equals("")) {
                    Util.showOKAleart(mContext, "Message", "Nothing to delete");
                    return;
                } else {
                    postToserver();
                }
            } else {
                Toast.makeText(mContext, "Please select a group", Toast.LENGTH_LONG).show();
            }

        }
    };

    private void postToserver() {
        progressDialog.show();
        RequestHandler handler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        Log.e("SENDER UID", "" + user.id);
        Log.e("RECEIVER UID", "" + memberList);
        params.put(AppConstant.SENDER_UID, user.id);
        params.put(AppConstant.RECEIVER_UID, memberList);
        handler.makePostRequest(mContext, params, AppConstant.DELETE_USER, new RequestListener() {
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
                            showList();
                        } else {
                            progressDialog.dismiss();
                            Util.showOKAleart(mContext, "Message", "Problem occured");
                            return;
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


    private void showList() {
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, user.id);
        requestHandler.makePostRequest(mContext, params, AppConstant.GET_FRIEND_LIST, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                Log.e("WRITE  SUCCESS", "YES");
                Util.writeFreindsToPrefrefs(mContext, result);
                showOKAleart(mContext, "Message", "The member " + "  " + memberName + "Deleted SuccussFully");

            }
        });

    }

    public void showOKAleart(Context context, String title, String message) {
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
                customDialog.cancel();
                Fragment fragment = new ContactListingfragement(mContext, mAddListCount, user);
                FragmentManager fragmentManager = ((Activity) mContext).getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment).addToBackStack(null).commit();
            }
        });
        alertDialog.show();
    }
}
