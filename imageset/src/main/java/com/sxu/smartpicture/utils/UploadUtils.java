package com.sxu.smartpicture.utils;

import android.app.Activity;
import android.content.Context;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.sxu.smartpicture.manager.ThreadPoolManager;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/*******************************************************************************
 * Description: 上传图片的工具类
 *
 * Author: Freeman
 *
 * Date: 2017/11/14
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class UploadUtils {

	public static void uploadToInnerServer(Activity context, String serverAddress, String filePath,
	                                       final OnUploadListener listener) {
		uploadToInnerServer(context, serverAddress, filePath, true, listener);
	}

	public static void uploadToInnerServer(final Activity context, final String serverAddress, final String filePath,
	                                       final boolean needCompress, final OnUploadListener listener) {
		if (!needCompress) {
			realUploadToInnerServer(context, serverAddress, filePath, listener);
			return;
		}

		compressImage(context, filePath, new OnCompressListener() {
			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(final File file) {
				realUploadToInnerServer(context, serverAddress, filePath, listener);
			}

			@Override
			public void onError(Throwable e) {
				realUploadToInnerServer(context, serverAddress, filePath, listener);
			}
		});
	}

	private static void realUploadToInnerServer(final Activity context, final String serverAddress,
	                                            final String filePath, final OnUploadListener listener) {
		ThreadPoolManager.executeTask(new Runnable() {
			@Override
			public void run() {
				uploadImage(context, serverAddress, filePath, listener);
			}
		});
	}

	private static void uploadImage(final Activity context, final String serverAddress, final String filePath,
	                                final OnUploadListener listener) {
		try {
			String boundary = "**";
			String end = "\r\n";
			URL url = new URL(serverAddress);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn != null) {
				conn.setConnectTimeout(5000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Charset", "UTF-8");
				conn.setRequestProperty("Accept-Charset", "UTF-8");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				dos.writeBytes("--" + boundary + end);
				dos.writeBytes("Content-Disposition: form-data; name=\"file\"; fileName=\""+filePath+"\""+end);
				dos.writeBytes("Content-Type: image/jpeg;text/html;charset=utf-8"+end);
				dos.writeBytes(end);
				// 读取图片数据，并写入到请求体中
				InputStream inputStream = new FileInputStream(filePath);
				final byte[] data = new byte[8192];
				while (inputStream.read(data) != -1) {
					dos.write(data, 0, data.length);
				}
				inputStream.close();
				dos.writeBytes(end);
				dos.writeBytes("--" + boundary + "--" + end);
				// 刷新内容缓冲区，生成http正文
				dos.flush();
				// 发送http请求
				InputStream resultStream = conn.getInputStream();
				if (resultStream != null) {
					Arrays.fill(data, 0, data.length, (byte) ' ');
					if (listener != null) {
						if (resultStream.read(data) != -1) {
							context.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									listener.onSuccess(data.toString());
								}
							});
						} else {
							context.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									listener.onError(new Exception("Server returns empty data"));
								}
							});
						}
					}
					resultStream.close();
				} else {
					context.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							listener.onError(new Exception("Server returns empty data"));
						}
					});
				}
				dos.close();
			} else {
				context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onError(new Exception("Please check network or url"));
					}
				});
			}
		} catch (final Exception e) {
			e.printStackTrace(System.out);
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					listener.onError(e);						}
			});
		}
	}

	public static void uploadToQiNiu(Activity context, String token, String filePath, OnUploadToQiNiuListener listener) {
		uploadToQiNiu(context, token, filePath, true, listener);
	}

	/**
	 * token需从服务端获取 具体可参考：https://developer.qiniu.com/kodo/manual/1208/upload-token
	 * @param token
	 * @param filePath
	 * @param needCompress
	 * @param listener
	 */
	public static void uploadToQiNiu(Activity context, final String token, final String filePath, boolean needCompress,
	                                 final OnUploadToQiNiuListener listener) {
		if (!needCompress) {
			uploadImageToQiNiu(token, filePath, listener);
			return;
		}

		compressImage(context, filePath, new OnCompressListener() {
			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(File file) {
				uploadImageToQiNiu(token, filePath, listener);
			}

			@Override
			public void onError(Throwable e) {
				uploadImageToQiNiu(token, filePath, listener);
			}
		});
	}

	private static void uploadImageToQiNiu(String token, String filePath, final OnUploadToQiNiuListener listener) {
		UploadManager uploadManager = new UploadManager();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String key = "icon_" + sdf.format(new Date());
		uploadManager.put(filePath, key, token, new UpCompletionHandler() {
			@Override
			public void complete(String key, ResponseInfo info, JSONObject res) {
				//  res 包含hash、key等信息，具体字段取决于上传策略的设置。
				if (listener != null) {
					listener.onCompleted(key, info, res);
				}
			}
		}, null);
	}

	public static void compressImage(Context context, String filePath, OnCompressListener listener) {
		Luban.with(context)
				.load(filePath)
				.setCompressListener(listener)
				.launch();
	}

	public static void compressImage(Context context, List<String> pathList, OnCompressListener listener) {
		Luban.with(context)
				.load(pathList)
				.setCompressListener(listener)
				.launch();
	}

	public interface OnUploadListener {

		void onSuccess(String result);

		void onError(Exception e);
	}

	public interface OnUploadToQiNiuListener {

		void onCompleted(String key, ResponseInfo info, JSONObject res);
	}
}
