package com.sxu.smartpicture.album;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Freeman
 * @date 2017/11/17
 */


public class PhotoManager {

	private final static PhotoManager instance = new PhotoManager();

	private List<String> allPhotoPath;
	private Map<String, PhotoDirectoryBean> directoryMap;

	private final int REQUEST_CODE_CHOOSE_PHOTO = 1000;

	private PhotoManager() {

	}

	public static PhotoManager getInstance() {
		return instance;
	}

	/**
	 * 获取所有的相册信息
	 * @param context
	 * @param listener
	 */
	public void getAllDirectory(final FragmentActivity context, final OnPhotoDirectoryLoadListener listener ) {
		context.getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int id, Bundle args) {
				CursorLoader loader = new CursorLoader(context);
				loader.setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				loader.setSelection(" 1=1) group by (" + MediaStore.Images.Media.BUCKET_ID);
				loader.setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");
				loader.setProjection(new String[] {
						MediaStore.Images.Media.BUCKET_ID,
						MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
						MediaStore.Images.Media.DATA,
						MediaStore.Images.Media.DATE_TAKEN
				});

				return loader;
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
				List<PhotoDirectoryBean> allPhotoDirectory = null;
				if (data != null && data.getCount() > 0) {
					if (data.getPosition() == data.getCount()) {
						data.moveToPosition(-1);
					}
					allPhotoDirectory = new ArrayList<>();
					while (data.moveToNext()) {
						final PhotoDirectoryBean itemDirection = new PhotoDirectoryBean();
						itemDirection.id = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
						itemDirection.name = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
						itemDirection.thumbPath = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
						itemDirection.createTime = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
						allPhotoDirectory.add(itemDirection);
					}
				}

				if (listener != null) {
					listener.onCompleted(allPhotoDirectory);
				}
			}

			@Override
			public void onLoaderReset(Loader<Cursor> loader) {

			}
		});
	}

	/**
	 * 根据相册ID获取指定相册的照片
	 * @param context
	 * @param bucketId
	 * @param listener
	 */
	public static void getPhotos(final FragmentActivity context, final String bucketId, final OnPhotoLoadListener listener ) {
		context.getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int id, Bundle args) {
				CursorLoader loader = new CursorLoader(context);
				loader.setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				if (!TextUtils.isEmpty(bucketId)) {
					loader.setSelection(" " + MediaStore.Images.Media.BUCKET_ID + "=");
					loader.setSelectionArgs(new String[]{bucketId + " "});
				}
				loader.setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");
				loader.setProjection(new String[] {
						MediaStore.Images.Thumbnails.DATA
				});

				return loader;
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
				List<String> allPhotos = null;
				if (data != null && data.getCount() > 0) {
					if (data.getPosition() == data.getCount()) {
						data.moveToPosition(-1);
					}
					allPhotos = new ArrayList<>();
					while (data.moveToNext()) {
						allPhotos.add(data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA)));
					}
				}

				if (listener != null) {
					listener.onCompleted(allPhotos);
				}
			}

			@Override
			public void onLoaderReset(Loader<Cursor> loader) {

			}
		});
	}

	/**
	 * 获取所有的照片
	 * @param context
	 * @param listener
	 */
	public void getAllPhotos(final FragmentActivity context, final OnPhotoDirectoryLoadListener listener) {
		if (directoryMap != null) {
			if (listener != null) {
				listener.onCompleted(new ArrayList<>(directoryMap.values()));
			}
		} else {
			directoryMap = new LinkedHashMap<>();
			context.getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
				@Override
				public Loader<Cursor> onCreateLoader(int id, Bundle args) {
					CursorLoader loader = new CursorLoader(context);
					loader.setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					loader.setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");
					loader.setProjection(new String[]{
							MediaStore.Images.Media.BUCKET_ID,
							MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
							MediaStore.Images.Media.DATA,
							MediaStore.Images.Media.DATE_TAKEN
					});

					return loader;
				}

				@Override
				public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
					if (data != null) {
						if (data.getPosition() == data.getCount()) {
							data.moveToPosition(-1);
						}
						PhotoDirectoryBean allPhotoDirectory = new PhotoDirectoryBean();
						allPhotoDirectory.name = "所有照片";
						directoryMap.put("All", allPhotoDirectory);
						while (data.moveToNext()) {
							PhotoDirectoryBean directory;
							String bucketId = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
							String photoPath = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
							if (directoryMap.containsKey(bucketId)) {
								directory = directoryMap.get(bucketId);
								directory.putPhoto(photoPath);
							} else {
								directory = new PhotoDirectoryBean(bucketId,
										data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
										photoPath,
										data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)));
								directoryMap.put(bucketId, directory);
							}
							if (data.isFirst()) {
								allPhotoDirectory.thumbPath = photoPath;
							}
							allPhotoDirectory.putPhoto(photoPath);
						}
					}

					if (listener != null) {
						listener.onCompleted(new ArrayList<>(directoryMap.values()));
					}
				}

				@Override
				public void onLoaderReset(Loader<Cursor> loader) {

				}
			});
		}
	}

	public List<String> getAllPhotosPath() {
		if (allPhotoPath == null) {
			allPhotoPath = new ArrayList<>();
			List<PhotoDirectoryBean> allDirectory = (List<PhotoDirectoryBean>) directoryMap.values();
			if (allDirectory != null && allDirectory.size() > 0) {
				for (PhotoDirectoryBean directoryInfo : allDirectory) {
					allPhotoPath.addAll(directoryInfo.photoList);
				}
			}
		}

		return allPhotoPath;
	}

	public void choosePhoto(Activity context, int maxCount, ArrayList<String> selectedPhotos) {
		Intent intent = new Intent(context, ChoosePhotoActivity.class);
		intent.putExtra("maxCount", maxCount);
		intent.putExtra("selectedPhotos", selectedPhotos);
		context.startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO);
	}

	public interface OnPhotoLoadListener {
		void onCompleted(List<String> photoList);
	}

	public interface OnPhotoDirectoryLoadListener {
		void onCompleted(List<PhotoDirectoryBean> directoryList);
	}
}
