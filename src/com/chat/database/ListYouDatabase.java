package com.chat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.oxilo.applistyou.StartUpActivity;
import com.oxilo.listyou.constant.AppConstant;

public class ListYouDatabase {

	Context context;

	SQLiteDatabase database;

	public ListYouDatabase(Context context) {
		this.context = context;
	}

	public void chatHistoryTable(SQLiteDatabase database) {

        StartUpActivity.databaseHelper.createTable(database,
                " CREATE TABLE IF NOT EXISTS " + AppConstant.CHAT_TABLE_NAME
                        + " (" + AppConstant.LISTYOU_LOGIN_ID
                        + " Text, " + AppConstant.LISTYOU_REMOTE_USER_ID
                        + " Text," + AppConstant.LISTYOU_SENDER_NAME
                        + " Text," + AppConstant.LISTYOU_CHAT_TEXT
                        + " Text," + AppConstant.LISTYOU_USER_PIC_URL
                        + " Text," + AppConstant.LISTYOU_CHAT_TIME
                        + " Text," + AppConstant.LISTYOU_CHAT_DATE
                        + " Text," + AppConstant.LISTYOU_LAST_CHAT
                        + " Text," + AppConstant.LISTYOU_RECEIVER_NAME
                        + " Text," + AppConstant.LISTYOU_RECEIVER_PIC_URL + " Text" + " );");
	}



	public void createdatabase() {
		if (StartUpActivity.databaseHelper.isDatabaseExists(context
				.getApplicationContext())) {
			Log.e("DATABASE ALREADY EXIST", "" + "IT IS EXIST");
		} else {
			database = StartUpActivity.databaseHelper.getWritableDatabase();
            chatHistoryTable(database);
		}
	}

}
