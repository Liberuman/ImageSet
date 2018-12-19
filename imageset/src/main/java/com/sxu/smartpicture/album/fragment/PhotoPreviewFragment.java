package com.sxu.smartpicture.album.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sxu.smartpicture.R;
import com.sxu.smartpicture.album.listener.OnItemPhotoCheckedListener;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.zoomimage.ZoomImageView;

import java.util.ArrayList;

/*******************************************************************************
 * Description: 图片预览
 *
 * Author: Freeman
 *
 * Date: 2018/03/31
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class PhotoPreviewFragment extends Fragment {

	private ViewPager mViewPager;

	private OnPagerChangedListener listener;
	private ArrayList<String> allPhotoPath;

	public static PhotoPreviewFragment getInstance(int currentIndex, ArrayList<String> allPhotoPath) {
		PhotoPreviewFragment fragment = new PhotoPreviewFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("currentIndex", currentIndex);
		bundle.putStringArrayList("allPhotoPath", allPhotoPath);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mViewPager = (ViewPager) inflater.inflate(R.layout.fragment_photo_preview_layout, null);
		return mViewPager;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle == null) {
			return;
		}
		allPhotoPath = bundle.getStringArrayList("allPhotoPath");
		if (allPhotoPath != null && allPhotoPath.size() > 0) {
			mViewPager.setAdapter(new PhotoAdapter());
			int currentIndex = bundle.getInt("currentIndex");
			mViewPager.setCurrentItem(currentIndex);
		}

		final ViewPager.OnPageChangeListener pagerListener = new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if (listener != null) {
					listener.onChanged(position, allPhotoPath.get(position));
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		};
		mViewPager.addOnPageChangeListener(pagerListener);
		mViewPager.post(new Runnable() {
			@Override
			public void run() {
				pagerListener.onPageSelected(mViewPager.getCurrentItem());
			}
		});
	}

	public void setOnPagerChangeListener(OnPagerChangedListener listener) {
		this.listener = listener;
	}

	private class PhotoAdapter extends PagerAdapter {

		@Override
		public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
			return view.equals(object);
		}

		@Override
		public int getCount() {
			return allPhotoPath.size();
		}

		@NonNull
		@Override
		public Object instantiateItem(@NonNull ViewGroup container, int position) {
			ZoomImageView imageView = new ZoomImageView(getActivity());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				imageView.setTransitionName("Preview_" + position);
			}
			ImageLoaderManager.getInstance().displayImage("file://" + allPhotoPath.get(position), imageView);
			container.addView(imageView);

			return imageView;
		}

		@Override
		public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
			container.removeView((View) object);
		}
	}

	public interface OnPagerChangedListener {
		void onChanged(int position, String currentPhoto);
	}
}
