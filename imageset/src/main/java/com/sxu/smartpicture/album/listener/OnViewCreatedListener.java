package com.sxu.smartpicture.album.listener;

import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

/*******************************************************************************
 * Description: 监听View加载（用于修改View样式）
 *
 * Author: Freeman
 *
 * Date: 2018/12/24
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public interface OnViewCreatedListener {
	void onViewCreated(View containerView);
}
