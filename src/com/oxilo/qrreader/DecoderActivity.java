package com.oxilo.qrreader;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.landing.screen.MainLandingActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.oxilo.showprofile.ShowProfileActivity;
import com.response.User;
import com.util.Util;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DecoderActivity extends Activity implements OnQRCodeReadListener {

    private QRCodeReaderView mydecoderview;
    private ImageView qr_imageView;

    Bitmap bitmapBussinessLogo, bitmapCompanyLogo, bitmapProfileLogo;

    static final String TAG_CAMERA = "Take Photo";
    static final String TAG_CHOOSE_FROM_LIBRARY = "Choose from Library";
    static final String TAG_CANCEL = "Cancel";
    static final String TAG_ADD_PHOTO = "Add photo";
    static final String TAG_SELECT_FILE = "Select File";
    static final int SELECT_FILE = 1;
    static final int REQUEST_CAMERA = 0;

    Button gallery;
    TextView header_Title, useGallery;


    Dialog customDialog;

    Button retreive_butoon;
    ImageView dilog_cross_button;
    ImageView mUserImageView, mUserCompanyImageView;
    TextView mUserName, mUserDesignation, mUserCompanyName;
    String mUniqueQrCodedigit;

    AsyncHttpClient client;
    ProgressDialog progressDialog;

    String qrLink, myQrLink;
    DisplayImageOptions options;
    public ImageLoader imageLoader;
    AnimateFirstDisplayListener animateFirstDisplayListener;
    private ProgressBar spinner1;
    String id, reciever_id;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoder);
        setActonBAr();
        init();
    }

    private void setActonBAr() {
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.scan_header);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
        // getActionBar().setIcon(R.drawable.side_drawer);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(
                        android.R.color.transparent)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void init() {
        user = getIntent().getParcelableExtra(AppConstant.USER);

        animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader = ImageLoader.getInstance();
        DecoderActivity.this.imageLoader.init(ImageLoaderConfiguration
                .createDefault(getBaseContext()));
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(null)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(0)).build();
        client = new AsyncHttpClient();

        id = getSenderID();
        progressDialog = new ProgressDialog(DecoderActivity.this);
        header_Title = (TextView) findViewById(R.id.header);
        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
        qr_imageView = (ImageView) findViewById(R.id.qrdecoderimageview);
        gallery = (Button) findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e("SRTTTT", "Fsdfdf");
                mydecoderview.getCameraManager().stopPreview();
                mydecoderview.setVisibility(View.GONE);
                selectImage(1);
                Log.e("SRTTTT", "Fsdfdf");
            }
        });
    }

    private void initDialogElement() {
        spinner1 = (ProgressBar) customDialog.findViewById(R.id.progress_bar_one);
        mUserImageView = (ImageView) customDialog.findViewById(R.id.user_profile_pic);
        mUserName = (TextView) customDialog.findViewById(R.id.profile_name);
        mUserDesignation = (TextView) customDialog.findViewById(R.id.designation);
        mUserCompanyName = (TextView) customDialog.findViewById(R.id.company);
        retreive_butoon = (Button) customDialog.findViewById(R.id.retreive_btn);
        mUserImageView.setVisibility(View.INVISIBLE);
        dilog_cross_button = (ImageView) customDialog
                .findViewById(R.id.cross_button);
        retreive_butoon.setOnClickListener(retreiveListener);
        dilog_cross_button.setOnClickListener(crossListener);

        customDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        customDialog.cancel();
                    }
                });
    }

    View.OnClickListener crossListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.dismiss();
            mydecoderview.getCameraManager().stopPreview();
            mydecoderview.getCameraManager().startPreview();
            mydecoderview.setVisibility(View.VISIBLE);
            qr_imageView.setVisibility(View.GONE);

        }
    };

    View.OnClickListener retreiveListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.dismiss();
            sentRequestToFriend();
//            customDialog.cancel();
//            Intent i = new Intent(DecoderActivity.this, MainLandingActivity.class);
//            startActivity(i);
//            finish();
        }
    };


    private void setCustomFont() {
        header_Title.setTypeface(SplashActivity.mpBold);
        gallery.setTypeface(SplashActivity.mpBold);
    }

    public String readQRImage(Bitmap bMap) {
        String contents = null;

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();// use this otherwise ChecksumException
        try {
            Result result = reader.decode(bitmap);
            myQrLink = result.getText();
            Log.e("RESULT QR LINK", "" + myQrLink);
            getQRTextLink();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return myQrLink;
    }


    private void selectImage(final int imageid) {

        takePictuureFromGallery(imageid);
    }

    private void takePictuureFromGallery(int imageID) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("imageId", imageID);
        startActivityForResult(Intent.createChooser(intent, TAG_SELECT_FILE),
                SELECT_FILE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Log.e("tag","" + imageid);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                getImageFromgallery(selectedImageUri);
            }
        }
    }

    private void getImageFromgallery(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        bitmapBussinessLogo = DecoderActivity
                .decodeSampledBitmapFromResource(picturePath, 200, 200);


        qr_imageView.setVisibility(View.VISIBLE);
        qr_imageView.setImageBitmap(bitmapBussinessLogo);

        String abc = readQRImage(bitmapBussinessLogo);

        Log.e("readQRImage", "" + abc);
        if (bitmapBussinessLogo != null
                && !bitmapBussinessLogo.isRecycled()) {
            // bitmapBussinessLogo.recycle();
            bitmapBussinessLogo = null;

        }

    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options
                options = new BitmapFactory.Options();
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


    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        mydecoderview.getCameraManager().stopPreview();
        mydecoderview.setVisibility(View.INVISIBLE);
        myQrLink = text.trim();
        getQRTextLink();
    }

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
                mydecoderview.getCameraManager().startPreview();
                mydecoderview.setVisibility(View.VISIBLE);
                qr_imageView.setVisibility(View.GONE);
            }
        });
        alertDialog.show();
    }

    public void showAleart(String title, String message) {
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
                Intent i = new Intent(DecoderActivity.this, MainLandingActivity.class);
                i.putExtra(AppConstant.USER, user);
                startActivity(i);
                finish();
            }
        });
        alertDialog.show();
    }


    // Called when your device have no camera
    @Override
    public void cameraNotFound() {

    }

    // Called when there's no QR codes in the camera preview image
    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mydecoderview.getCameraManager().stopPreview();
    }

    private void addFriendsPopUp() {
        customDialog = new Dialog(DecoderActivity.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.add_as_friend_popup);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();

        initDialogElement();
    }


    private void setFieldInPopup(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            reciever_id = jsonObject
                    .getString(AppConstant.SENDER_UID);
            String user_name = jsonObject
                    .getString(AppConstant.DECODER_USERNAME);
            String user_designation = jsonObject
                    .getString(AppConstant.DECODER_DESIGNATION);
            String user_company_name = jsonObject
                    .getString(AppConstant.DECODER_COMPANY_NAME);

            String user_profile_pic = jsonObject
                    .getString(AppConstant.DECODER_PROFILE_PIC_URL);

            imageLoader.displayImage(user_profile_pic, mUserImageView,
                    options, animateFirstDisplayListener, progressListener);
            mUserName.setText(user_name);
            if (user_designation == null || user_designation.equals("")
                    || user_designation.equals("null")) {
            } else {
                mUserDesignation.setText(user_designation);
            }
            if (user_company_name == null || user_company_name.equals("")
                    || user_company_name.equals("null")) {
            } else {
                mUserCompanyName.setText(user_company_name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            // TODO Auto-generated method stub
            super.onLoadingStarted(imageUri, view);
            spinner1.setProgress(0);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
//				Log.e("YES MAI HOO DON", "  " + "AYA DON");
                ImageView imageView = (ImageView) view;
                imageView.setVisibility(View.VISIBLE);
                spinner1.setVisibility(View.GONE);

            }
            progressDialog.dismiss();
        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
            // TODO Auto-generated method stub
            super.onLoadingFailed(imageUri, view, failReason);
            String message = null;
            switch (failReason.getType()) {
                case IO_ERROR:
                    message = "Input/Output error";
                    break;
                case DECODING_ERROR:
                    message = "Image can't be decoded";
                    break;
                case NETWORK_DENIED:
                    message = "Downloads are denied";
                    break;
                case OUT_OF_MEMORY:
                    message = "Out Of Memory error";
                    break;
                case UNKNOWN:
                    message = "Unknown error";
                    break;
            }
            ImageView imageView = (ImageView) view;
            progressDialog.dismiss();
            spinner1.setVisibility(View.GONE);
//            Toast.makeText(DecoderActivity.this, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            // TODO Auto-generated method stub
            super.onLoadingCancelled(imageUri, view);
            progressDialog.dismiss();
            spinner1.setVisibility(View.GONE);
        }

    }

    ImageLoadingProgressListener progressListener = new ImageLoadingProgressListener() {

        @Override
        public void onProgressUpdate(String arg0, View view, int current, int total) {
            // TODO Auto-generated method stub

            ImageView imageView = (ImageView) view;
            spinner1.setProgress(Math.round(100.0f * current / total));
        }
    };


    private void postRequestToServer() {
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                "http://www.accelortech.com/im_chat/api.php?q=get_qrcode_user_detail&qr_uid=" + mUniqueQrCodedigit,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        try {
                            String response = String.valueOf(new String(bytes, "UTF-8"));
                            Log.e("RESPONSE HAPPINESS", "" + response);
                            addFriendsPopUp();
                            setFieldInPopup(response);
                        } catch (UnsupportedEncodingException e1) {

                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
                        if (error.getMessage() != null) {
                            Util.showOKAleart(DecoderActivity.this, DecoderActivity.this.getString(R.string.error_title),
                                    error.getMessage());
                        } else {
                            Util.showOKAleart(DecoderActivity.this, DecoderActivity.this.getString(R.string.error_title), DecoderActivity.this.getString(R.string.registration_response_error));
                        }

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        super.onRetry(retryNo);

                    }
                });
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
                addFriendOnValidQr();
            } catch (UnsupportedEncodingException e1) {

            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(DecoderActivity.this, DecoderActivity.this.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(DecoderActivity.this, DecoderActivity.this.getString(R.string.error_title), DecoderActivity.this.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }
    };

    private boolean isUserScannedOwnQrCode() {
        if (myQrLink.equals(qrLink))
            return true;
        else
            return false;
    }

    private boolean isListYouQrCode() {
        if (myQrLink.length() > 35) {
            mUniqueQrCodedigit = myQrLink.substring(35);
            Log.e("SUBSTRING", "" + mUniqueQrCodedigit.length());
            if (mUniqueQrCodedigit.length() == 16) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void addFriendOnValidQr() {
        if (isUserScannedOwnQrCode()) {
            showOKAleart("Message", "You Can't add yourself as a friend");
        } else {
            Log.e("ISLISTYOUQRCODE", "" + isListYouQrCode());
            if (isListYouQrCode()) {
                postRequestToServer();
            } else {
                showOKAleart("Message", "Not a valid QrCode");
            }
        }

    }

    private String getSenderID() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "com.oxilo.listyou_app_country_code",
                ShowProfileActivity.MODE_PRIVATE);
        String sender_id = sharedPreferences.getString(AppConstant.REG_ID, "");
        return sender_id;
    }

    /**
     * -------------------Sent Request To friends API Calling method Implematation-----------------*
     */

    private void sentRequestToFriend() {
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, id);
        params.put(AppConstant.RECEIVER_UID, reciever_id);
        client.post(AppConstant.ADD_AS_FRIENDS_API, params, mResponseHandler);
    }

    AsyncHttpResponseHandler mResponseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            progressDialog.dismiss();

            try {
                String result = String.valueOf(new String(bytes, "UTF-8"));
                JSONObject jsonObject = new JSONObject(result);
                String response = jsonObject
                        .getString(AppConstant.EDIT_PROFILE_MESSAGE);
                if (response.toString().trim().equals("ALREADY_FRIEND")) {
                    showAleart("Message", "Already Friends");
                }
                if (response.toString().trim().equals("PENDING")) {
                    showAleart("Message", "Your friend request is panding");
                }

                if (response.toString().trim().equals("Request_Sent")) {
                    showAleart("Message", "Friend request has been sent");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e1) {

            }


        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                progressDialog.dismiss();
                showOKAleart(
                        DecoderActivity.this.getString(R.string.error_title),
                        error.getMessage());

            } else {
                progressDialog.dismiss();
                showOKAleart(DecoderActivity.this.getString(R.string.error_title), DecoderActivity.this.getString(R.string.registration_response_error));
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


}
