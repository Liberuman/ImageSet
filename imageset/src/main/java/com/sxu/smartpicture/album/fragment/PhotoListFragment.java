package com.sxu.smartpicture.album.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.sxu.smartpicture.R;
import com.sxu.smartpicture.album.CommonAdapter;
import com.sxu.smartpicture.album.PhotoDirectoryBean;
import com.sxu.smartpicture.album.PhotoManager;
import com.sxu.smartpicture.album.listener.OnPhotoListItemClickListener;
import com.sxu.smartpicture.album.listener.OnViewCreatedListener;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.WrapImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/*******************************************************************************
 * Description: 相册列表
 *
 * Author: Freeman
 *
 * Date: 2018/03/31
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class PhotoListFragment extends Fragment {

    private ListView photoList;
    private List<PhotoDirectoryBean> photoDirectories = new ArrayList<>();
    private OnPhotoListItemClickListener listener;

    private View mContentView;
    private int selectedDirectionIndex = 0;
    private int itemLayoutResId = R.layout.item_photo_list_layout;
    private OnViewCreatedListener viewCreatedListener;
    private CommonAdapter<PhotoDirectoryBean> directoryAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_photo_list_layout, null);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViews();
        initFragment();
    }
    
    protected void getViews() {
        photoList = mContentView.findViewById(R.id.photo_list);
    }

    protected void initFragment() {
        if (viewCreatedListener != null) {
            viewCreatedListener.onViewCreated(photoList);
        }

        PhotoManager.getInstance().loadAllPhotos(getActivity(), new PhotoManager.OnPhotoDirectoryLoadListener() {
            @Override
            public void onCompleted(List<PhotoDirectoryBean> directoryList) {
                photoDirectories.clear();
                photoDirectories.addAll(directoryList);
                setPhotoDirectoryAdapter();
            }
        });
    }

    private void setPhotoDirectoryAdapter() {
        directoryAdapter = new CommonAdapter<PhotoDirectoryBean>(getActivity(), photoDirectories, itemLayoutResId) {
            @Override
            public void convert(final ViewHolder holder, final PhotoDirectoryBean params) {
                WrapImageView photo = (WrapImageView) holder.getView(R.id.directory_icon);
                final ImageView checkIcon = (ImageView) holder.getView(R.id.check_icon);

                if (selectedDirectionIndex == holder.getPosition()) {
                    checkIcon.setSelected(true);
                } else {
                    checkIcon.setSelected(false);
                }
                holder.setText(R.id.directory_name_text, params.name);
                holder.setText(R.id.photo_count_text, params.photoList != null ? params.photoList.size() + "张" : "0张");
                ImageLoaderManager.getInstance().displayImage(Uri.fromFile(new File(params.thumbPath)).toString(), photo);

                holder.getContentView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkIcon.setSelected(true);
                        selectedDirectionIndex = holder.getPosition();
                        if (listener != null) {
                            listener.onPhotoListItemClick(selectedDirectionIndex,
                                    photoDirectories.get(selectedDirectionIndex));
                        }
                    }
                });
            }
        };
        photoList.setAdapter(directoryAdapter);
    }

    public void setOnPhotoListItemClickListener(OnPhotoListItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnViewCreatedListener(OnViewCreatedListener listener) {
        setOnViewCreatedListener(0, listener);
    }

    /**
     * 监听Fragment中View的加载，用于自定义View样式
     * @param itemLayoutResId 自定义的相册列表子布局
     * @param listener 监听Fragment中View的加载，用于修改ListView的样式
     */
    public void setOnViewCreatedListener(@LayoutRes int itemLayoutResId, OnViewCreatedListener listener) {
        if (itemLayoutResId != 0) {
            this.itemLayoutResId = itemLayoutResId;
        }
        this.viewCreatedListener = listener;
    }
}
