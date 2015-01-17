package com.listyou.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.oxilo.applistyou.R;

public abstract class AddListFragementClickListener implements OnClickListener {

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.relative_capture_qr:
                onCaptureQRClick(view);
                break;
            case R.id.relative_invite:
                onInviteClick(view);
                break;

            case R.id.relative_search_by_listyou:
                onSearchByListYouClick(view);
                break;

            case R.id.relative_my_bussiness:
                onMyBussinessCardClick(view);
                break;

            case R.id.search_icon:
                searchListYouUser(view);

            default:
                break;
        }
    }

    public abstract void onCaptureQRClick(View view);
    public abstract void onInviteClick(View view);
    public abstract void onSearchByListYouClick(View view);
    public abstract void onMyBussinessCardClick(View view);
    public abstract void searchListYouUser(View view);

}
