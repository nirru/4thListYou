package com.listyou.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.oxilo.applistyou.R;

public abstract class ShowProfileClickListener implements OnClickListener {

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.original_buss_card_button:
               bussinessCardClick(view);
			break;
			
		case R.id.btn_start:
            startBtnClick(view);
			break;
			
		case R.id.share_button:
            shareBtnClick(view);
			break;
			
		case R.id.back_arrow:
            backBtnClick(view);
			break;
			

		default:
			break;
		}
	}
	
	public abstract void bussinessCardClick(View view);
	public abstract void startBtnClick(View view);
	public abstract void shareBtnClick(View view);
	public abstract void backBtnClick(View view);
}
