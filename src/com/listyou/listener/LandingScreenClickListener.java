package com.listyou.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.oxilo.applistyou.R;

public abstract class LandingScreenClickListener implements OnClickListener {

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.QR_btn:
                onHeaderQrClick(view);
                break;
            case R.id.scan_btn:
                onScanBtnClick(view);
                break;

            case R.id.footer_linear_one:
                onContactListBtnClick(view);
                break;

            case R.id.footer_linear_three:
                onAddListBtnClick(view);
                break;
            case R.id.group_image_view:
                onGroupClk(view);

            default:
                break;
        }
    }

    public abstract void onHeaderQrClick(View view);
    public abstract void onScanBtnClick(View view);
    public abstract void onContactListBtnClick(View view);
    public abstract void onAddListBtnClick(View view);
    public abstract void onGroupClk(View view);

}
