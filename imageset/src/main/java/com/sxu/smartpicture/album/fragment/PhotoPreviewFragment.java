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
import com.sxu.smartpicture.album.listener.OnViewCreatedListener;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.zoomimage.ZoomImageView;

import java.util.ArrayList;
import java.util.List;

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

	private ViewPager viewPager;

	private OnViewCreatedListener viewCreatedListener;
	private OnPagerChangedListener listener;
	private ArrayList<String> allPhotoPath = new ArrayList<>();

	private final static String KEY_CURRENT_INDEX = "currentIndex";
	private final static String KEY_ALL_PHOTO = "allPhotoPath";

	public static PhotoPreviewFragment getInstance(int currentIndex, ArrayList<String> allPhotoPath) {
		PhotoPreviewFragment fragment = new PhotoPreviewFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(KEY_CURRENT_INDEX, currentIndex);
		bundle.putStringArrayList(KEY_ALL_PHOTO, allPhotoPath);
		fragment.setArguments(bundle);
		return fragment;
	}

	/**
	 * 更新要预览的图片数据
	 * @param currentIndex
	 * @param allPhotos
	 */
	public void updatePhotoList(int currentIndex, ArrayList<String> allPhotos) {
		Bundle bundle = getArguments();
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putInt(KEY_CURRENT_INDEX, currentIndex);
		bundle.putStringArrayList(KEY_ALL_PHOTO, allPhotos);
		setArguments(bundle);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewPager = (ViewPager) inflater.inflate(R.layout.fragment_photo_preview_layout, null);
		return viewPager;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (viewCreatedListener != null) {
			viewCreatedListener.onViewCreated(viewPager);
		}

		Bundle bundle = getArguments();
		List<String> allPhotos = bundle != null ? bundle.getStringArrayList(KEY_ALL_PHOTO) : null;
		if (allPhotos == null || allPhotos.size() == 0) {
			return;
		}

		allPhotoPath.clear();
		allPhotoPath.addAll(allPhotos);
		viewPager.setAdapter(new PhotoAdapter());
		viewPager.setCurrentItem(bundle.getInt(KEY_CURRENT_INDEX));
		setPagerChangeListener();
	}

	private void setPagerChangeListener() {
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
		viewPager.addOnPageChangeListener(pagerListener);
		// ViewPager设置setCurrentItem时不会调用onPageSelected，所以手动调用确保逻辑的一致性
		viewPager.post(new Runnable() {
			@Override
			public void run() {
				pagerListener.onPageSelected(viewPager.getCurrentItem());
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

	public void setOnViewCreatedListener(OnViewCreatedListener listener) {
		this.viewCreatedListener = listener;
	}

	/**
	 * 监听ViewPager的滑动切换
	 */
	public interface OnPagerChangedListener {
		void onChanged(int position, String currentPhoto);
	}
}
