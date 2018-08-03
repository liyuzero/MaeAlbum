package com.yu.bundles.album.image;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.yu.bundles.album.ConfigBuilder;
import com.yu.bundles.album.R;
import com.yu.bundles.album.entity.ImageInfo;
import com.yu.bundles.album.model.AlbumMediaModelImpl;
import com.yu.bundles.album.preview.ImagePreviewActivity;
import com.yu.bundles.album.utils.ImageQueue;
import com.yu.bundles.album.utils.MethodUtils;
import com.yu.bundles.monitorfragment.MAEActivityResultListener;
import com.yu.bundles.monitorfragment.MAEMonitorFragment;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.yu.bundles.album.ConfigBuilder.SELECT_ACTION;
import static com.yu.bundles.album.image.ImageAdapter.TAG_ID;
import static com.yu.bundles.album.image.ImageAdapter.TAG_ID2;
import static com.yu.bundles.album.image.ImageCursorActivity.EXTRA_RESULT_SELECTION_PATH;


/**
 * 图片列表界面
 */
public class ImageFragment extends Fragment implements ImageAdapter.OnPreviewListener, View.OnClickListener, ImageAdapter.OnCaptureClickListener {
    private String TAKE_PHOTO_ADD;
    private static final String ARG_PARAM1 = "param1";
    private static final int TAKE_PHOTO = 12;
    private String albumID;
    private ArrayList<ImageInfo> mImages;//要显示的image
    private ImageAdapter mAlbumGridViewAdapter;
    private GridView gridView;

    public static ImageFragment newInstance(String albumId) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, albumId);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumID = getArguments().getString(ARG_PARAM1);
            mImages = AlbumMediaModelImpl.cacheMap.get(albumID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mae_album_fragment_image, container, false);
        gridView = rootView.findViewById(R.id.gv_album);
        gridView.setNumColumns(ConfigBuilder.column);
        setImageAdapter();
        return rootView;
    }

    public void notifyDataChange(String albumId) {
        this.albumID = albumId;
        if (albumId == null) {
            mAlbumGridViewAdapter.notifyDataSetChanged();
        } else {
            this.mImages = AlbumMediaModelImpl.cacheMap.get(albumId);
            mAlbumGridViewAdapter = null;   // 设置old为null
            setImageAdapter();
        }
    }

    private void setImageAdapter(){
        mAlbumGridViewAdapter = new ImageAdapter(mImages, albumID);
        mAlbumGridViewAdapter.setImageItemClickListener(this);
        mAlbumGridViewAdapter.setOnCaptureClickListener(this);
        gridView.setAdapter(mAlbumGridViewAdapter);
    }

    //图片点击事件
    @Override
    public void onClick(View v) {
        ImageInfo imageInfo = (ImageInfo) v.getTag(TAG_ID2);
        if(ConfigBuilder.max > 1){
            int pos = (int) v.getTag(TAG_ID);
            onPreview(imageInfo, pos);
        } else {
            ImageQueue.clearSelected();
            ImageQueue.add(imageInfo.getFullPath());
            finishActivity();
        }
    }

    //点击了照相Item
    @Override
    public void onCaptureClick() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        TAKE_PHOTO_ADD = ConfigBuilder.photoSavedDirPath + "/" + System.currentTimeMillis() + ".jpg";

        final File file = new File(TAKE_PHOTO_ADD);

        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".yu.bundles.album.fileprovider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, data);

        MAEMonitorFragment.getInstance(getActivity()).startActivityForResult(intent, TAKE_PHOTO, new MAEActivityResultListener() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                if(requestCode == TAKE_PHOTO && TAKE_PHOTO_ADD != null){
                    if(!file.exists()){
                        return;
                    }
                    MethodUtils.send2SystemImgs(getActivity(), file);
                    ImageQueue.clearSelected();
                    ImageQueue.add(TAKE_PHOTO_ADD);
                    finishActivity();
                }
            }
        });
    }

    private void finishActivity(){
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, ImageQueue.getSelectedImages());
        getActivity().setResult(RESULT_OK, intent);
        getActivity().finish();
    }

    //——————————————————————————————预览回调——————————————————————————————

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            boolean extra = data.getBooleanExtra(ImagePreviewActivity.EXTRA_CHANGE, false);
            if (extra) {
                mAlbumGridViewAdapter.notifyDataSetChanged();
                // 发送广播
                if (getActivity() != null) {
                    Intent i = new Intent(SELECT_ACTION);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(i);
                }
            }
        }
    }

    @Override
    public void onPreview(ImageInfo imageInfo, int pos) {
        Intent i = new Intent(getContext(), ImagePreviewActivity.class);
        i.putExtra(ImagePreviewActivity.EXTRA_IMAGE_INFO, imageInfo);
        i.putExtra(ImagePreviewActivity.EXTRA_IMAGE_POS, pos);
        i.putExtra(ImagePreviewActivity.EXTRA_IMAGE_ALBUM_ID, albumID);
        startActivityForResult(i, 100);
    }

    //-----------------------------------------------IReceiverListener回调-----------------------------------------------
}
