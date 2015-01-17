package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.holder.ChildHolder;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.response.User;
import com.ui.MySwitch;

/**
 * Created by C-ShellWin on 12/28/2014.
 */
public class PrivacyAdapter extends BaseAdapter{

    private Context mContext;
    private GroupItem groupitem;
    private User user;
    private LayoutInflater inflater;
    public PrivacyAdapter(Context mContext , GroupItem groupitem, User user) {
        this.mContext = mContext;
        this.groupitem = groupitem;
        this.user = user;
        inflater = LayoutInflater.from(mContext);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ChildHolder holder = null;
        ChildItem item = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.privacy_custom_row, parent, false);
            holder = new ChildHolder();
            convertView.setTag(holder);
        }
        else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.mUserName = (TextView)convertView.findViewById(R.id.pricacy_text);
        holder.switch_button = (MySwitch)convertView.findViewById(R.id.switch1);

        holder.mUserName.setTypeface(SplashActivity.mpRegular);
        holder.mUserName.setText(item.mCompanyName);

        return convertView;
    }
}
