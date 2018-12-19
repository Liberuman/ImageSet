package com.sxu.smartpicture.album.listener;

import android.widget.ImageView;

/*******************************************************************************
 * Description: 图片被选择的监听
 *
 * Author: Freeman
 *
 * Date: 2018/12/19
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public interface OnItemPhotoCheckedListener {

	void onItemChecked(ImageView checkIcon, boolean isSelected, String photoPath);
}
