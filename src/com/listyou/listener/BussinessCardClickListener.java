package com.listyou.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.oxilo.applistyou.R;

public abstract class BussinessCardClickListener implements OnClickListener {

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.back_arrow:
               backButtonClick(view);
			break;
			
		case R.id.change_pic:
            changePicBtnClick(view);
			break;
			
		case R.id.save:
            saveClick(view);
			break;

		default:
			break;
		}
	}
	
	public abstract void backButtonClick(View view);
	public abstract void changePicBtnClick(View view);
	public abstract void saveClick(View view);
}
