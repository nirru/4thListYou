package com.chat;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.adapter.RecentUserChatListAdapter;
import com.chat.database.GetDataFromDatabase;
import com.custominterface.FooterAndActionbar;
import com.holder.GroupItem;
import com.oxilo.applistyou.R;
import com.response.User;

/**
 * Created by C-ShellWin on 2/10/2015.
 */
public class UserChatList extends Fragment {

    private Context mContext;
    private User user;
    private ListView listView;
    private GroupItem groupItem;
    private GetDataFromDatabase getDataFromDatabase;
    private RecentUserChatListAdapter adapter;
   private FooterAndActionbar footerAndActionbar;
    public UserChatList(Context mContext, User user,FooterAndActionbar footerAndActionbar) {
        this.mContext = mContext;
        this.user = user;
        this.footerAndActionbar = footerAndActionbar;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_chat_user_listing, container,
                false);
        init(rootView);
        return rootView;
    }

    private void init(View root) {
        listView = (ListView) root.findViewById(R.id.chat_list_view);
        getDataFromDatabase = new GetDataFromDatabase();
        if (getDataFromDatabase.checkForChatTables()) {
            groupItem = getDataFromDatabase.getRecentchatUser();
            Log.e("SIZE OF", "" + groupItem.items.size());
            setListviewToAdapter();
        }
        else{
            Toast.makeText(mContext, "No Recent Chat", Toast.LENGTH_LONG).show();
        }
    }

    private void setListviewToAdapter() {
        if (groupItem != null) {
            adapter = new RecentUserChatListAdapter(mContext, user, groupItem, footerAndActionbar);
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(mContext, "No Recent Chat", Toast.LENGTH_LONG).show();
        }
    }


}
