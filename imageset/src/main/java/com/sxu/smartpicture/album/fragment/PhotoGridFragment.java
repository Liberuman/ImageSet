package com.sxu.smartpicture.album.fragment;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.sxu.smartpicture.R;
import com.sxu.smartpicture.album.CommonAdapter;
import com.sxu.smartpicture.album.PhotoDirectoryBean;
import com.sxu.smartpicture.album.PhotoManager;
import com.sxu.smartpicture.album.listener.OnItemPhotoCheckedListener;
import com.sxu.smartpicture.album.listener.OnViewCreatedListener;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.WrapImageView;
import com.sxu.smartpicture.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;


/*******************************************************************************
 * Description: 相册展示页面
 *
 * Author: Freeman
 *
 * Date: 2018/03/31
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/

public class PhotoGridFragment extends Fragment {

    private GridView photoGrid;
    private View loadingLayout;
    private View mContentView;

    private int imageSize;
    private int directionIndex;
    private int itemLayoutResId = R.layout.item_photo_grid_layout;
    private CommonAdapter<String> gridAdapter;
    private ArrayList<String> selectedPhotos;
    private ArrayList<String> allPhotoPaths = new ArrayList<>();

    private OnItemPhotoCheckedListener checkedListener;
    private OnItemPhotoPreviewListener previewListener;
    private OnViewCreatedListener viewCreatedListener;

    private static final String TAG_DIRECTION_INDEX = "directionIndex";
    private static final String TAG_SELECTED_PHOTO = "selectedPhotos";

    public static PhotoGridFragment newInstance(int directionIndex, ArrayList<String> selectedPhotos) {
        Bundle bundle = new Bundle();
        bundle.putInt(TAG_DIRECTION_INDEX, directionIndex);
        bundle.putStringArrayList(TAG_SELECTED_PHOTO, selectedPhotos);
        PhotoGridFragment fragment = new PhotoGridFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_photo_grid_layout, null);
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getViews();
        if (viewCreatedListener != null) {
            viewCreatedListener.onViewCreated(photoGrid);
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            directionIndex = bundle.getInt(TAG_DIRECTION_INDEX, 0);
            selectedPhotos = bundle.getStringArrayList(TAG_SELECTED_PHOTO);
        }
        initFragment();
    }

    protected void getViews() {
        photoGrid = mContentView.findViewById(R.id.photo_grid);
        loadingLayout = mContentView.findViewById(R.id.loading_layout);
    }

    protected void initFragment() {
        PhotoManager.getInstance().loadAllPhotos(getActivity(), new PhotoManager.OnPhotoDirectoryLoadListener() {
            @Override
            public void onCompleted(List<PhotoDirectoryBean> directoryList) {
                if (directoryList != null && directionIndex < directoryList.size()) {
                    allPhotoPaths.clear();
                    allPhotoPaths.addAll(directoryList.get(directionIndex).photoList);
                    setPhotoAdapter();
                }
            }
        });

        photoGrid.setLayoutTransition(new LayoutTransition());
    }

    /**
     * 更新图片列表，如首次加载图片列表，点击相册列表，从预览页面返回
     * @param directionIndex 如果<0，表示directionIndex不变
     * @param selectedPhotos
     */
    public void updateGridLayout(int directionIndex, ArrayList<String> selectedPhotos) {
        if (directionIndex >= 0) {
            this.directionIndex = directionIndex;
        }
        this.selectedPhotos = selectedPhotos;
        initFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        startPostponedEnterTransition();
    }

    private void setPhotoAdapter() {
        loadingLayout.setVisibility(View.GONE);
        if (gridAdapter == null) {
            int columnCount = photoGrid.getNumColumns();
            int horizontalPadding = photoGrid.getPaddingLeft() + photoGrid.getPaddingRight();
            int horizontalSpace = (photoGrid.getNumColumns() - 1) * photoGrid.getHorizontalSpacing();
            imageSize = (DisplayUtil.getScreenWidth() - horizontalPadding - horizontalSpace) / columnCount;
            final FrameLayout.LayoutParams itemParams = new FrameLayout.LayoutParams(imageSize, imageSize);
            gridAdapter = new CommonAdapter<String>(getActivity(), allPhotoPaths, itemLayoutResId) {
                @Override
                public void convert(final ViewHolder holder, final String params) {
                    final WrapImageView photoIcon = (WrapImageView) holder.getView(R.id.photo);
                    final ImageView checkIcon = (ImageView) holder.getView(R.id.check_box);
                    ImageLoaderManager.getInstance().displayImage("file://" + params, photoIcon, imageSize, imageSize);
                    photoIcon.setLayoutParams(itemParams);
                    if (selectedPhotos != null && selectedPhotos.contains(params)) {
                        checkIcon.setSelected(true);
                    } else {
                        checkIcon.setSelected(false);
                    }
                    ViewCompat.setTransitionName(photoIcon, "Preview_" + holder.getPosition());
                    photoIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (previewListener != null) {
                                previewListener.onItemPreview(holder.getPosition(), photoIcon, allPhotoPaths);
                            }
                            //PhotoPreviewActivity.enter(getActivity(), photoIcon, holder.getPosition(), allPhotoPaths);
                        }
                    });

                    checkIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkedListener.onItemChecked(checkIcon, params);
                        }
                    });
                }
            };
            photoGrid.setAdapter(gridAdapter);
        } else {
            gridAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 预览改变图片的选中状态后需要刷新页面
     */
    public void notifyDataChanged() {
        setPhotoAdapter();
    }

    public void setOnItemPhotoCheckedListener(OnItemPhotoCheckedListener listener) {
        this.checkedListener = listener;
    }

    public void setOnItemPhotoPreviewListener(OnItemPhotoPreviewListener listener) {
        this.previewListener = listener;
    }

    public void setOnViewCreatedListener(OnViewCreatedListener listener) {
        setOnViewCreatedListener(0, listener);
    }

    /**
     * 监听Fragment中View的加载，用于自定义View样式
     * @param itemLayoutResId 自定义的相册子布局
     * @param listener 监听Fragment中View的加载，用于修改GridView的样式
     */
    public void setOnViewCreatedListener(@LayoutRes int itemLayoutResId,  OnViewCreatedListener listener) {
        if (itemLayoutResId != 0) {
            this.itemLayoutResId = itemLayoutResId;
        }
        this.viewCreatedListener = listener;
    }

    public interface OnItemPhotoPreviewListener {
        void onItemPreview(int currentItem, View itemView, ArrayList<String> allPhotos);
    }
}
