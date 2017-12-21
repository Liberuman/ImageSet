package com.sxu.smartpicture.album;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sxu.smartpicture.R;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.WrapImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Freeman on 17/3/31.
 */

public class PhotoListFragment extends Fragment {

    private ListView photoList;
    private List<PhotoDirectoryBean> photoDirectories = new ArrayList<>();
    private OnItemPhotoListClickListener listener;

    private View mContentView;
    private boolean isScrolling = false;
    private static final String TAG_PHOTO_DIRECTORIES = "photoDirectories";
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
        if (getActivity() instanceof ChoosePhotoActivity) {
            ChoosePhotoActivity context = ((ChoosePhotoActivity) getActivity());
            context.updateViewVisible(context.FRAGMENT_TAG_LIST);
        }
        getViews();
        initFragment();
    }
    
    protected void getViews() {
        photoList = mContentView.findViewById(R.id.photo_list);
    }

    protected void initFragment() {
        PhotoManager.getInstance().getAllPhotos(getActivity(), new PhotoManager.OnPhotoDirectoryLoadListener() {
            @Override
            public void onCompleted(List<PhotoDirectoryBean> directoryList) {
                photoDirectories.clear();
                photoDirectories.addAll(directoryList);
                setPhotoDirectoryAdapter();
            }
        });

        photoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listener != null) {
                    listener.onItemPhotoListClick(i);
                }
            }
        });

        photoList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == SCROLL_STATE_IDLE) {
                    isScrolling = false;
                    directoryAdapter.notifyDataSetChanged();
                } else {
                    isScrolling = true;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private void setPhotoDirectoryAdapter() {
        if (directoryAdapter == null) {
            directoryAdapter = new CommonAdapter<PhotoDirectoryBean>(getActivity(), photoDirectories,
                    R.layout.item_photo_list_layout) {
                @Override
                public void convert(ViewHolder holder, final PhotoDirectoryBean params) {
                    holder.setText(R.id.directory_name_text, params.name);
                    if (!isScrolling) {
                        WrapImageView photo = (WrapImageView) holder.getView(R.id.directory_icon);
                        ImageLoaderManager.getInstance().displayImage(Uri.fromFile(new File(params.thumbPath)).toString(), photo);
                    }
                }
            };
        }
        photoList.setAdapter(directoryAdapter);
    }

    public void setOnItemPhotoListClickListener(OnItemPhotoListClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemPhotoListClickListener {
        void onItemPhotoListClick(int directoryIndex);
    }
}
