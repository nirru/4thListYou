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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.adapter.ManageUserAdapter;
import com.asyn.RequestListener;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.response.User;
import com.util.Freinds;
import com.util.MoveDialog;
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
public class ManageUser extends Fragment {

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
    public ManageUserAdapter adapter;
    private Spinner sp_system_defined_group;
    private SystemSpinner systemSpinner;
    private RelativeLayout relative_bottom;
    private String old_group;
    private int old_group_pos;
    private TextView mAddListCount;
    private EditText search_action_for_all_contact;
    private TextView header;

    public ManageUser(Context mContext, User user, TextView mAddListCount) {
        this.mContext = mContext;
        this.user = user;
        this.mAddListCount = mAddListCount;
        items = new ArrayList<GroupItem>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_manage_user, container,
                false);
        init(rootView);
        setCustomFont();
        return rootView;
    }

    private void init(View root) {
        systemSpinner = new SystemSpinner();
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


        sp_system_defined_group = (Spinner) root.findViewById(R.id.spinner_system_defined_group);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.system_group_aray, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        sp_system_defined_group.setAdapter(adapter);
        sp_system_defined_group.setOnItemSelectedListener(systemSpinner);

        createAnim();

        relative_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move();
            }
        });
    }

    private void setCustomFont() {
        members.setTypeface(SplashActivity.mpBold);
        members.setText("Freinds");
        header.setTypeface(SplashActivity.mpBold);
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
            adapter = new ManageUserAdapter(mContext, user, groupItem, text1, text2, mTextSwitcher);
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
//                Log.e("ITEM Selected", "" + Util.removeLastChar(showResult()));
                moveGroup();
            } else {
                Util.showOKAleart(mContext, "Message", "Please add some friends and then try.");
            }
        } else {
            Toast.makeText(mContext, "You are trying to move with no friend , Please add some friends and then try.", Toast.LENGTH_LONG).show();
        }
    }

    private void moveGroup() {
        MoveDialog moveDialog = MoveDialog.getInstance();
        moveDialog.createDialog(mContext, old_group, old_group_pos, Util.removeLastChar(showResult()), user, mAddListCount);
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

    private class SystemSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(mContext, "Selected postion" + " " + position, Toast.LENGTH_LONG).show();
            old_group_pos = position;
            selectItem(position);
        }


        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                allFriends();
                break;
            case 1:
                coWorker();
                break;
            case 2:
                client();
                break;
            case 3:
                partner();
                break;
            case 4:
                aquitance();
                break;
            case 5:
                goodToKnow();
                break;
            case 6:
                family();
                break;
            case 7:
                others();
                break;

            default:
                Toast.makeText(mContext, "Not a valid selection", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void others() {
        old_group = mContext.getResources().getString(R.string.other_group);
        if (Util.readOthersFromPrefs(mContext).equals("")) {
            groupItem.items.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(mContext, "There is no member in this group", Toast.LENGTH_LONG).show();
        } else {
            showList(Util.readOthersFromPrefs(mContext));
        }

    }

    private void family() {
        old_group = mContext.getResources().getString(R.string.family_group);
        if (Util.readFamilyFromPrefs(mContext).equals("")) {
            groupItem.items.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(mContext, "There is no member in this group", Toast.LENGTH_LONG).show();
        } else {
            showList(Util.readFamilyFromPrefs(mContext));
        }

    }

    private void goodToKnow() {
        old_group = mContext.getResources().getString(R.string.good_to_know_group);
        if (Util.readGoodToKnowFromPrefs(mContext).equals("")) {
            groupItem.items.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(mContext, "There is no member in this group", Toast.LENGTH_LONG).show();
        } else {
            showList(Util.readGoodToKnowFromPrefs(mContext));
        }
    }

    private void aquitance() {
        old_group = mContext.getResources().getString(R.string.aquitance_group);
        if (Util.readAquitanceFromPrefs(mContext).equals("")) {
            groupItem.items.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(mContext, "There is no member in this group", Toast.LENGTH_LONG).show();
        } else {
            showList(Util.readAquitanceFromPrefs(mContext));
        }

    }

    private void partner() {
        old_group = mContext.getResources().getString(R.string.partner_group);
        if (Util.readPartnersFromPrefs(mContext).equals("")) {
            groupItem.items.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(mContext, "There is no member in this group", Toast.LENGTH_LONG).show();
        } else {
            showList(Util.readPartnersFromPrefs(mContext));
        }

    }


    private void client() {
        old_group = mContext.getResources().getString(R.string.client_group);
        if (Util.readClientFromPrefs(mContext).equals("")) {
            groupItem.items.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(mContext, "There is no member in this group", Toast.LENGTH_LONG).show();
        } else {
            showList(Util.readClientFromPrefs(mContext));
        }

    }

    private void coWorker() {
        old_group = mContext.getResources().getString(R.string.co_worker_group);
        if (Util.readCoWorkerFromPrefs(mContext).equals("")) {
            groupItem.items.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(mContext, "There is no member in this group", Toast.LENGTH_LONG).show();
        } else {
            Log.e("COWORKER", "" + "  " + Util.readCoWorkerFromPrefs(mContext));
            showList(Util.readCoWorkerFromPrefs(mContext));
        }
    }

    private void allFriends() {
        old_group = mContext.getResources().getString(R.string.all_group);
        if (Util.readFreindsFromPrefs(mContext).equals("")) {
            groupItem.items.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(mContext, "There is no member in this group", Toast.LENGTH_LONG).show();
        } else {
            showList(Util.readFreindsFromPrefs(mContext));
        }
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

    public void fetchSystemDetails() {
//            progressDialog.show();
        getCoWorkerDetails();

    }

    private void getCoWorkerDetails() {
        Freinds freinds = Freinds.getInstance();
        freinds.getSystemGroupInformation(mContext, user.id, mContext.getResources().getString(R.string.co_worker_group), 1, new RequestListener() {
            @Override
            public void onSuccess(String result) {
//                Log.e("COWORKER DETAILS", "" + result);
                Util.writeCoWorkerToPrefrefs(mContext, result);
                getClientDetails();
            }
        });
    }

    private void getClientDetails() {
        Freinds freinds = Freinds.getInstance();
        freinds.getSystemGroupInformation(mContext, user.id, mContext.getResources().getString(R.string.client_group), 2, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                Util.writeClientToPrefrefs(mContext, result);
                getPartnersDetails();
            }
        });
    }

    private void getPartnersDetails() {
        Freinds freinds = Freinds.getInstance();
        freinds.getSystemGroupInformation(mContext, user.id, mContext.getResources().getString(R.string.partner_group), 3, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                Util.writePartnersToPrefrefs(mContext, result);
                getAquitanceDetails();
            }
        });
    }

    private void getAquitanceDetails() {
        Freinds freinds = Freinds.getInstance();
        freinds.getSystemGroupInformation(mContext, user.id, mContext.getResources().getString(R.string.aquitance_group), 4, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                Util.writeAquitanceToPrefrefs(mContext, result);
                getgoodToKnowDetails();
            }
        });
    }

    private void getgoodToKnowDetails() {
        Freinds freinds = Freinds.getInstance();
        freinds.getSystemGroupInformation(mContext, user.id, mContext.getResources().getString(R.string.good_to_know_group), 5, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                Util.writeGoodToKnowToPrefrefs(mContext, result);
                familyDetails();
            }
        });
    }

    private void familyDetails() {
        Freinds freinds = Freinds.getInstance();
        freinds.getSystemGroupInformation(mContext, user.id, mContext.getResources().getString(R.string.family_group), 6, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                Util.writeFamilyToPrefrefs(mContext, result);
                othersDetails();
            }
        });
    }

    private void othersDetails() {
        Freinds freinds = Freinds.getInstance();
        freinds.getSystemGroupInformation(mContext, user.id, mContext.getResources().getString(R.string.other_group), 7, new RequestListener() {
            @Override
            public void onSuccess(String result) {
//                progressDialog.dismiss();
                Util.writeOthersToPrefrefs(mContext, result);
                Util.writeAppToPrefrefs(mContext, false);
            }
        });
    }

}

