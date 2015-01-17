package com.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.drawer.BlockUser;
import com.landing.screen.ReportUser;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.response.User;

/**
 * Created by C-ShellWin on 1/10/2015.
 */
public class SettingsDialog {

    private static SettingsDialog instance;
    private Context mContext;
    private User user;
    private TextView mAddListCount;
    private Dialog customDialog;
    private boolean isManageGroupToShow;
    private ImageView dialog_cross_button;

    private TextView header, reportUser, deleteUser, manageGroups;

    public static SettingsDialog getInstance() {
        if (instance == null) {
            instance = new SettingsDialog();
        }
        return instance;
    }

    public void createDialog(Context mContext, User user, TextView mAddListCount, boolean isManageGroupToShow) {
        this.mContext = mContext;
        this.user = user;
        this.mAddListCount = mAddListCount;
        this.isManageGroupToShow = isManageGroupToShow;
        customDialog = new Dialog(mContext);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.report_user_popup);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();
        init();
        setCustomFont();
        showHideManageGroup();
    }

    private void setCustomFont() {
        header.setTypeface(SplashActivity.mpBold);
        reportUser.setTypeface(SplashActivity.mpRegular);
        deleteUser.setTypeface(SplashActivity.mpRegular);
        manageGroups.setTypeface(SplashActivity.mpRegular);
    }

    private void init() {

        dialog_cross_button = (ImageView) customDialog.findViewById(R.id.cross_button);
        header = (TextView) customDialog.findViewById(R.id.settings);
        reportUser = (TextView) customDialog.findViewById(R.id.lets_start_label_one);
        deleteUser = (TextView) customDialog.findViewById(R.id.lets_start_label_two);
        manageGroups = (TextView) customDialog.findViewById(R.id.lets_start_label_three);

        reportUser.setOnClickListener(l);
        deleteUser.setOnClickListener(l);
        manageGroups.setOnClickListener(l);
        dialog_cross_button.setOnClickListener(l);
    }

    private void showHideManageGroup() {
        if (isManageGroupToShow) {
            manageGroups.setVisibility(View.VISIBLE);
        } else {
            manageGroups.setVisibility(View.GONE);
        }
    }

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.lets_start_label_one:
                    reportUser();
                    break;

                case R.id.lets_start_label_two:
                    deleteUser();
                    break;

                case R.id.lets_start_label_three:
                    manageGroups();
                    break;

                case R.id.cross_button:
                    customDialog.cancel();
                    break;

                default:
                    break;
            }

        }
    };

    private void reportUser() {
        customDialog.cancel();
        Fragment fragment = new ReportUser(mContext, user, mAddListCount);
        FragmentManager fragmentManager = ((Activity) mContext).getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).addToBackStack(null).commit();
    }

    private void deleteUser() {
        customDialog.cancel();
        Fragment fragment = new BlockUser(mContext, user, mAddListCount);
        FragmentManager fragmentManager = ((Activity) mContext).getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).addToBackStack(null).commit();
    }

    private void manageGroups() {
        customDialog.cancel();
    }

}
