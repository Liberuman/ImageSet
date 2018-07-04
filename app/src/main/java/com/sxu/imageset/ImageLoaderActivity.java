package com.sxu.imageset;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
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

		ImageLoaderManager.getInstance().displayImage("http://t.cn/RTRKzUt", image);
		ImageLoaderManager.getInstance().displayImage("http://img.tuku.cn/file_thumb/201602/m2016021513470744.jpg", blurImage);
		ImageLoaderManager.getInstance().displayImage("http://t.cn/RTRKzUt", rectangleImage);
		ImageLoaderManager.getInstance().displayImage("http://t.cn/RTRKJvS", circleImage);

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
}
