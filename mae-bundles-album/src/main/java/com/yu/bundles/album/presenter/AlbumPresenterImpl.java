package com.yu.bundles.album.presenter;

import android.Manifest;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.yu.bundles.album.R;
import com.yu.bundles.album.entity.Album;
import com.yu.bundles.album.model.AlbumMediaModel;
import com.yu.bundles.album.model.AlbumMediaModelImpl;
import com.yu.bundles.album.model.AlbumModel;
import com.yu.bundles.album.model.AlbumModelImpl;
import com.yu.bundles.album.utils.ImageQueue;
import com.yu.bundles.monitorfragment.MAEMonitorFragment;
import com.yu.bundles.monitorfragment.MAEPermissionCallback;

import java.util.List;

/**
 * Created by liyu on 2017/9/28.
 */

public class AlbumPresenterImpl implements AlbumModelImpl.AlbumCallbacks, AlbumMediaModelImpl.AlbumMediaCallbacks, AlbumPresenter {
    private AlbumView mAlbumView;
    private AlbumModel albumModel;
    private AlbumMediaModel albumMediaModel;

    public AlbumPresenterImpl(@NonNull AlbumView mAlbumView) {
        this.mAlbumView = mAlbumView;
        albumModel = new AlbumModelImpl(mAlbumView.getActivity(), mAlbumView.getLoadManager(), this);
        albumMediaModel = new AlbumMediaModelImpl(mAlbumView.getActivity(), mAlbumView.getLoadManager(), this);
        ImageQueue.init();
    }

    @Override
    public void startScanAlbumMedia(Album album) {
        albumMediaModel.loadAlbumMediaData(album);
    }

    @Override
    public void startScanAlbum() {
        MAEMonitorFragment.getInstance(mAlbumView.getActivity()).requestPermission(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new MAEPermissionCallback() {
                    @Override
                    public void onPermissionApplySuccess() {
                        albumModel.loadAlbumData();
                    }

                    @Override
                    public void onPermissionApplyFailure(List<String> list, List<Boolean> list1) {
                        mAlbumView.showMessage(R.string.mae_album_no_permission);
                        mAlbumView.getActivity().finish();
                    }
        });
    }

    /*
    * 相册目录数据加载
    * */
    @Override
    public void onAlbumLoad(Cursor cursor) {
        mAlbumView.onAlbumLoad(cursor);
    }

    @Override
    public void onAlbumReset() {
        mAlbumView.onAlbumReset();
    }

    /*
    * 相册详细数据数据加载
    * */
    @Override
    public void onAlbumMediaLoad(String albumId) {
        mAlbumView.notifyImageData(albumId);
    }

    @Override
    public void onAlbumMediaReset() {
        mAlbumView.notifyImageData(null);
    }

    public void onDestroy() {
        ImageQueue.clearSelected();
        albumModel.onDestroy();
        albumMediaModel.onDestroy();
    }
}
