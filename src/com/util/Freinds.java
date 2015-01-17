package com.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.asyn.RequestHandler;
import com.asyn.RequestListener;
import com.holder.GroupItem;
import com.loopj.android.http.RequestParams;
import com.oxilo.applistyou.R;
import com.oxilo.listyou.constant.AppConstant;
import com.response.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;

/**
 * Created by C-ShellWin on 12/14/2014.
 */
public class Freinds {
    private static Freinds instance;

    public static Freinds getInstance() {
        if (instance == null) {
            instance = new Freinds();
        }
        return instance;
    }

    public void acceptFriendRequest(final Context mContext, String login_user_id, String receiver_user_id, final String remoteUser, final RequestListener listener) {
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, login_user_id);
        params.put(AppConstant.RECEIVER_UID, receiver_user_id);
        requestHandler.makePostRequest(mContext, params, AppConstant.ACCEPT_INVITATION_FRIENDS_API, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                GroupItem item = new GroupItem();
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        if (jsonObject.getString("message").equals("Request_Accepted")) {
                            listener.onSuccess(result);
                            Toast.makeText(mContext, remoteUser + " " + "is friend now", Toast.LENGTH_SHORT).show();
                        } else {
                            showOKAleart(mContext, "Message", "error occured please try again");
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

    public void denyFriendRequest(final Context mContext, String login_user_id, String receiver_user_id, final String remoteUser, final RequestListener listener) {
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, login_user_id);
        params.put(AppConstant.RECEIVER_UID, receiver_user_id);
        requestHandler.makePostRequest(mContext, params, AppConstant.DENY_REQUEST, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                GroupItem item = new GroupItem();
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        if (jsonObject.getString("message").equals("Invitation_Deleted")) {
                            listener.onSuccess(result);
                            Toast.makeText(mContext, "friend request of" + remoteUser + "is denied.", Toast.LENGTH_SHORT).show();
                        } else {
                            showOKAleart(mContext, "Message", "error occured please try again");
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

    public void SendFriendRequest(final Context mContext, String login_user_id, String receiver_user_id, final String remoteUser, final RequestListener listener) {
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, login_user_id);
        params.put(AppConstant.RECEIVER_UID, receiver_user_id);
        requestHandler.makePostRequest(mContext, params, AppConstant.ADD_AS_FRIENDS_API, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                listener.onSuccess(result);
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        if (jsonObject.getString("message").trim().equals("ALREADY_FRIEND")) {
                            showOKAleart(mContext, "Message", "Already Friends");
                        } else if (jsonObject.getString("message").trim().equals("PENDING")) {
                            showOKAleart(mContext, "Message", "Your friend request is panding");
                        } else if (jsonObject.getString("message").trim().equals("Request_Sent")) {
                            showOKAleart(mContext, "Message", "Friend request has been sent");
                        } else {
                            showOKAleart(mContext, "Message", "error occured please try again");
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

    public void cretaeUserDefinedGroup(final Context mContext, String login_user_id, final String bitmapString, final String groupName, final ProgressDialog progressDialog, final RequestListener listener) {
        progressDialog.show();
        RequestHandler requestHandler = RequestHandler.getInstance();
        final RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, login_user_id);
        params.put(AppConstant.GROUP_NAME, groupName);
        params.put(AppConstant.GROUP_IMAGE, bitmapString);
        Log.e("PARAM URL CREAT GROUP ", "" + AppConstant.USER_DEFINED_GROUP + "" + params);
        requestHandler.makePostRequest(mContext, params, AppConstant.USER_DEFINED_GROUP, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                Log.e("Request PAram", "" + params);
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        if (jsonObject.getString("message").trim().equals("SUCCESS")) {
                            listener.onSuccess(result);
                        } else if (jsonObject.getString("message").trim().equals("ALREADY_FOUND")) {
                            progressDialog.dismiss();
                            showOKAleart(mContext, "Message", "Group with this name already exist, please try other name");
                        } else {
                            progressDialog.dismiss();
                            showOKAleart(mContext, "Message", "error occured please try again");
                        }
                    } else if (json instanceof JSONArray) {
                        progressDialog.dismiss();
                        JSONArray jsonArray = (JSONArray) json;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void addMemberToGroup(final Context mContext, String login_user_id, final String groupName, final String members, final ProgressDialog progressDialog, final RequestListener listener) {
        RequestHandler requestHandler = RequestHandler.getInstance();
        final RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, login_user_id);
        params.put(AppConstant.RECEIVER_UID, members);
        params.put(AppConstant.GROUP_NAME, groupName);
        Log.e("PARAM URL ADD MEMER TO GROUP ", "" + AppConstant.ADD_MEMEBER_TO_GROUP + "" + params);
        requestHandler.makePostRequest(mContext, params, AppConstant.ADD_MEMEBER_TO_GROUP, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                Log.e("ON SUCCES ARA<P ", "" + AppConstant.ADD_MEMEBER_TO_GROUP + "" + params);
                progressDialog.dismiss();
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        if (jsonObject.getString("message").trim().equals("SUCCESS")) {
                            listener.onSuccess(result);
//                            showOKAleart(mContext, "Message", groupName + " " + "created successfully");
                        } else if (jsonObject.getString("message").trim().equals("")) {
                            showOKAleart(mContext, "Message", "Members are already in the" + groupName);
                        } else {
                            showOKAleart(mContext, "Message", "error occured please try again");
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

    public void getUserStatus(Context mContext, String id, final RequestListener listener) {
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, id);
        requestHandler.makePostRequest(mContext, params, AppConstant.GET_USER_SATUS, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString(AppConstant.APP_USER_STATUS);
                    listener.onSuccess(message);
                }
                catch (JSONException e){

                }

            }
        });
    }

    public void getSystemGroupInformation(final Context mContext, String id, String groupName, final int pos ,final RequestListener listener){
        RequestHandler handler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, id);
        params.put(AppConstant.GROUP_NAME, groupName);
        handler.makePostRequest(mContext, params, AppConstant.GET_USER_SYTEM_GROUP, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                String message = null;
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject){
                        Log.e("I AIM CSDSD","" + result);
                        JSONObject jsonObject = (JSONObject) json;
                        if (jsonObject.getString("message").trim().equals("NOT_FOUND")) {
                            message = "";
                        }else{
                            message = "";
                        }
                    }
                    else if (json instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) json;
                        message = result;
                    }
                    listener.onSuccess(message);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static void sms(Context mContext, User user) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + Uri.encode("")));
        intent.putExtra("sms_body", mContext.getResources().getString(R.string.sms_text) + "\n" + user.qrcodeUrl);
        mContext.startActivity(intent);
    }

    public static void sendEmail(Context mContext, User user, String filename) {
        Uri uri = Uri.fromFile(new File(Util.getPathOfFile(filename)));
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, "");
        email.putExtra(Intent.EXTRA_SUBJECT, "ListYou");
        email.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.sms_text) + "\n" + user.qrcodeUrl);
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("message/rfc822");
        mContext.startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    public static void share(Context mContext, User user, String filename) {
        Uri uri = Uri.fromFile(new File(Util.getPathOfFile(filename)));
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, "");
        email.putExtra(Intent.EXTRA_SUBJECT, "ListYou");
        email.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.sms_text) + "\n" + user.qrcodeUrl);
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("image/*");
        mContext.startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    public static void shareFriendsQr(Context mContext, String qrLink, String filename) {
        Uri uri = Uri.fromFile(new File(Util.getPathOfFile(filename)));
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, "");
        email.putExtra(Intent.EXTRA_SUBJECT, "ListYou");
        email.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.sms_text) + "\n" + qrLink);
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("image/*");
        mContext.startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    public static void countRequest(int count, TextView text) {
        if (count == 0)
            text.setVisibility(View.INVISIBLE);
        else {
            text.setVisibility(View.VISIBLE);
            text.setText("" + count);
        }
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
            }
        });
        alertDialog.show();
    }

}
