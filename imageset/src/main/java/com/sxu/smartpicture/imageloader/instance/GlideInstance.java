package com.sxu.smartpicture.imageloader.instance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sxu.smartpicture.imageloader.WrapImageView;
import com.sxu.smartpicture.imageloader.listener.ImageLoaderListener;
import com.sxu.smartpicture.imageloader.transform.GlideBlurTransform;
import com.sxu.smartpicture.imageloader.transform.GlideCircleBitmapTransform;
import com.sxu.smartpicture.imageloader.transform.GlideRoundBitmapTransform;
import com.sxu.smartpicture.imageloader.utils.DiskLruCacheManager;


/*******************************************************************************
 * Description: Glide的封装类 【注意：Glide在加载图片时会自动将Image缩放到View的大小】
 *
 * Author: Freeman
 *
 * Date: 2018/12/17
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/

public class GlideInstance implements ImageLoaderInstance {

	private RequestManager requestManager;

	@Override
	public void init(Context context) {
		requestManager = Glide.with(context);
	}

	@Override
	public void displayImage(String url, WrapImageView imageView) {
		displayImage(url, imageView, null);
	}

	@Override
	public void displayImage(String url, WrapImageView imageView, int width, int height) {
		displayImage(url, imageView, width, height, null);
	}

	@Override
	public void displayImage(String url, WrapImageView imageView, ImageLoaderListener listener) {
		displayImage(url, imageView, 0, 0, listener);
	}

	@Override
	public void displayImage(String url, WrapImageView imageView, int width, int height, final ImageLoaderListener listener) {
		// 缓存的key
		Context context = imageView.getContext().getApplicationContext();
		String key = url + imageView.getParams();
		Bitmap bitmap = DiskLruCacheManager.getInstance(context).get(key);
		if (bitmap != null && !bitmap.isRecycled()) {
			imageView.setImageBitmap(bitmap);
			return;
		}

		RequestOptions options = new RequestOptions()
				.placeholder(imageView.getPlaceHolder())
				.error(imageView.getFailureHolder())
				.dontAnimate();
		// 根据imageView的形状，设置相应的transform
		boolean isBlur = imageView.isBlur();
		int shape = imageView.getShape();
		if (isBlur) {
			if (shape == WrapImageView.SHAPE_CIRCLE) {
				options.transforms(new GlideBlurTransform(context, key, imageView.getBlurRadius()),
						new GlideCircleBitmapTransform(context, key,imageView.getBorderWidth(),
								imageView.getBorderColor()));
			} else if (shape == WrapImageView.SHAPE_ROUND) {
				options.transforms(new GlideBlurTransform(context, key, imageView.getBlurRadius()),
						new GlideRoundBitmapTransform(context, key, imageView.getRadius(), imageView.getBorderWidth(),
								imageView.getBorderColor()));
			} else {
				options.transforms(new GlideBlurTransform(context, key, imageView.getBlurRadius()));
			}
		} else {
			if (shape == WrapImageView.SHAPE_CIRCLE) {
				options.transforms(new GlideCircleBitmapTransform(context, key,imageView.getBorderWidth(), imageView.getBorderColor()));
			} else if (shape == WrapImageView.SHAPE_ROUND) {
				options.transforms(new GlideRoundBitmapTransform(context, key,
						imageView.getRadius(), imageView.getBorderWidth(), imageView.getBorderColor()));
			}
		}

		RequestBuilder builder = requestManager.load(url).apply(options);
		if (listener != null) {
			builder.listener(new RequestListener() {
				@Override
				public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
					listener.onFailure(e);
					return false;
				}

				@Override
				public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
					if (resource instanceof BitmapDrawable) {
						listener.onCompleted(((BitmapDrawable) resource).getBitmap());
					}
					return false;
				}
			});
		}
		builder.into(imageView);
	}

	@Override
	public void downloadImage(Context context, String url, final ImageLoaderListener listener) {
		Glide.with(context.getApplicationContext())
				.load(url)
				.listener(new RequestListener<Drawable>() {
			@Override
			public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
				if (listener != null) {
					listener.onFailure(e);
				}
				return false;
			}

			@Override
			public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
				if (listener != null) {
					if (resource instanceof BitmapDrawable) {
						listener.onCompleted(((BitmapDrawable) resource).getBitmap());
					}
				}
				return false;
			}
		});
	}

	@Override
	public void destroy() {
		requestManager.onDestroy();
		requestManager = null;
		Glide.tearDown();
	}
}
