package com.sxu.imageset;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sxu.smartpicture.imageloader.FrescoInstance;
import com.sxu.smartpicture.imageloader.GlideInstance;
import com.sxu.smartpicture.imageloader.ImageLoaderListener;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.UILInstance;
import com.sxu.smartpicture.imageloader.WrapImageView;

/**
 * @author Freeman
 * @date 2017/12/19
 */


public class ImageLoaderActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_loader_layout);

		WrapImageView image = (WrapImageView) findViewById(R.id.image);
		WrapImageView blurImage = (WrapImageView) findViewById(R.id.blur_image);
		WrapImageView rectangleImage = (WrapImageView) findViewById(R.id.rectangle_image);
		WrapImageView circleImage = (WrapImageView) findViewById(R.id.circle_image);

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

		ImageLoaderManager.getInstance().displayImage("http://t.cn/RTRKzUt", image);
		ImageLoaderManager.getInstance().displayImage("http://dwz.cn/77b1Xa", blurImage);
		ImageLoaderManager.getInstance().displayImage("http://t.cn/RTRKzUt", rectangleImage);
		ImageLoaderManager.getInstance().displayImage("http://t.cn/RTRKJvS", circleImage);

		findViewById(R.id.download_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageLoaderManager.getInstance().downloadImage(ImageLoaderActivity.this,
						"http://c.hiphotos.baidu.com/image/pic/item/2fdda3cc7cd98d10dbceaca82a3fb80e7bec9015.jpg", new ImageLoaderListener() {
							@Override
							public void onStart() {

							}

							@Override
							public void onProcess(int completedSize, int totalSize) {

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
}
