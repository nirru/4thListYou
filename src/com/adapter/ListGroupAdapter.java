package com.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.asyn.RequestHandler;
import com.customclasses.AnimateFirstDisplayListener;
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
import com.response.User;

import java.util.ArrayList;
import java.util.List;

public class ListGroupAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    int layoutResourceId;
    private List<GroupItem> items;
    private AnimateFirstDisplayListener animateFirstListener;
    private DisplayImageOptions options;
    public ImageLoader imageLoader;
    private User user;
    RequestHandler requestHandler;
    private Context context;
    private ProgressDialog progressDialog;
    int memberCount = 0;
    public ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
    public ArrayList<String> selected = new ArrayList<String>();
    private GroupHolder groupHolder;
    private GroupItem groupitem;
    private TextView left, right;
    private TextSwitcher mTextSwitcher;
    private int k;
    public ListGroupAdapter(Context context, User user, GroupItem groupitem, TextView left,TextView right, TextSwitcher mTextSwitcher) {
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
        this.k = k;
        this.user = user;
        this.groupitem = groupitem;
        this.left = left;
        this.right = right;
        this.mTextSwitcher = mTextSwitcher;
        requestHandler = RequestHandler.getInstance();
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        createAnim();
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

}