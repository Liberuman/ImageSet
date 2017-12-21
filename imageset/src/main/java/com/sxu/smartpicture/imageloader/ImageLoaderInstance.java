package com.sxu.smartpicture.imageloader;

import android.content.Context;
import android.widget.ImageView;

/**
 * @author Freeman
 * @date 2017/12/5
 */

public interface ImageLoaderInstance {

	void init(Context context);

	void displayImage(String url, WrapImageView imageView);

	void displayImage(String url, WrapImageView imageView, final ImageLoaderListener listener);

	void downloadImage(Context context, String url, ImageLoaderListener listener);

	void destroy();
}
