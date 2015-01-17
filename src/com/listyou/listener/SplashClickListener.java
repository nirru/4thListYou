package com.listyou.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.oxilo.applistyou.R;

public abstract class SplashClickListener implements OnClickListener {

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.splash_register_btn:
               registerBtnClick(view);
			break;

		case R.id.splash_login_btn:
               loginBtnClk(view);
			break;

		default:
			break;
		}
	}
	
	public abstract void registerBtnClick(View view);
	public abstract void loginBtnClk(View view);

}
