package com.sxu.smartpicture.imageloader;

import android.content.Context;
import android.util.Log;

import com.sxu.smartpicture.imageloader.instance.ImageLoaderInstance;
import com.sxu.smartpicture.imageloader.listener.ImageLoaderListener;

/**

 * 类或接口的描述信息
 *
 * @author Freeman
 * @date 2017/12/5
 */


public class ImageLoaderManager {

	private ImageLoaderInstance mLoaderInstance;

	protected ImageLoaderManager() {

	}

	public static ImageLoaderManager getInstance() {
		return Singleton.instance;
	}

	public void init(Context context, ImageLoaderInstance loaderInstance) {
		if (loaderInstance == null) {
			throw new IllegalArgumentException("loaderInstance can't be null");
		} else if (mLoaderInstance != null) {
			Log.w("out", "ImageLoaderManager has initialized!!!");
		} else {
			synchronized (this) {
				mLoaderInstance = loaderInstance;
				mLoaderInstance.init(context);
			}
		}
	}

	public boolean isInit() {
		return mLoaderInstance != null;
	}

	public ImageLoaderInstance getImageLoaderInstance() {
		return mLoaderInstance;
	}

	public void displayImage(String url, WrapImageView imageView) {
		mLoaderInstance.displayImage(url, imageView);
	}

	public void displayImage(String url, WrapImageView imageView, int width, int height) {
		Log.i("out", "url====" + url);
		mLoaderInstance.displayImage(url, imageView, width, height);
	}

	public void displayImage(String url, WrapImageView imageView, ImageLoaderListener listener) {
		mLoaderInstance.displayImage(url, imageView, listener);
	}

	public void displayImage(String url, WrapImageView imageView, int width, int height, ImageLoaderListener listener) {
		mLoaderInstance.displayImage(url, imageView, width, height, listener);
	}

	public void downloadImage(Context context, String url, ImageLoaderListener listener) {
		mLoaderInstance.downloadImage(context, url, listener);
	}

	public void onDestroy() {
		mLoaderInstance.destroy();
		mLoaderInstance = null;
	}

	public static class Singleton {
		final static ImageLoaderManager instance = new ImageLoaderManager();
	}
}
