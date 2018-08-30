package com.yu.bundles.album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.yu.bundles.album.image.ImageCursorActivity;
import com.yu.bundles.album.image.ImageEngine;
import com.yu.bundles.album.preview.ImagePreviewOuter2Activity;
import com.yu.bundles.album.utils.MimeType;
import com.yu.bundles.monitorfragment.MAEActivityResultListener;
import com.yu.bundles.monitorfragment.MAEMonitorFragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 对外提供工具类调用，参考知乎相册
 */
public final class MaeAlbum {

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private int maxFileSize = -1;

    private int minFileSize = 10;

    private MimeType[] mimeTypes;

    /**
     * 文件总体类型，含三种：图片、视频、视频和图片
     */
    private ConfigBuilder.FILE_TYPE fileType = ConfigBuilder.FILE_TYPE.IMAGE;

    private int maxSize = 9;
    /**
     * 列数
     */
    private int column = 3;

    /*
    * 是否显示拍照
    * */
    private boolean isShowCapture = false;

    /*
    * 照相机图片存储路径
    * */
    private String photoSavedDirPath = ConfigBuilder.photoSavedDirPath;

    private MaeAlbum(Activity activity) {
        this(activity, null);
    }

    private MaeAlbum(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private MaeAlbum(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }


    //-------------------- 对外提供的方法 -----------------------------

    /**
     * Start  from an Activity.
     * <p>
     * This Activity's {@link Activity#onActivityResult(int, int, Intent)} will be called when user
     * finishes selecting.
     *
     * @param activity Activity instance.
     * @return Matisse instance.
     */
    public static MaeAlbum from(Activity activity) {
        return new MaeAlbum(activity);
    }

    /**
     * Start Matisse from a Fragment.
     * <p>
     * This Fragment's {@link Fragment#onActivityResult(int, int, Intent)} will be called when user
     * finishes selecting.
     *
     * @param fragment Fragment instance.
     * @return Matisse instance.
     */
    public static MaeAlbum from(Fragment fragment) {
        return new MaeAlbum(fragment);
    }

    public static void setStyle(int styleRes){
        ConfigBuilder.styleRes = styleRes;
    }

    /**
     * maxSize
     *
     * @param maxSize
     * @return
     */
    public MaeAlbum maxSize(int maxSize) {
        if (maxSize < 1) throw new IllegalArgumentException("maxSize cannot be less than 1");
        this.maxSize = maxSize;
        return this;
    }

    /**
     * specify column size
     *
     * @param column
     * @return
     */
    public MaeAlbum column(int column) {
        if (column < 1) throw new IllegalArgumentException("column cannot be less than 1");
        this.column = column;
        return this;
    }

    public MaeAlbum choose(Set<MimeType> mimeTypes) {
        ConfigBuilder.mimeTypeSet = mimeTypes;
        return this;
    }

    public static void setImageEngine(ImageEngine imageEngine) {
        ConfigBuilder.IMAGE_ENGINE = imageEngine;
    }

    public MaeAlbum setIsShowCapture(boolean isShowCapture, String photoSavedDirPath) {
        this.isShowCapture = isShowCapture;
        this.photoSavedDirPath = photoSavedDirPath;
        return this;
    }

    public MaeAlbum setIsShowCapture(boolean isShowCapture) {
        this.isShowCapture = isShowCapture;
        return this;
    }

    /*
     * 默认仅显示IMAGE，目前VIDEO显示形式与图片一致。
     * 后期有需求可开放该方法
     * */
    public MaeAlbum fileType(ConfigBuilder.FILE_TYPE fileType) {
        this.fileType = fileType;
        return this;
    }

    /*
     * 文件过滤方法
     * @param mimeTypesFilter：需要被过滤的文件类型
     * */
    public MaeAlbum mimeTypeFilter(MimeType[] mimeTypes) {
        this.mimeTypes = mimeTypes;
        return this;
    }

    /*
     * 尺寸单位为：KB
     * @param maxSize：最大尺寸，-1表示不做限制
     * @param minSize：最小尺寸，-1表示不做限制
     * */
    public MaeAlbum fileSizeLimit(int minSize, int maxSize) {
        this.maxFileSize = maxSize * 1024;
        this.minFileSize = minSize * 1024;
        return this;
    }

    public static void setImageDir(String dir){
        ConfigBuilder.cacheDir = dir;
    }

    public static void setNavigationIcon(int navigationIcon){
        ConfigBuilder.navigationIcon = navigationIcon;
    }

    public static void setOnPreviewLongClickListener (OnPreviewLongClickListener onPreviewLongClickListener){
        ConfigBuilder.onPreviewLongClickListener = onPreviewLongClickListener;
    }

    private void initConfig(Activity activity) {
        // 设置 内部config
        ConfigBuilder.column = this.column;
        ConfigBuilder.max = this.maxSize;
        ConfigBuilder.MIN_FILE_SIZE = this.minFileSize;
        ConfigBuilder.MAX_FILE_SIZE = this.maxFileSize;
        ConfigBuilder.mimeTypes = this.mimeTypes;
        ConfigBuilder.fileType = this.fileType;
        ConfigBuilder.IS_SHOW_CAPTURE = this.isShowCapture;
        ConfigBuilder.photoSavedDirPath = this.photoSavedDirPath;

        initStaticConfig(activity);
    }

    public void forResult(int requestCode) {
        Activity activity = mContext.get();
        if(activity == null && mFragment.get() != null){
            activity = mFragment.get().getActivity();
        }
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, ImageCursorActivity.class);
        initConfig(activity);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 启动方法
     *
     */
    public void forResult(final AlbumListener albumListener) {
        Activity activity = mContext.get();
        if(activity == null && mFragment.get() != null){
            activity = mFragment.get().getActivity();
        }
        if (activity == null || albumListener == null) {
            return;
        }
        ConfigBuilder.setListener(albumListener);

        //Intent intent = new Intent(activity, ImageActivity.class);
        Intent intent = new Intent(activity, ImageCursorActivity.class);
        initConfig(activity);

        MAEMonitorFragment.getInstance(activity).startActivityForResult(intent, 325467, new MAEActivityResultListener() {
            @Override
            public void onActivityResult(int i, int i1, Intent intent) {
                if(i1 == Activity.RESULT_OK && intent != null){
                    List<String> list = intent.getStringArrayListExtra(ImageCursorActivity.EXTRA_RESULT_SELECTION_PATH);
                    albumListener.onSelected(list);
                }
            }
        });
    }

    // --------------------- 外界用的图片预览方法
    public static void startPreview(Context context, ArrayList urls, int currPos) {
        startPreview(context, urls, currPos, true, false, true);
    }

    public static void startPreview(Context context, ArrayList urls, int currPos, boolean isShowDownloadIcon,
                                    boolean isShowDownloadSureDialog, boolean isShowSnackBar) {
        if (context == null) {
            return;
        }
        if(urls == null){
            return;
        }
        if(urls.size() <= 0){
            return;
        }
        if(urls.size() > 100){
            throw new IllegalArgumentException("The number of urls must be smaller than 100");
        }

        initStaticConfig(context);

        Intent i = new Intent(context, ImagePreviewOuter2Activity.class);
        if(urls.get(0) instanceof Parcelable){
            i.putParcelableArrayListExtra(ImagePreviewOuter2Activity.EXTRA_IMAGE_INFO_LIST, (ArrayList<Parcelable>) urls);
        } else if(urls.get(0) instanceof String){
            i.putStringArrayListExtra(ImagePreviewOuter2Activity.EXTRA_IMAGE_INFO_LIST, (ArrayList<String>) urls);
        } else {
            return;
        }
        i.putExtra(ImagePreviewOuter2Activity.EXTRA_IMAGE_POS, currPos);
        i.putExtra(ImagePreviewOuter2Activity.EXTRA_IS_SHOW_DOWNLOAD_SURE_DIALOG, isShowDownloadSureDialog);
        i.putExtra(ImagePreviewOuter2Activity.EXTRA_IS_SHOW_DOWNLOAD_ICON, isShowDownloadIcon);
        i.putExtra(ImagePreviewOuter2Activity.EXTRA_IS_SHOW_SNACK_BAR, isShowSnackBar);
        context.startActivity(i);
    }

    private static void initStaticConfig(Context context){
        ConfigBuilder.cacheDir = ConfigBuilder.cacheDir == null? context.getExternalCacheDir().getAbsolutePath()
                + "/MEChelsea": ConfigBuilder.cacheDir;

        ConfigBuilder.imgDownloadDirPath = ConfigBuilder.cacheDir + "/MEChelseaImages";
        ConfigBuilder.videoDownloadDirPath = ConfigBuilder.cacheDir + "/MEChelseaVideo";
        ConfigBuilder.photoSavedDirPath = ConfigBuilder.cacheDir + "/MEChelseaPhotos";

        new File(ConfigBuilder.cacheDir).mkdirs();
        new File(ConfigBuilder.imgDownloadDirPath).mkdirs();
        new File(ConfigBuilder.videoDownloadDirPath).mkdirs();
        new File(ConfigBuilder.photoSavedDirPath).mkdirs();

        if(ConfigBuilder.IMAGE_ENGINE == null){
            throw new NullPointerException("You must set ImageEngine !!!");
        }
    }

    // ------------ onActivityResult 收集数据方法 --------------------------
    public static List<String> obtainPathResult(Intent data) {
        return data.getStringArrayListExtra(ImageCursorActivity.EXTRA_RESULT_SELECTION_PATH);
    }
}
