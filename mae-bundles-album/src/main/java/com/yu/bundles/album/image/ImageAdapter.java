package com.yu.bundles.album.image;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yu.bundles.album.ConfigBuilder;
import com.yu.bundles.album.R;
import com.yu.bundles.album.entity.Album;
import com.yu.bundles.album.entity.ImageInfo;
import com.yu.bundles.album.utils.ImageQueue;
import com.yu.bundles.album.utils.MethodUtils;
import com.yu.bundles.album.widget.StateMaskView;

import java.util.List;

import static com.yu.bundles.album.ConfigBuilder.SELECT_ACTION;

class ImageAdapter extends BaseAdapter {
    static final int TAG_ID = 0x7f0b005f;
    static final int TAG_ID2 = 0x7f0b006f;
    private final int TAG_MASK = 0x7fb105f;
    private final int TYPE_IMG = 0;
    private final int TYPE_CAPTURE = 1;
    private String curAlbumID;
    private List<ImageInfo> mImageInfoList;
    private View.OnClickListener mImageItemClickListener;
    private CheckBox.OnCheckedChangeListener mImageOnSelectedListener;
    private OnCaptureClickListener onCaptureClickListener;

    ImageAdapter(List<ImageInfo> imageInfoList, String curAlbumID) {
        this.mImageInfoList = imageInfoList;
        this.curAlbumID = curAlbumID;
    }

    @Override
    public int getCount() {
        return mImageInfoList == null ? 0 : mImageInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return mImageInfoList.get(position).getId() == -1? TYPE_CAPTURE: TYPE_IMG;
    }

    @Override
    public int getViewTypeCount() {
        return ConfigBuilder.IS_SHOW_CAPTURE && curAlbumID.equals(Album.ALBUM_ID_ALL)? 2: 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(getItemViewType(position) == TYPE_CAPTURE){
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mae_album_image_grid_item_capture, parent, false);
                new CaptureHolder(convertView);
            }
        } else {
            ImageHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mae_album_album_grid_item, parent, false);
                holder = new ImageHolder(convertView);
            } else {
                holder = (ImageHolder) convertView.getTag();
                resetConvertView(holder);
            }
            holder.setData(convertView, position);
        }
        return convertView;
    }

    /**
     * remove listener and set status
     *
     * @param info
     */
    private void setSelectedImage(ImageHolder holder, ImageInfo info) {
        holder.imageSelectedCheckBox.setOnCheckedChangeListener(null);
        holder.imageSelectedCheckBox.setChecked(info.isSelected());
        holder.maskView.setState(info.isSelected() ? StateMaskView.STATE_FLAG_CHECKED : StateMaskView.STATE_NONE);
    }

    private void checkboxEvent(final ImageHolder holder) {
        // checkbox点击事件
        if (mImageOnSelectedListener == null) {
            mImageOnSelectedListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    ImageInfo imageInfo = (ImageInfo) compoundButton.getTag();
                    if (canSelected(compoundButton, imageInfo, b)) {
                        if (b) {
                            ImageQueue.add(imageInfo.getFullPath());
                        } else {
                            ImageQueue.remove(imageInfo.getFullPath());
                        }
                        Intent i = new Intent(SELECT_ACTION);
                        LocalBroadcastManager.getInstance(compoundButton.getContext()).sendBroadcast(i);

                        StateMaskView maskView = (StateMaskView) compoundButton.getTag(TAG_MASK);
                        if(maskView != null) {
                            maskView.setState(b ? StateMaskView.STATE_FLAG_CHECKED : StateMaskView.STATE_NONE);
                        }
                    }
                }
            };
        }
        holder.imageSelectedCheckBox.setOnCheckedChangeListener(mImageOnSelectedListener);
    }

    /**
     * 检测类型与数量
     *
     * @param compoundButton
     * @param imageInfo
     */
    private boolean canSelected(CompoundButton compoundButton, ImageInfo imageInfo, boolean b) {
        boolean canSelected = true;

        // count check
        if (b && ImageQueue.getSelectedImages().size() >= ConfigBuilder.max) {
            canSelected = false;
            if (ConfigBuilder.l != null)
                ConfigBuilder.l.onFull(ImageQueue.getSelectedImages(), imageInfo.getFullPath());
        }

        // 类型不对
        if (!ConfigBuilder.checkType(compoundButton.getContext().getContentResolver(), imageInfo.getContentUri())) {
            canSelected = false;
        }

        // 设置事件
        if (!canSelected) {
            compoundButton.setOnCheckedChangeListener(null);
            compoundButton.setChecked(false);
            compoundButton.setOnCheckedChangeListener(mImageOnSelectedListener);
        }

        return canSelected;
    }

    private void itemViewEvent(ImageHolder holder) {
        // item 点击事件，预览图片
        holder.albumItem.setOnClickListener(mImageItemClickListener);
    }

    public void setImageItemClickListener(View.OnClickListener mImageItemClickListener) {
        this.mImageItemClickListener = mImageItemClickListener;
    }

    /**
     * 重置缓存视图的初始状态
     */
    private void resetConvertView(ImageHolder viewHolder) {
        viewHolder.imageSelectedCheckBox.setOnCheckedChangeListener(null);
        viewHolder.imageSelectedCheckBox.setChecked(false);
        viewHolder.maskView.clearStatus();
    }

    private class ImageHolder {
        ImageView albumItem;
        CheckBox imageSelectedCheckBox;
        View gifIconView;
        StateMaskView maskView;
        TextView videoTime;

        public ImageHolder(View itemView) {
            int gridItemSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, itemView.getContext().getResources().getDisplayMetrics());
            int gridEdgeLength = (itemView.getContext().getResources().getDisplayMetrics().widthPixels - gridItemSpacing * 2) / ConfigBuilder.column;
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(gridEdgeLength, gridEdgeLength);
            itemView.setLayoutParams(layoutParams);

            videoTime = itemView.findViewById(R.id.album_video_time);
            gifIconView = itemView.findViewById(R.id.iv_album_gif);
            albumItem = itemView.findViewById(R.id.iv_album_item);
            imageSelectedCheckBox = itemView.findViewById(R.id.ckb_image_select);

            CompoundButtonCompat.setButtonTintList(imageSelectedCheckBox, MethodUtils.getCheckColorStateList(itemView.getContext()));
            maskView = itemView.findViewById(R.id.iv_mask);
            itemView.setTag(this);
        }

        void setData(View convertView, int position){
            //最大数量<=1时，不显示checkbox
            if(ConfigBuilder.max <= 1){
                imageSelectedCheckBox.setVisibility(View.GONE);
            }

            //默认加载非动图，加快加载速度
            ImageInfo imageInfo = mImageInfoList.get(position);
            gifIconView.setVisibility(imageInfo.isGif() ? View.VISIBLE : View.GONE);
            ConfigBuilder.IMAGE_ENGINE.loadImg(convertView.getContext(), imageInfo.getFullPath(), albumItem, true);

            //video时间显示
            videoTime.setVisibility(imageInfo.isVideo()? View.VISIBLE: View.GONE);
            if(imageInfo.isVideo()){
                long ss = imageInfo.getDuration() / 1000;
                videoTime.setText((ss/60 < 10? "0" + ss/60: ss/10) + ":" + (ss % 60 >= 10? ss % 60: "0" + ss % 60));
            }

            // 设置checkbox状态
            setSelectedImage(this, imageInfo);

            // checkbox 设置 tag 数据
            imageSelectedCheckBox.setTag(imageInfo);
            imageSelectedCheckBox.setTag(TAG_ID, position);
            imageSelectedCheckBox.setTag(TAG_MASK, maskView);

            // item view 设置 tag 数据 与 event
            albumItem.setTag(TAG_ID2, imageInfo);
            albumItem.setTag(TAG_ID, position);

            // 事件设置
            checkboxEvent(this);
            itemViewEvent(this);
        }
    }

    private class CaptureHolder implements View.OnClickListener{

        public CaptureHolder(View itemView) {
            int gridItemSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, itemView.getContext().getResources().getDisplayMetrics());
            int gridEdgeLength = (itemView.getContext().getResources().getDisplayMetrics().widthPixels - gridItemSpacing * 2) / ConfigBuilder.column;
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(gridEdgeLength, gridEdgeLength);
            itemView.setLayoutParams(layoutParams);
            itemView.setOnClickListener(this);
            itemView.setTag(this);
        }

        @Override
        public void onClick(View v) {
            if(onCaptureClickListener != null){
                onCaptureClickListener.onCaptureClick();
            }
        }
    }

    public void setOnCaptureClickListener(OnCaptureClickListener onCaptureClickListener) {
        this.onCaptureClickListener = onCaptureClickListener;
    }

    interface OnPreviewListener {
        void onPreview(ImageInfo imageInfo, int pos);
    }

    interface OnCaptureClickListener {
        void onCaptureClick();
    }
}
