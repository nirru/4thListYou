package com.customclasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oxilo.applistyou.R;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

    List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
    private Context mContext;
    public AnimateFirstDisplayListener(Context mContext) {
		// TODO Auto-generated constructor stub
    	this.mContext = mContext;
	}
    @Override
    public void onLoadingStarted(String imageUri, View view) {
        // TODO Auto-generated method stub
        super.onLoadingStarted(imageUri, view);
        ImageView imageView = (ImageView) view;
       

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
       
//        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        // TODO Auto-generated method stub
        super.onLoadingCancelled(imageUri, view);
        ImageView imageView = (ImageView) view;
       
    }
}

