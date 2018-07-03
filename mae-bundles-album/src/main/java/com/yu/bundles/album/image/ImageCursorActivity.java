package com.yu.bundles.album.image;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yu.bundles.album.AlbumBaseActivity;
import com.yu.bundles.album.ConfigBuilder;
import com.yu.bundles.album.R;
import com.yu.bundles.album.album.AlbumCursorBridge;
import com.yu.bundles.album.album.AlbumCursorFragment;
import com.yu.bundles.album.entity.Album;
import com.yu.bundles.album.entity.AlbumInfo;
import com.yu.bundles.album.model.AlbumMediaModelImpl;
import com.yu.bundles.album.presenter.AlbumPresenter;
import com.yu.bundles.album.presenter.AlbumPresenterImpl;
import com.yu.bundles.album.presenter.AlbumView;
import com.yu.bundles.album.utils.ImageQueue;

import java.util.ArrayList;

/**
 * 选择图片界面
 * 相册选择 - 主界面
 */
public class ImageCursorActivity extends AlbumBaseActivity implements View.OnClickListener, AlbumView, AlbumCursorBridge {

    final String TAG = "ImageCursorActivity";

    public static final String EXTRA_RESULT_SELECTION_PATH = "extra_result_selection_path";

    private AlbumPresenter albumPresenter;
    /**
     * 当前选中相册
     */
    private Album mCurrAlbum;
    /**
     * 所有相册
     */
    private Cursor albumCursor;
    private ImageFragment mImageFragment;
    private View mShadow;

    private TextView mToolbarTitle;
    private AlbumCursorFragment mAlbumFragment;
    private TextView toolbarRightView;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mae_album_activity_image_select);
        initViews();

        if (savedInstanceState != null) {
            resetSaveInstanceState(savedInstanceState);
        }

        AlbumMediaModelImpl.cacheMap.clear();
        albumPresenter = new AlbumPresenterImpl(this);
        albumPresenter.startScanAlbum();

        // 刷新页面广播
        final IntentFilter filter = new IntentFilter(ConfigBuilder.SELECT_ACTION);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        albumPresenter.onDestroy();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);
        toolbarRightView = findViewById(R.id.toolbar_right);
        mShadow = findViewById(R.id.id_shadow);
        mShadow.setVisibility(View.GONE);
        mShadow.setOnClickListener(this);
        showMenuItem(ConfigBuilder.max > 1);

        // 设置toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);    // 不显示 toolbar title 区域
        mToolbarTitle.setText(R.string.mae_album_all);
        mToolbarTitle.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.toolbar_title) {
            showOrHideAlbum();
        } else if (id == R.id.id_shadow) {
            showOrHideAlbum();
        }
    }


    /**
     * hide or show Album list
     */
    private void showOrHideAlbum() {
        if (albumCursor == null) {
            return;
        }

        final View container = findViewById(R.id.album_fragment_container);
        container.clearAnimation();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.mae_album_top_slide_in, R.anim.mae_album_bottom_slide_out);
        if (mShadow.getVisibility() == View.GONE) {
            if (Build.VERSION.SDK_INT >= 16) {
                mShadow.animate().alpha(1.0f).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mShadow.setVisibility(View.VISIBLE);
                    }
                }).start();
            } else {
                mShadow.setVisibility(View.VISIBLE);
            }

            if (mAlbumFragment == null) {
                mAlbumFragment = AlbumCursorFragment.newInstance(albumCursor);
                fragmentTransaction.replace(R.id.album_fragment_container, mAlbumFragment, AlbumCursorFragment.class.getName());
            } else {
                fragmentTransaction.show(mAlbumFragment);
            }
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            // fragment 不支持hide动画；
            final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mae_album_bottom_slide_out);
            mShadow.animate().alpha(0.0f).setDuration(150).start();
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mShadow.setVisibility(View.GONE);
                    if (mAlbumFragment != null)
                        fragmentTransaction.hide(mAlbumFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            container.startAnimation(animation);
        }
    }

    @Override
    public void finish() {
        if (mShadow.getVisibility() == View.VISIBLE) {
            showOrHideAlbum();
        } else {
            super.finish();
        }
    }

    private void showImages(Album album) {
        mToolbarTitle.setText(album == null ? getString(R.string.mae_album_no_image) : album.mDisplayName);
        albumPresenter.startScanAlbumMedia(album);
    }

    @Override
    public void notifyImageData(String albumId) {
        if (albumId == null) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mImageFragment == null) {
            mImageFragment = ImageFragment.newInstance(albumId);
            fragmentTransaction.replace(R.id.fragment_container, mImageFragment);
        } else {
            mImageFragment.notifyDataChange(albumId);
        }
        fragmentTransaction.commitAllowingStateLoss();
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
    }

    //---------------------------------AlbumView（扫描本地图片）回调---------------------------------------
    @Override
    public void refreshAlbumData(ArrayList<AlbumInfo> albumData) {
    }

    @Override
    public void onAlbumLoad(Cursor cursor) {
        this.albumCursor = cursor;
        if (mAlbumFragment != null) {
            mAlbumFragment.swapAdapterCursor(cursor);
        }
        if (albumCursor != null && albumCursor.getCount() > 0) {
            albumCursor.moveToFirst();
            mCurrAlbum = Album.create(albumCursor);
            showImages(mCurrAlbum);
        } else {
            //TODO no items
        }
    }

    @Override
    public void onAlbumReset() {
        this.albumCursor = null;
        if (mAlbumFragment != null) {
            mAlbumFragment.swapAdapterCursor(null);
        }
        showImages(null);
    }

    @Override
    public void showMessage(@StringRes int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public LoaderManager getSupportLoaderManager() {
        return super.getSupportLoaderManager();
    }

    //--------------------------------- 系统状态处理 ------------------------------------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void resetSaveInstanceState(Bundle savedInstanceState) {
        // saveInstance的处理
        if (savedInstanceState != null) {
            mAlbumFragment = (AlbumCursorFragment) getSupportFragmentManager().findFragmentByTag(AlbumCursorFragment.class.getName());
            if (mAlbumFragment != null) {
                getSupportFragmentManager().beginTransaction().hide(mAlbumFragment).commitAllowingStateLoss();
            }
        }
    }

    //---------------------------------AlbumBridge（和相册列表界面之间的通信）回调---------------------------------------
    @Override
    public Album getCurrAlbum() {
        return mCurrAlbum;
    }

    @Override
    public void onAlbumClick(final Album info) {
        if (!mCurrAlbum.equals(info)) {
            mCurrAlbum = info;
            showOrHideAlbum();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showImages(info);
                }
            }, 200);
        }
    }

    //---------------------------------IReceiverListener(局部广播)回调---------------------------------
    /**
     * 刷新title广播接收者
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            resetCount();
        }
    };

    private void resetCount() {
        if(ConfigBuilder.max <= 1){
            return ;
        }
        ArrayList<String> info = ImageQueue.getSelectedImages();
        if (info == null || info.size() == 0) {
            toolbarRightView.setText(getString(R.string.mae_album_selected_ok, 0, ConfigBuilder.max));
            toolbarRightView.setAlpha(0.6f);
            toolbarRightView.setEnabled(false);
        } else {
            toolbarRightView.setEnabled(true);
            toolbarRightView.setAlpha(1f);
            toolbarRightView.setText(getString(R.string.mae_album_selected_ok, info.size(), ConfigBuilder.max));
        }
    }

    private void showMenuItem(boolean isShowMenuItem){
        toolbarRightView.setEnabled(isShowMenuItem);
        if(ConfigBuilder.max > 1){
            toolbarRightView.setVisibility(View.VISIBLE);
            toolbarRightView.setText(getString(R.string.mae_album_selected_ok, 0, ConfigBuilder.max));
        } else {
            toolbarRightView.setVisibility(View.GONE);
        }
        toolbarRightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> result = ImageQueue.getSelectedImages();
                if (result == null || result.size() == 0) {
                    return ;
                }

                // 喜欢用 回调
                if (ConfigBuilder.l != null) {
                    ConfigBuilder.l.onSelected(result);
                } else {
                    // 喜欢用 onActivityResult
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, ImageQueue.getSelectedImages());
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
    }
}
