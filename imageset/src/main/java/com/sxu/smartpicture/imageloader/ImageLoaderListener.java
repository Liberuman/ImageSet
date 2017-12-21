package com.sxu.smartpicture.imageloader;

import android.graphics.Bitmap;

/**
 * @author Freeman
 * @date 2017/12/5
 */


public interface ImageLoaderListener {

	void onStart();

	void onProcess(int completedSize, int totalSize);

	void onCompleted(Bitmap bitmap);

	void onFailure(Exception e);
}
