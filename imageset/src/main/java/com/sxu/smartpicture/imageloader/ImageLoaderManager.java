package com.sxu.smartpicture.imageloader;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * @author Freeman
 * @date 2017/12/5
 */


public class ImageLoaderManager {

	private final static ImageLoaderManager instance = new ImageLoaderManager();
	private ImageLoaderInstance mLoaderInstance;

	private ImageLoaderManager() {

	}

	public static ImageLoaderManager getInstance() {
		return instance;
	}

	public void init(Context context, ImageLoaderInstance loaderInstance) {
		if (loaderInstance != null) {
			if (mLoaderInstance == null) {
				mLoaderInstance = loaderInstance;
				mLoaderInstance.init(context);
			}
		} else {
			throw new IllegalArgumentException("loaderInstance can't be null");
		}
	}

	public void displayImage(String url, WrapImageView imageView) {
		mLoaderInstance.displayImage(url, imageView);
	}

	public void displayImage(String url, WrapImageView imageView, ImageLoaderListener listener) {
		mLoaderInstance.displayImage(url, imageView, listener);
	}

	public void downloadImage(Context context, String url, ImageLoaderListener listener) {
		mLoaderInstance.downloadImage(context, url, listener);
	}

	public void onDestroy() {
		mLoaderInstance.destroy();
		mLoaderInstance = null;
	}
}
