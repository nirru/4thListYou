package com.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
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
import com.response.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BlockUserAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private AnimateFirstDisplayListener animateFirstListener;
    private DisplayImageOptions options;
    public ImageLoader imageLoader;
    public User user;
    RequestHandler requestHandler;
    private Context context;
    private ProgressDialog progressDialog;
    int memberCount = 0;
    public ArrayList<String> selected = new ArrayList<String>();
    private GroupItem groupitem;
    private List<ChildItem> originalData;
    private TextView left, right;
    private TextSwitcher mTextSwitcher;
    private int k;
    public BlockUserAdapter(Context context, User user, GroupItem groupitem, TextView left, TextView right, TextSwitcher mTextSwitcher) {
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
        this.user = user;
        this.groupitem = groupitem;
        this.originalData = new ArrayList<ChildItem>();
        this.originalData.addAll(groupitem.items);
        this.left = left;
        this.right = right;
        this.mTextSwitcher = mTextSwitcher;
        requestHandler = RequestHandler.getInstance();
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
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
            convertView = inflater.inflate(R.layout.group_list_row, parent, false);
            holder = new ChildHolder();
            convertView.setTag(holder);
        }
        else {
            holder = (ChildHolder) convertView.getTag();
        }
        holder.mUserName = (TextView) convertView
                .findViewById(R.id.user_profile_name);
        holder.mCompanyName = (TextView) convertView
                .findViewById(R.id.company);
        holder.mUserProfilePic = (ImageView) convertView
                .findViewById(R.id.imageView6);
        holder.selectionBox = (CheckBox) convertView.findViewById(R.id.selection_imageview);
        holder.selectionBox .setOnCheckedChangeListener(myCheckChangList);
        holder.selectionBox.setTag(pos);
        holder.mCompanyName.setTypeface(SplashActivity.mpRegular);
        holder.mUserName.setTypeface(SplashActivity.mpRegular);
        holder.mUserName.setText(item.mUserName);
        holder.mCompanyName.setText(item.mCompanyName);
        imageLoader.displayImage(item.mUserPicUrl,
                holder.mUserProfilePic, options, animateFirstListener);
        holder.selectionBox.setChecked(item.box);
        return convertView;
    }

    public ArrayList<ChildItem> getBox() {
        ArrayList<ChildItem> box = new ArrayList<ChildItem>();
        for (ChildItem p : groupitem.items) {
            if (p.box)
                box.add(p);
        }
        return box;
    }

    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (isChecked == true) {
                memberCount = memberCount + 1;
            } else {
                if (memberCount > 0)
                    memberCount = memberCount - 1;
                else
                    memberCount = 0;
            }
            getItem((Integer) buttonView.getTag()).box = isChecked;
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
            mTextSwitcher.setText("" + memberCount);
        }
    };

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        groupitem.items.clear();
        if (charText.length() == 0) {
            groupitem.items.addAll(originalData);
        }
        else
        {
            for (ChildItem cI : originalData)
            {
                if (cI.mUserName.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    groupitem.items.add(cI);
                }
            }
        }
        notifyDataSetChanged();
    }

}