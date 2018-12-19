package com.sxu.smartpicture.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;

/*******************************************************************************
 * Description: 屏幕相关的工具类
 *
 * Author: Freeman
 *
 * Date: 2017/06/20
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class DisplayUtil {

	public static int getScreenWidth() {
		return Resources.getSystem().getDisplayMetrics().widthPixels;
	}

	public static int getScreenHeight() {
		return Resources.getSystem().getDisplayMetrics().heightPixels;
	}

	public static float getScreenDensity() {
		return Resources.getSystem().getDisplayMetrics().density;
	}

	public static int dpToPx(int dp) {
		return Math.round(Resources.getSystem().getDisplayMetrics().density * dp + 0.5f);
	}

	public static int pxToDp(int px) {
		return Math.round(px / Resources.getSystem().getDisplayMetrics().density + 0.5f);
	}

	public static int getStatusHeight(Context context) {
		Rect rect = new Rect();
		((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		return rect.top;
	}

	public static String getScreenParams(Context context) {
		return getScreenWidth() + "*" + getScreenHeight();
	}
}
