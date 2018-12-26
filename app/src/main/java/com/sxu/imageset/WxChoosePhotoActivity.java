package com.sxu.imageset;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AndroidException;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sxu.smartpicture.album.ChoosePhotoPresenter;
import com.sxu.smartpicture.album.PhotoDirectoryBean;
import com.sxu.smartpicture.album.PhotoPicker;
import com.sxu.smartpicture.album.activity.PhotoPreviewActivity;
import com.sxu.smartpicture.album.fragment.PhotoGridFragment;
import com.sxu.smartpicture.album.fragment.PhotoPreviewFragment;
import com.sxu.smartpicture.album.listener.OnPhotoListItemClickListener;
import com.sxu.smartpicture.album.listener.OnViewCreatedListener;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.instance.GlideInstance;
import com.sxu.smartpicture.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

import static com.sxu.smartpicture.album.ChoosePhotoPresenter.FRAGMENT_TAG_GRID;
import static com.sxu.smartpicture.album.ChoosePhotoPresenter.FRAGMENT_TAG_LIST;

/*******************************************************************************
 * Description: 仿微信图片选择
 *
 * Author: Freeman
 *
 * Date: 2018/12/23
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class WxChoosePhotoActivity extends AppCompatActivity implements View.OnClickListener {


	/**
	 * 2. 预览时，修改图片选中状态，滑动页面闪退【原因：数据已改变，pagerAdapter未改变】
	 * 4. 查看部分相册图片不显示的问题；
	 */
	private ImageView returnIcon;
	private TextView titleText;
	private TextView countText;
	private TextView completeText;
	private TextView switchText;
	private TextView previewText;
	private TextView editText;
	private ImageView checkIcon;
	private View switchLayout;
	private View checkLayout;
	private View listContainLayout;

	private String currentPhoto;
	private List<String> previewList = new ArrayList<>();
	private ChoosePhotoPresenter presenter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wx_choose_photo_layout);
		if (!ImageLoaderManager.getInstance().isInit()) {
			ImageLoaderManager.getInstance().init(this, new GlideInstance());
		}

		getViews();
		initActivity();
	}

	protected void getViews() {
		returnIcon = findViewById(R.id.return_icon);
		titleText = findViewById(R.id.title_text);
		countText = findViewById(R.id.count_text);
		completeText = findViewById(R.id.complete_text);
		switchText = findViewById(R.id.switch_text);
		previewText = findViewById(R.id.preview_text);
		editText = findViewById(R.id.edit_text);
		checkIcon = findViewById(R.id.check_icon);
		switchLayout = findViewById(R.id.switch_layout);
		checkLayout = findViewById(R.id.check_layout);
		listContainLayout = findViewById(R.id.list_container_layout);
	}

	private void initActivity() {
		presenter = new ChoosePhotoPresenter(this);
		final int dividerWidth = DisplayUtil.dpToPx(2);
		presenter.setOnGridViewCreatedListener(R.layout.wx_item_photo_grid_layout, new OnViewCreatedListener() {
			@Override
			public void onViewCreated(View gridView) {
				gridView.setBackgroundColor(Color.parseColor("#222222"));
				gridView.setPadding(dividerWidth, dividerWidth/2, dividerWidth, dividerWidth/2);
				((GridView)gridView).setNumColumns(4);
				((GridView)gridView).setHorizontalSpacing(dividerWidth);
				((GridView)gridView).setVerticalSpacing(dividerWidth);
			}
		});

		final int padding = DisplayUtil.dpToPx(10);
		presenter.setOnListViewCreatedListener(R.layout.wx_item_photo_list_layout, new OnViewCreatedListener() {
			@Override
			public void onViewCreated(View listView) {
				listView.setPadding(padding, 0, padding, 0);
			}
		});
		presenter.setOnPreviewViewCreatedListener(new OnViewCreatedListener() {
			@Override
			public void onViewCreated(View containerView) {
				containerView.setBackgroundColor(Color.parseColor("#222222"));
			}
		});

		presenter.initPresenter(new ChoosePhotoPresenter.OnSelectedPhotoChangedListener() {
			@Override
			public void onChanged(List<String> selectedPhoto, int maxCount) {
				int photoSize = selectedPhoto.size();
				if (photoSize > 0) {
					previewText.setEnabled(true);
					completeText.setEnabled(true);
					previewText.setText(getString(R.string.preview_image, photoSize));
					completeText.setTextColor(Color.parseColor("#40c541"));
					completeText.setText(getString(R.string.complete_text, photoSize, maxCount));
				} else {
					previewText.setEnabled(false);
					completeText.setEnabled(false);
					previewText.setText(getString(R.string.preview));
					completeText.setTextColor(Color.parseColor("#8040c541"));
					completeText.setText("完成");
				}
			}
		});
		presenter.setOnFragmentChangedListener(new ChoosePhotoPresenter.OnFragmentChangedListener() {
			@Override
			public void onUpdateView(String tag, boolean isBack) {
				if (ChoosePhotoPresenter.FRAGMENT_TAG_PREVIEW.equals(tag)) {
					titleText.setVisibility(View.GONE);
					previewText.setVisibility(View.GONE);
					switchLayout.setVisibility(View.GONE);
					countText.setVisibility(View.VISIBLE);
					editText.setVisibility(View.VISIBLE);
					checkLayout.setVisibility(View.VISIBLE);
				} else {
					titleText.setVisibility(View.VISIBLE);
					previewText.setVisibility(View.VISIBLE);
					switchLayout.setVisibility(View.VISIBLE);
					countText.setVisibility(View.GONE);
					editText.setVisibility(View.GONE);
					checkLayout.setVisibility(View.GONE);
				}
				if (isBack) {
					WxChoosePhotoActivity.super.onBackPressed();
				}
			}
		});
		presenter.setOnCurrentPhotoChangedListener(new ChoosePhotoPresenter.OnCurrentPhotoChangedListener() {
			@Override
			public void onChanged(int position, boolean isSelected) {
				checkIcon.setSelected(isSelected);
				countText.setText((position + 1) + "/" + previewList.size());
			}
		});
		presenter.setOnGridItemClickListener(new ChoosePhotoPresenter.OnGridItemClickListener() {
			@Override
			public void onItemClick(int position, ArrayList<String> photoList) {
				previewPhoto(position, photoList);
			}
		});

		returnIcon.setOnClickListener(this);
		completeText.setOnClickListener(this);
		switchText.setOnClickListener(this);
		editText.setOnClickListener(this);
		previewText.setOnClickListener(this);
		checkLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.return_icon:
				onBackPressed();
				break;
			case R.id.complete_text:
				Intent intent = new Intent();
				intent.putStringArrayListExtra(PhotoPicker.SELECTED_PHOTOS, presenter.getSelectedPhotos());
				setResult(0, intent);
				finish();
				break;
			case R.id.switch_text:
				listContainLayout.setVisibility(View.VISIBLE);
				presenter.setOnPhotoListItemClickListener(new OnPhotoListItemClickListener() {
					@Override
					public void onPhotoListItemClick(int index, PhotoDirectoryBean directoryInfo) {
						listContainLayout.setVisibility(View.GONE);
						switchText.setText(directoryInfo.name);
						WxChoosePhotoActivity.super.onBackPressed();
					}
				});
				presenter.updateFragment(presenter.getListFragment(), R.id.list_container_layout,
						FRAGMENT_TAG_LIST, true,true);
				break;
			case R.id.edit_text:
				Toast.makeText(this, "暂不支持编辑", Toast.LENGTH_SHORT).show();
				break;
			case R.id.preview_text:
				previewPhoto(0, presenter.getSelectedPhotos());
				break;
			case R.id.check_layout:
				presenter.updateSelectedPhotos(checkIcon, presenter.getCurrentPhotoPath());
				break;
			default:
				break;
		}
	}

	private void previewPhoto(int position, ArrayList<String> photoList) {
		previewList.clear();
		previewList.addAll(photoList);
		presenter.updateFragment(presenter.getPreviewFragment(position, photoList),
				R.id.container_layout, ChoosePhotoPresenter.FRAGMENT_TAG_PREVIEW, true, true);
		countText.setText(position + "/" + photoList.size());
	}

	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		if (fm.findFragmentByTag(FRAGMENT_TAG_LIST) != null
				&& fm.getFragments().size() == 2) {
			listContainLayout.setVisibility(View.GONE);
		} else if (fm.findFragmentByTag(ChoosePhotoPresenter.FRAGMENT_TAG_PREVIEW) != null) {
			titleText.setVisibility(View.VISIBLE);
			previewText.setVisibility(View.VISIBLE);
			switchLayout.setVisibility(View.VISIBLE);
			countText.setVisibility(View.GONE);
			editText.setVisibility(View.GONE);
			checkLayout.setVisibility(View.GONE);
			PhotoGridFragment fragment = (PhotoGridFragment) fm.findFragmentByTag(FRAGMENT_TAG_GRID);
			fragment.updateGridLayout(-1, presenter.getSelectedPhotos());
			fragment.notifyDataChanged();
		}
		super.onBackPressed();
	}
}
