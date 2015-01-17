package com.landing.screen;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.adapter.ContactListAdapterAdapter;
import com.adapter.SearchableListAdapter;
import com.asyn.RequestHandler;
import com.asyn.RequestListener;
import com.customclasses.AnimatedExpandableListView;
import com.drawer.ManageUser;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.oxilo.showprofile.ShowProfileActivity;
import com.response.User;
import com.util.AllContact;
import com.util.ListYouAnimation;
import com.util.SelfProfileDialog;
import com.util.SettingsDialog;
import com.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Fragment that appears in the "content_frame", shows a planet
 */
public class ContactListingfragement extends Fragment {
    public static final String ARG_PLANET_NUMBER = "planet_number";

    AnimatedExpandableListView expListView;
    Context context;

    AsyncHttpClient client;
    ProgressDialog progressDialog;

    DisplayImageOptions options;
    public ImageLoader imageLoader;
    AnimateFirstDisplayListener animateFirstDisplayListener;

    String id;

    TextView user_profile_name, designation, companyName;
    ImageView user_proile_pic, settings;

    private ArrayList<ArrayList<String>> friendList;
    TextView mAddListCount;
    private User user;
    private List<GroupItem> items;
    private RelativeLayout list_you_friends_relative;
    private TextView friendListTab;
    private EditText friendListSearch_tv;
    private Spinner sp_system_defined_group;
    private SystemSpinner systemSpinner;
    private int spinnerSelectedPosition;
    ContactListAdapterAdapter adapter;
    private boolean isFirstTimes = false;
    private GroupItem filterGroupItem;
    private boolean isManageGroupToShow = true;
    /**
     * ---------------All Contact Layout Variable-----------------------
     */

    private ListView all_contact_list_view;
    private TextView all_contact_count, allContactTab, text1, text2;
    private ImageView icondown;
    private EditText search_action_for_all_contact;
    private RelativeLayout all_contact_relative;
    private Button click_search_button;
    private TextSwitcher mTextSwitcher;
    private GroupItem groupItem;
    private GroupItem groupItemForAllContact;
    private SearchableListAdapter searchableListAdapter;
    private Button button;

    public ContactListingfragement(Context context, TextView mAddListCount, User user) {
        // Empty constructor required for fragment subclasses
        this.context = context;
        this.mAddListCount = mAddListCount;
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_contact_listing, container,
                false);
        init(rootView);
        initForAllContactTab(rootView);
        createAnim();
        setDetails();
        checkWhetehrToLoadFriendFromLocalOrServer();
        return rootView;
    }

    private void init(View root) {
        filterGroupItem = new GroupItem();
        systemSpinner = new SystemSpinner();
        items = new ArrayList<GroupItem>();
        animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(context));
        options = new DisplayImageOptions.Builder().showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(null).cacheInMemory(false).cacheOnDisk(false)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(0)).build();
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "com.oxilo.listyou_app_country_code",
                ShowProfileActivity.MODE_PRIVATE);
        id = sharedPreferences.getString(AppConstant.REG_ID, "");
        client = new AsyncHttpClient();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        button = (Button) root.findViewById(R.id.button);
        expListView = (AnimatedExpandableListView) root.findViewById(R.id.lvExp);
        user_profile_name = (TextView) root.findViewById(R.id.user_profile_name);
        designation = (TextView) root.findViewById(R.id.designation);
        companyName = (TextView) root.findViewById(R.id.company);
        user_proile_pic = (ImageView) root.findViewById(R.id.imageView);
        settings = (ImageView) root.findViewById(R.id.imageView5);
        friendList = new ArrayList<ArrayList<String>>();
        list_you_friends_relative = (RelativeLayout) root.findViewById(R.id.list_you_friends_relative);
        friendListTab = (TextView) root.findViewById(R.id.friend_list);
        friendListSearch_tv = (EditText) root.findViewById(R.id.search_action);
        sp_system_defined_group = (Spinner) root.findViewById(R.id.spinner_system_defined_group);
        expListView.setGroupIndicator(null);
        settings.setOnClickListener(sL);

        friendListTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isManageGroupToShow = true;
                ListYouAnimation.changeBackgroundOnSelection(v, "#c3c3c3", "#242c3d");
                ListYouAnimation.changeBackgroundOnSelection(allContactTab, "#242c3d", "#c3c3c3");
                list_you_friends_relative.startAnimation(ListYouAnimation.inFromLeftAnimation());
                allContactTab.setEnabled(true);
                friendListTab.setEnabled(false);
                AllContact allContact = AllContact.getInstance();
                allContact.showview(context, list_you_friends_relative);
                allContact.hideview(context, all_contact_relative);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelfProfileDialog selfProfileDialog = SelfProfileDialog.getInstance();
                selfProfileDialog.createDialog(context, user);
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.system_group_aray, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        sp_system_defined_group.setAdapter(adapter);
        sp_system_defined_group.setOnItemSelectedListener(systemSpinner);

    }

    private void initForAllContactTab(View root) {
        text1 = (TextView) root.findViewById(R.id.text1);
        text2 = (TextView) root.findViewById(R.id.text2);
        groupItem = new GroupItem();
        groupItemForAllContact = new GroupItem();
        all_contact_relative = (RelativeLayout) root.findViewById(R.id.all_contact_relative);
        all_contact_count = (TextView) root.findViewById(R.id.lblListHeader);
        icondown = (ImageView) root.findViewById(R.id.drop_down);
        allContactTab = (TextView) root.findViewById(R.id.all_contact_list);
        search_action_for_all_contact = (EditText) root.findViewById(R.id.search_action_for_all_contact);
        click_search_button = (Button) root.findViewById(R.id.click_search_button);
        all_contact_list_view = (ListView) root.findViewById(R.id.all_contact_list_view);
        mTextSwitcher = (TextSwitcher) root.findViewById(R.id.main_textswitcher);
        text1.setVisibility(View.VISIBLE);
        text2.setVisibility(View.VISIBLE);

        all_contact_count.setText("All contacts");
        all_contact_count.setTypeface(SplashActivity.mpBold);

        allContactTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isManageGroupToShow = false;
                ListYouAnimation.changeBackgroundOnSelection(v, "#c3c3c3", "#242c3d");
                ListYouAnimation.changeBackgroundOnSelection(friendListTab, "#242c3d", "#c3c3c3");
                all_contact_relative.startAnimation(ListYouAnimation.inFromRightAnimation());
                allContactTab.setEnabled(false);
                friendListTab.setEnabled(true);
                AllContact allContact = AllContact.getInstance();
                allContact.hideview(context, list_you_friends_relative);
                allContact.showview(context, all_contact_relative);
            }
        });
    }


    private void setFooterCount(int count) {
        if (count == 0)
            mAddListCount.setVisibility(View.INVISIBLE);
        else {
            mAddListCount.setVisibility(View.VISIBLE);
            mAddListCount.setText("" + count);
        }
    }

    View.OnClickListener sL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showEditProfile();
        }
    };

    private void showEditProfile() {
        SettingsDialog settingsDialog = SettingsDialog.getInstance();
        settingsDialog.createDialog(context,user,mAddListCount,isManageGroupToShow);
    }

    private void setListviewToAdapter() {
        if (items.size() > 0) {
            adapter = new ContactListAdapterAdapter(context, user, filterGroupItem);
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

            friendListSearch_tv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    expListView.expandGroupWithAnimation(2);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    expListView.expandGroupWithAnimation(2);
                    String text = friendListSearch_tv.getText().toString().toLowerCase(Locale.getDefault());
                    Log.e("Filet Text", text);
                    adapter.filter(text);

                }

                @Override
                public void afterTextChanged(Editable s) {
                    expListView.expandGroupWithAnimation(2);
                }
            });
        } else {
            Toast.makeText(context, "No Friend, No Group , No Recent Freinds", Toast.LENGTH_LONG).show();
        }


    }

    private void setAdapterForAllContct() {
        if (groupItem != null) {
            searchableListAdapter = new SearchableListAdapter(context, user, groupItem, text1, text2, mTextSwitcher);
            all_contact_list_view.setAdapter(searchableListAdapter);
        } else {
            Toast.makeText(context, "No Friend", Toast.LENGTH_LONG).show();
        }

        search_action_for_all_contact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = search_action_for_all_contact.getText().toString().toLowerCase(Locale.getDefault());
                searchableListAdapter.filter(text);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void showList() {
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, user.id);
        requestHandler.makePostRequest(context, params, AppConstant.GET_FRIEND_LIST, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                Util.writeFreindsToPrefrefs(context, result);
                GroupItem item = new GroupItem();
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        item.title = "Friends" + "(" + 0 + ")";
                        items.add(item);
                    } else if (json instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) json;
                        getFriendsList(result, context.getResources().getString(R.string.all_group));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setListviewToAdapter();
                setAdapterForAllContct();
                getCount();
            }
        });

    }


    private void setDetails() {
        if (user.userPicUrl.equals("")
                || user.userPicUrl == null
                || user.userPicUrl == "null") {
//			Log.e("I AI IN IF CONDITION", "YES");
            user_proile_pic.setVisibility(View.INVISIBLE);
        } else {
            imageLoader.displayImage(user.userPicUrl, user_proile_pic,
                    options, animateFirstDisplayListener);
        }

        user_profile_name.setTypeface(SplashActivity.mpRegular);
        designation.setTypeface(SplashActivity.mpRegular);
        companyName.setTypeface(SplashActivity.mpRegular);

        user_profile_name.setText(user.firstName + " " + user.lastName);
        designation.setText(user.designation);
        companyName.setText(user.comapnyName);
    }

    private void getFriendsList(String message, String Title) {
        GroupItem item = new GroupItem();
        JSONArray jsonArray;
        if (message.trim().equals("")) {
            item.title = Title + "(" + 0 + ")";
            items.add(item);
        } else {
            try {
                jsonArray = new JSONArray(message.toString().trim());
                item.title = Title + "(" + jsonArray.length() + ")";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ChildItem child = new ChildItem();
                    child.remoteUserId = jsonObject
                            .getString(AppConstant.SENDER_UID);
                    child.mUserName = jsonObject
                            .getString(AppConstant.RECEIVER_USERNAME);
                    child.mCompanyName = jsonObject
                            .getString(AppConstant.RECEIVER_COMPANY_NAME);
                    child.mUserPicUrl = jsonObject
                            .getString(AppConstant.RECEIVER_PROFILE_PIC_URL);
                    child.qr_image_link = jsonObject
                            .getString(AppConstant.QR_IMAGE_URL);
                    item.items.add(child);
                    filterGroupItem.items.add(child);
                    if (!isFirstTimes)
                        groupItem.items.add(child);

                }
                items.add(item);

//            Log.e("<<<<<<<<<<RECEIVED REQUEST API LIST ARRAY", "" + items.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void getRecentFriendList() {
        progressDialog.show();
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, id);
        requestHandler.makePostRequest(context, params, AppConstant.GET_RECENT_FRIEND_LIST, new RequestListener() {
            @Override
            public void onSuccess(String message) {
                Util.writeRecentFreindsToPrefrefs(context, message);
                GroupItem item = new GroupItem();
                Object json = null;
                try {
                    json = new JSONTokener(message).nextValue();
                    if (json instanceof JSONObject) {
//                        Log.e("I am in IF", "YES");
                        JSONObject jsonObject = (JSONObject) json;
                        item.title = "Recently Added" + "(" + 0 + ")";
                    } else if (json instanceof JSONArray) {
//                        Log.e("I am in ELSE", "YES");
                        JSONArray jsonArray = (JSONArray) json;
//                        Log.e("I am in NOT FOUND", "Yes");
                        try {
                            jsonArray = new JSONArray(message.toString().trim());
                            item.title = "Recently Added" + "(" + jsonArray.length() + ")";
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ChildItem child = new ChildItem();
                                child.remoteUserId = jsonObject
                                        .getString(AppConstant.SENDER_UID);
                                child.mUserName = jsonObject
                                        .getString(AppConstant.RECEIVER_USERNAME);
                                child.mCompanyName = jsonObject
                                        .getString(AppConstant.RECEIVER_COMPANY_NAME);
                                child.mUserPicUrl = jsonObject
                                        .getString(AppConstant.RECEIVER_PROFILE_PIC_URL);
                                child.qr_image_link = jsonObject
                                        .getString(AppConstant.QR_IMAGE_URL);
                                item.items.add(child);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                items.add(item);
                getGRoups();
            }
        });
    }

    private void getGRoups() {
        final RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, user.id);
        requestHandler.makePostRequest(context, params, AppConstant.GET_GROUP_LIST, new RequestListener() {
            @Override
            public void onSuccess(String message) {
                Util.writeGroupToPrefrefs(context, message);
                GroupItem item = new GroupItem();
                Object json = null;
                try {
                    json = new JSONTokener(message).nextValue();
                    if (json instanceof JSONObject) {
//                        Log.e("I am in IF111", "YES");
                        JSONObject jsonObject = (JSONObject) json;
                        item.title = "Groups" + "(" + 0 + ")";
                    } else if (json instanceof JSONArray) {
//                        Log.e("I am in ELSE22222", "YES");
                        JSONArray jsonArray = (JSONArray) json;
                        try {
                            jsonArray = new JSONArray(message.toString().trim());
                            item.title = "Group" + "(" + jsonArray.length() + ")";
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ChildItem child = new ChildItem();
                                child.mUserName = jsonObject
                                        .getString(AppConstant.NAME_SET_TO_GROUP);
                                child.mCompanyName = jsonObject
                                        .getString(AppConstant.GROUP_MEMBER_COUNT);
                                child.mUserPicUrl = jsonObject
                                        .getString(AppConstant.GROUP_IMAGE);
                                item.items.add(child);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                items.add(item);
                showList();
            }
        });
    }

    private void getCount() {
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, id);
        requestHandler.makePostRequest(context, params, AppConstant.RECEIVE_REQUEST_FRIENDS_API, new RequestListener() {
            @Override
            public void onSuccess(String message) {
                GroupItem item = new GroupItem();
                Object json = null;
                try {
                    json = new JSONTokener(message).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        setFooterCount(0);
                    } else if (json instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) json;
                        setFooterCount(jsonArray.length());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void showOKAleart(String title, String message) {
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


    private void checkWhetehrToLoadFriendFromLocalOrServer() {
        if (Util.readContactListingFirstLaunchToPrefrefs(context)) {
            Log.e("[[[]]]]CONTACTLISTING TRUE]][[", "" + "<<<<<<<TRUE");
            getRecentFriendList();
            ManageUser manageUser = new ManageUser(context, user, mAddListCount);
            manageUser.fetchSystemDetails();
            Util.writeContactListingFirstLaunchToPrefrefs(context, false);
            Util.writeAppToPrefrefs(context, false);
        } else {
            Log.e("[[[]]]]CONTACTLISTING FALSE]][[", "" + "<<<<<<<FALSE");
            Util.writeContactListingFirstLaunchToPrefrefs(context, false);
            fetchRecentFreindListFromJson(Util.readRecentFreindsFromPrefs(context));
            setListviewToAdapter();
            setAdapterForAllContct();
        }
    }

    private void fetchRecentFreindListFromJson(String message) {
        GroupItem item = new GroupItem();
        Object json = null;
        try {
            json = new JSONTokener(message).nextValue();
            if (json instanceof JSONObject) {
//                        Log.e("I am in IF", "YES");
                JSONObject jsonObject = (JSONObject) json;
                item.title = "Recently Added" + "(" + 0 + ")";
            } else if (json instanceof JSONArray) {
//                        Log.e("I am in ELSE", "YES");
                JSONArray jsonArray = (JSONArray) json;
//                        Log.e("I am in NOT FOUND", "Yes");
                try {
                    jsonArray = new JSONArray(message.toString().trim());
                    item.title = "Recently Added" + "(" + jsonArray.length() + ")";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ChildItem child = new ChildItem();
                        child.remoteUserId = jsonObject
                                .getString(AppConstant.SENDER_UID);
                        child.mUserName = jsonObject
                                .getString(AppConstant.RECEIVER_USERNAME);
                        child.mCompanyName = jsonObject
                                .getString(AppConstant.RECEIVER_COMPANY_NAME);
                        child.mUserPicUrl = jsonObject
                                .getString(AppConstant.RECEIVER_PROFILE_PIC_URL);
                        child.qr_image_link = jsonObject
                                .getString(AppConstant.QR_IMAGE_URL);
                        item.items.add(child);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        items.add(item);
        fetchGroupFromJson(Util.readGroupsFromPrefs(context));
    }

    private void fetchGroupFromJson(String message) {
        GroupItem item = new GroupItem();
        Object json = null;
        try {
            json = new JSONTokener(message).nextValue();
            if (json instanceof JSONObject) {
//                Log.e("I am in IF111", "YES");
                JSONObject jsonObject = (JSONObject) json;
                item.title = "Groups" + "(" + 0 + ")";
            } else if (json instanceof JSONArray) {
//                Log.e("I am in ELSE22222", "YES");
                JSONArray jsonArray = (JSONArray) json;
                try {
                    jsonArray = new JSONArray(message.toString().trim());
                    item.title = "Group" + "(" + jsonArray.length() + ")";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ChildItem child = new ChildItem();
                        child.mUserName = jsonObject
                                .getString(AppConstant.NAME_SET_TO_GROUP);
                        child.mCompanyName = jsonObject
                                .getString(AppConstant.GROUP_MEMBER_COUNT);
                        child.mUserPicUrl = jsonObject
                                .getString(AppConstant.GROUP_IMAGE);
                        item.items.add(child);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        items.add(item);
        fetchAllFreindFromJson(Util.readFreindsFromPrefs(context));
    }

    private void fetchAllFreindFromJson(String message) {
        GroupItem item = new GroupItem();
        Object json = null;
        try {
            json = new JSONTokener(message).nextValue();
            if (json instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) json;
                item.title = "Freinds" + "(" + 0 + ")";
                items.add(item);
            } else if (json instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) json;
                chooseFriendsByFilters();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void chooseFriendsByFilters() {
        switch (spinnerSelectedPosition) {
            case 0:
                getFriendsList(Util.readFreindsFromPrefs(context), context.getResources().getString(R.string.all_group));
                break;
            case 1:
                getFriendsList(Util.readCoWorkerFromPrefs(context), context.getResources().getString(R.string.co_worker_group));
                break;
            case 2:
                getFriendsList(Util.readClientFromPrefs(context), context.getResources().getString(R.string.client_group));
                break;
            case 3:
                getFriendsList(Util.readPartnersFromPrefs(context), context.getResources().getString(R.string.partner_group));
                break;
            case 4:
                getFriendsList(Util.readAquitanceFromPrefs(context), context.getResources().getString(R.string.aquitance_group));
                break;
            case 5:
                getFriendsList(Util.readGoodToKnowFromPrefs(context), context.getResources().getString(R.string.good_to_know_group));
                break;
            case 6:
                getFriendsList(Util.readFamilyFromPrefs(context), context.getResources().getString(R.string.family_group));
                break;
            case 7:
                getFriendsList(Util.readOthersFromPrefs(context), context.getResources().getString(R.string.other_group));
                break;

            default:
                Toast.makeText(context, "Not a valid selection", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAnim() {
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                TextView myText = new TextView(context);
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

        mTextSwitcher.setInAnimation(AnimationUtils.loadAnimation(context,
                R.anim.push_up_in));
        mTextSwitcher.setOutAnimation(context, R.anim.push_up_out);
    }

    private class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
//				Log.e("YES MAI HOO DON", "  " + "AYA DON");
                ImageView imageView = (ImageView) view;
                imageView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
            // TODO Auto-generated method stub
            super.onLoadingFailed(imageUri, view, failReason);
//			Log.e("LOADING FAILED", "  " + "AYA DON");
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            // TODO Auto-generated method stub
            super.onLoadingCancelled(imageUri, view);
//			Log.e("LOADING Cancelled", "  " + "AYA DON");
        }
    }

    /**
     * ----------------------------------------------Spinner Class------------------------------*
     */

    private class SystemSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            Log.e("MERA NAM JOKER", "" + position);
            spinnerSelectedPosition = position;

            if (friendListSearch_tv != null) {
                if (!friendListSearch_tv.getText().toString().equals("")) {
                    friendListSearch_tv.getText().clear();
                }
            }

            if (spinnerSelectedPosition > 0) {
                isFirstTimes = true;
                clickItemAction();
            } else {
                if (isFirstTimes) {
                    clickItemAction();
                }
            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void clickItemAction() {
        items.clear();
        filterGroupItem.items.clear();
//        Toast.makeText(context, "MERA NAM JOKER" + " " + spinnerSelectedPosition, Toast.LENGTH_LONG).show();
        fetchRecentFreindListFromJson(Util.readRecentFreindsFromPrefs(context));
//        Log.e("Fileter GROUP ITEM SIZE", "" + filterGroupItem.items.size());
        searchableListAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
    }

}
