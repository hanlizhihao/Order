package com.hlz.Animation;

/**
 * Created by Hanlizhi on 2016/10/18.
 */
import android.content.Context;

import com.hlz.order.R;

public class FinishedOkView extends FinishedView {

    public FinishedOkView(Context context, int parentWidth, int mainColor, int secondaryColor) {
        super(context, parentWidth, mainColor, secondaryColor);
    }

    @Override
    protected int getDrawable() {
        return R.drawable.ic_checked_mark;
    }

    @Override
    protected int getCircleColor() {
        return mainColor;
    }
}

