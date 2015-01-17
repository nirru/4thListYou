package com.oxilo.listyou.register;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.countrypicker.CountryPicker;
import com.countrypicker.CountryPickerListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.listyou.listener.EditProfileClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.oxilo.listyou.login.Login;
import com.util.Util;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class EditProfileActivity extends FragmentActivity {

    ImageView img_profile_pic, img_company_logo, img_bussiness_logo;

    EditText first_name, last_name, first_name_in_another_language,
            last_name_in_another_language, title, company_name, company_email,
            country, city, country_code, mobile_number, fax_code, fax_number,
            telephone_code, telephone_number, adrress, website, skype, listYouID;

    // TextView bussiness_logo_pic_text_view, company_logo_pic_text_view;
    String countryZipCodeNumber;
    Button save;

    ImageView back_buttton;
    AsyncHttpClient client;
    ProgressDialog progressDialog;

    String id, user_first_name, user_last_name, user_email;
    String base64bussiness_Logo = "as", base64ProfilePic = "sdf",
            base64companyLogo = "sdfdf", base64QrLogo = "sdffs";
    ;

    Bitmap bitmapBussinessLogo, bitmapCompanyLogo, bitmapProfileLogo, qrBitmap;

    static final String TAG_CAMERA = "Take Photo";
    static final String TAG_CHOOSE_FROM_LIBRARY = "Choose from Library";
    static final String TAG_CANCEL = "Cancel";
    static final String TAG_ADD_PHOTO = "Add photo";
    static final String TAG_SELECT_FILE = "Select File";
    static final int SELECT_FILE = 1;
    static final int REQUEST_CAMERA = 0;
    int i;

    CountryPicker picker;
    String qrLink, regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        setActonBAr();
        init();
        setCustomFont();
        showUserInfo();
        getQRTextLink();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void setActonBAr() {
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.header);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(
                        android.R.color.transparent)));
    }


    private void init() {
        regid = getIntent().getStringExtra(AppConstant.GCMSENDER);
        Log.e("DEVICE ID", regid);
        client = new AsyncHttpClient();
        user_first_name = getIntent()
                .getStringExtra(AppConstant.REG_FIRST_NAME);
        user_last_name = getIntent().getStringExtra(AppConstant.REG_LAST_NAME);
        user_email = getIntent().getStringExtra(AppConstant.REG_EMAIL);
        id = getIntent().getStringExtra(AppConstant.REG_ID);
        Log.e("USER ID", "" + id);
        picker = CountryPicker.newInstance("Select Country");

        if (progressDialog != null) {
            progressDialog = null;
        }
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);

        img_profile_pic = (ImageView) findViewById(R.id.img_profile_pic);
        img_company_logo = (ImageView) findViewById(R.id.img_company_logo_pic);
        img_bussiness_logo = (ImageView) findViewById(R.id.img_bussiness_logo_pic);

        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        first_name_in_another_language = (EditText) findViewById(R.id.first_name_in_another_language);
        last_name_in_another_language = (EditText) findViewById(R.id.last_name_in_another_language);
        title = (EditText) findViewById(R.id.title);
        company_name = (EditText) findViewById(R.id.company_name);
        company_email = (EditText) findViewById(R.id.company_email);
        country = (EditText) findViewById(R.id.company_country);
        city = (EditText) findViewById(R.id.company_city);
        country_code = (EditText) findViewById(R.id.country_code);
        mobile_number = (EditText) findViewById(R.id.mobile_number);
        fax_code = (EditText) findViewById(R.id.fax_code);
        fax_number = (EditText) findViewById(R.id.fax_number);
        telephone_code = (EditText) findViewById(R.id.telephone_code);
        telephone_number = (EditText) findViewById(R.id.telephone_number);
        adrress = (EditText) findViewById(R.id.address);
        website = (EditText) findViewById(R.id.website);
        skype = (EditText) findViewById(R.id.skype);
        listYouID = (EditText) findViewById(R.id.listyou_id);

        back_buttton = (ImageView) findViewById(R.id.back_arrow);
        save = (Button) findViewById(R.id.save);

        img_profile_pic.setOnClickListener(editListener);
        img_company_logo.setOnClickListener(editListener);
        img_bussiness_logo.setOnClickListener(editListener);
        country.setOnClickListener(editListener);
        save.setOnClickListener(editListener);
        back_buttton.setOnClickListener(backlistener);

        fax_code.setKeyListener(null);
        telephone_code.setKeyListener(null);
        country_code.setKeyListener(null);
    }

    View.OnClickListener backlistener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            finish();
        }
    };

    private void showUserInfo() {

        first_name.setText(user_first_name);
        last_name.setText(user_last_name);
//		company_email.setText(user_email);
    }

    EditProfileClickListener editListener = new EditProfileClickListener() {

        @Override
        public void selectCountry(View view) {
            // TODO Auto-generated method stub
            showCountryListAsDialog();
        }

        @Override
        public void saveToServer(View view) {
            // TODO Auto-generated method stub
            saveData();
        }

        @Override
        public void profilePhotoClick(View view) {
            // TODO Auto-generated method stub
            i = 1;
            selectImage(i);
        }

        @Override
        public void companyLogoPhotoClick(View view) {
            // TODO Auto-generated method stub
            i = 2;
            selectImage(i);
        }

        @Override
        public void bussinessLogoPhotoClick(View view) {
            // TODO Auto-generated method stub
            i = 3;
            selectImage(i);
        }
    };

    CountryPickerListener countryListener = new CountryPickerListener() {

        @Override
        public void onSelectCountry(String name, String code) {
            // TODO Auto-generated method stub
            Log.e("COUNTRY NAME", "" + name + "  COUNTRY CODE " + "  " + code);
            country.setText(name);
            countryZipCodeNumber = GetCountryZipCode(code);
            country_code.setText(countryZipCodeNumber);
            fax_code.setText(countryZipCodeNumber);
            telephone_code.setText(countryZipCodeNumber);
            picker.dismiss();
        }
    };

    private void showCountryListAsDialog() {
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
        picker.setListener(countryListener);
    }

    private String GetCountryZipCode(String countryId) {

        String CountryID = countryId;
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        // getNetworkCountryIso
        // CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int k = 0; k < rl.length; k++) {
            String[] g = rl[k].split(",");
            if (g[1].trim().equals(countryId.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
//		Log.e("COUNTRY ZIP CODE", "" + CountryZipCode);
        return CountryZipCode;
    }

    private void selectImage(final int imageid) {
        final CharSequence[] items = {TAG_CAMERA, TAG_CHOOSE_FROM_LIBRARY,
                TAG_CANCEL};
        AlertDialog.Builder builder = new AlertDialog.Builder(
                EditProfileActivity.this);
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
        if (imaID == 3) {
            bitmapBussinessLogo = bitMapPhoto;
            img_bussiness_logo.setImageBitmap(bitmapBussinessLogo);
            ConvertBitmapToBAse64String(bitmapBussinessLogo,
                    base64bussiness_Logo, 3);
            img_bussiness_logo.setTag("6");
        } else if (imaID == 2) {
            bitmapCompanyLogo = bitMapPhoto;
            img_company_logo.setImageBitmap(bitmapCompanyLogo);
            ConvertBitmapToBAse64String(bitmapCompanyLogo, base64companyLogo, 2);
            img_company_logo.setTag("5");
        } else if (imaID == 1) {
            bitmapProfileLogo = bitMapPhoto;
            img_profile_pic.setImageBitmap(bitmapProfileLogo);
            ConvertBitmapToBAse64String(bitmapProfileLogo, base64ProfilePic, 1);
            img_profile_pic.setTag("4");
        }
    }

    private void getImageFromgallery(Uri selectedImage, int number) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        if (number == 3) {
            bitmapBussinessLogo = EditProfileActivity
                    .decodeSampledBitmapFromResource(picturePath, 100, 100);
            img_bussiness_logo.setImageBitmap(bitmapBussinessLogo);
            ConvertBitmapToBAse64String(bitmapBussinessLogo,
                    base64bussiness_Logo, 3);
            img_bussiness_logo.setTag("6");

            if (bitmapBussinessLogo != null
                    && !bitmapBussinessLogo.isRecycled()) {
                // bitmapBussinessLogo.recycle();
                bitmapBussinessLogo = null;
            }
        } else if (number == 2) {
            bitmapCompanyLogo = EditProfileActivity
                    .decodeSampledBitmapFromResource(picturePath, 100, 100);
            img_company_logo.setImageBitmap(bitmapCompanyLogo);
            ConvertBitmapToBAse64String(bitmapCompanyLogo, base64companyLogo, 2);
            img_company_logo.setTag("5");

            if (bitmapCompanyLogo != null && !bitmapCompanyLogo.isRecycled()) {
                // bitmapBussinessLogo.recycle();
                bitmapCompanyLogo = null;
            }
        } else if (number == 1) {
            bitmapProfileLogo = EditProfileActivity
                    .decodeSampledBitmapFromResource(picturePath, 100, 100);
            img_profile_pic.setImageBitmap(bitmapProfileLogo);
            ConvertBitmapToBAse64String(bitmapProfileLogo, base64ProfilePic, 1);
            img_profile_pic.setTag("4");

            if (bitmapProfileLogo != null && !bitmapProfileLogo.isRecycled()) {
                // bitmapBussinessLogo.recycle();
                bitmapProfileLogo = null;
            }
        }

    }

	/*----------- Android convert image to Base64 String ---------*/

    private void ConvertBitmapToBAse64String(Bitmap photo, String val, int id) {
        if (photo != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String val1 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            if (id == 1) {
                base64ProfilePic = val1;
            } else if (id == 2) {
                base64companyLogo = val1;
            } else if (id == 3) {
                base64bussiness_Logo = val1;
            }
        } else {
            val = "vfg";
        }
    }

	/* reduced Imaage without lossing its quality */

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

    /* Method Closed */
    private void saveData() {

        // if (!checkProfilePic()) {
        // return;
        // }

        if (!checkFirstName()) {
            return;
        }

        if (!checkLastName()) {
            return;
        }

        // if (!checkCompanyPic()) {
        // return;
        // }

        if (!checkTitle()) {
            return;
        }

        if (!checkCompanyName()) {
            return;
        }

        if (!checkCompanyEmail()) {
            return;
        }

        if (!checkCountry()) {
            return;
        }

        if (!checkCity()) {
            return;
        }

        if (!checkMobileNumber()) {
            return;
        }

        if (!checkFaxNumber()) {
            return;
        }

        // if (!checkTelephoneNumber()) {
        // return;
        // }

        if (!checkAddress()) {
            return;
        }
        if (!checkListYouID()) {
            return;
        }


        // if (!checkBussinessPic()) {
        // return;
        // }

        else {
            startConnection();
        }
    }

    private void startConnection() {
        com.listyou.main.ConnectionDetector cd = new com.listyou.main.ConnectionDetector(
                getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            isListYouIdAvialble();
        } else {
            showOKAleart("Connectivity", "You are not connected to internet");
        }
    }

    private void postRequestToServer() {
        RequestParams params = new RequestParams();
        params.put(AppConstant.EDIT_PROFILE_LOGIN_ID, id);
        params.put(AppConstant.EDIT_PROFILE_FIRST_NAME, first_name.getText()
                .toString().trim());
        params.put(AppConstant.EDIT_PROFILE_LAST_NAME, last_name.getText()
                .toString().trim());
        params.put(AppConstant.EDIT_PROFILE_FIRST_NAME_IN_ANOTHER_LANGUAGE,
                first_name_in_another_language.getText().toString().trim());
        params.put(AppConstant.EDIT_PROFILE_LAST_NAME_IN_ANOTHER_LANGUAGE,
                last_name_in_another_language.getText().toString().trim());
        params.put(AppConstant.EDIT_PROFILE_COMPANY_TITLE, title.getText()
                .toString().trim());
        params.put(AppConstant.EDIT_PROFILE_COMPANY_NAME, company_name
                .getText().toString().trim());
        params.put(AppConstant.EDIT_PROFILE_ANOTHER_EMAIL, company_email
                .getText().toString().trim());
        params.put(AppConstant.EDIT_PROFILE_COMPANY_EMAIL, user_email);
        params.put(AppConstant.EDIT_PROFILE_MOBILE_NUMBER, mobile_number
                .getText().toString().trim());
        params.put(AppConstant.EDIT_PROFILE_FAX_NUMBER, fax_number.getText()
                .toString().trim());
        params.put(AppConstant.EDIT_PROFILE_TELEPHONE_NUMBER, telephone_number
                .getText().toString().trim());
        params.put(AppConstant.EDIT_PROFILE_COUNTRY, country.getText()
                .toString().trim());
        params.put(AppConstant.EDIT_PROFILE_COUNTRY_CODE, country_code.getText().toString().trim());

        params.put(AppConstant.EDIT_PROFILE_CITY, city.getText().toString()
                .trim());
        params.put(AppConstant.EDIT_PROFILE_ADDRESS, adrress.getText()
                .toString().trim());
        params.put(AppConstant.EDIT_PROFILE_SKYPE_ID, skype.getText()
                .toString().trim());
        params.put(AppConstant.EDIT_PROFILE_WEBSITE, website.getText()
                .toString().trim());
        params.put(AppConstant.EDIT_PROFILE_PROFILE_PIC, base64ProfilePic);
        params.put(AppConstant.EDIT_PROFILE_COMPANY_LOGO_PIC, base64companyLogo);
        params.put(AppConstant.EDIT_PROFILE_BUSSINESS_LOGO_PIC,
                base64bussiness_Logo);
        params.put(AppConstant.EDIT_PROFILE_QR_LOGO_PIC,
                base64QrLogo);
        params.put(AppConstant.EDIT_PROFILE_LOGIN_ID, id);
//        params.put(AppConstant.LIST_YOU_ID, listYouID.getText()
//                .toString().trim());
        client.post(AppConstant.URL_EDIT_PROFILE_URL, params, responseHandler);

    }

    AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
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
                Util.showOKAleart(EditProfileActivity.this, EditProfileActivity.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(EditProfileActivity.this, EditProfileActivity.this.getString(R.string.error_title), EditProfileActivity.this.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }
    };

    protected void getResponseMessageToShowInAlert(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_MESSAGE);
            Log.e("RESPONSE  ", "" + message);
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
                putValueInPrefs();
                Intent i = new Intent(EditProfileActivity.this,
                        Login.class);
                Log.e("<<>>USER ID", "" + id);
                i.putExtra(AppConstant.REG_FIRST_NAME, Integer.parseInt(id));
                i.putExtra(AppConstant.GCMSENDER, regid);
                startActivity(i);
                finish();
            }
        });
        alertDialog.show();
    }

    private void putValueInPrefs() {
        // Toast.makeText(LoginProfileActivity.this, "Data saved succesfully",
        // Toast.LENGTH_LONG).show();
        SharedPreferences.Editor prefs = EditProfileActivity.this
                .getSharedPreferences("com.oxilo.listyou_app_country_code",
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.REG_ID, id);
        prefs.putString(AppConstant.EDIT_PROFILE_BUSSINESS_LOGO_PIC,
                base64bussiness_Logo);
        prefs.putString(AppConstant.EDIT_PROFILE_COMPANY_LOGO_PIC,
                base64companyLogo);
        prefs.putString(AppConstant.EDIT_PROFILE_PROFILE_PIC, base64ProfilePic);
        prefs.commit();
    }

    private boolean checkProfilePic() {
        if (img_profile_pic
                .getDrawable()
                .getConstantState()
                .equals(getResources().getDrawable(R.drawable.icon_capture)
                        .getConstantState())) {
            showOKAleart("Message", "Please select profile picture");
            return false;
        }
        return true;
    }

    private boolean checkCompanyPic() {
        if (img_company_logo.getTag().equals("2")) {
            showOKAleart("Message", "Please select image for company logo");
            return false;
        }
        return true;
    }

    private boolean checkBussinessPic() {
        if (img_bussiness_logo.getTag().equals("3")) {
            showOKAleart("Message", "Please select image for bussiness logo");
            return false;
        }
        return true;
    }

    private boolean checkFirstName() {

        if (first_name.getText().toString().equals("")) {
            showOKAleart("Message", "First Name field is Blank");
            return false;
        }
        return true;
    }

    private boolean checkLastName() {

        if (last_name.getText().toString().equals("")) {
            showOKAleart("Message", "Surname Name field is Blank");
            return false;
        }
        return true;
    }

    private boolean checkTitle() {

        if (title.getText().toString().equals("")) {
            showOKAleart("Message", "Title field is Blank");
            return false;
        }
        return true;
    }

    private boolean checkCompanyName() {

        if (company_name.getText().toString().equals("")) {
            showOKAleart("Message", "Company name field is Blank");
            return false;
        }
        return true;
    }

    private boolean checkCompanyEmail() {

        if (company_email.getText().toString().equals("")) {
            showOKAleart("Message", "Company email field is Blank");
            return false;
        }
        return true;
    }

    public boolean checkEmailPattern() {
        Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9+._%-+]{1,100}"
                + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,10}" + "(" + "."
                + "[a-zA-Z0-9][a-zA-Z0-9-]{0,20}" + ")+");
        if (!EMAIL_PATTERN.matcher(company_email.getText().toString())
                .matches()) {
            showOKAleart("Message", "Please enter the correct email");
            return false;
        }
        return true;
    }

    private boolean checkCountry() {

        if (country.getText().toString().equals("")) {
            showOKAleart("Message", "Country field is Blank");
            return false;
        }
        return true;
    }

    private boolean checkCity() {

        if (city.getText().toString().equals("")) {
            showOKAleart("Message", "City field is Blank");
            return false;
        }
        return true;
    }

    private boolean checkMobileNumber() {

        if (mobile_number.getText().toString().equals("")) {
            showOKAleart("Message", "Mobile number field is Blank");
            return false;
        }
        return true;
    }

    private boolean checkFaxNumber() {

        if (fax_number.getText().toString().equals("")) {
            showOKAleart("Message", "Fax number field is Blank");
            return false;
        }
        return true;
    }

    private boolean checkTelephoneNumber() {

        if (telephone_number.getText().toString().equals("")) {
            showOKAleart("Message", "Telephone number field is Blank");
            return false;
        }
        return true;
    }

    private boolean checkAddress() {

        if (adrress.getText().toString().equals("")) {
            showOKAleart("Message", "Address field is Blank");
            return false;
        }
        return true;
    }

    private boolean checkListYouID() {
        if (listYouID.getText().toString().equals("")) {
            showOKAleart("Message", "ListYou Id is Blank");
            return false;
        }
        return true;
    }

    private void isListYouIdAvialble() {
        RequestParams params = new RequestParams();
        params.put(AppConstant.LOGIN_USE_ID, id);
        params.put(AppConstant.LIST_YOU_ID, listYouID.getText().toString()
                .trim());
        client.post(AppConstant.USER_LISTYOU_ID_URL, params, mResponseHandler);
    }

    AsyncHttpResponseHandler mResponseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String msg = "";
            try {
                String response = String.valueOf(new String(bytes, "UTF-8"));
                Log.e("<<<FINAL RESPONSE>>>  ", "" + response);
                JSONObject jsonObject = new JSONObject(response);
                msg = jsonObject.getString(AppConstant.LOGIN_MESSAGE);
                if (msg.trim().equals("ALREADY_FOUND")) {
                    progressDialog.dismiss();
                    showOKAleart(EditProfileActivity.this.getResources().getString(R.string.message_error), EditProfileActivity.this.getResources().getString(R.string.already_found));
                } else if (msg.trim().equals("CANT_UPDATE")) {
                    progressDialog.dismiss();
                    showOKAleart(EditProfileActivity.this.getResources().getString(R.string.message_error), EditProfileActivity.this.getResources().getString(R.string.cant_update));

                } else if (msg.trim().equals("SUCCESS")) {
                    createQrInAnotherThread();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e1) {

            }

        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(EditProfileActivity.this, EditProfileActivity.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(EditProfileActivity.this, EditProfileActivity.this.getString(R.string.error_title), EditProfileActivity.this.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onProgress(int bytesWritten, int totalSize) {
            progressDialog.setProgress((int) bytesWritten);
        }

        @Override
        public void onStart() {
            progressDialog.show();
        }
    };

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
            }
        });
        alertDialog.show();
    }

    private void setCustomFont() {
        first_name.setTypeface(SplashActivity.mpRegular);
        last_name.setTypeface(SplashActivity.mpRegular);
        first_name_in_another_language.setTypeface(SplashActivity.mpRegular);
        last_name_in_another_language.setTypeface(SplashActivity.mpRegular);
        title.setTypeface(SplashActivity.mpRegular);
        company_name.setTypeface(SplashActivity.mpRegular);
        company_email.setTypeface(SplashActivity.mpRegular);
        country.setTypeface(SplashActivity.mpRegular);
        city.setTypeface(SplashActivity.mpRegular);
        country_code.setTypeface(SplashActivity.mpRegular);
        mobile_number.setTypeface(SplashActivity.mpRegular);
        fax_code.setTypeface(SplashActivity.mpRegular);
        fax_number.setTypeface(SplashActivity.mpRegular);
        telephone_code.setTypeface(SplashActivity.mpRegular);
        telephone_number.setTypeface(SplashActivity.mpRegular);
        adrress.setTypeface(SplashActivity.mpRegular);
        website.setTypeface(SplashActivity.mpRegular);
        skype.setTypeface(SplashActivity.mpRegular);
        listYouID.setTypeface(SplashActivity.mpRegular);
    }

    private void createQrInAnotherThread() {
//        progressDialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // code that needs 6 seconds for execution
                writeQr();
//                writeToFile(qrBitmap,"qrImagePic.png");
                postRequestToServer();

                // after finishing, close the progress bar
//                progressDialog.dismiss();
            }
        };

        new Thread(runnable).start();
    }

    private void writeQr() {

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrLink, BarcodeFormat.QR_CODE, 350, 350);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(R.color.qr_color) : Color.WHITE);
                }
            }
            qrBitmap = bmp;
            qrCodeBase64(bmp, base64QrLogo);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void qrCodeBase64(final Bitmap photo, String val) {
        if (photo != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String val1 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            base64QrLogo = val1;
            EditProfileActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Util.saveImageToExternalStorage(EditProfileActivity.this, photo, "QrCode");
                }
            });
        } else {
            val = "vfg";
        }
    }

    private void getQRTextLink() {
        RequestParams params = new RequestParams();
        params.put(AppConstant.LOGIN_USE_ID, id);
        client.post(AppConstant.URL_QR_CODE_GET_CONTENT, params,
                qrResponseHandler);
    }

    AsyncHttpResponseHandler qrResponseHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            try {
                String response = String.valueOf(new String(bytes, "UTF-8"));
                qrLink = response.trim();
                Log.e("URL_QR", qrLink);
            } catch (UnsupportedEncodingException e1) {

            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(EditProfileActivity.this, EditProfileActivity.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(EditProfileActivity.this, EditProfileActivity.this.getString(R.string.error_title), EditProfileActivity.this.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }
    };
}
