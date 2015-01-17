package com.oxilo.listyou.register;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.oxilo.applistyou.R;

public class TermServices extends Activity{
	
	ImageView back_buttton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_term_and_serveices);
		setActonBAr();
		init();
	}
	

	private void setActonBAr() {
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.header);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		// getActionBar().setIcon(R.drawable.side_drawer);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));
	}
	
	
	private void init(){
		back_buttton = (ImageView) findViewById(R.id.back_arrow);
	    back_buttton.setOnClickListener(bl);
	}
	
	View.OnClickListener bl = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	};
}
