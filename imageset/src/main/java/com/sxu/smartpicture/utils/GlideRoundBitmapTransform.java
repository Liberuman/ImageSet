package com.sxu.smartpicture.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * @author Freeman
 * @date 2017/12/19
 */


public class GlideRoundBitmapTransform extends BitmapTransformation {

	private int mRadius;
	private int mBorderWidth;
	private int mBorderColor;

	public GlideRoundBitmapTransform() {

	}

	public GlideRoundBitmapTransform(int radius, int borderWidth, int borderColor) {
		this.mRadius = radius;
		this.mBorderWidth = borderWidth;
		this.mBorderColor = borderColor;
	}

	@Override
	protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
		if (mRadius == 0 && mBorderWidth == 0) {
			return toTransform;
		}
		int width = toTransform.getWidth();
		int height = toTransform.getHeight();
		RectF rectF = new RectF(mBorderWidth, mBorderWidth, width - mBorderWidth, height - mBorderWidth);
		Bitmap result = pool.get(width, height, Bitmap.Config.ARGB_8888);
		if (result == null) {
			result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		}
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setShader(new BitmapShader(toTransform, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP ));
		paint.setAntiAlias(true);
		canvas.drawRoundRect(rectF, mRadius, mRadius, paint);
		if (mBorderWidth > 0) {
			Paint borderPaint = new Paint();
			borderPaint.setStyle(Paint.Style.STROKE);
			borderPaint.setStrokeWidth(mBorderWidth);
			borderPaint.setColor(mBorderColor);
			borderPaint.setAntiAlias(true);
			canvas.drawRoundRect(rectF, mRadius, mRadius, borderPaint);
		}

		return result;
	}

	@Override
	public void updateDiskCacheKey(MessageDigest messageDigest) {

	}
}
