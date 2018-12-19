package com.sxu.smartpicture.album.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sxu.smartpicture.R;
import com.sxu.smartpicture.album.listener.OnItemPhotoCheckedListener;
import com.sxu.smartpicture.album.listener.OnSelectPhotoListener;
import com.sxu.smartpicture.album.PhotoPicker;
import com.sxu.smartpicture.album.fragment.PhotoGridFragment;
import com.sxu.smartpicture.album.fragment.PhotoListFragment;
import com.sxu.smartpicture.album.fragment.PhotoPreviewFragment;
import com.sxu.smartpicture.imageloader.ImageLoaderManager;
import com.sxu.smartpicture.imageloader.instance.GlideInstance;

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
    private String currentPhoto;
    private Fragment currentFragment;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private static OnSelectPhotoListener selectListener;
    public ArrayList<String> selectedPhotos = new ArrayList<>();

    public final String FRAGMENT_TAG_GRID = "grid";
    public final String FRAGMENT_TAG_LIST = "list";
    public final String FRAGMENT_TAG_PREVIEW = "preview";

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

    protected void initActivity() {
        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
        maxCount = getIntent().getIntExtra(PhotoPicker.MAX_PHOTO_COUNT, 0);
        final List<String> photoList = getIntent().getStringArrayListExtra(PhotoPicker.SELECTED_PHOTOS);
        if (photoList != null && photoList.size() > 0) {
            selectedPhotos.addAll(photoList);
            completeText.setText(getString(R.string.complete_text, photoList.size()));
        }
        updateFragment(PhotoGridFragment.newInstance(0, selectedPhotos), FRAGMENT_TAG_GRID, true, false);

        completeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectListener != null) {
                    selectListener.onSelected(selectedPhotos);
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
                setCheckIconStatus(true, currentPhoto);
            }
        });
        fm.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState);
                updateViewVisible(f.getTag());
            }

            @Override
            public void onFragmentResumed(FragmentManager fm, Fragment f) {
                super.onFragmentResumed(fm, f);
                currentFragment = f;
                if (currentFragment instanceof PhotoPreviewFragment) {
                    ((PhotoPreviewFragment) currentFragment).setOnPagerChangeListener(new PhotoPreviewFragment.OnPagerChangedListener() {
                        @Override
                        public void onChanged(int position, String photoPath) {
                            currentPhoto = photoPath;
                            setCheckIconStatus(false, photoPath);
                        }
                    });
                }
            }
        }, true);
    }

    /**
     *
     * @param fragment
     * @param fragmentTag
     * @param isAdd true表示添加操作，否则表示替换操作
     * @param addToStack 是否需要添加到回退栈中
     */
    public void updateFragment(final Fragment fragment, String fragmentTag, final boolean isAdd, final boolean addToStack) {
        final FragmentTransaction transaction = fm.beginTransaction();
        if (isAdd) {
            transaction.add(R.id.container_layout, fragment, fragmentTag);
        } else {
            transaction.replace(R.id.container_layout, fragment, fragmentTag);
        }
        if (addToStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();

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
                    //previewFragment.setAllowEnterTransitionOverlap(true);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        Transition transition = TransitionInflater.from(getBaseContext())
                                .inflateTransition(android.R.transition.move);
                        transition.setDuration(400);
                        previewFragment.setSharedElementEnterTransition(transition);
                    }

                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.add(R.id.container_layout, previewFragment, FRAGMENT_TAG_PREVIEW);
                    transaction.addSharedElement(itemView, "Preview_" + currentItem);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    updateViewVisible(FRAGMENT_TAG_PREVIEW);

//                    previewFragment.setOnItemPhotoCheckedListener(
//                            new OnItemPhotoCheckedListener() {
//                                @Override
//                                public void onItemChecked(ImageView checkIcon, boolean isSelected, String photoPath) {
//                                    updateSelectedPhotos(checkIcon, isSelected, photoPath);
//                                }
//                            });
                }
            });
        } else if (fragment instanceof PhotoListFragment) {
            ((PhotoListFragment)fragment).setOnItemPhotoListClickListener(new PhotoListFragment.OnItemPhotoListClickListener() {
                @Override
                public void onItemPhotoListClick(int directoryIndex) {
                    updateFragment(PhotoGridFragment.newInstance(directoryIndex, selectedPhotos), FRAGMENT_TAG_GRID, false, true);
                }
            });
        } else {
            updateViewVisible(FRAGMENT_TAG_PREVIEW);
        }
    }

    public void setCheckIconStatus(boolean isClick, String currentPhoto) {
        if (currentFragment instanceof PhotoPreviewFragment) {
            if (isClick) {
                updateSelectedPhotos(checkIcon, !checkIcon.isSelected(), currentPhoto);
            } else {
                checkIcon.setSelected(selectedPhotos.contains(currentPhoto));
            }
        }
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
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackCount = fm.getBackStackEntryCount();
        if (fm.findFragmentByTag(FRAGMENT_TAG_GRID) != null) {
            if (backStackCount == 0) {
                updateFragment(new PhotoListFragment(), FRAGMENT_TAG_LIST, false, false);
            } else if (fm.findFragmentByTag(FRAGMENT_TAG_PREVIEW) != null) { // 从预览页面返回时更新View
                super.onBackPressed();
                updateViewVisible(FRAGMENT_TAG_GRID);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }

        List<Fragment> fragmentList = fm.getFragments();
        if (fragmentList != null && fragmentList.size() > 0) {
            String tag = fragmentList.get(fragmentList.size() - 1).getTag();
            updateViewVisible(tag);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectListener = null;
    }
}
