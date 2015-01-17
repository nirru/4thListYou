package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asyn.RequestHandler;
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
import com.response.UserFriends;

public class ContactDetailAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private AnimateFirstDisplayListener animateFirstListener;
    private DisplayImageOptions options;
    public ImageLoader imageLoader;
    RequestHandler requestHandler;
    private Context context;
    private GroupItem groupitem;
    private UserFriends userFriends;
	public ContactDetailAdapter(Context context,GroupItem groupitem, UserFriends userFriends) {
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
        this.context = context;
        this.groupitem = groupitem;
        this.userFriends = userFriends;
        requestHandler = RequestHandler.getInstance();
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
	public View getView(final int pos, View convertView, ViewGroup parent) {

        ChildHolder holder;
        ChildItem item = getItem(pos);
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.contact_detail_list_row, parent, false);

            holder.mCompanyName = (TextView) convertView
                    .findViewById(R.id.company);
            holder.mUserProfilePic = (ImageView) convertView
                    .findViewById(R.id.imageView6);

            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        holder.mCompanyName.setTypeface(SplashActivity.mpRegular);
        holder.mCompanyName.setText(item.mCompanyName + "(" + item.groupMemberCount + ")");
        imageLoader.displayImage(userFriends.userPicUrl,
                holder.mUserProfilePic, options, animateFirstListener);
        return convertView;
	}
}