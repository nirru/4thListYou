package com.listyou.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.oxilo.applistyou.R;

public abstract class EditProfileClickListener implements OnClickListener {

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.img_profile_pic:
			profilePhotoClick(view);
			break;

		case R.id.img_company_logo_pic:
			companyLogoPhotoClick(view);
			break;

		case R.id.img_bussiness_logo_pic:
			bussinessLogoPhotoClick(view);
			break;

		case R.id.company_country:
			selectCountry(view);
			break;

		case R.id.save:
			saveToServer(view);
			break;

		default:
			break;
		}
	}

	public abstract void profilePhotoClick(View view);

	public abstract void companyLogoPhotoClick(View view);

	public abstract void bussinessLogoPhotoClick(View view);

	public abstract void selectCountry(View view);

	public abstract void saveToServer(View view);

}
