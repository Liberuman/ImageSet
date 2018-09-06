package com.sxu.smartpicture.album;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxu.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.R;
import com.sxu.smartpicture.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Freeman
 * @date 2017/12/18
 */


public class PhotoPreviewActivity extends AppCompatActivity {

	private static final String EXTRA_PARAM_CURRENT_INDEX = "currentIndex";
	private static final String EXTRA_PARAM_IMAGE_LIST = "imageList";

	public static final String TRANSITION_NAME_PREFIX = "Preview_";

	private View childView;
	private ViewPager photoPager;
	private static String transitionName;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_photo_preview_layout);

		final List<String> photoList = getIntent().getStringArrayListExtra(EXTRA_PARAM_IMAGE_LIST);
		if (photoList == null || photoList.size() == 0) {
			return;
		}

		View returnIcon = findViewById(R.id.return_icon);
		final TextView indexText = findViewById(R.id.index_text);
		photoPager = findViewById(R.id.photo_pager);
		ViewCompat.setTransitionName(photoPager, transitionName);
		int currentIndex = getIntent().getIntExtra(EXTRA_PARAM_CURRENT_INDEX, 0);
		final int photoSize = photoList.size();
		final StringBuilder builder = new StringBuilder().append(currentIndex + 1).append(" / ").append(photoSize);
		indexText.setText(builder);
		photoPager.setAdapter(new PhotoAdapter(photoList));
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
				childView = photoPager.getChildAt(position);
				transitionName = ViewCompat.getTransitionName(childView);
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
		transitionName = ViewCompat.getTransitionName(sharedView);
		if (TextUtils.isEmpty(transitionName)) {
			transitionName = TRANSITION_NAME_PREFIX + currentIndex;
		}
		ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context,
				sharedView, transitionName);
		ActivityCompat.startActivityForResult(context, intent, 0, options.toBundle());
	}

	public static class PhotoAdapter extends PagerAdapter {

		private List<String> photoList;

		public PhotoAdapter(List<String> photoList) {
			this.photoList = photoList;
		}

		@Override
		public int getCount() {
			return photoList.size();
		}

		@Override
		public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
			return view.equals(object);
		}

		@NonNull
		@Override
		public Object instantiateItem(@NonNull ViewGroup container, int position) {
			PhotoView imageView = new PhotoView(container.getContext());
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			ViewCompat.setTransitionName(imageView, TRANSITION_NAME_PREFIX + position);
			ImageLoaderManager.getInstance().displayImage(photoList.get(position), imageView);
			container.addView(imageView);

			return imageView;
		}

		@Override
		public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
			container.removeView((View) object);
		}
	}

//	@Override
//	public void onBackPressed() {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			setEnterSharedElementCallback(new SharedElementCallback() {
//				@Override
//				public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
//					super.onMapSharedElements(names, sharedElements);
//					names.clear();
//					sharedElements.clear();
//					names.add(transitionName);
//					sharedElements.put(transitionName, childView);
//				}
//			});
//		}
//
//		Intent intent = new Intent();
//		intent.putExtra(EXTRA_PARAM_CURRENT_INDEX, photoPager.getCurrentItem());
//		setResult(RESULT_OK, intent);
//		super.onBackPressed();
//	}
}
