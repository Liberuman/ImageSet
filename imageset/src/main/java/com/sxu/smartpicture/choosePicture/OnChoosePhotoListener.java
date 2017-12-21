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
	 * @param errMsg
	 */
	void choosePhotoFromAlbum(Uri uri, String errMsg);

	/**
	 * 拍照
	 * @param uri
	 * @param errMsg
	 */
	void choosePhotoFromCamera(Uri uri, String errMsg);

	/**
	 * 裁剪图片
	 * @param uri
	 * @param errMsg
	 */
	void cropPhoto(Uri uri, String errMsg);
}
