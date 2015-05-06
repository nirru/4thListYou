package com.chat.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.oxilo.applistyou.StartUpActivity;
import com.oxilo.listyou.constant.AppConstant;

public class InsertTable {

    SQLiteDatabase database;

    public InsertTable(SQLiteDatabase database) {
        this.database = database;
    }

    public void addRowforChatTable(String listyou_login_id, String list_you_remote_user_id,
                                   String sender_name, String chat_text, String user_pic_url,
                                   String chat_time, String chat_date, boolean isLastMessage,String receiver_name, String receiver_pic_url) {
//        Log.e("NAME", "" + listyou_login_id + "  " + list_you_remote_user_id + " " + sender_name + " "
//                + chat_text + " " + user_pic_url + " " + chat_time + "" + chat_date);

        ContentValues contentValue = new ContentValues();

        contentValue.put(AppConstant.LISTYOU_LOGIN_ID, listyou_login_id);
        contentValue.put(AppConstant.LISTYOU_REMOTE_USER_ID, list_you_remote_user_id);
        contentValue.put(AppConstant.LISTYOU_SENDER_NAME, sender_name);
        contentValue.put(AppConstant.LISTYOU_CHAT_TEXT, chat_text);
        contentValue.put(AppConstant.LISTYOU_USER_PIC_URL, user_pic_url);
        contentValue.put(AppConstant.LISTYOU_CHAT_TIME, chat_time);
        contentValue.put(AppConstant.LISTYOU_CHAT_DATE, chat_date);
        contentValue.put(AppConstant.LISTYOU_LAST_CHAT, isLastMessage);
        contentValue.put(AppConstant.LISTYOU_RECEIVER_NAME, receiver_name);
        contentValue.put(AppConstant.LISTYOU_RECEIVER_PIC_URL, receiver_pic_url);
        // Log.e("Cotent value", "" + contentValue);

        try {
            StartUpActivity.databaseHelper.insertInToTable(database,
                    AppConstant.CHAT_TABLE_NAME, contentValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRowforChatTable(String listyou_login_id, String list_you_remote_user_id,
                                      String sender_name, String chat_text, String user_pic_url,
                                      String chat_time, String chat_date, boolean isLastMessage,String receiver_name, String receiver_pic_url) {
//        Log.e("NAME", "" + listyou_login_id + "  " + list_you_remote_user_id + " " + sender_name + " "
//                + chat_text + " " + user_pic_url + " " + chat_time + "" + chat_date);

        ContentValues contentValue = new ContentValues();

        contentValue.put(AppConstant.LISTYOU_LOGIN_ID, listyou_login_id);
        contentValue.put(AppConstant.LISTYOU_REMOTE_USER_ID, list_you_remote_user_id);
        contentValue.put(AppConstant.LISTYOU_SENDER_NAME, sender_name);
        contentValue.put(AppConstant.LISTYOU_CHAT_TEXT, chat_text);
        contentValue.put(AppConstant.LISTYOU_USER_PIC_URL, user_pic_url);
        contentValue.put(AppConstant.LISTYOU_CHAT_TIME, chat_time);
        contentValue.put(AppConstant.LISTYOU_CHAT_DATE, chat_date);
        contentValue.put(AppConstant.LISTYOU_LAST_CHAT, isLastMessage);
        contentValue.put(AppConstant.LISTYOU_RECEIVER_NAME, receiver_name);
        contentValue.put(AppConstant.LISTYOU_RECEIVER_PIC_URL, receiver_pic_url);
        // Log.e("Cotent value", "" + contentValue);

        try {
            String whereClause = AppConstant.LISTYOU_REMOTE_USER_ID + "='" + list_you_remote_user_id + "'" + " AND " + AppConstant.LISTYOU_CHAT_TEXT + "='" + chat_text + "'";

            StartUpActivity.databaseHelper.updateTableItem(database,
                    AppConstant.CHAT_TABLE_NAME, contentValue, whereClause);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteAllTable() {
        database.delete(AppConstant.CHAT_TABLE_NAME, null, null);
    }

}
