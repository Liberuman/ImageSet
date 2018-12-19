package com.sxu.smartpicture.choosepicture;

import android.net.Uri;

/*******************************************************************************
 * Description: 
 *
 * Author: Freeman
 *
 * Date: 2018/12/17
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public abstract class OnSimpleChoosePhotoListener implements OnChoosePhotoListener {

	@Override
	public void choosePhotoFromAlbum(Uri uri) {

	}

	@Override
	public void choosePhotoFromCamera(Uri uri) {

	}

	@Override
	public void cropPhoto(Uri uri) {

	}
}
