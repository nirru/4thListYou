package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.customclasses.AnimateFirstDisplayListener;
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

/**
 * Created by C-ShellWin on 2/12/2015.
 */
public class ChatMessageAdapter extends BaseAdapter{

    private Context mContext;
    private GroupItem groupitem;
    private LayoutInflater inflater;
    private User user;
    private AnimateFirstDisplayListener animateFirstListener;
    private DisplayImageOptions options;
    public ImageLoader imageLoader;
    public ChatMessageAdapter(Context mContext,GroupItem groupitem,User user) {
        this.mContext = mContext;
        this.groupitem = groupitem;
        this.user = user;
        inflater = LayoutInflater.from(mContext);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        animateFirstListener = new AnimateFirstDisplayListener(mContext);
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
        ChildHolder holder = null;
        ChildItem item = getItem(pos);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chat_custom_row, parent, false);
            holder = new ChildHolder();
            convertView.setTag(holder);
        }
        else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.rel_one = (RelativeLayout)convertView.findViewById(R.id.parent_sender_layout);
        holder.rel_two = (RelativeLayout)convertView.findViewById(R.id.receiver_layout);
        holder.mUserProfilePic = (ImageView)convertView.findViewById(R.id.sender_image);
        holder.mSenderText = (TextView)convertView.findViewById(R.id.sender_chat_text);
        holder.mSenderChatTextTime = (TextView)convertView.findViewById(R.id.sender_chat_text_time);
        holder.mReceiverText = (TextView)convertView.findViewById(R.id.receiver_chat_text);
        holder.mReceiverChatTextTime = (TextView)convertView.findViewById(R.id.receiver_chat_text_time);

        holder.mSenderText.setTypeface(SplashActivity.mpRegular);
        holder.mSenderChatTextTime.setTypeface(SplashActivity.mpRegular);
        holder.mReceiverText.setTypeface(SplashActivity.mpRegular);
        holder.mReceiverChatTextTime.setTypeface(SplashActivity.mpRegular);

        if (item.mChatSenderName.trim().equals(user.firstName)){
            holder.rel_one.setVisibility(View.GONE);
            holder.rel_two.setVisibility(View.VISIBLE);
        }
        else{
            holder.rel_one.setVisibility(View.VISIBLE);
            holder.rel_two.setVisibility(View.GONE);
            imageLoader.displayImage(item.mUserPicUrl,
                    holder.mUserProfilePic, options, animateFirstListener);
        }

        holder.mSenderText.setText(item.mChatText);
        holder.mReceiverText.setText(item.mChatText);
        holder.mSenderChatTextTime.setText(item.mChatTextTime);
        holder.mReceiverChatTextTime.setText(item.mChatTextTime);

        return convertView;
    }
}
