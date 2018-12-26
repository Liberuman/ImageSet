package com.sxu.smartpicture.album.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxu.smartpicture.R;
import com.sxu.smartpicture.album.ChoosePhotoPresenter;
import com.sxu.smartpicture.album.PhotoDirectoryBean;
import com.sxu.smartpicture.album.listener.OnPhotoListItemClickListener;
import com.sxu.smartpicture.album.listener.OnSelectPhotoListener;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.instance.GlideInstance;

import java.util.List;

import static com.sxu.smartpicture.album.ChoosePhotoPresenter.FRAGMENT_TAG_GRID;
import static com.sxu.smartpicture.album.ChoosePhotoPresenter.FRAGMENT_TAG_LIST;
import static com.sxu.smartpicture.album.ChoosePhotoPresenter.FRAGMENT_TAG_PREVIEW;


/**
 * Created by Freeman on 17/3/31.
 */

public class ChoosePhotoActivity extends AppCompatActivity {

    private ImageView returnIcon;
    private TextView titleText;
    private TextView cancelText;
    public ImageView checkIcon;
    private LinearLayout completeLayout;
    private TextView completeText;

    private static OnSelectPhotoListener selectListener;

    private ChoosePhotoPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo_layout);
        if (!ImageLoaderManager.getInstance().isInit()) {
            ImageLoaderManager.getInstance().init(this, new GlideInstance());
        }
        getViews();
        initActivity();
    }

    public static void setSelectListener(OnSelectPhotoListener listener) {
        selectListener = listener;
    }

    protected void getViews() {
        returnIcon = findViewById(R.id.return_icon);
        titleText = findViewById(R.id.title_text);
        cancelText = findViewById(R.id.cancel_text);
        checkIcon = findViewById(R.id.check_icon);
        completeLayout = findViewById(R.id.complete_layout);
        completeText = findViewById(R.id.complete_text);
    }

    private void initActivity() {
        presenter = new ChoosePhotoPresenter(this);
        presenter.initPresenter(new ChoosePhotoPresenter.OnSelectedPhotoChangedListener() {
            @Override
            public void onChanged(List<String> selectedPhoto, int maxCount) {
                int photoCount = selectedPhoto.size();
                if (photoCount > 0) {
                    completeText.setEnabled(true);
                    completeText.setText(getString(R.string.complete_text, selectedPhoto.size(), maxCount));
                } else {
                    completeText.setEnabled(false);
                    completeText.setText(getString(R.string.complete));
                }
            }
        });
        presenter.setOnFragmentChangedListener(new ChoosePhotoPresenter.OnFragmentChangedListener() {
            @Override
            public void onUpdateView(String tag, boolean isBack) {
                updateViewVisible(tag, isBack);
            }
        });
        presenter.setOnPhotoListItemClickListener(new OnPhotoListItemClickListener() {
            @Override
            public void onPhotoListItemClick(int index, PhotoDirectoryBean directoryInfo) {
                updateViewVisible(FRAGMENT_TAG_GRID, false);
            }
        });
        presenter.setOnCurrentPhotoChangedListener(new ChoosePhotoPresenter.OnCurrentPhotoChangedListener() {
            @Override
            public void onChanged(int position, boolean isSelected) {
                checkIcon.setSelected(isSelected);
            }
        });

        completeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectListener != null) {
                    selectListener.onSelected(presenter.getSelectedPhotos());
                }
                finish();
            }
        });
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        returnIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        checkIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.updateSelectedPhotos(checkIcon, presenter.getCurrentPhotoPath());
            }
        });
    }

    public void updateViewVisible(String tag, boolean isBack) {
        if (isBack) {
            super.onBackPressed();
        }
        if (FRAGMENT_TAG_GRID.equals(tag)) {
            titleText.setText(getString(R.string.all_photo));
            returnIcon.setVisibility(View.VISIBLE);
            cancelText.setVisibility(View.VISIBLE);
            checkIcon.setVisibility(View.GONE);
            completeLayout.setVisibility(View.VISIBLE);
        } else if (FRAGMENT_TAG_LIST.equals(tag)) {
            titleText.setText(getString(R.string.photo_text));
            returnIcon.setVisibility(View.GONE);
            cancelText.setVisibility(View.VISIBLE);
            checkIcon.setVisibility(View.GONE);
            completeLayout.setVisibility(View.GONE);
        } else if (FRAGMENT_TAG_PREVIEW.equals(tag)) {
            titleText.setText(getString(R.string.preview_photo));
            returnIcon.setVisibility(View.VISIBLE);
            cancelText.setVisibility(View.GONE);
            checkIcon.setVisibility(View.VISIBLE);
            completeLayout.setVisibility(View.VISIBLE);
        } else {
            /**
             * Nothing
             */
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectListener = null;
    }
}
