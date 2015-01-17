package com.listyou.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.oxilo.applistyou.R;

public abstract class RegisterClickListener implements OnClickListener {

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.create_account:
               createAccountBtnClick(view);
			break;
			
		case R.id.facebook:
            facebbokBtnClick(view);
			break;
			
		case R.id.twitter:
            twitterBtnClick(view);
			break;
			
		case R.id.google:
            googleBtnClick(view);
			break;
			
		case R.id.back_arrow:
            backBtnClick(view);
			break;
			
		case R.id.relative_web:
            onrelativeWeb(view);
			break;

		default:
			break;
		}
	}
	
	public abstract void createAccountBtnClick(View view);
	public abstract void facebbokBtnClick(View view);
	public abstract void twitterBtnClick(View view);
	public abstract void googleBtnClick(View view);
	public abstract void backBtnClick(View view);
	public abstract void onrelativeWeb(View view);
}
