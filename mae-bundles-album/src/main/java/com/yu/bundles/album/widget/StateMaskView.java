package com.yu.bundles.album.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yu.bundles.album.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 为ImageView添加状态
 * Created by zhaoyu on 2017/10/14.
 */
public class StateMaskView extends View {

    // 3个状态
    public static final int STATE_NONE = 0x00;
    public static final int STATE_FLAG_CHECKED = 0x01;
    public static final int STATE_FLAG_UNUSED = 0x02;

    private static final int[] STATE_CHECKED = {R.attr.state_checked};
    private static final int[] STATE_UNUSED = {R.attr.state_unused};

    // 状态
    public static final int[][] STATUS = {STATE_CHECKED, STATE_UNUSED};

    @IntDef({STATE_NONE, STATE_FLAG_CHECKED, STATE_FLAG_UNUSED})
    public @interface State {
    }

    private final List<Integer> STATE_LIST = new ArrayList<>();
    private int state;

    public StateMaskView(Context context) {
        this(context, null);
    }

    public StateMaskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateMaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        for (int i = 0; i < STATUS.length; i++) {
            STATE_LIST.add(STATUS[i][0]);
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateMaskView);
        setStateEnabledInner(a.getInt(R.styleable.StateMaskView_stateEnabled, STATE_NONE));
        a.recycle();
    }

    public void setState(@State int flag) {
        setStateEnabledInner(flag);
    }

    private void setStateEnabledInner(int flag) {
        state = flag;
        refreshDrawableState();
    }

    public void clearStatus() {
        state = 0;
        refreshDrawableState();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (STATE_NONE != state) {
            mergeDrawableStates(drawableState, STATUS[state - 1]);
        }
        return drawableState;
    }
}
