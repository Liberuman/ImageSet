package com.sxu.smartpicture.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.sxu.smartpicture.utils.FastBlurUtil;

import java.io.File;

/**
 * @author Freeman
 * @date 2017/12/5
 */


public class UILInstance implements ImageLoaderInstance {

	private DisplayImageOptions options;
	private WrapImageView mImageView;

	private void initOptions(final WrapImageView imageView) {
		if (options == null) {
			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(imageView.getPlaceHolder())
					.showImageForEmptyUri(imageView.getPlaceHolder())
					.showImageOnFail(imageView.getFailureHolder())
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.considerExifParams(true)
					.preProcessor(null)
					.preProcessor(!imageView.isBlur() ? null : new BitmapProcessor() {
						@Override
						public Bitmap process(Bitmap bitmap) {
							return FastBlurUtil.doBlur(bitmap, 2, imageView.getBlurRadius() * 4);
							//return FastBlurUtil.setBlur(imageView.getContext(), bitmap, 25);
						}
					})
					.displayer(imageView.getShape() == WrapImageView.SHAPE_CIRCLE
							? new CircleBitmapDisplayer(imageView.getBorderColor(), imageView.getBorderWidth())
							: new RoundedBitmapDisplayer(imageView.getRadius())
					)
					.build();
		} else {
			if (!mImageView.equals(imageView)) {
				options = new DisplayImageOptions.Builder()
						.showImageOnLoading(imageView.getPlaceHolder())
						.showImageForEmptyUri(imageView.getPlaceHolder())
						.showImageOnFail(imageView.getFailureHolder())
						.cacheInMemory(true)
						.cacheOnDisk(true)
						.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
						.bitmapConfig(Bitmap.Config.RGB_565)
						.considerExifParams(true)
						.preProcessor(!imageView.isBlur() ? null : new BitmapProcessor() {
							@Override
							public Bitmap process(Bitmap bitmap) {
								return FastBlurUtil.doBlur(bitmap, 2, imageView.getBlurRadius() * 4);
								//return FastBlurUtil.setBlur(imageView.getContext(), bitmap, 25);
							}
						})
						.displayer(imageView.getShape() == WrapImageView.SHAPE_CIRCLE
								? new CircleBitmapDisplayer(imageView.getBorderColor(), imageView.getBorderWidth())
								: new RoundedBitmapDisplayer(imageView.getRadius())
						)
						.build();
			}
		}
		mImageView = imageView;
	}

	Context context;
	@Override
	public void init(Context context) {
		this.context = context;
		int memCacheSize = (int) Math.min(Runtime.getRuntime().maxMemory() / 8, 64 * 1024 * 1024);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCacheSize(memCacheSize)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.threadPriority(10)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCacheFileCount(Integer.MAX_VALUE)
				.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 5 * 1000))
				.writeDebugLogs()
				.build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void displayImage(String url, WrapImageView imageView) {
		initOptions(imageView);
		ImageLoader.getInstance().displayImage(url, imageView, options);
	}

	@Override
	public void displayImage(String url, WrapImageView imageView, final ImageLoaderListener listener) {
		initOptions(imageView);
		ImageLoader.getInstance().displayImage(url, imageView, options, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				if (listener != null) {
					listener.onStart();
				}
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				if (listener != null) {
					listener.onFailure(new Exception(failReason.getCause()));
				}
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (listener != null) {
					listener.onCompleted(loadedImage);
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				if (listener != null) {
					listener.onFailure(new Exception("task is cancelled"));
				}
			}
		});
	}

	@Override
	public void downloadImage(Context context, String url, final ImageLoaderListener listener) {
		ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				if (listener != null) {
					listener.onStart();
				}
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				if (listener != null) {
					listener.onFailure(new Exception(failReason.getCause()));
				}
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (listener != null) {
					listener.onCompleted(loadedImage);
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				if (listener != null) {
					listener.onFailure(new Exception("task is cancelled"));
				}
			}
		});
	}

	@Override
	public void destroy() {
		if (ImageLoader.getInstance().isInited()) {
			ImageLoader.getInstance().destroy();
		}
	}
}
