package com.landing.screen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oxilo.applistyou.R;
import com.oxilo.listyou.constant.AppConstant;
import com.util.Util;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/**
 * Created by C-ShellWin on 12/10/2014.
 */
public class Footer {
    private Context mContext;
    private TextView mContactListCount, mChatCount, mAddListCount, mNotiicationCount;
    private String mUserID;
    private AsyncHttpClient client;

    public Footer(Context mContext, TextView mContactListCount, TextView mChatCount, TextView mAddListCount, TextView mNotiicationCount, String mUserID) {
        this.mContext = mContext;
        this.mContactListCount = mContactListCount;
        this.mChatCount = mChatCount;
        this.mNotiicationCount = mNotiicationCount;
        this.mUserID = mUserID;
        client = new AsyncHttpClient();
    }

    public void fetchCount() {
        getAddListCount();
    }

    private void getContactListCount() {

    }

    private void getChatCount() {

    }

    private void getAddListCount() {
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, mUserID);
        client.post(AppConstant.RECEIVE_REQUEST_FRIENDS_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JSONArray jsonArray;
                try {
                    String response = String.valueOf(new String(bytes, "UTF-8"));
                    jsonArray = new JSONArray(response.toString().trim());
                    setFooterCount(jsonArray.length());
                    Log.e("RECEIVED REQUEST API LIST ARRAY", "" + jsonArray.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (UnsupportedEncodingException e1) {

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
                if (error.getMessage() != null) {
                    Util.showOKAleart(mContext, mContext.getString(R.string.error_title),
                            error.getMessage());
                } else {
                    Util.showOKAleart(mContext, mContext.getString(R.string.error_title), mContext.getString(R.string.registration_response_error));
                }
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void getNotificationCount() {

    }

    private void setFooterCount(int addListCount)
    {
        if (addListCount == 0)
            mAddListCount.setVisibility(View.INVISIBLE);
        else{
            mAddListCount.setVisibility(View.VISIBLE);
            mAddListCount.setText("" + addListCount);
        }
    }

    public void showOKAleart(String title, String message) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(mContext).create();
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
}
