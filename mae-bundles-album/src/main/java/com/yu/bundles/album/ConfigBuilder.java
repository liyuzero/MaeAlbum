package com.yu.bundles.album;


import android.content.ContentResolver;
import android.net.Uri;

import com.yu.bundles.album.image.ImageEngine;
import com.yu.bundles.album.utils.MimeType;

import java.util.List;
import java.util.Set;

public class ConfigBuilder {

    /**
     * 选择图片事件
     */
    public static final String SELECT_ACTION = "com.yu.bundles.album_select_photo";

    /**
     * 最大可选数
     */
    public static int max;

    /*
    * 文件大小限制
    * 值为-1，表示不做限制
    * */
    public static int MIN_FILE_SIZE;
    public static int MAX_FILE_SIZE;

    public static MimeType[] mimeTypes;

    /*
    * 可选文件类型
    * */
    public enum FILE_TYPE {
        IMAGE, VIDEO, IMAGE_AND_VIDEO
    }

    /*
    * 图片文件具体类型
    * */
    public static FILE_TYPE fileType = FILE_TYPE.IMAGE;

    /*
    * 全部图片浏览时，是否显示拍照Item
    * */
    public static boolean IS_SHOW_CAPTURE = false;

    /*
    * 当前图像显示引擎
    * */
    public static ImageEngine IMAGE_ENGINE;

    /*
    * NavigationBar的icon资源
    * */
    public static Integer navigationIcon;

    /**
     * 选中回调
     */
    public static AlbumListener l;

    // 列数
    public static int column;

    public static String cacheDir;

    /**
    * 存储图片的目录地址
    * */
    public static String imgDownloadDirPath;

    /*
    * 存储视频文件的地址
    * */
    public static String videoDownloadDirPath;

    /*
    *  存储照片地址
    * */
    public static String photoSavedDirPath;

    public static Set<MimeType> mimeTypeSet;

    public static int styleRes = R.style.Mae_Album_Base_theme;

    /*
    * 长按监听器
    * */
    public static OnPreviewLongClickListener onPreviewLongClickListener;

    static void setListener(AlbumListener listener) {
        if (listener != null) {
            l = new LW(listener);
        }
    }

    private static class LW implements AlbumListener {
        private AlbumListener l;

        LW(AlbumListener l) {
            this.l = l;
        }

        @Override
        public void onSelected(List<String> ps) {
            if (l != null)
                l.onSelected(ps);
        }

        @Override
        public void onFull(List<String> ps, String path) {
            if (l != null)
                l.onFull(ps, path);
        }
    }

    /**
     * 检测类型是否合法,为null类型合法
     *
     * @return
     */
    public static boolean checkType(ContentResolver resolver, Uri uri) {
        if (ConfigBuilder.mimeTypeSet != null) {
            for (MimeType mineType : ConfigBuilder.mimeTypeSet) {
                if (mineType.checkType(resolver, uri)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
