package com.sxu.smartpicture.choosePicture;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Freeman
 * @date 17/11/8
 */

public class ChoosePhotoManager {

	private Uri iconUri;
	private Uri cropImageUri;
	private boolean autoCrop = false;
	private OnChoosePhotoListener listener;

	private static final int REQUEST_CODE_TAKE_PHOTO = 1001;
	private static final int REQUEST_CODE_CHOOSE_IMAGE = 1002;
	private static final int REQUEST_CODE_CROP_IMAGE = 1003;

	private static ChoosePhotoManager instance;

	private ChoosePhotoManager() {

	}

	public static ChoosePhotoManager getInstance() {
		if (instance == null) {
			instance = new ChoosePhotoManager();
		}

		return instance;
	}

	public void choosePhotoFromAlbum(Activity activity) {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		activity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
	}

	public void choosePhotoFromCamera(Activity activity) {
		Date date = new Date();
		String fileName = "IMG_" + new SimpleDateFormat("yyyyMMddHHmmss").format(date);
		File imageFile = new File(activity.getExternalCacheDir() + "" + fileName);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			try {
				ContentValues values = new ContentValues(1);
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
				values.put(MediaStore.Images.Media.DATA, imageFile.getPath());
				iconUri = activity.getContentResolver()
						.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			iconUri = Uri.fromFile(imageFile);
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, iconUri);
		activity.startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
	}

	public void cropPhoto(Activity activity, Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("scale", true);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		// 以Uri的方式传递照片
		File cropFile = new File(activity.getExternalCacheDir().getAbsolutePath() + "crop_image.jpg");
		cropImageUri = Uri.fromFile(cropFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		// return-data=true传递的为缩略图，小米手机默认传递大图，所以会导致onActivityResult调用失败
		intent.putExtra("return-data", false);
		intent.putExtra("noFaceDetection", false);
		activity.startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	}

	public void onActivityResult(Activity activity, int requestCode, Intent intent) {
		if(intent != null) {
			switch (requestCode) {
				case REQUEST_CODE_CHOOSE_IMAGE:
					iconUri = intent.getData();
					if (autoCrop && iconUri != null) {
						cropPhoto(activity, iconUri);
					}
					if (listener != null) {
						listener.choosePhotoFromAlbum(iconUri, iconUri != null ? "Succeed!" : "Failed!");
					}
					break;
				case REQUEST_CODE_CROP_IMAGE:
					if (listener != null) {
						listener.cropPhoto(cropImageUri, cropImageUri != null ? "Succeed!" : "Failed!");
					}
					break;
				default:
					break;
			}
		} else {
			// 关闭拍照界面或拍照完成后都会调用
			if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
				if (autoCrop && iconUri != null) {
					cropPhoto(activity, iconUri);
				}
				if (listener != null) {
					listener.choosePhotoFromCamera(iconUri, iconUri != null ? "Succeed!" : "Failed!");
				}
			}
		}
	}

	public void setAutoCrop(boolean autoCrop) {
		this.autoCrop = autoCrop;
	}

	public void setChoosePhotoListener(OnChoosePhotoListener listener) {
		this.listener = listener;
	}
}
