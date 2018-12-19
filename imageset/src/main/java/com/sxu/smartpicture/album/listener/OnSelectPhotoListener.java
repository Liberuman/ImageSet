package com.sxu.smartpicture.album.listener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*******************************************************************************
 * Description: 选择图片监听器
 *
 * Author: Freeman
 *
 * Date: 2018/12/17
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public interface OnSelectPhotoListener {

	void onSelected(ArrayList<String> selectedPhotoList);
}
