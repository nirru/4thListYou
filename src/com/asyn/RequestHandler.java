package com.asyn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oxilo.applistyou.R;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

/**
 * Created by C-ShellWin on 12/11/2014.
 */
public class RequestHandler {

    private static RequestHandler instance;
    private AsyncHttpClient client;

    private RequestHandler() {
        client = new AsyncHttpClient();
    }

    public static RequestHandler getInstance() {
        if (instance == null) {
            instance = new RequestHandler();
        }
        return instance;
    }

    public void makePostRequest(final Context mContext, RequestParams mParam, final String url, final RequestListener listener) {
        client.post(url , mParam, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String server_response = String.valueOf(new String(bytes, "UTF-8"));
                    listener.onSuccess(server_response);
//                    Log.e("Server response", server_response);
                } catch (UnsupportedEncodingException e1) {

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
                if (error.getMessage() != null) {
                    showOKAleart(
                            mContext.getString(R.string.error_title),
                            error.getMessage(),mContext);
                } else {
                    showOKAleart(mContext.getString(R.string.error_title), mContext.getString(R.string.registration_response_error),mContext);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
            }
        });
    }

    public void make_get_Request(final Context mContext, final String url, final RequestListener listener) {
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String server_response = String.valueOf(new String(bytes, "UTF-8"));
                    listener.onSuccess(server_response);
                    Log.e("Server response", server_response);
                } catch (UnsupportedEncodingException e1) {

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
                if (error.getMessage() != null) {
                    showOKAleart(
                            mContext.getString(R.string.error_title),
                            error.getMessage(),mContext);
                } else {
                    showOKAleart(mContext.getString(R.string.error_title), mContext.getString(R.string.registration_response_error),mContext);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
            }
        });
    }

    public void showOKAleart(String title, String message , Context context) {
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
}
