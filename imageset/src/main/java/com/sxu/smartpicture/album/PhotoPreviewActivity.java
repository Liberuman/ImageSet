package com.sxu.smartpicture.album;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.sxu.imageloader.ImageLoaderManager;
import com.sxu.imageloader.WrapImageView;
import com.sxu.smartpicture.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Freeman
 * @date 2017/12/18
 */


public class PhotoPreviewActivity extends AppCompatActivity {

	private static final String EXTRA_PARAM_CURRENT_INDEX = "currentIndex";
	private static final String EXTRA_PARAM_IMAGE_LIST = "imageList";

	public static final String TRANSITION_NAME_PREFIX = "Preview00";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
		getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
		//getWindow().setEnterTransition(new Slide());
		getWindow().setExitTransition(new ChangeBounds());
		setContentView(R.layout.activity_photo_preview_layout);

		final List<String> photoList = getIntent().getStringArrayListExtra(EXTRA_PARAM_IMAGE_LIST);
		if (photoList == null || photoList.size() == 0) {
			return;
		}

		View returnIcon = findViewById(R.id.return_icon);
		final TextView indexText = findViewById(R.id.index_text);
		ViewPager photoPager = findViewById(R.id.photo_pager);

		int currentIndex = getIntent().getIntExtra(EXTRA_PARAM_CURRENT_INDEX, 0);
		final int photoSize = photoList.size();
		final StringBuilder builder = new StringBuilder().append(currentIndex).append(" / ").append(photoSize);
		indexText.setText(builder);
		photoPager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return photoSize;
			}

			@Override
			public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
				return view.equals(object);
			}

			@NonNull
			@Override
			public Object instantiateItem(@NonNull ViewGroup container, int position) {
				WrapImageView imageView = new WrapImageView(container.getContext());
				ViewCompat.setTransitionName(imageView, TRANSITION_NAME_PREFIX + position);
				ImageLoaderManager.getInstance().displayImage(photoList.get(position), imageView);
				container.addView(imageView);

				return imageView;
			}
		});
		photoPager.setCurrentItem(currentIndex);

		photoPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				builder.setLength(0);
				builder.append(position).append(" / ").append(photoSize);
				indexText.setText(builder);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		returnIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityCompat.finishAfterTransition(PhotoPreviewActivity.this);
			}
		});
	}

	public static void enter(Activity context, View sharedView, int currentIndex, List<String> imageList) {
		Intent intent = new Intent(context, PhotoPreviewActivity.class);
		intent.putExtra(EXTRA_PARAM_CURRENT_INDEX, currentIndex);
		intent.putExtra(EXTRA_PARAM_IMAGE_LIST, new ArrayList<>(imageList));
		String transitionName = ViewCompat.getTransitionName(sharedView);
		if (TextUtils.isEmpty(transitionName)) {
			transitionName = TRANSITION_NAME_PREFIX + currentIndex;
		}
		ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context,
				sharedView, transitionName);
		ActivityCompat.startActivity(context, intent, options.toBundle());
	}
}
