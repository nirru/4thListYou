package com.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.oxilo.listyou.constant.AppConstant;
import com.response.User;
import com.response.UserFriends;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by C-ShellWin on 12/12/2014.
 */
public class Util {

    public static void saveImageToSD(Context mContext, Bitmap bmp) {

        FileOutputStream fos = null;
        /*--- this method will save your downloaded image to SD card ---*/

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        /*--- you can select your preferred CompressFormat and quality.
         * I'm going to use JPEG and 100% quality ---*/
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        /*--- create a new file on SD card ---*/
        File file = new File(mContext.getFilesDir() + File.separator
                + "QrCode.jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*--- create a new FileOutputStream and write bytes to file ---*/
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bytes.toByteArray());
            fos.close();
//            Toast.makeText(mContext, "Image saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveImageToExternalStorage(Context mContext, Bitmap bmp, String filename) {
        FileOutputStream fos = null;
        File wallpaperDirectory = new File("/sdcard/ListYouImages");
        wallpaperDirectory.mkdirs();
        /*--- this method will save your downloaded image to SD card ---*/

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        /*--- you can select your preferred CompressFormat and quality.
         * I'm going to use JPEG and 100% quality ---*/
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        /*--- create a new file on SD card ---*/
        File file = new File(wallpaperDirectory, filename + ".jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*--- create a new FileOutputStream and write bytes to file ---*/
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bytes.toByteArray());
            fos.close();
//            Toast.makeText(mContext, "Image saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPathOfFile(String filename) {
        File localFile = new File("/sdcard/ListYouImages");
        String path = localFile.getAbsolutePath() + "/" + filename + ".jpg";
        Log.e("GetPath", "" + path);
        return path;
    }

    public static int getPixelValue(int dp, Context context) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static String commaSeperatedString(ArrayList<String> selectedList) {
        StringBuilder builder = new StringBuilder();
        builder.setLength(0);
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.size() == 1) {
                builder.append(selectedList.get(i).toString().trim() + ",");
            } else {
                builder.append(selectedList.get(i).toString().trim() + ",");
            }
        }
        Log.e("SIZEEEEE", "" + selectedList.size());
        Log.e("BRESILT CHADA", "" + builder.toString());
        return removeLastChar(builder.toString().trim());
    }

    public static String removeLastChar(String str) {
        if (str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        Log.e("BUILDER TO STRING", "" + str);
        return str;
    }

    public static void setScreenNavigation(Context mContext, int id) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PARCEABLE,
                        Context.MODE_PRIVATE).edit();
        prefs.putInt(AppConstant.PREFS_SCREEN_ID, id);
        prefs.commit();
    }

    public static int getScreennavigation(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PARCEABLE,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getInt(AppConstant.PREFS_SCREEN_ID, 0);
    }

    public static void writeToPrefrefs(Context mContext, String result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PARCEABLE,
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.PREFS_USER_DETAILS, result);
        prefs.commit();
    }

    public static void writeFreindsToPrefrefs(Context mContext, String result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_FREINDS_DETAILS,
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.PREFS_FREINDS_LIST, result);
        prefs.commit();
    }

    public static void writeRecentFreindsToPrefrefs(Context mContext, String result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_FREINDS_DETAILS,
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.PREFS_RECENT_FREINDS_LIST, result);
        prefs.commit();
    }

    public static void writeGroupToPrefrefs(Context mContext, String result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_FREINDS_DETAILS,
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.PREFS_GROUP, result);
        prefs.commit();
    }

    public static void writeCoWorkerToPrefrefs(Context mContext, String result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_FREINDS_DETAILS,
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.PREFS_COWORKER_SYTEM_GROUP, result);
        prefs.commit();
    }

    public static void writeClientToPrefrefs(Context mContext, String result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_FREINDS_DETAILS,
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.PREFS_CLIENT_SYTEM_GROUP, result);
        prefs.commit();
    }

    public static void writePartnersToPrefrefs(Context mContext, String result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_FREINDS_DETAILS,
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.PREFS_PARTNER_SYTEM_GROUP, result);
        prefs.commit();
    }

    public static void writeAquitanceToPrefrefs(Context mContext, String result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_FREINDS_DETAILS,
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.PREFS_AQUITANCE_SYTEM_GROUP, result);
        prefs.commit();
    }

    public static void writeGoodToKnowToPrefrefs(Context mContext, String result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_FREINDS_DETAILS,
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.PREFS_GOOD_TO_KNOW_SYTEM_GROUP, result);
        prefs.commit();
    }

    public static void writeFamilyToPrefrefs(Context mContext, String result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_FREINDS_DETAILS,
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.PREFS_FAMILY_SYTEM_GROUP, result);
        prefs.commit();
    }

    public static void writeOthersToPrefrefs(Context mContext, String result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_FREINDS_DETAILS,
                        Context.MODE_PRIVATE).edit();
        prefs.putString(AppConstant.PREFS_OTHERS_SYTEM_GROUP, result);
        prefs.commit();
    }

    public static void writeAppToPrefrefs(Context mContext, boolean result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_APP_LAUNCHES,
                        Context.MODE_PRIVATE).edit();
        prefs.putBoolean(AppConstant.PREFS_FIRST_LAUNCHES, result);
        prefs.commit();
    }

    public static void writeContactListingFirstLaunchToPrefrefs(Context mContext, boolean result) {
        SharedPreferences.Editor prefs = mContext
                .getSharedPreferences(AppConstant.PREFS_APP_LAUNCHES,
                        Context.MODE_PRIVATE).edit();
        prefs.putBoolean(AppConstant.PREFS_FIRST_LAUNCHES_CONTACT_LISTING, result);
        prefs.commit();
    }

    public static boolean readContactListingFirstLaunchToPrefrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_APP_LAUNCHES,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getBoolean(AppConstant.PREFS_FIRST_LAUNCHES_CONTACT_LISTING, true);
    }

    public static boolean readAppFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_APP_LAUNCHES,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getBoolean(AppConstant.PREFS_FIRST_LAUNCHES, true);
    }


    public static String readFreindsFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_FREINDS_DETAILS,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.PREFS_FREINDS_LIST, "");
    }

    public static String readRecentFreindsFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_FREINDS_DETAILS,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.PREFS_RECENT_FREINDS_LIST, "");
    }

    public static String readGroupsFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_FREINDS_DETAILS,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.PREFS_GROUP, "");
    }

    public static String readCoWorkerFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_FREINDS_DETAILS,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.PREFS_COWORKER_SYTEM_GROUP, "");
    }

    public static String readClientFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_FREINDS_DETAILS,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.PREFS_CLIENT_SYTEM_GROUP, "");
    }

    public static String readPartnersFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_FREINDS_DETAILS,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.PREFS_PARTNER_SYTEM_GROUP, "");
    }

    public static String readAquitanceFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_FREINDS_DETAILS,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.PREFS_AQUITANCE_SYTEM_GROUP, "");
    }

    public static String readGoodToKnowFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_FREINDS_DETAILS,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.PREFS_GOOD_TO_KNOW_SYTEM_GROUP, "");
    }

    public static String readFamilyFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_FREINDS_DETAILS,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.PREFS_FAMILY_SYTEM_GROUP, "");
    }

    public static String readOthersFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PREFS_FREINDS_DETAILS,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.PREFS_OTHERS_SYTEM_GROUP, "");
    }

    public static String readFromPrefs(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                AppConstant.PARCEABLE,
                mContext.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.PREFS_USER_DETAILS, "");
    }



    public static User saveParcel(String result, String id) {
        User user = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            // String message = jsonObject
            // .getString(AppConstant.EDIT_PROFILE_MESSAGE);
            String fname = jsonObject.getString(AppConstant.EDIT_PROFILE_FIRST_NAME);
            String lname = jsonObject.getString(AppConstant.EDIT_PROFILE_LAST_NAME);
            String fname_other = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_FIRST_NAME_IN_ANOTHER_LANGUAGE);
            String lname_other = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_LAST_NAME_IN_ANOTHER_LANGUAGE);
            String com_designation = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_COMPANY_TITLE);
            String company_name = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_COMPANY_NAME);
            String company_email = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_COMPANY_EMAIL);
            String company_another_email = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_ANOTHER_EMAIL);
            String company_website = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_WEBSITE);
            String company_address = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_ADDRESS);
            String company_city = jsonObject.getString(AppConstant.EDIT_PROFILE_CITY);
            String company_country = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_COUNTRY);
            String company_mobile = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_MOBILE_NUMBER);
            String company_telephone = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_TELEPHONE_NUMBER);
            String company_fax = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_FAX_NUMBER);
            String country_code = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_COUNTRY_CODE);
            String company_skype = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_SKYPE_ID);
            String company_user_profile_pic = jsonObject
                    .getString(AppConstant.SHOW_PROFILE_PROFILE_PIC_URL);
            String company_user_logo_pic = jsonObject
                    .getString(AppConstant.SHOW_PROFILE_COMPANY_PIC_URL);
            String company_user_bussiness_card_pic = jsonObject
                    .getString(AppConstant.SHOW_PROFILE_BUSSINESS_PIC_URL);
            String qr_image_url = jsonObject
                    .getString(AppConstant.QR_IMAGE_URL);
//			Log.e("COUNTRY CODE Address", "" + country_code);
            String listYouID = jsonObject
                    .getString(AppConstant.LIST_YOU_ID);

            String listYoucreationDate = jsonObject
                    .getString(AppConstant.LIST_YOU_ID_CREATION_DATE);
            user = new User(id, fname, lname, fname_other, lname_other,
                    com_designation, company_name, company_email,
                    company_another_email, company_website, company_address,
                    company_city, company_country, company_mobile,
                    company_telephone, company_fax, country_code,
                    company_skype, company_user_profile_pic, company_user_logo_pic,
                    company_user_bussiness_card_pic, qr_image_url, listYouID, listYoucreationDate);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return user;
    }

    public static UserFriends saveParcelForUserFirnds(String result, String id) {
        UserFriends user = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            String fname = jsonObject.getString(AppConstant.EDIT_PROFILE_FIRST_NAME);
            String lname = jsonObject.getString(AppConstant.EDIT_PROFILE_LAST_NAME);
            String fname_other = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_FIRST_NAME_IN_ANOTHER_LANGUAGE);
            String lname_other = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_LAST_NAME_IN_ANOTHER_LANGUAGE);
            String com_designation = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_COMPANY_TITLE);
            String company_name = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_COMPANY_NAME);
            String company_email = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_COMPANY_EMAIL);
            String company_another_email = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_ANOTHER_EMAIL);
            String company_website = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_WEBSITE);
            String company_address = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_ADDRESS);
            String company_city = jsonObject.getString(AppConstant.EDIT_PROFILE_CITY);
            String company_country = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_COUNTRY);
            String company_mobile = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_MOBILE_NUMBER);
            String company_telephone = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_TELEPHONE_NUMBER);
            String company_fax = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_FAX_NUMBER);
            String country_code = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_COUNTRY_CODE);
            String company_skype = jsonObject
                    .getString(AppConstant.EDIT_PROFILE_SKYPE_ID);
            String company_user_profile_pic = jsonObject
                    .getString(AppConstant.SHOW_PROFILE_PROFILE_PIC_URL);
            String company_user_logo_pic = jsonObject
                    .getString(AppConstant.SHOW_PROFILE_COMPANY_PIC_URL);
            String company_user_bussiness_card_pic = jsonObject
                    .getString(AppConstant.SHOW_PROFILE_BUSSINESS_PIC_URL);
            String qr_image_url = jsonObject
                    .getString(AppConstant.QR_IMAGE_URL);
//			Log.e("COUNTRY CODE Address", "" + country_code);
            String listYouID = jsonObject
                    .getString(AppConstant.LIST_YOU_ID);
            user = new UserFriends(id, fname, lname, fname_other, lname_other,
                    com_designation, company_name, company_email,
                    company_another_email, company_website, company_address,
                    company_city, company_country, company_mobile,
                    company_telephone, company_fax, country_code,
                    company_skype, company_user_profile_pic, company_user_logo_pic,
                    company_user_bussiness_card_pic, qr_image_url, listYouID);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return user;
    }

    public static void showOKAleart(Context context, String title, String message) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
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
}
