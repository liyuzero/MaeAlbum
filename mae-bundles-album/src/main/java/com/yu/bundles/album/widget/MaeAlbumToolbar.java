package com.yu.bundles.album.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yu.bundles.album.AlbumBaseActivity;
import com.yu.bundles.album.R;

public class MaeAlbumToolbar extends Toolbar {
    private TypedValue typedValue = new TypedValue();
    private TypedValue contentVal = new TypedValue();

    public MaeAlbumToolbar(Context context) {
        super(context);
    }

    public MaeAlbumToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        refreshStyle();
    }

    public void init(final AlbumBaseActivity activity, View... view){
        activity.setSupportActionBar(this);
        if(view != null && view.length > 0){
            addView(view[0]);
        }
    }

    public void refreshStyle(){
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        getContext().getTheme().resolveAttribute(R.attr.mae_album_topBar_text_color, contentVal, true);
        setBackgroundColor(typedValue.data);
        setColor(contentVal.data);
    }

    /*
    * 设置toolbar颜色
    *
    * @param backgroundColor 背景颜色
    * @param contentColor 内容颜色
    *
    * */
    private void setColor(final int contentColor){
        setContentColor(MaeAlbumToolbar.this, contentColor);
    }

    private void setContentColor(ViewGroup viewGroup, int contentColor){
        for (int i=0; i<viewGroup.getChildCount(); i++){
            View view = viewGroup.getChildAt(i);
            if(view instanceof ViewGroup){
                setContentColor((ViewGroup) view, contentColor);
            } else if(view instanceof ImageView){
                ((ImageView) view).setColorFilter(contentColor);
            } else if(!(view instanceof EditText) && view instanceof TextView){
                ((TextView) view).setTextColor(contentColor);
            }
        }
    }

}
