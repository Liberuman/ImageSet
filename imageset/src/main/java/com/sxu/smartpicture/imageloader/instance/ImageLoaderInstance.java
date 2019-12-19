package com.sxu.smartpicture.imageloader.instance;

import android.content.Context;

import com.sxu.smartpicture.imageloader.WrapImageView;
import com.sxu.smartpicture.imageloader.listener.ImageLoaderListener;


/*******************************************************************************
 * Description: ImageLoader的接口
 *
 * Author: Freeman
 *
 * Date: 2018/12/17
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/

public interface ImageLoaderInstance {

	void init(Context context);

	void displayImage(String url, WrapImageView imageView);

	void displayImage(String url, WrapImageView imageView, int width, int height);

	void displayImage(String url, WrapImageView imageView, int width, int height, final ImageLoaderListener listener);

	void displayImage(String url, WrapImageView imageView, final ImageLoaderListener listener);

	void downloadImage(Context context, String url, ImageLoaderListener listener);

	void destroy();
}
