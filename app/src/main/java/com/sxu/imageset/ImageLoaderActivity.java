package com.sxu.imageset;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sxu.smartpicture.album.activity.PhotoPreviewActivity;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.WrapImageView;
import com.sxu.smartpicture.imageloader.instance.FrescoInstance;
import com.sxu.smartpicture.imageloader.instance.GlideInstance;
import com.sxu.smartpicture.imageloader.instance.UILInstance;
import com.sxu.smartpicture.imageloader.listener.ImageLoaderListener;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Freeman
 * @date 2017/12/19
 */


public class ImageLoaderActivity extends AppCompatActivity {

	private WrapImageView image;
	private WrapImageView blurImage;
	private WrapImageView rectangleImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_loader_layout);

		WrapImageView image = (WrapImageView) findViewById(R.id.image);
		WrapImageView blurImage = (WrapImageView) findViewById(R.id.blur_image);
		WrapImageView rectangleImage = (WrapImageView) findViewById(R.id.rectangle_image);
		WrapImageView circleImage = (WrapImageView) findViewById(R.id.circle_image);
		final TextView downloadText = findViewById(R.id.download_button);

		int type = getIntent().getIntExtra("type", 0);
		if (type == 0) {
			ImageLoaderManager.getInstance().init(getApplicationContext(), new FrescoInstance());
		} else if (type == 1) {
			ImageLoaderManager.getInstance().init(getApplicationContext(), new UILInstance());
		} else if (type == 2) {
			ImageLoaderManager.getInstance().init(getApplicationContext(), new GlideInstance());
		} else {
			/**
			 * Nothing
			 */
		}

		ViewCompat.setTransitionName(image, PhotoPreviewActivity.TRANSITION_NAME_PREFIX + "0");
		ViewCompat.setTransitionName(blurImage, PhotoPreviewActivity.TRANSITION_NAME_PREFIX + "1");
		ViewCompat.setTransitionName(rectangleImage, PhotoPreviewActivity.TRANSITION_NAME_PREFIX + "2");
		ImageLoaderManager.getInstance().displayImage("http://t.cn/RTRKzUt", image);
		ImageLoaderManager.getInstance().displayImage("http://t.cn/RTRKzUt", blurImage);
		ImageLoaderManager.getInstance().displayImage("http://t.cn/RTRKzUt", rectangleImage);
		ImageLoaderManager.getInstance().displayImage("http://t.cn/RTRKJvS", circleImage);


		//ViewCompat.setTransitionName(image, PhotoPreviewActivity.TRANSITION_NAME_PREFIX + "0");
		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> photoList = new ArrayList<>();
				photoList.add("http://t.cn/RTRKzUt");
				photoList.add("http://img.tuku.cn/file_thumb/201602/m2016021513470744.jpg");
				photoList.add("http://t.cn/RTRKzUt");
				PhotoPreviewActivity.enter(ImageLoaderActivity.this, v, 0, photoList);
			}
		});
		downloadText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageLoaderManager.getInstance().downloadImage(ImageLoaderActivity.this,
						"http://f8.topitme.com/8/0d/dd/1131049236b4ddd0d8o.jpg", new ImageLoaderListener() {
							@Override
							public void onStart() {

							}

							@Override
							public void onProcess(int completedSize, int totalSize) {
								float progress = completedSize * 1.0f / totalSize;
								if (progress < 1.0f) {
									downloadText.setText("已下载" + (progress * 100) + "%s");
								} else {
									downloadText.setText("下载完成");
								}
							}

							@Override
							public void onCompleted(Bitmap bitmap) {
								Toast.makeText(getBaseContext(), "下载成功", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onFailure(Exception e) {
								Toast.makeText(getBaseContext(), "下载失败", Toast.LENGTH_SHORT).show();
							}
						});
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 这里只是为了测试需要，使用时不需要调用
		ImageLoaderManager.getInstance().onDestroy();
	}

//	@Override
//	public void onActivityReenter(int resultCode, Intent data) {
//		if (resultCode == RESULT_OK && data != null) {
//			final int currentIndex = data.getIntExtra("currentIndex", 0);
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//				setExitSharedElementCallback(new SharedElementCallback() {
//					@Override
//					public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
//						super.onMapSharedElements(names, sharedElements);
//						names.clear();
//						sharedElements.clear();
//						names.add(PhotoPreviewActivity.TRANSITION_NAME_PREFIX + currentIndex);
//						sharedElements.put(PhotoPreviewActivity.TRANSITION_NAME_PREFIX + currentIndex, blurImage);
//					}
//				});
//			}
//		}
//		super.onActivityReenter(resultCode, data);
//	}
}
