package com.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.response.User;

/**
 * Created by C-ShellWin on 12/15/2014.
 */
public class InviteDialog {
    private static InviteDialog instance;
    private Dialog customDialog;
    private TextView mSms, mEmail, mShare;
    private Button retreive_butoon;
    private ImageView dilog_cross_button;
    private Context mContext;
    private User user;

    public static InviteDialog getInstance() {
        if (instance == null) {
            instance = new InviteDialog();
        }
        return instance;
    }

    public void creatInviteDialogBySmsEmailAndShare(Context mContext , User user) {
        this.mContext = mContext;
        this.user = user;
        customDialog = new Dialog(mContext);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.invite_friend_popup);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();

        init();
    }

    private void init() {
        mSms = (TextView) customDialog
                .findViewById(R.id.sms);
        mEmail = (TextView) customDialog
                .findViewById(R.id.email);
        mShare = (TextView) customDialog
                .findViewById(R.id.share);
        TextView inviteFriend_By = (TextView) customDialog
                .findViewById(R.id.invite_friend_by);
        mSms.setTypeface(SplashActivity.mpRegular);
        mEmail.setTypeface(SplashActivity.mpRegular);
        mShare.setTypeface(SplashActivity.mpRegular);
        inviteFriend_By.setTypeface(SplashActivity.mpBold);

        dilog_cross_button = (ImageView) customDialog
                .findViewById(R.id.cross_button);
        dilog_cross_button.setOnClickListener(crossListener);
        mEmail.setOnClickListener(emailListener);
        mShare.setOnClickListener(shareListener);
        mSms.setOnClickListener(smsListener);
    }

    View.OnClickListener smsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customDialog.cancel();
            Freinds.sms(mContext, user);
        }
    };

    View.OnClickListener crossListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.cancel();
        }
    };

    View.OnClickListener emailListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.cancel();
            Freinds.sendEmail(mContext, user, "QrCode");
        }
    };
    View.OnClickListener shareListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.cancel();
            Freinds.share(mContext, user, "QrCode");
        }
    };
}
