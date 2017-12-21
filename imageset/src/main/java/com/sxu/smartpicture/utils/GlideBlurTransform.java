package com.sxu.smartpicture.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * @author Freeman
 * @date 2017/12/19
 */


public class GlideBlurTransform extends BitmapTransformation {

	private Context mContext;
	private int mScaleRadius;
	private int mBlurRadius;

	public GlideBlurTransform(Context context, int scaleRadius, int blurRadius) {
		this.mContext = context;
		this.mScaleRadius = scaleRadius;
		this.mBlurRadius = blurRadius;
	}

	@Override
	protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
		//return FastBlurUtil.setBlur(mContext, toTransform, 8, mBlurRadius);
		return FastBlurUtil.doBlur(toTransform, mScaleRadius, mBlurRadius);
	}

	@Override
	public void updateDiskCacheKey(MessageDigest messageDigest) {

	}
}
