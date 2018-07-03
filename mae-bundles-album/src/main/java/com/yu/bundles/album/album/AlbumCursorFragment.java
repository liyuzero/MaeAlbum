package com.yu.bundles.album.album;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yu.bundles.album.R;
import com.yu.bundles.album.entity.Album;

/**
 * Created by liyu on 2017/9/28.
 */

public class AlbumCursorFragment extends Fragment implements AdapterView.OnItemClickListener {

    private AlbumCursorBridge bridge;
    /**
     * 相册目录列表
     */
    private ListView mFolderListView;
    private AlbumCursorAdapter mAlbumCursorAdapter;

    private Cursor cursor;

    public AlbumCursorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AlbumCursorBridge) {
            bridge = (AlbumCursorBridge) context;
        }
    }

    //SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。Fragment自身的Bug，v4的没有此问题
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (activity instanceof AlbumCursorBridge) {
                bridge = (AlbumCursorBridge) activity;
            }
        }
    }

    /**
     * @param cursor 相册目录数据指针
     */
    public static AlbumCursorFragment newInstance(Cursor cursor) {
        AlbumCursorFragment fragment = new AlbumCursorFragment();
        fragment.setCursor(cursor);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mae_album_fragment_album_directory, container, false);
        mFolderListView = rootView.findViewById(R.id.list_album);
        mAlbumCursorAdapter = new AlbumCursorAdapter(getActivity(), cursor, false);
        mFolderListView.setAdapter(mAlbumCursorAdapter);
        mFolderListView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mFolderListView) {
            mAlbumCursorAdapter.getCursor().moveToPosition(position);
            Album album = Album.create(mAlbumCursorAdapter.getCursor());
            if (bridge != null) {
                bridge.onAlbumClick(album);
            }
        }
    }

    public void setCursor(Cursor mCursor) {
        this.cursor = mCursor;
    }

    public void swapAdapterCursor(Cursor cursor) {
        mAlbumCursorAdapter.swapCursor(cursor);
    }

    public int getCount(){
        return mAlbumCursorAdapter.getCount();
    }
}
