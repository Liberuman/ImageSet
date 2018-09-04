package com.sxu.smartpicture.album;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sxu.imageloader.ImageLoaderManager;
import com.sxu.imageloader.instance.GlideInstance;
import com.sxu.smartpicture.R;
import com.sxu.smartpicture.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.List;


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

    private int maxCount = 0;
    public ArrayList<String> selectedPhotos = new ArrayList<>();

    public final String FRAGMENT_TAG_GRID = "grid";
    public final String FRAGMENT_TAG_LIST = "list";
    public final String FRAGMENT_TAG_PREVIEW = "preview";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo_layout);
        ImageLoaderManager.getInstance().init(this, new GlideInstance());
        getViews();
        initActivity();
    }

    protected void getViews() {
        returnIcon = findViewById(R.id.return_icon);
        titleText = findViewById(R.id.title_text);
        cancelText = findViewById(R.id.cancel_text);
        checkIcon = findViewById(R.id.check_icon);
        completeLayout = findViewById(R.id.complete_layout);
        completeText = findViewById(R.id.complete_text);
    }

    protected void initActivity() {
        maxCount = getIntent().getIntExtra(PhotoPicker.MAX_PHOTO_COUNT, 0);
        List<String> photoList = getIntent().getStringArrayListExtra(PhotoPicker.SELECTED_PHOTOS);
        if (photoList != null && photoList.size() > 0) {
            selectedPhotos.addAll(photoList);
            completeText.setText(getString(R.string.complete_text, photoList.size()));
        }
        updateFragment(PhotoGridFragment.newInstance(0), FRAGMENT_TAG_GRID, true, false);

        completeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(PhotoPicker.SELECTED_PHOTOS, selectedPhotos);
                setResult(RESULT_OK, intent);
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
    }

    /**
     *
     * @param fragment
     * @param fragmentTag
     * @param isAdd true表示添加操作，否则表示替换操作
     * @param addToStack 是否需要添加到回退栈中
     */
    public void updateFragment(final Fragment fragment, String fragmentTag, final boolean isAdd, final boolean addToStack) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();
        if (isAdd) {
            transaction.add(R.id.container_layout, fragment, fragmentTag);
        } else {
            transaction.replace(R.id.container_layout, fragment, fragmentTag);
        }
        if (addToStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();

        if (fragment instanceof PhotoGridFragment) {
            ((PhotoGridFragment)fragment).setOnItemPhotoCheckedListener(
                    new OnItemPhotoCheckedListener() {
                        @Override
                        public void onItemChecked(ImageView checkIcon, boolean isSelected, String photoPath) {
                            updateSelectedPhotos(checkIcon, isSelected, photoPath);
                        }
                    });
            ((PhotoGridFragment)fragment).setOnItemPhotoPreviewListener(new PhotoGridFragment.OnItemPhotoPreviewListener() {
                @Override
                public void onItemPreview(int currentItem, View itemView, ArrayList<String> allPhotos) {
                    PhotoPreviewFragment previewFragment = PhotoPreviewFragment.getInstance(currentItem, allPhotos);
                    previewFragment.setAllowEnterTransitionOverlap(true);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        Transition transition = TransitionInflater.from(getBaseContext())
                                .inflateTransition(android.R.transition.move);
                        transition.setDuration(450);
                        previewFragment.setSharedElementEnterTransition(transition);
                    }

                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.container_layout, previewFragment);
                    transaction.addSharedElement(itemView, "Preview" + currentItem);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    updateViewVisible(FRAGMENT_TAG_PREVIEW);

                    previewFragment.setOnItemPhotoCheckedListener(
                            new OnItemPhotoCheckedListener() {
                                @Override
                                public void onItemChecked(ImageView checkIcon, boolean isSelected, String photoPath) {
                                    updateSelectedPhotos(checkIcon, isSelected, photoPath);
                                }
                            });
                }
            });
        } else if (fragment instanceof PhotoListFragment) {
            ((PhotoListFragment)fragment).setOnItemPhotoListClickListener(new PhotoListFragment.OnItemPhotoListClickListener() {
                @Override
                public void onItemPhotoListClick(int directoryIndex) {
                    updateFragment(PhotoGridFragment.newInstance(directoryIndex), FRAGMENT_TAG_GRID, false, true);
                }
            });
        } else {
            updateViewVisible(FRAGMENT_TAG_PREVIEW);
        }
    }

    public void setCheckIconStatus(String photoPath) {
        checkIcon.setSelected(selectedPhotos.contains(photoPath));
    }

    private void updateSelectedPhotos(ImageView checkIcon,
                                      boolean isSelected, String photoPath) {
        if (isSelected && !selectedPhotos.contains(photoPath)) {
            if (selectedPhotos.size() < maxCount) {
                checkIcon.setSelected(true);
                selectedPhotos.add(photoPath);
            } else {
                Toast.makeText(ChoosePhotoActivity.this, getString(R.string.max_select_count, maxCount),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (!isSelected && selectedPhotos.contains(photoPath)) {
            checkIcon.setSelected(false);
            selectedPhotos.remove(photoPath);
        }
        completeText.setText(getString(R.string.complete_text, selectedPhotos.size()));
    }

    public void updateViewVisible(String tag) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.requestCallback(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0 && fm.findFragmentByTag(FRAGMENT_TAG_GRID) != null) {
            updateFragment(new PhotoListFragment(), FRAGMENT_TAG_LIST, false, false);
        } else {
            super.onBackPressed();
        }
    }

    public interface OnItemPhotoCheckedListener {
        void onItemChecked(ImageView checkIcon, boolean isSelected, String photoPath);
    }
}
