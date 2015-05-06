package com.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asyn.RequestHandler;
import com.chat.XMPPClient;
import com.customclasses.AnimateFirstDisplayListener;
import com.custominterface.FooterAndActionbar;
import com.holder.ChildHolder;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.response.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecentUserChatListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private AnimateFirstDisplayListener animateFirstListener;
    private DisplayImageOptions options;
    public ImageLoader imageLoader;
    public User user;
    RequestHandler requestHandler;
    private Context context;
    public ArrayList<String> selected = new ArrayList<String>();
    private GroupItem groupitem;
    private List<ChildItem> originalData;
    private FooterAndActionbar footerAndActionbar;
    public RecentUserChatListAdapter(Context context, User user, GroupItem groupitem,FooterAndActionbar footerAndActionbar) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        animateFirstListener = new AnimateFirstDisplayListener(context);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(0)).build();
        this.user = user;
        this.groupitem = groupitem;
        this.originalData = new ArrayList<ChildItem>();
        this.originalData.addAll(groupitem.items);
        requestHandler = RequestHandler.getInstance();
        this.footerAndActionbar = footerAndActionbar;
    }

    @Override
    public int getCount() {
        if (groupitem != null)
            return groupitem.items.size();
        else
            return 0;
    }

    @Override
    public ChildItem getItem(int arg0) {
        return groupitem.items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        ChildHolder holder = null;
        ChildItem item = getItem(pos);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chat_list_row, parent, false);
            holder = new ChildHolder();
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        holder.mUserName = (TextView) convertView
                .findViewById(R.id.user_profile_name);
        holder.mCompanyName = (TextView) convertView
                .findViewById(R.id.company);
        holder.mUserProfilePic = (ImageView) convertView
                .findViewById(R.id.imageView6);

        holder.mSenderChatTextTime = (TextView) convertView.findViewById(R.id.time_status);
        holder.mCompanyName.setTypeface(SplashActivity.mpRegular);
        holder.mUserName.setTypeface(SplashActivity.mpRegular);
        holder.mSenderChatTextTime.setTypeface(SplashActivity.mpRegular);

        holder.mUserName.setText(item.mChatReceiverName);
        holder.mCompanyName.setText(item.mChatText);
        holder.mSenderChatTextTime.setText(item.mChatTextTime);

        imageLoader.displayImage(item.mReceiverPicUrl,
                holder.mUserProfilePic, options, animateFirstListener);
        convertView.setId(pos);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int pos = v.getId();
                ChildItem childItem = groupitem.items.get(pos);
                startchat(childItem);
            }
        });
        return convertView;



    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        groupitem.items.clear();
        if (charText.length() == 0) {
            groupitem.items.addAll(originalData);
        } else {
            for (ChildItem cI : originalData) {
                if (cI.mUserName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    groupitem.items.add(cI);
                }
            }
        }

        notifyDataSetChanged();
    }

    private void startchat(ChildItem childItem){
        Fragment fragment = new XMPPClient(context, user, childItem.remoteUserId,childItem.mReceiverPicUrl,childItem.mChatReceiverName, footerAndActionbar);
        android.app.FragmentManager fragmentManager = ((Activity)context).getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.content_frame, fragment).addToBackStack(null);
        transaction.commit();
    }

}