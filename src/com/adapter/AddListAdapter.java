package com.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asyn.RequestHandler;
import com.asyn.RequestListener;
import com.customclasses.AnimateFirstDisplayListener;
import com.customclasses.AnimatedExpandableListView;
import com.holder.ChildHolder;
import com.holder.ChildItem;
import com.holder.GroupHolder;
import com.holder.GroupItem;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.response.User;
import com.util.Freinds;
import com.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

public class AddListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;

    private List<GroupItem> items;
    private AnimateFirstDisplayListener animateFirstListener;
    private DisplayImageOptions options;
    public ImageLoader imageLoader;
    private User user;
    RequestHandler requestHandler;
    private Context context;
    private Dialog customDialog;
    private ProgressDialog progressDialog;

    private Button retreive_butoon;
    private ImageView dilog_cross_button;
    private ImageView mUserImageView;
    private TextView mUserName, mUserDesignation, mUserCompanyName;
    private ProgressBar spinner1;
    private RelativeLayout mChatToUser, mDeleteUser;
    private TextView mAddListCount;

    public AddListAdapter(Context context, User user, TextView mAddListCount) {
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
        this.mAddListCount = mAddListCount;
        requestHandler = RequestHandler.getInstance();
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void setData(List<GroupItem> items) {
        this.items = items;
    }

    @Override
    public ChildItem getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public ChildItem removeChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.remove(childPosition);
    }

    @Override
    public View getRealChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        ChildItem item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.add_friend_list_item, parent, false);
            holder.mUserName = (TextView) convertView
                    .findViewById(R.id.user_profile_name);
            holder.mCompanyName = (TextView) convertView
                    .findViewById(R.id.company);
            holder.mUserProfilePic = (ImageView) convertView
                    .findViewById(R.id.imageView6);
            holder.mAddImageButton = (ImageView) convertView.findViewById(R.id.add_friend_btn);
            convertView.setTag(holder);
            convertView.setId(childPosition);
            holder.mAddImageButton.setId(childPosition);
            Log.e("CHILD POS", "" + childPosition);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.mCompanyName.setTypeface(SplashActivity.mpRegular);
        holder.mUserName.setTypeface(SplashActivity.mpRegular);
        holder.mUserName.setText(item.mUserName);
        holder.mCompanyName.setText(item.mCompanyName);
        imageLoader.displayImage(item.mUserPicUrl,
                holder.mUserProfilePic, options, animateFirstListener);

        holder.mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int id = view.getId();
                ChildItem item = getChild(groupPosition, id);
                Log.e("SENDER UID", "" + id);
                progressDialog.show();
                Freinds freinds = Freinds.getInstance();
                freinds.acceptFriendRequest(context, user.id, item.remoteUserId, item.mUserName, new RequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        progressDialog.dismiss();
                        animateRowDeletion(view, groupPosition, id);
                    }
                });
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id11 = v.getId();
                Log.e("IDIDIDID", "" + id11);
                ChildItem item = getChild(groupPosition, id11);
                addFriendsPopUp(item, id11, groupPosition, v);
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

    private void acceptInvitation(String toUID, final int pos, final String remoteUser, final int groupPos) {
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, user.id);
        params.put(AppConstant.RECEIVER_UID, toUID);
        requestHandler.makePostRequest(context, params, AppConstant.ACCEPT_INVITATION_FRIENDS_API, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                GroupItem item = new GroupItem();
                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) json;
                        if (jsonObject.getString("message").equals("Request_Accepted")) {
                            removeChild(groupPos, pos);
                            Toast.makeText(context, remoteUser + " " + "is friend now", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        } else {
                            showOKAleart("Message", "error occured please try again");
                        }
                    } else if (json instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) json;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void addFriendsPopUp(ChildItem mUser, int pos, int groupPos, View v) {
        customDialog = new Dialog(context);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.block_friend_popup);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();

        initDialogElement(mUser, pos, groupPos, v);
    }

    private void initDialogElement(final ChildItem item, final int pos, final int groupPos, final View v) {

        spinner1 = (ProgressBar) customDialog.findViewById(R.id.progress_bar_one);
        mChatToUser = (RelativeLayout) customDialog.findViewById(R.id.rel_layout_left);
        mDeleteUser = (RelativeLayout) customDialog.findViewById(R.id.rel_layout_right);
        mUserImageView = (ImageView) customDialog.findViewById(R.id.user_profile_pic);
        mUserName = (TextView) customDialog.findViewById(R.id.profile_name);
        mUserDesignation = (TextView) customDialog.findViewById(R.id.designation);
        mUserCompanyName = (TextView) customDialog.findViewById(R.id.company);
        retreive_butoon = (Button) customDialog.findViewById(R.id.retreive_btn);
        mUserImageView.setVisibility(View.INVISIBLE);

        ImageLoader.getInstance().displayImage(item.mUserPicUrl, mUserImageView,
                options, animateFirstListener, progressListener);

        mUserName.setText(item.mUserName);
        mUserCompanyName.setText(item.mCompanyName);
        mUserDesignation.setText("");

        dilog_cross_button = (ImageView) customDialog
                .findViewById(R.id.cross_button);
        retreive_butoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customDialog.cancel();
                Freinds freinds = Freinds.getInstance();
                freinds.acceptFriendRequest(context, user.id, item.remoteUserId, item.mUserName, new RequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        animateRowDeletion(v, groupPos, pos);
                    }
                });
            }
        });
        dilog_cross_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.cancel();
            }
        });
        mChatToUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.cancel();
            }
        });
        mDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.cancel();
                Freinds freinds = Freinds.getInstance();
                freinds.denyFriendRequest(context, user.id, item.remoteUserId, item.mUserName, new RequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        animateRowDeletion(v, groupPos, pos);
                    }
                });
            }
        });

    }

    public void showOKAleart(String title, String message) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle("Message");
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void animateRowDeletion(View v, final int groupPos, final int pos) {

        showList(v,groupPos,pos);
//        Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
//        anim.setDuration(500);
//        v.startAnimation(anim);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                removeChild(groupPos, pos);
//                notifyDataSetChanged();
//                Freinds.countRequest(items.size(),mAddListCount);
//            }
//        }, anim.getDuration());
    }

    private void showList(final View v, final int groupPos, final int pos) {
        RequestHandler requestHandler = RequestHandler.getInstance();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, user.id);
        requestHandler.makePostRequest(context, params, AppConstant.GET_FRIEND_LIST, new RequestListener() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                Log.e("WRITE  SUCCESS", "YES");
                Util.writeFreindsToPrefrefs(context, result);
                Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
                anim.setDuration(500);
                v.startAnimation(anim);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeChild(groupPos, pos);
                        notifyDataSetChanged();
                        Freinds.countRequest(items.size(), mAddListCount);
                    }
                }, anim.getDuration());

            }
        });

    }

    ImageLoadingProgressListener progressListener = new ImageLoadingProgressListener() {

        @Override
        public void onProgressUpdate(String arg0, View view, int current, int total) {
            // TODO Auto-generated method stub

            ImageView imageView = (ImageView) view;
            if (imageView.getId() == R.id.user_profile_pic) {
                spinner1.setProgress(Math.round(100.0f * current / total));
            }

        }
    };
}