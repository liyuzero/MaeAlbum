package com.yu.bundles.album.preview;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yu.bundles.album.AlbumBaseActivity;
import com.yu.bundles.album.ConfigBuilder;
import com.yu.bundles.album.R;
import com.yu.bundles.album.entity.ImageInfo;
import com.yu.bundles.album.image.ImageEngine;
import com.yu.bundles.album.model.AlbumMediaModelImpl;
import com.yu.bundles.album.photoview.OnPhotoTapListener;
import com.yu.bundles.album.photoview.PhotoView;
import com.yu.bundles.album.photoview.PhotoViewAttacher;
import com.yu.bundles.album.utils.ImageQueue;
import com.yu.bundles.album.utils.MethodUtils;

import java.util.ArrayList;


/**
 * 图片预览界面
 */
public class ImagePreviewActivity extends AlbumBaseActivity implements CompoundButton.OnCheckedChangeListener {

    /**
     * 相册目录ID
     */
    public final static String EXTRA_IMAGE_ALBUM_ID = "EXTRA_IMAGE_ALBUM_ID";
    /**
     * 进入预览界面时显示的图片
     */
    public final static String EXTRA_IMAGE_INFO = "ImageInfo";
    /**
     * 是否修改了选择的图片
     */
    public final static String EXTRA_CHANGE = "is_change";
    /**
     * 进入界面时显示的位置
     */
    public final static String EXTRA_IMAGE_POS = "image_pos";
    private ViewPager mPreviewViewPager;
    private PagerAdapter mPreviewPagerAdapter;
    private ViewPager.OnPageChangeListener mPreviewChangeListener;
    private CheckBox mImageSelectedBox;
    private boolean isChanged = false;

    /**
     * 所有图片的列表
     */
    private ArrayList<ImageInfo> mImages;
    /**
     * 刚进入页面显示的图片
     */
    private ImageInfo mCurrImage;
    private int mCurrPos;
    private Toolbar mToolbar;
    private View footerView;
    private View videoPlayBtn;
    private TextView topRightView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mae_album_activity_image_preview);
        mImages = AlbumMediaModelImpl.cacheMap.get(getIntent().getStringExtra(EXTRA_IMAGE_ALBUM_ID));
        if (mImages == null) {
            finish();
            return;
        }

        mCurrImage = getIntent().getParcelableExtra(EXTRA_IMAGE_INFO);
        mCurrPos = getIntent().getIntExtra(EXTRA_IMAGE_POS, -1);
        if (mCurrPos == -1) {
            mCurrPos = mImages.indexOf(mCurrImage);
        }
        initView();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        topRightView = findViewById(R.id.toolbar_right);
        footerView = findViewById(R.id.footer_view);
        mImageSelectedBox = findViewById(R.id.ckb_image_select);
        if (mCurrImage != null) {
            mImageSelectedBox.setChecked(mCurrImage.isSelected());
        }
        CompoundButtonCompat.setButtonTintList(mImageSelectedBox, MethodUtils.getCheckColorStateList(this));
        mImageSelectedBox.setOnCheckedChangeListener(this);
        mPreviewViewPager = findViewById(R.id.gallery_viewpager);
        mPreviewPagerAdapter = new PreviewPagerAdapter();
        mPreviewViewPager.setAdapter(mPreviewPagerAdapter);
        mPreviewViewPager.setCurrentItem(mCurrPos);
        mPreviewChangeListener = new PreviewChangeListener();
        mPreviewViewPager.addOnPageChangeListener(mPreviewChangeListener);

        // 设置toolbar
        setToolbarTitle(mCurrPos + 1);
        initTopRightView();

        findViewById(R.id.action_download).setVisibility(View.GONE);
        videoPlayBtn = findViewById(R.id.video_play_button);
        setVideo(mCurrImage);
    }

    private void initTopRightView(){
        setCount();
        topRightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setVideo(final ImageInfo imageInfo){
        if(imageInfo.isVideo()){
            videoPlayBtn.setVisibility(View.VISIBLE);
            videoPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(imageInfo.getContentUri(), "video/*");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(ImagePreviewActivity.this, R.string.mae_error_no_video_activity, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            videoPlayBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mImageSelectedBox) {
            int currentPosition = mPreviewViewPager.getCurrentItem();
            ImageInfo imageInfo = mImages.get(currentPosition);
            if (isChecked && ImageQueue.getSelectedImages().size() >= ConfigBuilder.max) {
                mImageSelectedBox.setChecked(false);
                if (ConfigBuilder.l != null) {
                    ConfigBuilder.l.onFull(ImageQueue.getSelectedImages(), imageInfo.getFullPath());
                }
                return;
            }

            // 类型检测
            if (!ConfigBuilder.checkType(buttonView.getContext().getContentResolver(), imageInfo.getContentUri())) {
                mImageSelectedBox.setChecked(false);
                Toast.makeText(getApplicationContext(), R.string.mae_album_not_allow_type, Toast.LENGTH_SHORT).show();
                return;
            }

            isChanged = true;
            if (isChecked) {
                ImageQueue.add(imageInfo.getFullPath());
            } else {
                ImageQueue.remove(imageInfo.getFullPath());
            }
            setCount();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId() || R.id.action_ok == item.getItemId()) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra(EXTRA_CHANGE, isChanged);
        setResult(RESULT_OK, data);
        super.finish();
    }

    private void setCount() {
        if (ImageQueue.getImageSize() > 0) {
            topRightView.setEnabled(true);
            topRightView.setAlpha(1f);
            topRightView.setText(getString(R.string.mae_album_selected_ok, ImageQueue.getImageSize(), ConfigBuilder.max));
        } else {
            topRightView.setText(getString(R.string.mae_album_selected_ok, 0, ConfigBuilder.max));
            topRightView.setAlpha(0.6f);
            topRightView.setEnabled(false);
        }
    }

    /**
     * 相册适配器
     */
    private class PreviewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            if (mImages == null) {
                return 0;
            }
            return mImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            ImageView galleryPhotoView = view.findViewById(R.id.iv_show_image);
            if (galleryPhotoView instanceof PhotoView) {
                ((PhotoView) galleryPhotoView).setScale(1.0f);//让图片在滑动过程中恢复回缩放操作前原图大小
            }
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View galleryItemView = View.inflate(ImagePreviewActivity.this, R.layout.mae_album_preview_image_item, null);
            final ImageInfo imageInfo = mImages.get(position);
            final ImageView galleryPhotoView = galleryItemView.findViewById(R.id.iv_show_image);
            final View progressBar = galleryItemView.findViewById(R.id.progressbar);
            progressBar.setVisibility(View.VISIBLE);
            ImageEngine.AlbumEngineLoadListener target = new ImageEngine.AlbumEngineLoadListener(){
                @Override
                public void onLoadComplete() {
                    progressBar.setVisibility(View.GONE);
                }
            };
            if(imageInfo.isGif()){
                ConfigBuilder.IMAGE_ENGINE.loadGifImg(ImagePreviewActivity.this, imageInfo.getFullPath(), galleryPhotoView, false, target);
            } else {
                ConfigBuilder.IMAGE_ENGINE.loadImg(ImagePreviewActivity.this, imageInfo.getFullPath(), galleryPhotoView, false, target);
            }

            // PhotoView click
            PhotoViewAttacher mAttacher = new PhotoViewAttacher(galleryPhotoView);
            mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView imageView, float v, float v1) {
                    toggleHideyBar();
                }
            });
            mAttacher.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(ConfigBuilder.onPreviewLongClickListener != null){
                        ConfigBuilder.onPreviewLongClickListener.onPreviewLongClick(imageInfo.getFullPath());
                    }
                    return true;
                }
            });

            container.addView(galleryItemView);
            return galleryItemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void setToolbarTitle(int pos) {
        getSupportActionBar().setTitle(String.format("%d/%d", pos, mImages.size()));
    }

    /**
     * 相册详情页面滑动监听
     */
    private class PreviewChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mImageSelectedBox.setOnCheckedChangeListener(null);//先反注册监听，避免重复更新选中的状态
            setPositionToTitle(position);
            ImageInfo imageInfo = mImages.get(position);
            setVideo(imageInfo);
            mImageSelectedBox.setChecked(imageInfo.isSelected());
            mImageSelectedBox.setOnCheckedChangeListener(ImagePreviewActivity.this);
            showBottomBar();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private void setPositionToTitle(int position) {
        if (mImages != null) {
            setToolbarTitle(position + 1);
        }
    }

    // ------------------------------- 全屏显示代码 与 底部操作条的隐藏显示 ----------------------------------------------
    public void toggleHideyBar() {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        if (getSupportActionBar() != null && getSupportActionBar().isShowing()) {
            hideActionBar();
            hideBottomBar();
        } else {
            showActionBar();
            showBottomBar();
        }
    }

    private void hideBottomBar() {
        footerView.animate().alpha(0.0f).start();
    }

    private void showBottomBar() {
        footerView.animate().alpha(1.0f).start();
    }

    protected void hideActionBar() {
        final ActionBar ab = getSupportActionBar();
        if (Build.VERSION.SDK_INT >= 16) {
            mToolbar.animate().translationY(-ab.getHeight()).setDuration(200L)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            ab.hide();
                        }
                    }).start();
        } else {
            ab.hide();
        }
    }

    protected void showActionBar() {
        ActionBar ab = getSupportActionBar();
        ab.show();
        mToolbar.animate().translationY(0).setDuration(200L).start();
    }
}
