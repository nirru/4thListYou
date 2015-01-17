package com.landing.screen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.ListGroupAdapter;
import com.asyn.RequestHandler;
import com.asyn.RequestListener;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.loopj.android.http.RequestParams;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.oxilo.listyou.login.LoginProfileActivity;
import com.response.User;
import com.util.Freinds;
import com.util.ListYouAnimation;
import com.util.PhotoPicker;
import com.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by C-ShellWin on 12/16/2014.
 */
public class GroupFragement extends Fragment {
    private Context mContext;
    private User user;
    private ImageView groupImageView;
    private EditText mEditTextGroupName;
    private TextView mEditTextAddMember;
    private ListView listView;
    private RelativeLayout saveGroup;
    private LinearLayout linearLayout, linearExpand;
    private TextView mText6, mText7, mTextHeader, mTextSaveGroup;
    private List<GroupItem> items;
    private GroupItem item;
    private TextView members, text1, text2;
    private ImageView icondown;
    private TextSwitcher mTextSwitcher;
    ListGroupAdapter adapter;
    private Bitmap bmp;
    private String groupImageBitmapTobase64String = "TNOIMAGE";
    private ProgressDialog progressDialog;
   private TextView mAddListCount;
    public GroupFragement(Context mContext, User user, TextView mAddListCount) {
        this.mContext = mContext;
        this.user = user;
        this.mAddListCount = mAddListCount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_group, container,
                false);
        init(rootView);
        setCustomFont();
        showList();
        return rootView;
    }

    private void init(View root) {
        items = new ArrayList<GroupItem>();
        text1 = (TextView) root.findViewById(R.id.text1);
        text2 = (TextView) root.findViewById(R.id.text2);
        members = (TextView) root.findViewById(R.id.lblListHeader);
        icondown = (ImageView) root.findViewById(R.id.drop_down);
        groupImageView = (ImageView) root.findViewById(R.id.group_image_view);
        mEditTextGroupName = (EditText) root.findViewById(R.id.group_edit_box__id);
        mEditTextAddMember = (TextView) root.findViewById(R.id.add_member_id);
        linearLayout = (LinearLayout) root.findViewById(R.id.linear_one);
        linearExpand = (LinearLayout) root.findViewById(R.id.linear_two);
        saveGroup = (RelativeLayout) root.findViewById(R.id.relative_bottom);
        mText6 = (TextView) root.findViewById(R.id.textView6);
        mText7 = (TextView) root.findViewById(R.id.textView7);
        mTextSaveGroup = (TextView) root.findViewById(R.id.save_group);
        mTextHeader = (TextView) root.findViewById(R.id.header_group);
        listView = (ListView) root.findViewById(R.id.lvExp);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);

        text1.setVisibility(View.GONE);
        text2.setVisibility(View.GONE);
        icondown.setVisibility(View.GONE);
        members.setText("Members");
        mTextSwitcher = (TextSwitcher) root.findViewById(R.id.main_textswitcher);
        mEditTextAddMember.setEnabled(true);
        mEditTextAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null) {
                    linearLayout.setVisibility(View.GONE);
                    linearExpand.startAnimation(ListYouAnimation.inFromToptAnimation());
                    linearExpand.setVisibility(View.VISIBLE);
                    mEditTextAddMember.setEnabled(false);
                }
            }
        });

        saveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGroupReadyToSave();
            }
        });

        groupImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void setCustomFont() {
        mTextHeader.setTypeface(SplashActivity.mpBold);
        mEditTextGroupName.setTypeface(SplashActivity.mpRegular);
        mEditTextAddMember.setTypeface(SplashActivity.mpRegular);
        mText6.setTypeface(SplashActivity.mpRegular);
        mText7.setTypeface(SplashActivity.mpRegular);
        members.setTypeface(SplashActivity.mpBold);
        mTextSaveGroup.setTypeface(SplashActivity.mpRegular);
    }

    private void showList() {
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, user.id);
        requestHandler.makePostRequest(mContext, params, AppConstant.GET_FRIEND_LIST, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                GroupItem item = new GroupItem();
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        item.title = "Members" + "(" + 0 + ")";
                        items.add(item);
//                        Toast.makeText(mContext, "No Friend Found please add first", Toast.LENGTH_LONG).show();
                    } else if (json instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) json;
                        getFriendsList(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setListviewToAdapter();
            }
        });
    }

    private void getFriendsList(String message) {
        item = new GroupItem();
        JSONArray jsonArray;

        try {
            jsonArray = new JSONArray(message.toString().trim());
            item.title = "Members" + "(" + jsonArray.length() + ")";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                ChildItem child = new ChildItem();
                child.remoteUserId = jsonObject.getString(AppConstant.SENDER_UID);
                child.mUserName = jsonObject
                        .getString(AppConstant.RECEIVER_USERNAME);
                child.mCompanyName = jsonObject
                        .getString(AppConstant.RECEIVER_COMPANY_NAME);
                child.mUserPicUrl = jsonObject
                        .getString(AppConstant.RECEIVER_PROFILE_PIC_URL);
                child.box = false;
                item.items.add(child);
            }

            items.add(item);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setListviewToAdapter() {
        if (item != null) {
            adapter = new ListGroupAdapter(mContext, user, item, text1, text2, mTextSwitcher);
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(mContext, "No Friend to add to group, please first add friends first", Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserDefinedGroup() {
        if (item != null) {
            if (item.items.size() > 0 && !showResult().trim().equals("")) {
                Log.e("ITEM Selected", "" + Util.removeLastChar(showResult()));
                createGroup();
            } else {
                Util.showOKAleart(mContext, "Message", "You are trying to create a group with no friend , Please add some friends and then try.");
            }
        } else {
            Toast.makeText(mContext, "You are trying to create a group with no friend , Please add some friends and then try.", Toast.LENGTH_LONG).show();
        }
    }

    private void isGroupReadyToSave() {
        if (!isGroupNameEmpty())
            return;
        else
            saveUserDefinedGroup();
    }

    private boolean isGroupNameEmpty() {
        if (mEditTextGroupName.getText().toString().equals("")) {
            Util.showOKAleart(mContext, "Message", "Please fill the group name");
            return false;
        }
        return true;
    }


    private void selectImage() {
        final CharSequence[] items = {LoginProfileActivity.TAG_CAMERA, LoginProfileActivity.TAG_CHOOSE_FROM_LIBRARY,
                LoginProfileActivity.TAG_CANCEL};
        AlertDialog.Builder builder = new AlertDialog.Builder(
                mContext);
        builder.setTitle(LoginProfileActivity.TAG_ADD_PHOTO);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(LoginProfileActivity.TAG_CAMERA)) {
                    takePictureFromCamera();
                } else if (items[item].equals(LoginProfileActivity.TAG_CHOOSE_FROM_LIBRARY)) {
                    takePictuureFromGallery();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void takePictuureFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, LoginProfileActivity.TAG_SELECT_FILE),
                LoginProfileActivity.SELECT_FILE);
    }

    private void takePictureFromCamera() {
        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(
                Intent.createChooser(cameraIntent, "TAG_SELECT_FILE"),
                LoginProfileActivity.REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK) {
            if (reqCode == LoginProfileActivity.REQUEST_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                saveImageToImageView(photo);
            } else if (reqCode == LoginProfileActivity.SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                getImageFromgallery(selectedImageUri);
            }
        }
    }

    private void getImageFromgallery(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        bmp = PhotoPicker.decodeSampledBitmapFromResource(picturePath,
                100, 100);
        groupImageView.setImageBitmap(bmp);
        groupImageBitmapTobase64String = PhotoPicker.ConvertBitmapToBAse64String(bmp);
        if (bmp != null && !bmp.isRecycled()) {
            bmp = null;
        }

    }

    private void saveImageToImageView(Bitmap bitMapPhoto) {
        bmp = bitMapPhoto;
        groupImageView.setImageBitmap(bmp);
        groupImageBitmapTobase64String = PhotoPicker.ConvertBitmapToBAse64String(bmp);
    }

    private void createGroup() {
        Freinds freinds = Freinds.getInstance();
        freinds.cretaeUserDefinedGroup(mContext, user.id, groupImageBitmapTobase64String, mEditTextGroupName.getText().toString().trim(), progressDialog, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                addMembersToCreatedGroup();
            }
        });
    }

    private void addMembersToCreatedGroup() {
        Freinds freinds = Freinds.getInstance();
        freinds.addMemberToGroup(mContext, user.id, mEditTextGroupName.getText().toString().trim(), Util.removeLastChar(showResult()), progressDialog, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                Util.writeContactListingFirstLaunchToPrefrefs(mContext, true);
                showOKAleart(mContext, "Message", mEditTextGroupName.getText().toString() + " " + "created successfully");
            }
        });
    }



    public String showResult() {
        String result = "";
        for (ChildItem p : adapter.getBox()) {
            if (p.box){
                result += p.remoteUserId.trim() + ",";
            }
        }
//        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        return result.trim();
    }

    public void showOKAleart(Context context, String title, String message) {
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
                Fragment fragment = new ContactListingfragement(mContext, mAddListCount ,user);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment).addToBackStack(null).commit();
            }
        });
        alertDialog.show();
    }
}