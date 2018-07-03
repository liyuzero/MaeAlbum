package com.yu.bundles.album.album;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yu.bundles.album.ConfigBuilder;
import com.yu.bundles.album.R;
import com.yu.bundles.album.entity.Album;

class AlbumCursorAdapter extends CursorAdapter {

    public AlbumCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.mae_album_album_directory_item, null)).rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        Album album = Album.create(cursor);
        ConfigBuilder.IMAGE_ENGINE.loadImg(view.getContext(), album.mCoverPath, holder.ivAlbumCover, true);
        holder.tvDirectoryName.setText(album.mDisplayName);
        holder.tvChildCount.setText(String.valueOf(album.mCount));
    }

    private static class ViewHolder {
        ImageView ivAlbumCover;
        TextView tvDirectoryName;
        TextView tvChildCount;
        View rootView;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            ivAlbumCover = rootView.findViewById(R.id.iv_album_cover);
            tvDirectoryName = rootView.findViewById(R.id.tv_directory_name);
            tvChildCount = rootView.findViewById(R.id.tv_child_count);
            rootView.setTag(this);
        }
    }
}
