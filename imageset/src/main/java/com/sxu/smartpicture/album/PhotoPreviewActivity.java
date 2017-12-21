package com.sxu.smartpicture.album;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sxu.smartpicture.R;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.WrapImageView;

/**
 * @author Freeman
 * @date 2017/12/18
 */


public class PhotoPreviewActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_photo_preview_layout);
		WrapImageView imageView = findViewById(R.id.image);
		if (getIntent() != null) {
			ImageLoaderManager.getInstance().displayImage(getIntent().getStringExtra("photoPath"), imageView);
		}
	}
}
