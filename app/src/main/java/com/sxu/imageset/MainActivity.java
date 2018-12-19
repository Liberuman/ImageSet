package com.sxu.imageset;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private int imageLoaderType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View imageLoaderButton = findViewById(R.id.image_loader_button);
		View albumButton = findViewById(R.id.album_button);
		View chooseImageButton = findViewById(R.id.choose_image_button);
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

		imageLoaderButton.setOnClickListener(this);
		albumButton.setOnClickListener(this);
		chooseImageButton.setOnClickListener(this);

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (group.getCheckedRadioButtonId()) {
					case R.id.fresco_radio:
						imageLoaderType = 0;
						break;
					case R.id.url_radio:
						imageLoaderType = 1;
						break;
					case R.id.glide_radio:
						imageLoaderType = 2;
						break;
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.image_loader_button:
				Intent intent = new Intent(this, ImageLoaderActivity.class);
				intent.putExtra("type", imageLoaderType);
				startActivity(intent);
				break;
			case R.id.album_button:
				AlbumActivity.enter(this, imageLoaderType);
				break;
			case R.id.choose_image_button:
				//test();
				startActivity(new Intent(this, ChooseAndCropImageActivity.class));
				break;
			default:
				break;
		}
	}
}
