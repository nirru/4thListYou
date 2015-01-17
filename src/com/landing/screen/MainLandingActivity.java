/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.landing.screen;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.drawer.BlockUser;
import com.drawer.DrawerItemClick;
import com.drawer.ManageUser;
import com.drawer.PrivacyFragement;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.listyou.listener.LandingScreenClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.oxilo.qrreader.DecoderActivity;
import com.response.User;
import com.util.Freinds;
import com.util.InviteDialog;

import java.util.ArrayList;

/**
 * This example illustrates a common usage of the DrawerLayout widget in the
 * Android support library.
 * <p/>
 * <p>
 * When a navigation (left) drawer is present, the host activity should detect
 * presses of the action bar's Up affordance as a signal to open and close the
 * navigation drawer. The ActionBarDrawerToggle facilitates this behavior. Items
 * within the drawer should fall into one of two categories:
 * </p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic
 * policies as list or tab navigation in that a view switch does not create
 * navigation history. This pattern should only be used at the root activity of
 * a task, leaving some form of Up navigation active for activities further down
 * the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an
 * alternate parent for Up navigation. This allows a user to jump across an
 * app's navigation hierarchy at will. The application should treat this as it
 * treats Up navigation from a different task, replacing the current task stack
 * using TaskStackBuilder or similar. This is the only form of navigation drawer
 * that should be used outside of the root activity of a task.</li>
 * </ul>
 * <p/>
 * <p>
 * Right side drawers should be used for actions, not navigation. This follows
 * the pattern established by the Action Bar that navigation should be to the
 * left and actions to the right. An action should be an operation performed on
 * the current contents of the window, for example enabling or disabling a data
 * overlay on top of the current content.
 * </p>
 */
public class MainLandingActivity extends FragmentActivity implements DrawerItemClick {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    ArrayList<String> drawerListItemName;
    ArrayList<Drawable> drawerIconList;

    AsyncHttpClient client;
    ProgressDialog progressDialog;

    String id;
    String fname, lname, com_designation, company_name;

    ImageView header_qr, scan_btn, group_btn;

    private TextView mMoveLine, mFTextContactList, mFTextChat, mFTextAddList, mFTextNotification;
    private ImageView mFContactList, mFChat, mFAddList, mFNotification;
    private LinearLayout footer_linear_one, footer_linear_two, footer_linear_three, footer_linear_four;

    private TextView mContactListCount, mChatCount, mAddListCount, mNotificationCount;

    private User user;

    private GroupItem groupItem;
    private DrawerItemClickListener drawerItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);
        setActonBar();
        init();
        setCustomFont();
        drawerItemInit(savedInstanceState);
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            Log.e("GET FRAGEMENT MANAGER", "" + getFragmentManager().getBackStackEntryCount());
            this.finish();
        } else {
            Log.e("ELSE CONDITION", "" + getFragmentManager().getBackStackEntryCount());
            Fragment f = MainLandingActivity.this.getFragmentManager().findFragmentById(R.id.content_frame);
            if (f instanceof AddListFragement) {
                showContactPage();
            } else if (f instanceof GroupFragement) {
                showContactPage();
            } else if (f instanceof ManageUser) {
                showContactPage();
            } else if (f instanceof ContactListingfragement) {
                finish();
            } else {
                getFragmentManager().popBackStack();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        // view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_settings:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available,
                            Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void ItemClick(int pos) {
        Log.e("POSITION VALUSE", "" + pos);
        selectClickItem(pos);

    }

    /* The click listner for ListView in the naviga                                                tion drawer */
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Log.e("VALUSE OF POSITIION", "" + position);
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new ContactListingfragement(MainLandingActivity.this, mAddListCount, user);
        Bundle args = new Bundle();
        args.putInt(ContactListingfragement.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void selectClickItem(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                break;
            case 1:
                openMyBussinessCard(position);
                break;
            case 2:
                scanQr();
                break;
            case 3:
                manageContacts(position);
                break;
            case 4:
                inviteUserToListYou(position);
                break;
            case 5:
                share(position);
                break;
            case 6:
                manageGroups(position);
                break;
            case 7:
                privacySettings(position);
                break;
            case 8:
                blockUser(position);
                break;
            case 9:
                helpAndFaq(position);
                break;
            case 10:
                logOut(position);
                break;

            default:
                Toast.makeText(MainLandingActivity.this, "Not a valid selection", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * -------------------------init Item -------------- *
     */

    private void drawerItemInit(Bundle saBundle) {
        mTitle = mDrawerTitle = getTitle();
//        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        // set up the drawer's list view with items and click listener
        DrawerListAdaptor drawerListAdaptor = new DrawerListAdaptor(MainLandingActivity.this, groupItem, user, R.layout.custom_drawer_row, MainLandingActivity.this);
        mDrawerList.setAdapter(drawerListAdaptor);
        mDrawerList.setOnItemClickListener(drawerItemClickListener);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.bar, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (saBundle == null) {
            selectItem(0);
        }

    }

    private void init() {
        drawerItemClickListener = new DrawerItemClickListener();
        groupItem = new GroupItem();
        user = getIntent().getParcelableExtra(AppConstant.USER);

//        Log.e("USER LOGIN ID", "" + user.id);
//        Log.e("USER FIRST NAME", "" + user.firstName);

        mContactListCount = (TextView) findViewById(R.id.contact_list_count);
        mChatCount = (TextView) findViewById(R.id.chat_count);
        mAddListCount = (TextView) findViewById(R.id.add_list_count);
        mNotificationCount = (TextView) findViewById(R.id.notification_count);


        header_qr = (ImageView) findViewById(R.id.QR_btn);
        scan_btn = (ImageView) findViewById(R.id.scan_btn);
        group_btn = (ImageView) findViewById(R.id.group_image_view);

        footer_linear_one = (LinearLayout) findViewById(R.id.footer_linear_one);
        footer_linear_two = (LinearLayout) findViewById(R.id.footer_linear_two);
        footer_linear_three = (LinearLayout) findViewById(R.id.footer_linear_three);
        footer_linear_four = (LinearLayout) findViewById(R.id.footer_linear_four);

        mFTextContactList = (TextView) findViewById(R.id.footer_contact_listing_textview);
        mFTextChat = (TextView) findViewById(R.id.footer_chat_listing_textview);
        mFTextAddList = (TextView) findViewById(R.id.footer_add_listing_textview);
        mFTextNotification = (TextView) findViewById(R.id.footer_notification_listing_textview);

        mFContactList = (ImageView) findViewById(R.id.footer_contact_list);
        mFChat = (ImageView) findViewById(R.id.footer_chat_list);
        mFAddList = (ImageView) findViewById(R.id.footer_add_list);
        mFNotification = (ImageView) findViewById(R.id.footer_notification_list);
        mMoveLine = (TextView) findViewById(R.id.moveline);

        drawerListItemName = new ArrayList<String>();
        drawerIconList = new ArrayList<Drawable>();

        drawerListItemName.add(0, "");
        drawerListItemName.add(1, getResources().getString(R.string.drawer_my_buss_card));
        drawerListItemName.add(2, getResources().getString(R.string.drawer_scan_new_list));
        drawerListItemName.add(3, getResources().getString(R.string.drawer_manage_con_list));
        drawerListItemName.add(4, getResources().getString(R.string.drawer_invite_friend));
        drawerListItemName.add(5, getResources().getString(R.string.drawer_share_my_contact));
        drawerListItemName.add(6, getResources().getString(R.string.drawer_manage_group));
        drawerListItemName.add(7, getResources().getString(R.string.drawer_privacy_settings));
        drawerListItemName.add(8, getResources().getString(R.string.drawer_block_user));
        drawerListItemName.add(9, getResources().getString(R.string.drawer_help_faq));
        drawerListItemName.add(10, getResources().getString(R.string.drawer_logout));

        drawerIconList.add(0, getResources().getDrawable(R.drawable.user_profile_pic));
        drawerIconList.add(1, getResources().getDrawable(R.drawable.bussiness_card_selector));
        drawerIconList.add(2, getResources().getDrawable(R.drawable.scan_qr_selector));
        drawerIconList.add(3, getResources().getDrawable(R.drawable.manage_contact_selector));
        drawerIconList.add(4, getResources().getDrawable(R.drawable.invite_selector));
        drawerIconList.add(5, getResources().getDrawable(R.drawable.share_my_profile_selector));
        drawerIconList.add(6, getResources().getDrawable(R.drawable.manage_group_selector));
        drawerIconList.add(7, getResources().getDrawable(R.drawable.privacy_setting_selector));
        drawerIconList.add(8, getResources().getDrawable(R.drawable.blocked_user_selector));
        drawerIconList.add(9, getResources().getDrawable(R.drawable.help_selector));
        drawerIconList.add(10, getResources().getDrawable(R.drawable.logout_selector));


        for (int i = 0; i < drawerIconList.size(); i++) {
            ChildItem childItem = new ChildItem();
            childItem.mUserName = drawerListItemName.get(i).toString().trim();
            childItem.drawer_icon = drawerIconList.get(i);
            groupItem.items.add(childItem);
        }

        mContactListCount.setVisibility(View.INVISIBLE);
        mChatCount.setVisibility(View.INVISIBLE);
        mAddListCount.setVisibility(View.INVISIBLE);
        mNotificationCount.setVisibility(View.INVISIBLE);


        header_qr.setOnClickListener(landingScreenListener);
        scan_btn.setOnClickListener(landingScreenListener);
        group_btn.setOnClickListener(landingScreenListener);
        footer_linear_one.setOnClickListener(landingScreenListener);
        footer_linear_three.setOnClickListener(landingScreenListener);


    }

    private void setCustomFont() {
        mFTextContactList.setTypeface(SplashActivity.mpRegular);
        mFTextChat.setTypeface(SplashActivity.mpRegular);
        mFTextAddList.setTypeface(SplashActivity.mpRegular);
        mFTextNotification.setTypeface(SplashActivity.mpRegular);
    }

    private void setActonBar() {
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.landing_screen_header);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#242c3d")));
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(
                        android.R.color.transparent)));

    }

    LandingScreenClickListener landingScreenListener = new LandingScreenClickListener() {
        @Override
        public void onHeaderQrClick(View view) {
            showQr();
        }

        @Override
        public void onScanBtnClick(View view) {
            scanQr();
        }

        @Override
        public void onContactListBtnClick(View view) {
            showContactPage();
        }

        @Override
        public void onAddListBtnClick(View view) {
            showAddListPage();
        }

        @Override
        public void onGroupClk(View view) {
            showGroup();
        }
    };

    private void showQr() {
        notSelectedIcon();
        Fragment fragment = new ShowProfileFragement(MainLandingActivity.this, user);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.content_frame, fragment).addToBackStack(null);
        transaction.commit();
    }

    private void scanQr() {
        Intent i = new Intent(MainLandingActivity.this, DecoderActivity.class);
        i.putExtra(AppConstant.USER, user);
        startActivity(i);
//        finish();
    }

    private void showContactPage() {
        mFAddList.setImageResource(R.drawable.add_list_not_selected);
        mFContactList.setImageResource(R.drawable.contact_list_selected);
        Fragment fragment = new ContactListingfragement(MainLandingActivity.this, mAddListCount, user);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.content_frame, fragment).addToBackStack(null);
        transaction.commit();
        Log.e("FOOTER LINEAR_ONE", "--" + footer_linear_one.getX());
        Log.e("FOOTER LINEAR_FOUR", "--" + footer_linear_four.getX());
        moveRedLine(footer_linear_three.getX(), footer_linear_one.getX(), footer_linear_four.getY(), footer_linear_four.getY());
    }

    private void showAddListPage() {
        mFAddList.setImageResource(R.drawable.add_list_selected);
        mFContactList.setImageResource(R.drawable.contact_list_not_selected);
        Fragment fragment = new AddListFragement(MainLandingActivity.this, false, mAddListCount, user);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.content_frame, fragment, "MY_FRAGMENT").addToBackStack(null);
        transaction.commit();
        Log.e("FOOTER LINEAR_ONE", "--" + footer_linear_one.getX());
        Log.e("FOOTER LINEAR_FOUR", "--" + footer_linear_four.getX());
        moveRedLine(footer_linear_one.getX(), footer_linear_three.getX(), footer_linear_four.getY(), footer_linear_four.getY());
    }

    private void showGroup() {
        notSelectedIcon();
        Fragment fragment = new GroupFragement(MainLandingActivity.this, user, mAddListCount);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.content_frame, fragment).addToBackStack(null);
        transaction.commit();
    }

    private void moveRedLine(float xFrom, float xTo, float yFrom, final float yTo) {
        TranslateAnimation animation = new TranslateAnimation(xFrom, xTo,
                yFrom, yTo);
        animation.setDuration(600);
        animation.setRepeatCount(0);
        animation.setRepeatMode(0);
        animation.setFillAfter(true);
        mMoveLine.startAnimation(animation);
    }

    private void openMyBussinessCard(int position) {
        notSelectedIcon();
        Intent i = new Intent(MainLandingActivity.this, BussinessCardScreenForAddFriend.class);
        startActivity(i);

        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void manageContacts(int position) {
        notSelectedIcon();
        Fragment fragment = new ManageUser(MainLandingActivity.this, user, mAddListCount);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.content_frame, fragment).addToBackStack(null);
        transaction.commit();

        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void inviteUserToListYou(int position) {
        notSelectedIcon();
        InviteDialog inviteDialog = InviteDialog.getInstance();
        inviteDialog.creatInviteDialogBySmsEmailAndShare(MainLandingActivity.this, user);

        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void share(int position) {
        notSelectedIcon();
        Freinds.share(MainLandingActivity.this, user, "QrCode");
        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void manageGroups(int position) {

    }

    private void privacySettings(int position) {
        notSelectedIcon();
        Fragment fragment = new PrivacyFragement(MainLandingActivity.this, user);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.content_frame, fragment).addToBackStack(null);
        transaction.commit();

        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void blockUser(int position) {
        notSelectedIcon();
        Fragment fragment = new BlockUser(MainLandingActivity.this, user, mAddListCount);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.content_frame, fragment).addToBackStack(null);
        transaction.commit();
        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void helpAndFaq(int position) {
        notSelectedIcon();
        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void logOut(int position) {
        notSelectedIcon();
        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void notSelectedIcon() {
        mFAddList.setImageResource(R.drawable.add_list_not_selected);
        mFContactList.setImageResource(R.drawable.contact_list_not_selected);
    }

}