package com.sxu.smartpicture.album;


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
import android.widget.ImageView;

import com.sxu.imageloader.ImageLoaderManager;
import com.sxu.imageloader.WrapImageView;
import com.sxu.smartpicture.photoview.PhotoView;
import com.sxu.smartpicture.utils.DisplayUtil;
import com.sxu.smartpicture.R;

import java.util.ArrayList;

/**
 * Created by Freeman on 17/3/31.
 */

public class PhotoPreviewFragment extends Fragment {

	private ViewPager mViewPager;

	private ArrayList<String> allPhotoPath;
	private ChoosePhotoActivity.OnItemPhotoCheckedListener checkedListener;

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
		final ChoosePhotoActivity context = (ChoosePhotoActivity) getActivity();
		if (bundle != null) {
			allPhotoPath = bundle.getStringArrayList("allPhotoPath");
			if (allPhotoPath != null && allPhotoPath.size() > 0) {
				mViewPager.setAdapter(new PhotoAdapter());
				int currentIndex = bundle.getInt("currentIndex");
				mViewPager.setCurrentItem(currentIndex);
				context.setCheckIconStatus(allPhotoPath.get(currentIndex));
			}
		}

		if (getActivity() instanceof ChoosePhotoActivity) {
			((ChoosePhotoActivity) getActivity()).checkIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (checkedListener != null) {
						checkedListener.onItemChecked(context.checkIcon, !v.isSelected(),
								allPhotoPath.get(mViewPager.getCurrentItem()));
					}
				}
			});
		}

		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				context.setCheckIconStatus(allPhotoPath.get(position));
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	public void setOnItemPhotoCheckedListener(ChoosePhotoActivity.OnItemPhotoCheckedListener listener) {
		this.checkedListener = listener;
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
			PhotoView imageView = new PhotoView(getActivity());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				imageView.setTransitionName("Preview" + position);
			}
			ImageLoaderManager.getInstance().displayImage(allPhotoPath.get(position), imageView);
			container.addView(imageView);

			return imageView;
		}

		@Override
		public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
		}

	}
}
