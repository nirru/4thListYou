package com.util;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.adapter.ListGroupAdapter;

/**
 * Created by C-ShellWin on 12/18/2014.
 */
public class AllContact {
    private static AllContact instance;
    private ListGroupAdapter adapter;

    public static AllContact getInstance() {
        if (instance == null) {
            instance = new AllContact();
        }
        return instance;
    }

    public void showview(Context mContext, RelativeLayout layout) {
        layout.setVisibility(View.VISIBLE);
    }

    public void hideview(Context mContext, RelativeLayout layout) {
        layout.setVisibility(View.GONE);
    }
}
