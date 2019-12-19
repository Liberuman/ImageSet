package com.sxu.smartpicture.imageloader.listener;

import android.graphics.Bitmap;

/**

 * 图片加载监听
 *
 * @author Freeman
 * @date 2017/12/5
 */


public abstract class SimpleImageLoaderListener implements ImageLoaderListener {

	@Override
	public void onStart() {

	}

	@Override
	public void onProcess(int completedSize, int totalSize) {

	}

	@Override
	public void onCompleted(Bitmap bitmap) {

	}

	@Override
	public void onFailure(Exception e) {

	}
}
