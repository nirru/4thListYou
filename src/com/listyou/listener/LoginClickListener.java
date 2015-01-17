package com.listyou.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.oxilo.applistyou.R;

public abstract class LoginClickListener implements OnClickListener {

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.login:
               signInClick(view);
			break;
			
		case R.id.forgot_password:
            forgotPasswordClick(view);
			break;
			
		case R.id.facebook:
            facebookClick(view);
			break;
			
		case R.id.twitter:
            twitterClick(view);
			break;
			
		case R.id.google:
            googleClick(view);
			break;

		default:
			break;
		}
	}
	
	public abstract void signInClick(View view);
	public abstract void forgotPasswordClick(View view);
	public abstract void facebookClick(View view);
	public abstract void twitterClick(View view);
	public abstract void googleClick(View view);

}
