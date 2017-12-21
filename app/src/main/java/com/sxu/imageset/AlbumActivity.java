package com.sxu.imageset;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sxu.smartpicture.album.PhotoPicker;
import com.sxu.smartpicture.utils.DisplayUtil;

import java.util.ArrayList;

/**
 * @author Freeman
 * @date 2017/12/21
 */


public class AlbumActivity extends AppCompatActivity {

	private LinearLayout photoLayout;
	private ArrayList<String> selectedPhotos;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_layout);
		Button chooseButton = (Button)findViewById(R.id.choose_button);
		photoLayout = (LinearLayout) findViewById(R.id.photo_layout);

		chooseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new PhotoPicker.Builder()
						.setIsDialog(false)
						.setIsShowCamera(false)
						.setMaxPhotoCount(3)
						.setSelectedPhotos(selectedPhotos)
						.builder().chooseImage(AlbumActivity.this);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PhotoPicker.REQUEST_CODE_CHOOSE_PHOTO && data != null) {
			selectedPhotos = data.getStringArrayListExtra(PhotoPicker.SELECTED_PHOTOS);
			if (selectedPhotos != null && selectedPhotos.size() > 0) {
				photoLayout.removeAllViews();
				int size =(DisplayUtil.getScreenWidth() - DisplayUtil.dpToPx(48)) / 3;
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
				params.leftMargin = DisplayUtil.dpToPx(8);
				for (String photo : selectedPhotos) {
					ImageView imageView = new ImageView(AlbumActivity.this);
					imageView.setImageURI(Uri.parse("file://" + photo));
					imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
					photoLayout.addView(imageView, params);
				}
			}
		}
	}
}
