package com.oxilo.applistyou;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public final class TestFragment extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";

	public static TestFragment newInstance(String content) {
		TestFragment fragment = new TestFragment();

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 1; i++) {
			builder.append(content).append(" ");
		}
		builder.deleteCharAt(builder.length() - 1);
		fragment.mContent = builder.toString();

		return fragment;
	}

	private String mContent = "???";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView text = new TextView(getActivity());
		text.setGravity(Gravity.CENTER);
		text.setTypeface(SplashActivity.mpRegular);
		text.setText(mContent);

		text.setTextSize(20 * getResources().getDisplayMetrics().density);
		text.setPadding(20, 20, 20, 20);

		View view = inflater
				.inflate(R.layout.paged_view_item, container, false);
		// ImageView imageView = new ImageView(getActivity());
		((ImageView) view).setScaleType(ScaleType.CENTER_CROP);
		Log.e("MCONTENT", "" + mContent);
		if (mContent.equals("A")) {
			((ImageView) view).setImageDrawable(getResources().getDrawable(
					R.drawable._2));
		} else if (mContent.equals("B")) {
			((ImageView) view).setImageDrawable(getResources().getDrawable(
					R.drawable._3));
		} else if (mContent.equals("C")) {
			((ImageView) view).setImageDrawable(getResources().getDrawable(
					R.drawable._4));
		}

		else if (mContent.equals("D")) {
			((ImageView) view).setImageDrawable(getResources().getDrawable(
					R.drawable._5));
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}
}
