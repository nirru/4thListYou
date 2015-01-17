package com.landing.screen;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.AddListAdapter;
import com.asyn.RequestHandler;
import com.asyn.RequestListener;
import com.customclasses.AnimatedExpandableListView;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.listyou.listener.AddListFragementClickListener;
import com.loopj.android.http.RequestParams;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.oxilo.qrreader.DecoderActivity;
import com.oxilo.showprofile.ShowProfileActivity;
import com.response.User;
import com.util.AddFriendsDialog;
import com.util.Freinds;
import com.util.InviteDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by C-ShellWin on 12/1/2014.
 */
public class AddListFragement extends Fragment {

    private AnimatedExpandableListView expListView;
    private Context mContext;
    private ProgressDialog progressDialog;

    private RelativeLayout mRelativeCaptureQr, mRelativeInvite, mRelativeSearchByListyouId, mRealtiveMyBussinessCard;
    private RelativeLayout mSearchField;

    private TextView mCaptureQrtext, mInvite, mSearchByListYou, mBussinessCard, mSearchPeople;

    private String id;
    private boolean isSearchByListYouId;


    private TextView mAddListCount;
    private EditText mSearch_keyword;
    private ImageView mSearchIcon;

    private User user;
    private List<GroupItem> items;

    public AddListFragement(Context mContext, boolean isSearchByListYouId, TextView mAddListCount, User user) {
        this.mContext = mContext;
        this.isSearchByListYouId = isSearchByListYouId;
        this.mAddListCount = mAddListCount;
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_add_list, container,
                false);
        init(rootView);
        hideAndShowSearchBox();
        setCustomFont();
        recieveFriendsRequest();
        return rootView;
    }


    private void init(View root) {
        items = new ArrayList<GroupItem>();
        mCaptureQrtext = (TextView) root.findViewById(R.id.capture_qr_text_view);
        mInvite = (TextView) root.findViewById(R.id.invite_text_view);
        mSearchByListYou = (TextView) root
                .findViewById(R.id.search_text_view);
        mBussinessCard = (TextView) root.findViewById(R.id.mybussiness_text_view);
        mSearchPeople = (TextView) root.findViewById(R.id.search);
        mSearchField = (RelativeLayout) root.findViewById(R.id.relative_one);
        mSearch_keyword = (EditText) root.findViewById(R.id.search_edit_box);
        mSearchIcon = (ImageView) root.findViewById(R.id.search_icon);
        mRelativeCaptureQr = (RelativeLayout) root.findViewById(R.id.relative_capture_qr);
        mRelativeInvite = (RelativeLayout) root.findViewById(R.id.relative_invite);
        mRelativeSearchByListyouId = (RelativeLayout) root.findViewById(R.id.relative_search_by_listyou);
        mRealtiveMyBussinessCard = (RelativeLayout) root.findViewById(R.id.relative_my_bussiness);

        mRelativeCaptureQr.setOnClickListener(mAddFragementListener);
        mRelativeInvite.setOnClickListener(mAddFragementListener);
        mRelativeSearchByListyouId.setOnClickListener(mAddFragementListener);
        mRealtiveMyBussinessCard.setOnClickListener(mAddFragementListener);
        mSearchIcon.setOnClickListener(mAddFragementListener);

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                "com.oxilo.listyou_app_country_code",
                ShowProfileActivity.MODE_PRIVATE);
        id = sharedPreferences.getString(AppConstant.REG_ID, "");
        
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        expListView = (AnimatedExpandableListView) root.findViewById(R.id.lvExp);

    }

    private void hideAndShowSearchBox() {
        if (!isSearchByListYouId) {
            mSearchField.setVisibility(View.GONE);
            mSearchPeople.setVisibility(View.GONE);
        } else {
            mSearchField.setVisibility(View.VISIBLE);
            mSearchPeople.setVisibility(View.VISIBLE);
        }
    }

    private void setCustomFont() {
        mSearch_keyword.setTypeface(SplashActivity.mpRegular);
        mCaptureQrtext.setTypeface(SplashActivity.mpRegular);
        mInvite.setTypeface(SplashActivity.mpRegular);
        mSearchByListYou.setTypeface(SplashActivity.mpRegular);
        mBussinessCard.setTypeface(SplashActivity.mpRegular);
        mSearchPeople.setTypeface(SplashActivity.mpRegular);
    }

    private void setListviewToAdapter() {
        AddListAdapter adapter = new AddListAdapter(mContext, user, mAddListCount);
        adapter.setData(items);
        expListView.setAdapter(adapter);
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (expListView.isGroupExpanded(groupPosition)) {
                    expListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    expListView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });

    }

    AddListFragementClickListener mAddFragementListener = new AddListFragementClickListener() {
        @Override
        public void onCaptureQRClick(View view) {
            scanQr();
        }

        @Override
        public void onInviteClick(View view) {
            inviteUserToListYou();
        }

        @Override
        public void onSearchByListYouClick(View view) {
            searchUser();
        }

        @Override
        public void onMyBussinessCardClick(View view) {
            openMyBussinessCard();
        }

        @Override
        public void searchListYouUser(View view) {
            search();
        }
    };

    private void search() {
        if (mSearch_keyword.getText().toString().trim().equals(user.listyouid)) {
            showOKAleart(mContext.getString(R.string.message_error), "You Can't add yourself as a friend");
        } else {
            AddFriendsDialog addFriendsDialog = AddFriendsDialog.getInstance();
            addFriendsDialog.searchUser(mContext, user, mSearch_keyword.getText().toString().trim());
        }

    }

    private void scanQr() {
        Intent i = new Intent(mContext, DecoderActivity.class);
        i.putExtra(AppConstant.USER, user);
        startActivity(i);
    }

    private void inviteUserToListYou() {
        InviteDialog inviteDialog = InviteDialog.getInstance();
        inviteDialog.creatInviteDialogBySmsEmailAndShare(mContext, user);
    }

    private void searchUser() {
        Fragment fragment = new AddListFragement(mContext, true, mAddListCount, user);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).addToBackStack(null).commit();
    }

    private void openMyBussinessCard() {
        Intent i = new Intent(mContext, BussinessCardScreenForAddFriend.class);
        startActivity(i);
    }

    private void recieveFriendsRequest() {
        progressDialog.show();
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, id);
        requestHandler.makePostRequest(mContext, params, AppConstant.RECEIVE_REQUEST_FRIENDS_API, new RequestListener() {
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                GroupItem item = new GroupItem();
                Object json = null;
                try {
                    json = new JSONTokener(message).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        item.title = "Received Request" + "(" + 0 + ")";
                        Freinds.countRequest(0, mAddListCount);
                    } else if (json instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) json;
                        try {
                            jsonArray = new JSONArray(message.toString().trim());
                            item.title = "Received Request" + "(" + jsonArray.length() + ")";
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
                                item.items.add(child);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Freinds.countRequest(jsonArray.length(), mAddListCount);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                items.add(item);
                setListviewToAdapter();
                progressDialog.dismiss();
            }
        });
    }

    public void showOKAleart(String title, String message) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(mContext).create();
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
