package com.hlz.Animation;

/**
 * Created by Hanlizhi on 2016/10/18.
 */
import android.content.Context;

import com.hlz.order.R;

public class FinishedFailureView extends FinishedView {

    public FinishedFailureView(Context context, int parentWidth, int mainColor, int secondaryColor) {
        super(context, parentWidth, mainColor, secondaryColor);
    }

    @Override
    protected int getDrawable() {
        return R.drawable.ic_failure_mark;
    }

    @Override
    protected int getCircleColor() {
        return secondaryColor;
    }
}
