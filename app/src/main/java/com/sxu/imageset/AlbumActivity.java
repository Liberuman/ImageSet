package com.sxu.imageset;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.sxu.smartpicture.album.listener.OnSelectPhotoListener;
import com.sxu.smartpicture.album.PhotoPicker;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.WrapImageView;
import com.sxu.smartpicture.imageloader.instance.FrescoInstance;
import com.sxu.smartpicture.imageloader.instance.GlideInstance;
import com.sxu.smartpicture.imageloader.instance.UILInstance;
import com.sxu.smartpicture.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Freeman
 * @date 2017/12/21
 */


public class AlbumActivity extends AppCompatActivity {

	private GridView photoGrid;
	private BaseAdapter photoAdapter;
	private ArrayList<String> selectedPhotos = new ArrayList<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_layout);
		Button chooseButton = (Button)findViewById(R.id.choose_button);
		Button chooseWxButton = (Button)findViewById(R.id.choose_wx_button);
		photoGrid = (GridView) findViewById(R.id.photo_grid);

		int type = getIntent().getIntExtra("type", 0);
		if (!ImageLoaderManager.getInstance().isInit()) {
			if (type == 0) {
				ImageLoaderManager.getInstance().init(getApplicationContext(), new FrescoInstance());
			} else if (type == 1) {
				ImageLoaderManager.getInstance().init(getApplicationContext(), new UILInstance());
			} else if (type == 2) {
				ImageLoaderManager.getInstance().init(getApplicationContext(), new GlideInstance());
			} else {
				/**
				 * Nothing
				 */
			}
		}

		chooseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openDefaultPhotoPicker();
			}
		});

		chooseWxButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openWxPhotoPicker();
			}
		});
	}

	/**
	 * 使用默认的图片选择组件
	 */
	private void openDefaultPhotoPicker() {
		PhotoPicker picker = new PhotoPicker.Builder()
				.setIsDialog(false)
				.setIsShowCamera(false)
				.setMaxPhotoCount(9)
				.setSelectedPhotos(selectedPhotos)
				.builder();
		picker.chooseImage(AlbumActivity.this, new OnSelectPhotoListener() {
			@Override
			public void onSelected(ArrayList<String> selectedPhotoList) {
				selectedPhotos.clear();
				selectedPhotos.addAll(selectedPhotoList);
				setPhotoAdapter();
			}
		});
	}

	/**
	 * 使用仿微信的图片选择组件 注意这种方式需要在onActivityResult获取选择结果
	 */
	private void openWxPhotoPicker() {
		PhotoPicker picker = new PhotoPicker.Builder()
				.setIsDialog(false)
				.setIsShowCamera(false)
				.setMaxPhotoCount(9)
				.setSelectedPhotos(selectedPhotos)
				.builder();
		picker.chooseImage(AlbumActivity.this, WxChoosePhotoActivity.class);
	}

	public static void enter(Context context, int imageLoaderType) {
		Intent intent = new Intent(context, AlbumActivity.class);
		intent.putExtra("type", imageLoaderType);
		context.startActivity(intent);
	}

	private void setPhotoAdapter() {
		if (photoAdapter == null) {
			final int itemSize = (DisplayUtil.getScreenWidth() - DisplayUtil.dpToPx(72)) / 3;
			final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemSize);
			photoAdapter = new BaseAdapter() {
				@Override
				public int getCount() {
					return selectedPhotos.size();
				}

				@Override
				public Object getItem(int position) {
					return selectedPhotos.get(position);
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					WrapImageView imageView = new WrapImageView(AlbumActivity.this);
					imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
					ImageLoaderManager.getInstance().displayImage("file://" + getItem(position), imageView, itemSize, itemSize);
					imageView.setLayoutParams(params);
					return imageView;
				}
			};

			photoGrid.setAdapter(photoAdapter);
		} else {
			photoAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != PhotoPicker.REQUEST_CODE_CHOOSE_PHOTO || data == null) {
			return;
		}

		List<String> photoList = data.getStringArrayListExtra(PhotoPicker.SELECTED_PHOTOS);
		if (photoList != null && photoList.size() > 0) {
			selectedPhotos.clear();
			selectedPhotos.addAll(photoList);
			setPhotoAdapter();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoaderManager.getInstance().onDestroy();
	}
}
