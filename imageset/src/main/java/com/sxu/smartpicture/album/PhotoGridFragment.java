package com.sxu.smartpicture.album;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.sxu.smartpicture.utils.DisplayUtil;
import com.sxu.smartpicture.R;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.WrapImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Freeman on 17/3/31.
 */

public class PhotoGridFragment extends Fragment {

    private GridView photoGrid;
    private View mContentView;

    private int scrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private int imageSize;
    //private int directionIndex;
    private CommonAdapter<String> gridAdapter;
    private List<PhotoDirectoryBean> photoDirectories = new ArrayList<>();
    private ArrayList<String> allPhotoPaths = new ArrayList<>();

    private ChoosePhotoActivity.OnItemPhotoCheckedListener checkedListener;
    private OnItemPhotoPreviewListener previewListener;

    private static final String TAG_DIRECTION_INDEX = "directionIndex";

    public static PhotoGridFragment newInstance(int directionIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(TAG_DIRECTION_INDEX, directionIndex);
        PhotoGridFragment fragment = new PhotoGridFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() instanceof ChoosePhotoActivity) {
            ChoosePhotoActivity context = ((ChoosePhotoActivity) getActivity());
            context.updateViewVisible(context.FRAGMENT_TAG_GRID);
        }
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
    }

    protected void initFragment() {
        final int directionIndex = getArguments().getInt(TAG_DIRECTION_INDEX, 0);

        PhotoManager.getInstance().getAllPhotos(getActivity(), new PhotoManager.OnPhotoDirectoryLoadListener() {
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
        photoGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int state) {
                scrollState = state;
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //startPostponedEnterTransition();
    }

    private void setPhotoAdapter() {
        if (gridAdapter == null) {
            imageSize = (DisplayUtil.getScreenWidth() - DisplayUtil.dpToPx(32)) / 3;
            final FrameLayout.LayoutParams itemParams = new FrameLayout.LayoutParams(imageSize, imageSize);
            gridAdapter = new CommonAdapter<String>(getActivity(), allPhotoPaths, R.layout.item_photo_grid_layout) {
                @Override
                public void convert(final ViewHolder holder, final String params) {
                    //if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    final WrapImageView photoIcon = (WrapImageView) holder.getView(R.id.photo);
                    final ImageView checkIcon = (ImageView) holder.getView(R.id.check_box);
                    ImageLoaderManager.getInstance().displayImage("file://" + params, photoIcon);
                    photoIcon.setLayoutParams(itemParams);
                    Log.i("out", "selected===" + ((ChoosePhotoActivity)getActivity()).selectedPhotos.size());
                    if (((ChoosePhotoActivity)getActivity()).selectedPhotos.contains(params)) {
                        checkIcon.setSelected(true);
                    } else {
                        checkIcon.setSelected(false);
                    }
                    ViewCompat.setTransitionName(photoIcon, "Preview00" + holder.getPosition());
                    photoIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (previewListener != null) {
                                previewListener.onItemPreview(holder.getPosition(), photoIcon, allPhotoPaths);
                            }
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
        }
        photoGrid.setAdapter(gridAdapter);
    }

    public void setOnItemPhotoCheckedListener(ChoosePhotoActivity.OnItemPhotoCheckedListener listener) {
        this.checkedListener = listener;
    }

    public void setOnItemPhotoPreviewListener(OnItemPhotoPreviewListener listener) {
        this.previewListener = listener;
    }

    public interface OnItemPhotoPreviewListener {
        void onItemPreview(int currentItem, View itemView, ArrayList<String> allPhotos);
    }
}
