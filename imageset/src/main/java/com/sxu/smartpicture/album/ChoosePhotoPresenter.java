package com.sxu.smartpicture.album;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sxu.smartpicture.R;
import com.sxu.smartpicture.album.fragment.PhotoGridFragment;
import com.sxu.smartpicture.album.fragment.PhotoListFragment;
import com.sxu.smartpicture.album.fragment.PhotoPreviewFragment;
import com.sxu.smartpicture.album.listener.OnItemPhotoCheckedListener;
import com.sxu.smartpicture.album.listener.OnPhotoListItemClickListener;
import com.sxu.smartpicture.album.listener.OnViewCreatedListener;

import java.util.ArrayList;
import java.util.List;

/*******************************************************************************
 * Description: 选择照片的逻辑
 *
 * Author: Freeman
 *
 * Date: 2018/12/21
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class ChoosePhotoPresenter {

	private int maxCount;
	private @LayoutRes int gridItemLayoutId = 0;
	private @LayoutRes int listItemLayoutId = 0;
	private String currentPhotoPath;
	private FragmentActivity context;
	private FragmentManager fm;
	private PhotoGridFragment gridFragment;
	private PhotoListFragment listFragment;
	private PhotoPreviewFragment previewFragment;
	private FragmentHandlerManager handlerManager;
	private OnPhotoListItemClickListener itemClickListener;
	private OnFragmentChangedListener fragmentChangedListener;
	private OnSelectedPhotoChangedListener selectedPhotoChangedListener;
	private OnCurrentPhotoChangedListener currentPhotoChangedListener;
	private OnGridItemClickListener onGridItemClickListener;
	private OnViewCreatedListener gridViewCreatedListener;
	private OnViewCreatedListener listViewCreatedListener;
	private OnViewCreatedListener priviewViewCreatedListener;
	private ArrayList<String> selectedPhotos = new ArrayList<>();

	public final static String FRAGMENT_TAG_GRID = "grid";
	public final static String FRAGMENT_TAG_LIST = "list";
	public final static String FRAGMENT_TAG_PREVIEW = "preview";

	public ChoosePhotoPresenter(FragmentActivity context) {
		this.context = context;
		this.maxCount = context.getIntent().getIntExtra(PhotoPicker.MAX_PHOTO_COUNT, 0);
		final List<String> photoList = context.getIntent().getStringArrayListExtra(PhotoPicker.SELECTED_PHOTOS);
		if (photoList != null && photoList.size() > 0) {
			selectedPhotos.addAll(photoList);
		}
	}

	public void initPresenter(OnSelectedPhotoChangedListener listener) {
		this.selectedPhotoChangedListener = listener;
		if (selectedPhotoChangedListener != null) {
			selectedPhotoChangedListener.onChanged(selectedPhotos, maxCount);
		}

		handlerManager = new FragmentHandlerManager(context);
		fm = handlerManager.getFragmentManager();
		updateFragment(getGridFragment(0), FRAGMENT_TAG_GRID, true, false);
		fm.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
			@Override
			public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
				super.onFragmentViewCreated(fm, f, v, savedInstanceState);
				if (fragmentChangedListener != null) {
					fragmentChangedListener.onUpdateView(f.getTag(), false);
				}
			}

			@Override
			public void onFragmentResumed(FragmentManager fm, Fragment f) {
				super.onFragmentResumed(fm, f);
				if (!(f instanceof PhotoPreviewFragment)) {
					return;
				}

				((PhotoPreviewFragment) f).setOnPagerChangeListener(new PhotoPreviewFragment.OnPagerChangedListener() {
					@Override
					public void onChanged(int position, String photoPath) {
						currentPhotoPath = photoPath;
						if (currentPhotoChangedListener != null) {
							currentPhotoChangedListener.onChanged(position, selectedPhotos.contains(photoPath));
						}
					}
				});
			}
		}, true);
	}

	public PhotoGridFragment getGridFragment(int index) {
		// 使用临时列表避免在预览页面改变图片状态后，selectedPhotos发生变化，
		// 而PagerAdapter未notifyDataChanged出现闪退.
		ArrayList<String> tempSelectedPhoto = new ArrayList<>();
		tempSelectedPhoto.addAll(selectedPhotos);
		if (gridFragment == null) 	 {
			gridFragment = PhotoGridFragment.newInstance(index, tempSelectedPhoto);
			gridFragment.setOnViewCreatedListener(gridItemLayoutId, gridViewCreatedListener);
			gridFragment.setOnItemPhotoCheckedListener(new OnItemPhotoCheckedListener() {
				@Override
				public void onItemChecked(ImageView checkIcon, String photoPath) {
					updateSelectedPhotos(checkIcon, photoPath);
				}
			});
			gridFragment.setOnItemPhotoPreviewListener(new PhotoGridFragment.OnItemPhotoPreviewListener() {
				@Override
				public void onItemPreview(int currentItem, View itemView, ArrayList<String> allPhotos) {
					if (onGridItemClickListener != null) {
						onGridItemClickListener.onItemClick(currentItem, allPhotos);
						return;
					}

					updateFragment(getPreviewFragment(currentItem, allPhotos), FRAGMENT_TAG_PREVIEW, true, true);
				}
			});
		} else {
			// 刷新PhotoGridFragment中的数据
			gridFragment.updateGridLayout(index, tempSelectedPhoto);
		}

		return gridFragment;
	}

	public Fragment getListFragment() {
		if (listFragment == null) {
			listFragment = new PhotoListFragment();
			listFragment.setOnViewCreatedListener(listItemLayoutId, listViewCreatedListener);
			listFragment.setOnPhotoListItemClickListener(new OnPhotoListItemClickListener() {
				@Override
				public void onPhotoListItemClick(int index, PhotoDirectoryBean directoryInfo) {
					getGridFragment(index);
					handlerManager.removeFragment(listFragment);
					if (itemClickListener != null) {
						itemClickListener.onPhotoListItemClick(index, directoryInfo);
					}
				}
			});
		}

		return listFragment;
	}

	public Fragment getPreviewFragment(int currentItem, ArrayList<String> allPhotos) {
		if (previewFragment == null) {
			previewFragment = PhotoPreviewFragment.getInstance(currentItem, allPhotos);
			previewFragment.setOnViewCreatedListener(priviewViewCreatedListener);
		} else {
			previewFragment.updatePhotoList(currentItem, allPhotos);
		}

		return previewFragment;
	}

	public void updateFragment(Fragment fragment, String fragmentTag, boolean isAdd, boolean addToStack) {
		updateFragment(fragment, R.id.container_layout, fragmentTag, isAdd, addToStack);
	}

	public void updateFragment(final Fragment fragment, @IdRes int containerId, String fragmentTag,
	                           boolean isAdd, final boolean addToStack) {
		if (isAdd) {
			handlerManager.addFragment(containerId, fragment, fragmentTag, addToStack);
		} else {
			handlerManager.replaceFragment(containerId, fragment, fragmentTag, addToStack);
		}

		if (fragmentChangedListener != null) {
			fragmentChangedListener.onUpdateView(fragmentTag, false);
		}
	}

	/**
	 * 更新选中的图片列表
	 * @param checkIcon
	 * @param photoPath
	 */
	public void updateSelectedPhotos(ImageView checkIcon, String photoPath) {
		boolean isSelected = !checkIcon.isSelected();
		if (isSelected && !selectedPhotos.contains(photoPath)) {
			if (selectedPhotos.size() < maxCount) {
				checkIcon.setSelected(true);
				selectedPhotos.add(photoPath);
			} else {
				Toast.makeText(context, context.getString(R.string.max_select_count, maxCount),
						Toast.LENGTH_SHORT).show();
			}
		} else if (!isSelected && selectedPhotos.contains(photoPath)) {
			checkIcon.setSelected(false);
			selectedPhotos.remove(photoPath);
		}

		if (selectedPhotoChangedListener != null) {
			selectedPhotoChangedListener.onChanged(selectedPhotos, maxCount);
		}
	}

	public int getMaxCount() {
		return maxCount;
	}

	public String getCurrentPhotoPath() {
		return currentPhotoPath;
	}

	public ArrayList<String> getSelectedPhotos() {
		return selectedPhotos;
	}

	public void onBackPressed() {
		String tag = null;
		boolean updateView = true;
		int backStackCount = fm.getBackStackEntryCount();
		Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_GRID);
		if (fragment != null && backStackCount == 0) {
			if (fm.findFragmentByTag(FRAGMENT_TAG_LIST) == null) {
				updateView = false;
				updateFragment(getListFragment(), FRAGMENT_TAG_LIST, true, false);
			}
		} else if (fragment != null && fm.findFragmentByTag(FRAGMENT_TAG_PREVIEW) != null) {
			tag = FRAGMENT_TAG_GRID;
			((PhotoGridFragment)fragment).updateGridLayout(-1, selectedPhotos);
			((PhotoGridFragment)fragment).notifyDataChanged();
		} else {
			/**
			 * Nothing
			 */
		}

		if (updateView && fragmentChangedListener != null) {
			fragmentChangedListener.onUpdateView(tag, true);
		}
	}

	public void setOnFragmentChangedListener(OnFragmentChangedListener listener) {
		this.fragmentChangedListener = listener;
	}

	public void setOnCurrentPhotoChangedListener(OnCurrentPhotoChangedListener listener) {
		this.currentPhotoChangedListener = listener;
	}

	public void setOnPhotoListItemClickListener(OnPhotoListItemClickListener listener) {
		this.itemClickListener = listener;
	}

	public void setOnGridViewCreatedListener(@LayoutRes int gridItemLayoutId,  OnViewCreatedListener listener) {
		this.gridItemLayoutId = gridItemLayoutId;
		this.gridViewCreatedListener = listener;
	}

	public void setOnListViewCreatedListener(@LayoutRes int listItemLayoutId,  OnViewCreatedListener listener) {
		this.listItemLayoutId = listItemLayoutId;
		this.listViewCreatedListener = listener;
	}

	public void setOnPreviewViewCreatedListener(OnViewCreatedListener listener) {
		this.priviewViewCreatedListener = listener;
	}

	public void setOnGridItemClickListener(OnGridItemClickListener listener) {
		this.onGridItemClickListener = listener;
	}

	/**
	 * 监听页面切换
	 */
	public interface OnFragmentChangedListener {
		void onUpdateView(String tag, boolean isBack);
	}

	/**
	 * 监听图片的选中状态
	 */
	public interface OnSelectedPhotoChangedListener {
		void onChanged(List<String> selectedPhoto, int maxCount);
	}

	/**
	 * 监听当前图片的选中状态
	 */
	public interface OnCurrentPhotoChangedListener {
		void onChanged(int position, boolean isSelected);
	}

	/**
	 * 监听图片的点击事件
	 */
	public interface OnGridItemClickListener {
		void onItemClick(int position, ArrayList<String> photoList);
	}
}
