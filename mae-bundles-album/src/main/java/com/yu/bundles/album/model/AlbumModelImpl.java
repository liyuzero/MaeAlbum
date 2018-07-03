package com.yu.bundles.album.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

/**
 * Created by liyu on 2017/9/28.
 */
public class AlbumModelImpl implements LoaderManager.LoaderCallbacks<Cursor>, AlbumModel {
    private static final int LOADER_ID = 1;
    private Context mContext;
    private LoaderManager mLoaderManager;
    private AlbumCallbacks mCallbacks;

    public AlbumModelImpl(@NonNull Context mContext, @NonNull LoaderManager mLoaderManager, @NonNull AlbumCallbacks mCallbacks) {
        this.mCallbacks = mCallbacks;
        this.mLoaderManager = mLoaderManager;
        this.mContext = mContext;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return AlbumLoader.newInstance(mContext);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCallbacks.onAlbumLoad(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCallbacks.onAlbumReset();
    }

    @Override
    public void loadAlbumData() {
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID);
        mCallbacks = null;
    }

    public interface AlbumCallbacks {
        void onAlbumLoad(Cursor cursor);

        void onAlbumReset();
    }
}
