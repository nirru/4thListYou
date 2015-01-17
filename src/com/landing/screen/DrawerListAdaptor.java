package com.landing.screen;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.customclasses.AnimateFirstDisplayListener;
import com.drawer.DrawerItemClick;
import com.holder.ChildHolder;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.oxilo.applistyou.R;
import com.response.User;

import java.util.ArrayList;

public class DrawerListAdaptor extends BaseAdapter {

    Context context;
    int layoutResourceId;
    ArrayList<String> values;
    ArrayList<Drawable> drawable;
    String profile_name, company_name;
    private GroupItem groupitem;
    private User user;
    private AnimateFirstDisplayListener animateFirstListener;
    private DisplayImageOptions options;
    public ImageLoader imageLoader;
    DrawerItemClick drawerItemClick;
    public DrawerListAdaptor(Context context,
                             GroupItem groupitem, User user, int layoutResourceId, DrawerItemClick drawerItemClick) {
        // super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.groupitem = groupitem;
        this.user = user;
        this.drawerItemClick = drawerItemClick;

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        animateFirstListener = new AnimateFirstDisplayListener(context);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(0)).build();
    }

    @Override
    public int getCount() {
        if (groupitem != null)
            return groupitem.items.size();
        else
            return 0;
    }

    @Override
    public ChildItem getItem(int position) {
        return groupitem.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        View row = convertView;
        ChildHolder holder = null;
        ChildItem item = getItem(pos);
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ChildHolder();
            row.setTag(holder);

        } else {
            holder = (ChildHolder) row.getTag();
        }

        holder.rel_one = (RelativeLayout) row.findViewById(R.id.rel_one);
        holder.rel_two = (RelativeLayout) row.findViewById(R.id.rel_two);
        holder.mUserProfilePic = (ImageView) row.findViewById(R.id.user_profile_pic);
        holder.list_text_desc = (TextView) row.findViewById(R.id.list_text_desc);
        holder.list_icon = (Button) row.findViewById(R.id.list_icon);
        holder.mUserName = (TextView)row.findViewById(R.id.profile_name);
        holder.mCompanyName = (TextView)row.findViewById(R.id.com_name);
        holder.mDesignation = (TextView)row.findViewById(R.id.designation);
        if (item.mUserName.toString().equals("")) {
            imageLoader.displayImage(user.userPicUrl,
                    holder.mUserProfilePic, options, animateFirstListener);
            holder.mUserName.setText(user.firstName + " " + user.lastName);
            holder.mCompanyName.setText(user.comapnyName);
            holder.mDesignation.setText(user.designation);
            holder.rel_one.setVisibility(View.VISIBLE);
            holder.rel_two.setVisibility(View.GONE);
        } else {
            holder.rel_one.setVisibility(View.GONE);
            holder.rel_two.setVisibility(View.VISIBLE);
            holder.list_text_desc.setText(item.mUserName);
            holder.list_icon.setBackgroundDrawable(item.drawer_icon);
        }

        row.setId(pos);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerItemClick.ItemClick(v.getId());
            }
        });

//        holder.rel_two.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("DNA","YES");
//            }
//        });

        return row;
    }
}