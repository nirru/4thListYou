package com.drawer;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.adapter.BlockUserAdapter;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.response.User;
import com.util.BlockDialog;
import com.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by C-ShellWin on 12/29/2014.
 */
public class BlockUser extends Fragment {

    private Context mContext;
    private User user;
    private GroupItem groupItem;
    private List<GroupItem> items;
    private TextView mTextHeader, mTextSaveGroup;
    private TextView members, text1, text2;
    private RelativeLayout saveGroup;
    private ListView listView;
    private ImageView icondown;
    private TextSwitcher mTextSwitcher;
    private ProgressDialog progressDialog;
    public BlockUserAdapter adapter;
    private RelativeLayout relative_bottom;
    private String old_group;
    private int old_group_pos;
    private TextView mAddListCount;
    private EditText search_action_for_all_contact;
    private TextView header;

    public BlockUser(Context mContext, User user, TextView mAddListCount) {
        this.mContext = mContext;
        this.user = user;
        this.mAddListCount = mAddListCount;
        items = new ArrayList<GroupItem>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_delete_user, container,
                false);
        init(rootView);
        createAnim();
        setCustomFont();
        showList(Util.readFreindsFromPrefs(mContext));
        return rootView;
    }

    private void init(View root) {
        search_action_for_all_contact = (EditText) root.findViewById(R.id.search_action);
        text1 = (TextView) root.findViewById(R.id.text1);
        text2 = (TextView) root.findViewById(R.id.text2);
        members = (TextView) root.findViewById(R.id.lblListHeader);
        icondown = (ImageView) root.findViewById(R.id.drop_down);
        saveGroup = (RelativeLayout) root.findViewById(R.id.relative_bottom);
        mTextSaveGroup = (TextView) root.findViewById(R.id.save_group);
        mTextHeader = (TextView) root.findViewById(R.id.header_group);
        mTextSwitcher = (TextSwitcher) root.findViewById(R.id.main_textswitcher);
        listView = (ListView) root.findViewById(R.id.lvExp);
        relative_bottom = (RelativeLayout) root.findViewById(R.id.relative_bottom);
        header = (TextView) root.findViewById(R.id.header_group);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("You are visiting this page first times so please wait while the application is fetching all your details");
        progressDialog.setCanceledOnTouchOutside(false);

        members.setText("Freinds");



        relative_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move();
            }
        });
    }

    private void setCustomFont() {
        mTextHeader.setText(mContext.getResources().getString(R.string.fragement_delete_members));
        mTextHeader.setTypeface(SplashActivity.mpBold);
        header.setTypeface(SplashActivity.mpBold);
        members.setTypeface(SplashActivity.mpBold);
        mTextSaveGroup.setTypeface(SplashActivity.mpRegular);
    }

    private void showList(String result) {
        groupItem = new GroupItem();
        Object json = null;
        try {
            json = new JSONTokener(result).nextValue();
            if (json instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) json;
                groupItem.title = "Members" + "(" + 0 + ")";
                items.add(groupItem);
//                        Toast.makeText(mContext, "No Friend Found please add first", Toast.LENGTH_LONG).show();
            } else if (json instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) json;
                getFriendsList(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mTextSwitcher.setText("" + groupItem.items.size());
        setListviewToAdapter();
    }


    private void getFriendsList(String message) {
        JSONArray jsonArray;

        try {
            jsonArray = new JSONArray(message.toString().trim());
            groupItem.title = "Members" + "(" + jsonArray.length() + ")";
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
                groupItem.items.add(child);
            }

            items.add(groupItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setListviewToAdapter() {
        if (groupItem != null) {
            adapter = new BlockUserAdapter(mContext, user, groupItem, text1, text2, mTextSwitcher);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            search_action_for_all_contact.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String text = search_action_for_all_contact.getText().toString().toLowerCase(Locale.getDefault());
                    adapter.filter(text);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        } else {
            Toast.makeText(mContext, "No Friend to add to group, please first add friends first", Toast.LENGTH_LONG).show();
        }
    }

    private void move() {
        if (groupItem != null) {
            if (groupItem.items.size() > 0 && !showResult().trim().equals("")) {
                Log.e("ITEM Selected", "" + Util.removeLastChar(showResult()));
                moveGroup();
            } else {
                Util.showOKAleart(mContext, "Message", "Please add some friends and then try.");
            }
        } else {
            Toast.makeText(mContext, "You are trying to move with no friend , Please add some friends and then try.", Toast.LENGTH_LONG).show();
        }
    }

    private void moveGroup() {
        BlockDialog blockDialog = BlockDialog.getInstance();
        blockDialog.createDialog(mContext, Util.removeLastChar(showResult()), Util.removeLastChar(getName()), user, mAddListCount);
    }

    public String showResult() {
        String result = "";
        for (ChildItem p : adapter.getBox()) {
            if (p.box) {
                result += p.remoteUserId.trim() + ",";
            }
        }
//        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        return result.trim();
    }

    public String getName() {
        String result = "";
        for (ChildItem p : adapter.getBox()) {
            if (p.box) {
                result += p.mUserName.trim() + ",";
            }
        }
//        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        return result.trim();
    }


    private void createAnim() {
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                TextView myText = new TextView(mContext);
                myText.setGravity(Gravity.CENTER);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER);
                myText.setLayoutParams(params);

                myText.setTextSize(13);
                myText.setTextColor(Color.BLACK);
                myText.setTypeface(SplashActivity.mpBold);
                return myText;
            }
        });

        mTextSwitcher.setInAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_up_in));
        mTextSwitcher.setOutAnimation(mContext, R.anim.push_up_out);
    }
}

