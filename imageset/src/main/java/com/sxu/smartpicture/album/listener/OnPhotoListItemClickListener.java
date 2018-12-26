package com.sxu.smartpicture.album.listener;

import com.sxu.smartpicture.album.PhotoDirectoryBean;

/*******************************************************************************
 * Description: 监听相册目录列表点击
 *
 * Author: Freeman
 *
 * Date: 2018/12/24
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public interface OnPhotoListItemClickListener {

	void onPhotoListItemClick(int index, PhotoDirectoryBean directoryInfo);
}
