package com.sxu.smartpicture.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * @author Freeman
 * @date 2017/12/19
 */


public class GlideCircleBitmapTransform extends BitmapTransformation {

	private int mBorderWidth;
	private int mBorderColor;

	public GlideCircleBitmapTransform() {

	}

	public GlideCircleBitmapTransform(int borderWidth, int borderColor) {
		this.mBorderWidth = borderWidth;
		this.mBorderColor = borderColor;
	}

	@Override
	protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
		int size = Math.min(toTransform.getWidth(), toTransform.getHeight());
		int x = (toTransform.getWidth() - size) / 2 + mBorderWidth;
		int y = (toTransform.getHeight() - size) / 2 + mBorderWidth;
		int newSize = size - mBorderWidth * 2;
		int radius = newSize / 2;
		Bitmap bitmap = Bitmap.createBitmap(toTransform, x, y, newSize, newSize);
		Bitmap result = pool.get(newSize, newSize, Bitmap.Config.ARGB_8888);
		if (result == null) {
			result = Bitmap.createBitmap(newSize, newSize, Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(result);
		if (mBorderWidth > 0) {
			Paint borderPaint = new Paint();
			borderPaint.setStyle(Paint.Style.STROKE);
			borderPaint.setStrokeWidth(mBorderWidth);
			borderPaint.setColor(mBorderColor);
			borderPaint.setAntiAlias(true);
			canvas.drawCircle(radius, radius, radius - mBorderWidth/2, borderPaint);
		}
		Paint paint = new Paint();
		paint.setShader(new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP ));
		paint.setAntiAlias(true);
		canvas.drawCircle(radius, radius, radius - mBorderWidth, paint);

		return result;
	}

	@Override
	public void updateDiskCacheKey(MessageDigest messageDigest) {

	}
}
