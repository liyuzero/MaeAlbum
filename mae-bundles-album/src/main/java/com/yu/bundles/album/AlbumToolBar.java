package com.yu.bundles.album;

import android.support.v7.widget.Toolbar;

public abstract class AlbumToolBar implements AlbumToolbarListener{
    protected Toolbar toolbar;

    public AlbumToolBar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public Toolbar getToolbar(){
        return toolbar;
    }

}
