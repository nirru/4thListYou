package com.landing.screen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxilo.applistyou.R;
import com.oxilo.listyou.constant.AppConstant;
import com.util.Util;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unchecked")
public class NewAdapter extends BaseExpandableListAdapter {

    public ArrayList<String> groupItem;
    public ArrayList<ArrayList<String>> tempChild;
    public ArrayList<ArrayList<String>> childItem = new ArrayList<ArrayList<String>>();
    public LayoutInflater minflater;
    public Context activity;
    private Context _context;
    DisplayImageOptions options;
    AnimateFirstDisplayListener animateFirstListener;
    String login_user_id, receiver_ID;
    ProgressDialog progressDialog;
    int removePos;
    private TextView mAddListCount;
    private Dialog customDialog;

    private Button retreive_butoon;
    private ImageView dilog_cross_button;
    private ImageView mUserImageView;
    private TextView mUserName, mUserDesignation, mUserCompanyName;
    private ProgressBar spinner1;
    private RelativeLayout mChatToUser, mDeleteUser;

    public NewAdapter(Context context, String login_user_id, ArrayList<String> grList, ArrayList<ArrayList<String>> childItem, TextView mAddListCount) {
        groupItem = grList;
        this.childItem = childItem;
        this._context = context;
        this.login_user_id = login_user_id;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(100))
                .build();
        this.mAddListCount = mAddListCount;
        Log.e("CHILD ITEM", "" + childItem);
        Log.e("GROUP LIST", "" + grList);
    }

    public void setInflater(LayoutInflater mInflater, Context act) {
        this.minflater = mInflater;
        activity = act;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
//        text.setText(Childtem.get(groupPosition).get(childPosition).toString());
        View row = convertView;
        ItemHolder holder = null;
        final int position = childPosition;
        if (row == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = infalInflater.inflate(R.layout.add_friend_list_item, null);
            holder = new ItemHolder();
            row.setTag(holder);

        } else {
            holder = (ItemHolder) row.getTag();
        }

        row.setId(position);
        animateFirstListener = new AnimateFirstDisplayListener();
        holder.mUserName = (TextView) row.findViewById(R.id.user_profile_name);
        holder.mCompanyName = (TextView) row.findViewById(R.id.company);
        holder.mProfilePic = (ImageView) row.findViewById(R.id.imageView6);
        holder.mImageViewAdd = (ImageView) row.findViewById(R.id.add_friend_btn);
        holder.mAdd_friend_text = (TextView) row.findViewById(R.id.add_friend_text);

        holder.mAdd_friend_text.setVisibility(View.VISIBLE);
        holder.mUserName.setText(childItem.get(position).get(2).toString());
        holder.mCompanyName.setText(childItem.get(position).get(3).toString());
        ImageLoader.getInstance().displayImage(childItem.get(position).get(1).toString(), holder.mProfilePic, options, animateFirstListener);
        holder.mImageViewAdd.setTag(position);
        holder.mImageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (Integer) v.getTag();
                Log.e("SENDER UID", "" + id);
                acceptInvitation(childItem.get(position).get(0).toString(), id);
            }
        });

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = v.getId();
                addFriendsPopUp(childItem.get(pos).get(1).toString(), childItem.get(pos).get(2).toString(), childItem.get(pos).get(3).toString(), childItem.get(pos).get(0).toString(), pos);

            }
        });

        return row;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childItem.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return groupItem.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = minflater.inflate(R.layout.list_group, null);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText("Received Request" + "(" + childItem.size() + ")");
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void acceptInvitation(String toUID, int pos) {
        removePos = pos;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, login_user_id);
        params.put(AppConstant.RECEIVER_UID, toUID);
        client.post(AppConstant.ACCEPT_INVITATION_FRIENDS_API, params, mResponseHandler);
    }

    AsyncHttpResponseHandler mResponseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
//            Log.e("<<<RECEIVED RESPONSE>>>  ", "" + response);
            try {
                String response = String.valueOf(new String(bytes, "UTF-8"));
                removeAddedFriedFromList(removePos);
                progressDialog.dismiss();
            } catch (UnsupportedEncodingException e1) {

            }

        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(_context, _context.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(_context, _context.getString(R.string.error_title), _context.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }

        @Override
        public void onProgress(int bytesWritten, int totalSize) {
            progressDialog.setProgress((int) bytesWritten);
        }

        @Override
        public void onStart() {
            progressDialog = new ProgressDialog(_context);
            progressDialog.setMessage("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    };

    private void addFriendsPopUp(String user_pic_url, String user_name, String company_name, String uid_of_receiver, int pos) {
        customDialog = new Dialog(_context);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setContentView(R.layout.block_friend_popup);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();

        initDialogElement(user_pic_url, user_name, company_name, uid_of_receiver, pos);
    }

    private void initDialogElement(String user_pic_url, String user_name, String company_name, String uid_of_receiver, int pos) {
        removePos = pos;
        receiver_ID = uid_of_receiver;
        spinner1 = (ProgressBar) customDialog.findViewById(R.id.progress_bar_one);
        mChatToUser = (RelativeLayout) customDialog.findViewById(R.id.rel_layout_left);
        mDeleteUser = (RelativeLayout) customDialog.findViewById(R.id.rel_layout_right);
        mUserImageView = (ImageView) customDialog.findViewById(R.id.user_profile_pic);
        mUserName = (TextView) customDialog.findViewById(R.id.profile_name);
        mUserDesignation = (TextView) customDialog.findViewById(R.id.designation);
        mUserCompanyName = (TextView) customDialog.findViewById(R.id.company);
        retreive_butoon = (Button) customDialog.findViewById(R.id.retreive_btn);
        mUserImageView.setVisibility(View.INVISIBLE);

        ImageLoader.getInstance().displayImage(user_pic_url, mUserImageView,
                options, animateFirstListener, progressListener);

        mUserName.setText(user_name);
        mUserCompanyName.setText(company_name);
        mUserDesignation.setText("");

        dilog_cross_button = (ImageView) customDialog
                .findViewById(R.id.cross_button);
        retreive_butoon.setOnClickListener(retreiveListener);
        dilog_cross_button.setOnClickListener(crossListener);
        mChatToUser.setOnClickListener(chatClickListener);
        mDeleteUser.setOnClickListener(deleteClickListener);

    }

    View.OnClickListener crossListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.dismiss();
        }
    };

    View.OnClickListener retreiveListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            acceptInvitation(login_user_id, removePos);
        }
    };

    View.OnClickListener chatClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialog.dismiss();
        }
    };
    View.OnClickListener deleteClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            denyInvitation(receiver_ID, removePos);

        }
    };

    private void denyInvitation(String toUID, int pos) {
        removePos = pos;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(AppConstant.SENDER_UID, login_user_id);
        params.put(AppConstant.RECEIVER_UID, toUID);
        client.post(AppConstant.DENY_REQUEST, params, mResponseHandler7);
    }

    AsyncHttpResponseHandler mResponseHandler7 = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
//            Log.e("<<<RECEIVED RESPONSE>>>  ", "" + response);
            try {
                String response = String.valueOf(new String(bytes, "UTF-8"));
                removeAddedFriedFromList(removePos);
                progressDialog.dismiss();
                customDialog.dismiss();
            } catch (UnsupportedEncodingException e1) {

            }

        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable error) {
            if (error.getMessage() != null) {
                Util.showOKAleart(_context, _context.getString(R.string.error_title),
                        error.getMessage());
            } else {
                Util.showOKAleart(_context, _context.getString(R.string.error_title), _context.getString(R.string.registration_response_error));
            }
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
        }

        @Override
        public void onProgress(int bytesWritten, int totalSize) {
            progressDialog.setProgress((int) bytesWritten);
        }

        @Override
        public void onStart() {
            progressDialog = new ProgressDialog(_context);
            progressDialog.setMessage("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    };


    private void removeAddedFriedFromList(int pos) {
        childItem.remove(pos);
        setFooterCount(childItem.size());
        notifyDataSetChanged();
    }

    private void setFooterCount(int chatCount) {
        if (chatCount == 0)
            mAddListCount.setVisibility(View.INVISIBLE);
        else {
            mAddListCount.setVisibility(View.VISIBLE);
            mAddListCount.setText("" + chatCount);
        }
    }

    public void showOKAleart(String title, String message) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(_context).create();
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

    static class ItemHolder {
        TextView mUserName;
        TextView mCompanyName;
        ImageView mImageViewAdd;
        ImageView mProfilePic;
        TextView mAdd_friend_text;
    }

    private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            // TODO Auto-generated method stub
            super.onLoadingStarted(imageUri, view);
            ImageView imageView = (ImageView) view;
            if (imageView.getId() == R.id.user_profile_pic) {
                spinner1.setProgress(0);
            }

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                if (imageView.getId() == R.id.imageView6) {
                    boolean firstDisplay = !displayedImages.contains(imageUri);
                    if (firstDisplay) {
                        FadeInBitmapDisplayer.animate(imageView, 500);
                        displayedImages.add(imageUri);
                    }
                } else {
                    imageView.setVisibility(View.VISIBLE);
                    spinner1.setVisibility(View.GONE);

                }

            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
            // TODO Auto-generated method stub
            super.onLoadingFailed(imageUri, view, failReason);
            String message = null;
            switch (failReason.getType()) {
                case IO_ERROR:
                    message = "Input/Output error";
                    break;
                case DECODING_ERROR:
                    message = "Image can't be decoded";
                    break;
                case NETWORK_DENIED:
                    message = "Downloads are denied";
                    break;
                case OUT_OF_MEMORY:
                    message = "Out Of Memory error";
                    break;
                case UNKNOWN:
                    message = "Unknown error";
                    break;
            }
            ImageView imageView = (ImageView) view;
            if (imageView.getId() == R.id.user_profile_pic) {
                spinner1.setVisibility(View.GONE);
            }

            Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            // TODO Auto-generated method stub
            super.onLoadingCancelled(imageUri, view);
            ImageView imageView = (ImageView) view;
            if (imageView.getId() == R.id.user_profile_pic) {
                spinner1.setVisibility(View.GONE);
            }
        }
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
