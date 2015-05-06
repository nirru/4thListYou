package com.chat.database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.holder.ChildItem;
import com.holder.GroupItem;
import com.oxilo.applistyou.StartUpActivity;
import com.oxilo.listyou.constant.AppConstant;

public class GetDataFromDatabase {

	public GroupItem getAllRowsAsArrays(String id) {
		GroupItem groupItem = new GroupItem();
		SQLiteDatabase database = StartUpActivity.databaseHelper
				.getReadableDatabase();
		Cursor cursor;
		try {

            String whereClause = " SELECT  *  From "
                    + AppConstant.CHAT_TABLE_NAME + " WHERE "
                    + AppConstant.LISTYOU_REMOTE_USER_ID + "='" + id
                    + "'";


            Log.e("WHERE CALUSE ", "" + whereClause);
            cursor = database.rawQuery(whereClause, null);

			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				do {
                    ChildItem childItem = new ChildItem();
                    childItem.mListYouLoginId = cursor.getString(0);
                    childItem.remoteUserId = cursor.getString(1);
                    childItem.mChatSenderName = cursor.getString(2);
                    childItem.mChatText = cursor.getString(3);
                    childItem.mUserPicUrl = cursor.getString(4);
                    childItem.mChatTextTime = cursor.getString(5);
                    childItem.mChatTextDate = cursor.getString(6);
                    groupItem.items.add(childItem);

				} while (cursor.moveToNext());
			}
		} catch (SQLException e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
		// Log.w("--Result-----", "" + dataArrays);
		return groupItem;
	}

    public GroupItem getRecentchatUser(){
        GroupItem groupItem = new GroupItem();
        SQLiteDatabase database = StartUpActivity.databaseHelper
                .getReadableDatabase();
        Cursor cursor;
        try {

            String whereClause = " SELECT  *  From "
                    + AppConstant.CHAT_TABLE_NAME + " WHERE "
                    + AppConstant.LISTYOU_LAST_CHAT + "='" + 1
                    + "'";


            Log.e("WHERE CALUSE ", "" + whereClause);
            cursor = database.rawQuery(whereClause, null);

            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    ChildItem childItem = new ChildItem();
                    childItem.mListYouLoginId = cursor.getString(0);
                    childItem.remoteUserId = cursor.getString(1);
                    childItem.mChatSenderName = cursor.getString(2);
                    childItem.mChatText = cursor.getString(3);
                    childItem.mUserPicUrl = cursor.getString(4);
                    childItem.mChatTextTime = cursor.getString(5);
                    childItem.mChatTextDate = cursor.getString(6);
                    childItem.mChatReceiverName = cursor.getString(8);
                    childItem.mReceiverPicUrl = cursor.getString(9);
                    groupItem.items.add(childItem);

                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        // Log.w("--Result-----", "" + dataArrays);
        return groupItem;

    }

	public boolean checkForChatTables() {
		String whereClause = "Select * from " + AppConstant.CHAT_TABLE_NAME;
		SQLiteDatabase database = StartUpActivity.databaseHelper
				.getReadableDatabase();
		Cursor cursor = database.rawQuery(whereClause, null);
		if (cursor.getCount() == 0)
			return false;
		if (cursor.getCount() > 0)
			return true;

		cursor.close();
		return false;
	}
}
