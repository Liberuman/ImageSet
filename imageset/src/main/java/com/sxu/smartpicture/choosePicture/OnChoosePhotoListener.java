package com.sxu.smartpicture.choosePicture;

import android.net.Uri;

/**
 * @author Freeman
 * @date 17/11/8
 */


public interface OnChoosePhotoListener {

	/**
	 * 从相册选择图片
	 * @param uri   uri != null表示成功，否则表示失败
	 */
	void choosePhotoFromAlbum(Uri uri);

	/**
	 * 拍照
	 * @param uri
	 */
	void choosePhotoFromCamera(Uri uri);

	/**
	 * 裁剪图片
	 * @param uri
	 */
	void cropPhoto(Uri uri);
}
