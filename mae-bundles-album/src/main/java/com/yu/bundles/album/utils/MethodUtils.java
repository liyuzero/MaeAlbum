package com.yu.bundles.album.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.AttrRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yu.bundles.album.ConfigBuilder;
import com.yu.bundles.album.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liyu on 2017/10/19.
 */

public class MethodUtils {

    public static ColorStateList getCheckColorStateList(Context context){
        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {getColorByAttrId(context, R.attr.mae_album_checkedColorRes), getColorByAttrId(context, R.attr.mae_album_unCheckedColorRes)};
        return new ColorStateList(states, colors);
    }

    public static boolean isNull(Object[] objects){
        return objects == null || objects.length == 0;
    }

    //将图片插入至系统相册
    public static void send2SystemImgs(Context context, File file){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }

    public static void downloadImg(final Activity activity, final String url, final String dirPath, final OnDownloadListener onDownloadListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final File file = ConfigBuilder.IMAGE_ENGINE.downloadFile(activity, url);
                    //Glide下载图片失败，会在本地生成一个无效文件，其对应的bitmap为空，所以此时需删除无效文件，
                    // 并通过抛出异常，处理下载失败逻辑
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if(bitmap == null){
                        file.delete();
                        throw new IOException("The image is invalid");
                    } else {
                        bitmap.recycle();
                    }
                    final File outputFile = new File(dirPath + "/" + System.currentTimeMillis()+".jpg");
                    moveFile(file, outputFile);
                    if(activity != null && !activity.isFinishing()){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onDownloadListener.downloadComplete(true, outputFile);
                            }
                        });
                    }
                } catch (Exception e){
                    if(activity != null && !activity.isFinishing()){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onDownloadListener.downloadComplete(false, null);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public static final void moveFile(File src, File des) throws IOException{
        des.mkdirs();
        if(des.exists()){
            des.delete();
        }
        des.createNewFile();
        FileInputStream fileInputStream = new FileInputStream(src);
        FileOutputStream fileOutputStream = new FileOutputStream(des);
        byte[] buffer = new byte[1024];
        int count;
        while((count = fileInputStream.read(buffer)) > 0){
            fileOutputStream.write(buffer, 0, count);
        }
        fileInputStream.close();
        fileOutputStream.flush();
        fileOutputStream.close();
        src.delete();
    }

    public interface OnDownloadListener {
        void downloadComplete(boolean isSuccess, File file);
    }

    public static void showSnackBar(View parentView, String msg, Integer msgColorRes, String btnMsg, Integer btnMsgColorRes, Integer backgroundColorRes, View.OnClickListener onClickListener) {
        Snackbar snackbar = Snackbar.make(parentView, msg, Snackbar.LENGTH_LONG);

        View view = snackbar.getView();
        if(backgroundColorRes != null){
            view.setBackgroundColor(ContextCompat.getColor(parentView.getContext(), backgroundColorRes));
        }

        TextView textView = view.findViewById(R.id.snackbar_text);
        if(msgColorRes != null){
            textView.setTextColor(ContextCompat.getColor(parentView.getContext(), msgColorRes));
        }

        Button button = view.findViewById(R.id.snackbar_action);
        if(btnMsgColorRes != null){
            button.setTextColor(ContextCompat.getColor(parentView.getContext(), btnMsgColorRes));
        }

        if(btnMsg != null && onClickListener != null){
            snackbar.setAction(btnMsg, onClickListener);
        }

        snackbar.show();
    }

    public static int getColorByAttrId(Context context, @AttrRes int attrIdForColor) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attrIdForColor, typedValue, true);
        return typedValue.data;
    }
}

