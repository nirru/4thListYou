package com.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.customclasses.AnimateFirstDisplayListener;
import com.customclasses.AnimatedExpandableListView;
import com.holder.ChildHolder;
import com.holder.ChildItem;
import com.holder.GroupHolder;
import com.holder.GroupItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.response.User;
import com.util.RecentTapDialog;
import com.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactListAdapterAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;
    private List<GroupItem> items;
    private AnimateFirstDisplayListener animateFirstListener;
    private DisplayImageOptions options;
    public ImageLoader imageLoader;
    private Context context;
    private User user;
    private GroupItem filterGroupItem;
    private List<ChildItem> originalData;
    private List<GroupItem> originalItems;

    public ContactListAdapterAdapter(Context context, User user, GroupItem filterGroupItem) {
        this.context = context;
        this.user = user;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        animateFirstListener = new AnimateFirstDisplayListener(context);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(100)).build();
        this.filterGroupItem = filterGroupItem;
        this.originalData = new ArrayList<ChildItem>();
        this.originalData.addAll(filterGroupItem.items);
        Log.e("<><><><SIZE OF FILET GROUP>><><", "" + filterGroupItem.items.size());
    }

    public void setData(List<GroupItem> items) {
        this.items = items;
        originalItems = new ArrayList<GroupItem>();
        this.originalItems.addAll(items);
    }

    @Override
    public ChildItem getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(final int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        ChildItem item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder.mUserName = (TextView) convertView
                    .findViewById(R.id.user_profile_name);
            holder.mCompanyName = (TextView) convertView
                    .findViewById(R.id.company);
            holder.mUserProfilePic = (ImageView) convertView
                    .findViewById(R.id.imageView6);
            holder.mGroupName = (TextView) convertView
                    .findViewById(R.id.group_name);
            convertView.setTag(holder);
            convertView.setId(childPosition);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        if (groupPosition == 0) {
            convertView.setClickable(true);
            holder.mCompanyName.setVisibility(View.VISIBLE);
            holder.mUserName.setVisibility(View.VISIBLE);
            holder.mGroupName.setVisibility(View.INVISIBLE);
        } else if (groupPosition == 1) {
            convertView.setClickable(false);
            holder.mCompanyName.setVisibility(View.INVISIBLE);
            holder.mUserName.setVisibility(View.INVISIBLE);
            holder.mGroupName.setVisibility(View.VISIBLE);
        } else {
            convertView.setClickable(false);
            holder.mCompanyName.setVisibility(View.VISIBLE);
            holder.mUserName.setVisibility(View.VISIBLE);
            holder.mGroupName.setVisibility(View.INVISIBLE);
        }
        holder.mGroupName.setText(item.mUserName + "(" + item.mCompanyName + ")");
        holder.mCompanyName.setTypeface(SplashActivity.mpRegular);
        holder.mUserName.setTypeface(SplashActivity.mpRegular);
        holder.mGroupName.setTypeface(SplashActivity.mpRegular);
        holder.mUserName.setText(item.mUserName);
        holder.mCompanyName.setText(item.mCompanyName);
        imageLoader.displayImage(item.mUserPicUrl,
                holder.mUserProfilePic, options, animateFirstListener);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (groupPosition == 0 || groupPosition == 2) {
                    ChildItem item = getChild(groupPosition, id);
                    RecentTapDialog recentTapDialog = RecentTapDialog.getInstance();
                    recentTapDialog.createDialog(item, user, context);
                }

            }
        });
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).items.size();
    }

    @Override
    public GroupItem getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupHolder holder;
        GroupItem item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.list_group, parent, false);
            holder.title = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }
        holder.title.setTypeface(SplashActivity.mpBold);
        holder.title.setText(item.title);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    // Filter Class
    public void filter(String charText) {
        Log.e("Filter Group item title", "" + items.get(2).title);
        String title = items.get(2).title;
        charText = charText.toLowerCase(Locale.getDefault());
        items.clear();
        filterGroupItem.items.clear();
        if (charText.length() == 0) {
            items.addAll(originalItems);
            filterGroupItem.items.addAll(originalData);
        } else {
            for (ChildItem cI : originalData) {
                if (cI.mUserName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    filterGroupItem.title = title;
                    filterGroupItem.items.add(cI);
                    fetchRecentFreindListFromJson(Util.readRecentFreindsFromPrefs(context));
                }
            }
        }
        notifyDataSetChanged();
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
                Log.e("I am in IF111", "YES");
                JSONObject jsonObject = (JSONObject) json;
                item.title = "Groups" + "(" + 0 + ")";
            } else if (json instanceof JSONArray) {
                Log.e("I am in ELSE22222", "YES");
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
        fetchAllFreindFromJson();
    }

    private void fetchAllFreindFromJson() {
        items.add(filterGroupItem);
    }

}