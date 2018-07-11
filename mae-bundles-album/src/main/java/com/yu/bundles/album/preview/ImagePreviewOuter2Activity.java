package com.yu.bundles.album.preview;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.yu.bundles.album.AlbumBaseActivity;
import com.yu.bundles.album.ConfigBuilder;

import com.yu.bundles.album.R;
import com.yu.bundles.album.image.ImageEngine;
import com.yu.bundles.album.photoview.OnPhotoTapListener;
import com.yu.bundles.album.photoview.PhotoView;
import com.yu.bundles.album.photoview.PhotoViewAttacher;
import com.yu.bundles.album.utils.MethodUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 图片预览界面
 */
public class ImagePreviewOuter2Activity extends AlbumBaseActivity implements View.OnClickListener{
    /**
     * 所有图片
     */
    public final static String EXTRA_IMAGE_INFO_LIST = "ImageInfoList";
    /**
     * 进入界面时显示的位置
     */
    public final static String EXTRA_IMAGE_POS = "image_pos";
    /**
    * 是否显示下载icon
    * */
    public final static String EXTRA_IS_SHOW_DOWNLOAD_ICON = "EXTRA_IS_SHOW_DOWNLOAD_ICON";
    /**
     * 点击下载icon后是否显示下载确认对话框
     * */
    public final static String EXTRA_IS_SHOW_DOWNLOAD_SURE_DIALOG = "EXTRA_IS_SHOW_DOWNLOAD_SURE_DIALOG";
    /**
     * 下载完成后是否显示SnackBar
     * */
    public final static String EXTRA_IS_SHOW_SNACK_BAR = "EXTRA_IS_SHOW_SNACK_BAR";
    /**
     * 所有图片的列表
     */
    private ArrayList<Parcelable> mImages;
    /**
    * 图片下载成功标识
    * */
    private File[] downloadSuccessFlags;
    private int mCurrPos;
    private Toolbar mToolbar;
    private View imgDownloadView;
    private boolean isShowDownloadIcon;
    private boolean isShowDownloadSureDialog;
    private boolean isShowSnackBar;
    private int mainColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mae_album_activity_image_preview);
        //所有图片
        mImages = getIntent().getParcelableArrayListExtra(EXTRA_IMAGE_INFO_LIST);
        if (mImages == null) {
            finish();
            return;
        }
        downloadSuccessFlags = new File[mImages.size()];
        //当前显示的图片的url
        //当前显示的位置
        mCurrPos = getIntent().getIntExtra(EXTRA_IMAGE_POS, 0);
        isShowDownloadIcon = getIntent().getBooleanExtra(EXTRA_IS_SHOW_DOWNLOAD_ICON, true);
        isShowDownloadSureDialog = getIntent().getBooleanExtra(EXTRA_IS_SHOW_DOWNLOAD_SURE_DIALOG, false);
        isShowSnackBar = getIntent().getBooleanExtra(EXTRA_IS_SHOW_SNACK_BAR, false);
        initView();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        findViewById(R.id.footer_view).setVisibility(View.INVISIBLE);
        ViewPager mPreviewViewPager = findViewById(R.id.gallery_viewpager);
        PreviewPagerAdapter mPreviewPagerAdapter = new PreviewPagerAdapter();
        mPreviewViewPager.setAdapter(mPreviewPagerAdapter);
        mPreviewViewPager.setCurrentItem(mCurrPos);
        PreviewChangeListener mPreviewChangeListener = new PreviewChangeListener();
        mPreviewViewPager.addOnPageChangeListener(mPreviewChangeListener);

        // 设置toolbar
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setToolbarTitle(mCurrPos + 1);

        imgDownloadView = findViewById(R.id.action_download);
        if(isShowDownloadIcon){
            imgDownloadView.setOnClickListener(this);
        } else {
            imgDownloadView.setVisibility(View.GONE);
        }

        findViewById(R.id.toolbar_right).setVisibility(View.GONE);

        //获取attr的colorPrimary
        TypedValue typedValue = new  TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        mainColor = typedValue.data;
    }

    @Override
    public void onClick(View v) {
        if (R.id.action_download == v.getId()) {
            if(isShowDownloadSureDialog) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ImagePreviewOuter2Activity.this);
                builder.setMessage(getString(R.string.mae_save_picture_2_local));
                builder.setCancelable(false);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadImg();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            } else {
                downloadImg();
            }
        }
    }

    private void downloadImg(){
        final Object imgInfo = mImages.get(mCurrPos);
        if(downloadSuccessFlags[mCurrPos] != null){
            showInfoAfterDownload(downloadSuccessFlags[mCurrPos]);
            return;
        }
        MethodUtils.downloadImg(ImagePreviewOuter2Activity.this, imgInfo, ConfigBuilder.imgDownloadDirPath, new MethodUtils.OnDownloadListener() {
            @Override
            public void downloadComplete(boolean isSuccess, File file) {
                if(getApplicationContext() == null){
                    return ;
                }
                if(isSuccess){
                    MethodUtils.send2SystemImgs(getApplicationContext(), file);
                    imgDownloadView.setVisibility(View.GONE);
                    downloadSuccessFlags[mCurrPos] = file;
                    showInfoAfterDownload(file);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.mae_picture_save_failure), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showInfoAfterDownload(final File file){
        if(isShowSnackBar) {
            MethodUtils.showSnackBar(findViewById(android.R.id.content), getString(R.string.mae_save_picture_2_local),
                    android.R.color.black, getString(R.string.mae_bundles_album_open), android.R.color.darker_gray,
                    android.R.color.white, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri fileUri;
                            if(Build.VERSION.SDK_INT >= 24){
                                fileUri = FileProvider.getUriForFile(getApplicationContext(), getApplication().getPackageName() +".mae.bundles.album.fileprovider", file);
                            } else {
                                fileUri = Uri.fromFile(file);
                            }
                            intent.setDataAndType(fileUri, "image/*");
                            startActivity(intent);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.mae_picture_has_been_saved) + ConfigBuilder.imgDownloadDirPath, Toast.LENGTH_SHORT).show();
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
            View galleryItemView = View.inflate(getApplicationContext(), R.layout.mae_album_preview_image_item, null);
            final Object imageInfo = mImages.get(position);
            PhotoView galleryPhotoView = galleryItemView.findViewById(R.id.iv_show_image);
            final View progressBar = galleryItemView.findViewById(R.id.progressbar);
            progressBar.setVisibility(View.VISIBLE);
            ImageEngine.AlbumEngineLoadListener target = new ImageEngine.AlbumEngineLoadListener(){
                @Override
                public void onLoadComplete() {
                    progressBar.setVisibility(View.GONE);
                }
            };
            ConfigBuilder.IMAGE_ENGINE.loadImg(ImagePreviewOuter2Activity.this, imageInfo, galleryPhotoView, false, target);

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
                        ConfigBuilder.onPreviewLongClickListener.onPreviewLongClick(imageInfo);
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
            mCurrPos = position;
            imgDownloadView.setVisibility(downloadSuccessFlags[position] != null? View.GONE: View.VISIBLE);
            setPositionToTitle(position);
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
        } else {
            showActionBar();
        }
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
