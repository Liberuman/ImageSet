package com.sxu.smartpicture.imageloader.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.sxu.smartpicture.imageloader.utils.DiskLruCacheManager;

import java.security.MessageDigest;

/**

 * 类或接口的描述信息
 *
 * @author Freeman
 * @date 2017/12/19
 */


public class GlideCircleBitmapTransform extends BitmapTransformation {

	private int mBorderWidth;
	private int mBorderColor;
	private String mKey;
	private Context mContext;

	public GlideCircleBitmapTransform(Context context, String key, int borderWidth, int borderColor) {
		this.mContext = context;
		this.mKey = key;
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
		Bitmap result = pool.get(newSize, newSize, toTransform.getConfig());
		if (result == null) {
			result = Bitmap.createBitmap(newSize, newSize, toTransform.getConfig());
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
		paint.setShader(new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
		paint.setAntiAlias(true);
		canvas.drawCircle(radius, radius, radius - mBorderWidth, paint);

		DiskLruCacheManager.getInstance(mContext).put(mKey, result);

		return result;
	}

	@Override
	public void updateDiskCacheKey(MessageDigest messageDigest) {

	}
}
