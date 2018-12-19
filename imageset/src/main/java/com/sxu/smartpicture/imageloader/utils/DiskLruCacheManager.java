package com.sxu.smartpicture.imageloader.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/*******************************************************************************
 * Description: 用于缓存经过高斯模糊的图片
 *
 * Author: Freeman
 *
 * Date: 2018/9/4
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class DiskLruCacheManager {

	private DiskLruCache diskLruCache;
	private static DiskLruCacheManager instance;

	private final int MAX_CACHE_SIZE = 64 * 1024 * 1024;

	private DiskLruCacheManager(Context context) {
		try {
			diskLruCache = DiskLruCache.open(context.getCacheDir(), 1, 1,
					MAX_CACHE_SIZE, Integer.MAX_VALUE);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public static DiskLruCacheManager getInstance(Context context) {
		if (instance == null) {
			synchronized (DiskLruCacheManager.class) {
				if (instance == null) {
					instance = new DiskLruCacheManager(context.getApplicationContext());
				}
			}
		}

		return instance;
	}

	public void put(String url, Bitmap bitmap) {
		if (TextUtils.isEmpty(url) || bitmap == null || bitmap.isRecycled()) {
			return;
		}

		try {
			DiskLruCache.Editor editor = diskLruCache.edit(getKey(url));
			OutputStream outputStream = editor.newOutputStream(0);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
				editor.commit();
			}
			diskLruCache.flush();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public Bitmap get(String url) {
		try {
			DiskLruCache.Snapshot snapshot = diskLruCache.get(getKey(url));
			if (snapshot != null) {
				InputStream inputStream = snapshot.getInputStream(0);
				return BitmapFactory.decodeStream(inputStream);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return null;
	}

	public static String getKey(String url) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] md5 = digest.digest(url.getBytes());
			BigInteger bigInteger = new BigInteger(1, md5);
			return bigInteger.toString(16);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return null;
	}

	public void close() {
		try {
			diskLruCache.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
