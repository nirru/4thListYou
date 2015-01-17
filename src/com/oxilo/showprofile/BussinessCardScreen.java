package com.oxilo.showprofile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.listyou.listener.BussinessCardClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxilo.applistyou.R;
import com.oxilo.listyou.constant.AppConstant;
import com.oxilo.listyou.login.LoginProfileActivity;
import com.util.Util;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BussinessCardScreen extends Activity {

	ImageView back_buttton, user_bussiness_logo;

	ProgressDialog progressDialog;
	AsyncHttpClient client;

	static final String TAG_CAMERA = "Take Photo";
	static final String TAG_CHOOSE_FROM_LIBRARY = "Choose from Library";
	static final String TAG_CANCEL = "Cancel";

	static final String TAG_ADD_PHOTO = "Add photo";
	static final String TAG_SELECT_FILE = "Select File";
	static final int SELECT_FILE = 1;
	static final int REQUEST_CAMERA = 0;
	Bitmap bitmapBussinessLogo;
	ImageView imageView;
	String base64bussiness_Logo = "as", base64ProfilePic = "sdf",
			base64companyLogo = "sdfdf";;
	int i, k = 0;
	String idd;
	ImageView change_pic;
	Button save;

	DisplayImageOptions options;
	public ImageLoader imageLoader;
	AnimateFirstDisplayListener animateFirstDisplayListener;

	String id, user_first_name, user_last_name,
			user_first_name_in_another_language,
			user_last_name_in_another_language, user_title, user_company_name,
			user_email, user_country, user_country_code, user_city,
			user_mobile_number, user_fax_number, user_telephone_number,
			user_address, user_website, user_messanger, user_profile_url,
			user_company_url, user_bussiness_url, user_another_email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bussiness_card_screen);
		setActonBAr();
		init();
		fetchDataDromServer();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		animateFirstDisplayListener.displayedImages.clear();
	}

	private void setActonBAr() {
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.header_bussiness_card);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));
	}

	private void init() {
		animateFirstDisplayListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		BussinessCardScreen.this.imageLoader.init(ImageLoaderConfiguration
				.createDefault(getBaseContext()));
		options = new DisplayImageOptions.Builder().showImageOnLoading(null)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(null).cacheInMemory(false).cacheOnDisk(false)
				.considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(0)).build();
		client = new AsyncHttpClient();
		progressDialog = new ProgressDialog(BussinessCardScreen.this);
		progressDialog.setMessage("Loading...");
		progressDialog.setCanceledOnTouchOutside(false);
		SharedPreferences sharedPreferences = this.getSharedPreferences(
				"com.oxilo.listyou_app_country_code",
				ShowProfileActivity.MODE_PRIVATE);
		id = sharedPreferences.getString(AppConstant.REG_ID, "");
		base64bussiness_Logo = sharedPreferences.getString(
				AppConstant.EDIT_PROFILE_BUSSINESS_LOGO_PIC, "");
		base64companyLogo = sharedPreferences.getString(
				AppConstant.EDIT_PROFILE_COMPANY_LOGO_PIC, "");
		base64ProfilePic = sharedPreferences.getString(
				AppConstant.EDIT_PROFILE_PROFILE_PIC, "");
		// Log.e("USER ID IS NULL OR NOT", "" + base64bussiness_Logo);

		change_pic = (ImageView) findViewById(R.id.change_pic);
		user_bussiness_logo = (ImageView) findViewById(R.id.imageView1);
		back_buttton = (ImageView) findViewById(R.id.back_arrow);
		save = (Button) findViewById(R.id.save);

		back_buttton.setOnClickListener(bussinessCardClickListener);
		change_pic.setOnClickListener(bussinessCardClickListener);
		save.setOnClickListener(bussinessCardClickListener);

		user_bussiness_logo.setId(1);
	}

	private void showPic() {
		if (user_bussiness_url.equals("") || user_bussiness_url == null
				|| user_bussiness_url == "null") {
//			Log.e("I AI IN IF CONDITION", "YES");
			user_bussiness_logo.setImageDrawable(null);
			progressDialog.dismiss();
		} else {
//			Log.e("I AI IN ELSE CONDITION", "YESHAHAHA" + " "
//					+ user_bussiness_url);
			user_bussiness_logo.setImageDrawable(null);
			// UrlImageViewHelper.setUrlDrawable(user_bussiness_logo,
			// user_bussiness_url);
			// ImageLoadingListener animateFirstListener = new
			// AnimateFirstDisplayListener();
			imageLoader.displayImage(user_bussiness_url, user_bussiness_logo,
					options, animateFirstDisplayListener);
//			Log.e("hahahahahhha", "YESHAHAHA" + " " + user_bussiness_url);
			// progressDialog.dismiss();
		}

	}

	private void fetchDataDromServer() {
		progressDialog.show();
		RequestParams params = new RequestParams();
		params.put(AppConstant.EDIT_PROFILE_LOGIN_ID, id);
		client.post(AppConstant.URL_SHOW_PROFILE_URL, params, responseHandler);
	}

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
		@Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
//			Log.e("FINAL RESPONSE  ", "" + response);
            try {
                String response = String.valueOf(new String(bytes, "UTF-8"));
                setUserDetailToField(response);
            } catch (UnsupportedEncodingException e1) {
            }

		}

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(BussinessCardScreen.this, BussinessCardScreen.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(BussinessCardScreen.this, BussinessCardScreen.this.getString(R.string.error_title), BussinessCardScreen.this.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }
	};

	private void setUserDetailToField(String response) {
		try {
			user_bussiness_url = null;
			JSONObject jsonObject = new JSONObject(response);
			user_first_name = jsonObject
					.getString(AppConstant.EDIT_PROFILE_FIRST_NAME);
			user_last_name = jsonObject
					.getString(AppConstant.EDIT_PROFILE_LAST_NAME);
			user_country_code = jsonObject
					.getString(AppConstant.EDIT_PROFILE_COUNTRY_CODE);

			user_first_name_in_another_language = jsonObject
					.getString(AppConstant.EDIT_PROFILE_FIRST_NAME_IN_ANOTHER_LANGUAGE);
			user_last_name_in_another_language = jsonObject
					.getString(AppConstant.EDIT_PROFILE_LAST_NAME_IN_ANOTHER_LANGUAGE);

			user_title = jsonObject
					.getString(AppConstant.EDIT_PROFILE_COMPANY_TITLE);
			user_email = jsonObject
					.getString(AppConstant.EDIT_PROFILE_COMPANY_EMAIL);

			user_another_email = jsonObject
					.getString(AppConstant.EDIT_PROFILE_ANOTHER_EMAIL);
			user_company_name = jsonObject
					.getString(AppConstant.EDIT_PROFILE_COMPANY_NAME);
			user_country = jsonObject
					.getString(AppConstant.EDIT_PROFILE_COUNTRY);
			user_country_code = jsonObject
					.getString(AppConstant.EDIT_PROFILE_COUNTRY_CODE);
			user_city = jsonObject.getString(AppConstant.EDIT_PROFILE_CITY);
			user_mobile_number = jsonObject
					.getString(AppConstant.EDIT_PROFILE_MOBILE_NUMBER);
			user_fax_number = jsonObject
					.getString(AppConstant.EDIT_PROFILE_FAX_NUMBER);
			user_telephone_number = jsonObject
					.getString(AppConstant.EDIT_PROFILE_TELEPHONE_NUMBER);
			user_address = jsonObject
					.getString(AppConstant.EDIT_PROFILE_ADDRESS);
			user_website = jsonObject
					.getString(AppConstant.EDIT_PROFILE_WEBSITE);
			user_messanger = jsonObject
					.getString(AppConstant.EDIT_PROFILE_SKYPE_ID);

			user_profile_url = jsonObject
					.getString(AppConstant.SHOW_PROFILE_PROFILE_PIC_URL);
			user_bussiness_url = jsonObject
					.getString(AppConstant.SHOW_PROFILE_BUSSINESS_PIC_URL);
			user_company_url = jsonObject
					.getString(AppConstant.SHOW_PROFILE_COMPANY_PIC_URL);

			showPic();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void selectImage(final int imageid) {
		final CharSequence[] items = { TAG_CAMERA, TAG_CHOOSE_FROM_LIBRARY,
				TAG_CANCEL };
		AlertDialog.Builder builder = new AlertDialog.Builder(
				BussinessCardScreen.this);
		builder.setTitle(TAG_ADD_PHOTO);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals(TAG_CAMERA)) {
					takePictureFromCamera(imageid);
				} else if (items[item].equals(TAG_CHOOSE_FROM_LIBRARY)) {
					takePictuureFromGallery(imageid);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	private void takePictuureFromGallery(int imageID) {

		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		intent.putExtra("imageId", imageID);
		startActivityForResult(Intent.createChooser(intent, TAG_SELECT_FILE),
				SELECT_FILE);
	}

	private void takePictureFromCamera(int imageID) {
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra("imageId", imageID);
		startActivityForResult(
				Intent.createChooser(cameraIntent, "TAG_SELECT_FILE"),
				REQUEST_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Log.e("tag","" + imageid);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CAMERA) {
//				Log.e("IMAGE NUMBER", "" + i);
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				saveImageToImageView(i, photo);
			} else if (requestCode == SELECT_FILE) {
				Uri selectedImageUri = data.getData();
				getImageFromgallery(selectedImageUri, i);
			}
		}
	}

	private void saveImageToImageView(int imaID, Bitmap bitMapPhoto) {

		bitmapBussinessLogo = bitMapPhoto;
		user_bussiness_logo.setImageBitmap(bitmapBussinessLogo);
		ConvertBitmapToBAse64String(bitmapBussinessLogo, base64bussiness_Logo,
				1);
		k = 1;

		if (bitmapBussinessLogo != null && !bitmapBussinessLogo.isRecycled()) {
			// bitmapBussinessLogo.recycle();
			bitmapBussinessLogo = null;
			// temp = null;
		}
	}

	private void getImageFromgallery(Uri selectedImage, int number) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();

		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inSampleSize = 1;
		// Bitmap temp = BitmapFactory.decodeFile(picturePath, options);
		// user_bussiness_logo.setImageBitmap(temp);

		bitmapBussinessLogo = BussinessCardScreen
				.decodeSampledBitmapFromResource(picturePath, 300, 300);
		user_bussiness_logo.setImageBitmap(bitmapBussinessLogo);
		ConvertBitmapToBAse64String(bitmapBussinessLogo, base64bussiness_Logo,
				1);
		k = 1;

		if (bitmapBussinessLogo != null && !bitmapBussinessLogo.isRecycled()) {
			// bitmapBussinessLogo.recycle();
			bitmapBussinessLogo = null;
			// temp = null;
		}
	}

	/*----------- Android convert image to Base64 String ---------*/

	private void ConvertBitmapToBAse64String(Bitmap photo, String val, int id) {
		if (photo != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] imageBytes = baos.toByteArray();
			String val1 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
			base64bussiness_Logo = val1;
		} else {
			val = "vfg";
		}

		// Log.e("HITIT", "" + base64bussiness_Logo);
	}

	public static Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	BussinessCardClickListener bussinessCardClickListener = new BussinessCardClickListener() {

		@Override
		public void saveClick(View view) {
			// TODO Auto-generated method stub
			saveToServer();
		}

		@Override
		public void changePicBtnClick(View view) {
			// TODO Auto-generated method stub
			selectImage(1);
		}

		@Override
		public void backButtonClick(View view) {
			// TODO Auto-generated method stub
			finish();
		}
	};

	private void saveToServer() {

		if (k == 0) {
			showOKAleart("Message", "Saved");
		} else {
			progressDialog.show();
			update();
		}
	}

	@SuppressWarnings("deprecation")
	public void showOKAleart(String title, String message) {
		final AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setTitle("Message");
		alertDialog.setMessage(message);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				finish();
			}
		});
		alertDialog.show();
	}

	private void update() {
		RequestParams params = new RequestParams();
		params.put(AppConstant.EDIT_PROFILE_FIRST_NAME, user_first_name.trim());
		params.put(AppConstant.EDIT_PROFILE_LAST_NAME, user_last_name.trim());
		params.put(AppConstant.EDIT_PROFILE_FIRST_NAME_IN_ANOTHER_LANGUAGE,
				user_first_name_in_another_language.trim());

		params.put(AppConstant.EDIT_PROFILE_LAST_NAME_IN_ANOTHER_LANGUAGE,
				user_last_name_in_another_language.trim());
		params.put(AppConstant.EDIT_PROFILE_COMPANY_TITLE, user_title.trim());
		params.put(AppConstant.EDIT_PROFILE_COMPANY_EMAIL, user_email.trim());
		params.put(AppConstant.EDIT_PROFILE_ANOTHER_EMAIL,
				user_another_email.trim());
		params.put(AppConstant.EDIT_PROFILE_COMPANY_NAME,
				user_company_name.trim());
		params.put(AppConstant.EDIT_PROFILE_MOBILE_NUMBER,
				user_mobile_number.trim());
		params.put(AppConstant.EDIT_PROFILE_FAX_NUMBER, user_fax_number.trim());
		params.put(AppConstant.EDIT_PROFILE_TELEPHONE_NUMBER,
				user_telephone_number.trim());
		params.put(AppConstant.EDIT_PROFILE_COUNTRY, user_country.trim());
		params.put(AppConstant.EDIT_PROFILE_CITY, user_city.trim());
		params.put(AppConstant.EDIT_PROFILE_ADDRESS, user_address.trim());
		params.put(AppConstant.EDIT_PROFILE_COUNTRY_CODE,
				user_country_code.trim());
		params.put(AppConstant.EDIT_PROFILE_WEBSITE, user_website.trim());
		params.put(AppConstant.EDIT_PROFILE_SKYPE_ID, user_messanger.trim());
		params.put(AppConstant.EDIT_PROFILE_PROFILE_PIC, base64ProfilePic);
		params.put(AppConstant.EDIT_PROFILE_COMPANY_LOGO_PIC, base64companyLogo);
		params.put(AppConstant.EDIT_PROFILE_BUSSINESS_LOGO_PIC,
				base64bussiness_Logo);
		params.put(AppConstant.EDIT_PROFILE_LOGIN_ID, id);

		client.post(AppConstant.URL_EDIT_PROFILE_URL, params, responseHandler1);
	}

	AsyncHttpResponseHandler responseHandler1 = new AsyncHttpResponseHandler() {
		@Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            try {
                String response = String.valueOf(new String(bytes, "UTF-8"));
                progressDialog.dismiss();
                getResponseMessageToShowInAlert(response);
            } catch (UnsupportedEncodingException e1) {

            }
		}

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(BussinessCardScreen.this, BussinessCardScreen.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(BussinessCardScreen.this, BussinessCardScreen.this.getString(R.string.error_title), BussinessCardScreen.this.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }
	};

	protected void getResponseMessageToShowInAlert(String response) {
		try {
//			Log.e("RESPONSE  ", "" + response);
			JSONObject jsonObject = new JSONObject(response);
			String message = jsonObject
					.getString(AppConstant.EDIT_PROFILE_MESSAGE);
			// Log.e("RESPONSE  ", "" + message);
			saveSuccessfull(message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveSuccessfull(String message) {
		if (message.trim().equals(AppConstant.EDIT_PROFILE_SUCCESSFULL_MESSAGE)
				|| message.trim().equals(
						AppConstant.EDIT_PROFILE_ALREADY_UPDATED)) {

			showOKAleartOnSuccesFull(
					"Message",
					getResources().getString(
							R.string.edit_profile_diaplay_text_on_save));

		} else {
			showOKAleart("Message", message);
			return;

		}

	}

	@SuppressWarnings("deprecation")
	public void showOKAleartOnSuccesFull(String title, final String message) {
		final AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setTitle("Message");
		alertDialog.setMessage(message);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				finish();
			}
		});
		alertDialog.show();
	}

	private class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
//				Log.e("YES MAI HOO DON", "  " + "AYA DON");
				ImageView imageView = (ImageView) view;
				if (imageView.getId() == R.id.imageView1) {
//					Log.e("MERE DIL MAI HAI KON", "  " + "AYA DON");
					ConvertBitmapToBAse64String(bitmapBussinessLogo,
							base64bussiness_Logo, 1);
				}
				// ImageView imageView = (ImageView) view;
				// boolean firstDisplay = !displayedImages.contains(imageUri);
				// if (firstDisplay) {
				// FadeInBitmapDisplayer.animate(imageView, 500);
				// displayedImages.add(imageUri);
				// }

			}
			progressDialog.dismiss();
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			// TODO Auto-generated method stub
			super.onLoadingFailed(imageUri, view, failReason);
//			Log.e("LOADING FAILED", "  " + "AYA DON");
			progressDialog.dismiss();
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			// TODO Auto-generated method stub
			super.onLoadingCancelled(imageUri, view);
//			Log.e("LOADING Cancelled", "  " + "AYA DON");
			progressDialog.dismiss();
		}

	}

}
