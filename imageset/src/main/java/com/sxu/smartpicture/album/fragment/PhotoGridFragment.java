package com.sxu.smartpicture.album.fragment;

import android.animation.LayoutTransition;
import android.os.Bundle;
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
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.WrapImageView;
import com.sxu.smartpicture.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Freeman on 17/3/31.
 */

public class PhotoGridFragment extends Fragment {

    private GridView photoGrid;
    private View loadingLayout;
    private View mContentView;

    private int imageSize;
    private int directionIndex;
    private CommonAdapter<String> gridAdapter;
    private ArrayList<String> selectedPhotos;
    private ArrayList<String> allPhotoPaths = new ArrayList<>();

    private OnItemPhotoCheckedListener checkedListener;
    private OnItemPhotoPreviewListener previewListener;

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
        initFragment();
    }

    protected void getViews() {
        photoGrid = mContentView.findViewById(R.id.photo_grid);
        loadingLayout = mContentView.findViewById(R.id.loading_layout);
    }

    protected void initFragment() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            directionIndex = bundle.getInt(TAG_DIRECTION_INDEX, 0);
            selectedPhotos = bundle.getStringArrayList(TAG_SELECTED_PHOTO);
        }

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

    @Override
    public void onResume() {
        super.onResume();
        startPostponedEnterTransition();
    }

    private void setPhotoAdapter() {
        loadingLayout.setVisibility(View.GONE);
        if (gridAdapter == null) {
            imageSize = (DisplayUtil.getScreenWidth() - DisplayUtil.dpToPx(32)) / 3;
            final FrameLayout.LayoutParams itemParams = new FrameLayout.LayoutParams(imageSize, imageSize);
            gridAdapter = new CommonAdapter<String>(getActivity(), allPhotoPaths, R.layout.item_photo_grid_layout) {
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
                            checkedListener.onItemChecked(checkIcon, !checkIcon.isSelected(), params);
                        }
                    });
                }
            };
            photoGrid.setAdapter(gridAdapter);
        } else {
            gridAdapter.notifyDataSetChanged();
        }
    }

    public void setOnItemPhotoCheckedListener(OnItemPhotoCheckedListener listener) {
        this.checkedListener = listener;
    }

    public void setOnItemPhotoPreviewListener(OnItemPhotoPreviewListener listener) {
        this.previewListener = listener;
    }

    public interface OnItemPhotoPreviewListener {
        void onItemPreview(int currentItem, View itemView, ArrayList<String> allPhotos);
    }
}
