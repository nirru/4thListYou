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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
public class MoveDialog {
    private static MoveDialog instance;
    private Context mContext;
    private String oldGroup, newGroup, memberList;
    private User user;
    private Dialog customDialog;
    private TextView title, lets_start_label_one, move_btn;
    private ProgressDialog progressDialog;
    private Spinner spinner_system_defined_group;
    private int spinnerSelectedItem;
    private ImageView dilog_cross_button;
    private TextView mAddListCount;
    private int old_group_pos;

    public static MoveDialog getInstance() {
        if (instance == null) {
            instance = new MoveDialog();
        }
        return instance;
    }

    public void createDialog(Context mContext, String oldGroup, int old_group_pos, String memberList, User user, TextView mAddListCount) {
        this.mContext = mContext;
        this.oldGroup = oldGroup;
        this.old_group_pos = old_group_pos;
        this.memberList = memberList;
        this.user = user;
        this.mAddListCount = mAddListCount;
        customDialog = new Dialog(mContext);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.move_user_popup);
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
        move_btn = (TextView) customDialog.findViewById(R.id.retreive_btn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.system_group_aray, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner_system_defined_group.setAdapter(adapter);
        spinner_system_defined_group.setOnItemSelectedListener(new SystemSpinner());
        move_btn.setOnClickListener(l);
        dilog_cross_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.cancel();
            }
        });
    }

    private void setCustomFont() {
        title.setTypeface(SplashActivity.mpBold);
        lets_start_label_one.setTypeface(SplashActivity.mpRegular);
        move_btn.setTypeface(SplashActivity.mpRegular);
    }

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (oldGroup != null && newGroup != null) {
                if (newGroup.trim().equals(mContext.getResources().getString(R.string.all_group))) {
                    Util.showOKAleart(mContext, "Message", "This is reserved group you can't modify or edit it, Please select another group");
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
        params.put(AppConstant.SENDER_UID, user.id);
        params.put(AppConstant.RECEIVER_UID, memberList);
        params.put(AppConstant.OLD_GROUP_NAME, oldGroup);
        params.put(AppConstant.NEW_GROUP_NAME, newGroup);
        handler.makePostRequest(mContext, params, AppConstant.MOVE_GROUP, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject) {
                        Log.e("I AIM CSDSD", "" + result);
                        JSONObject jsonObject = (JSONObject) json;
                        if (jsonObject.getString("message").trim().equals("ALREADY_FOUND")) {
                            progressDialog.dismiss();
                            Util.showOKAleart(mContext, "Message", "The member already exist in" + "  " + newGroup);
                        } else if (jsonObject.getString("message").trim().equals("SUCCESS")) {
                            saveInPrefsForNewFroup();
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

    private void saveInPrefsForNewFroup() {
        RequestHandler handler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, user.id);
        params.put(AppConstant.GROUP_NAME, newGroup);
        handler.makePostRequest(mContext, params, AppConstant.GET_USER_SYTEM_GROUP, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                writePrefs(spinnerSelectedItem, result, -4);
                saveInPrefsForOldGroup();
            }
        });
    }

    private void saveInPrefsForOldGroup() {
        RequestHandler handler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, user.id);
        params.put(AppConstant.GROUP_NAME, oldGroup);
        handler.makePostRequest(mContext, params, AppConstant.GET_USER_SYTEM_GROUP, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                writePrefs(old_group_pos, result, -3);
            }
        });
    }

    public void writePrefs(int pos, String result, int dismissDailog) {
        switch (pos) {
            case 1:
                Util.writeCoWorkerToPrefrefs(mContext, result);
                break;
            case 2:
                Util.writeClientToPrefrefs(mContext, result);
                break;
            case 3:
                Util.writePartnersToPrefrefs(mContext, result);
                break;
            case 4:
                Util.writeAquitanceToPrefrefs(mContext, result);
                break;
            case 5:
                Util.writeGoodToKnowToPrefrefs(mContext, result);
                break;
            case 6:
                Util.writeFamilyToPrefrefs(mContext, result);
                break;
            case 7:
                Util.writeOthersToPrefrefs(mContext, result);
                break;

            default:
                Toast.makeText(mContext, "Not a valid selection", Toast.LENGTH_LONG).show();
                break;
        }
        if (dismissDailog == -3){
            progressDialog.dismiss();
            showOKAleart(mContext, "Message", "Members moved successfully");
        }
    }

    private class SystemSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(mContext, "Selected postion" + " " + position, Toast.LENGTH_LONG).show();
            spinnerSelectedItem = position;
            selectItem(spinnerSelectedItem);
        }


        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    private void selectItem(int position) {
        switch (position) {
            case 0:
                newGroup = mContext.getResources().getString(R.string.all_group);
                break;
            case 1:
                newGroup = mContext.getResources().getString(R.string.co_worker_group);
                break;
            case 2:
                newGroup = mContext.getResources().getString(R.string.client_group);
                break;
            case 3:
                newGroup = mContext.getResources().getString(R.string.partner_group);
                break;
            case 4:
                newGroup = mContext.getResources().getString(R.string.aquitance_group);
                break;
            case 5:
                newGroup = mContext.getResources().getString(R.string.good_to_know_group);
                break;
            case 6:
                newGroup = mContext.getResources().getString(R.string.family_group);
                break;
            case 7:
                newGroup = mContext.getResources().getString(R.string.other_group);
                break;

            default:
                Toast.makeText(mContext, "Not a valid selection", Toast.LENGTH_LONG).show();
                break;
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
