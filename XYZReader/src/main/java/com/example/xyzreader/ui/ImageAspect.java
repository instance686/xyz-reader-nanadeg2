package com.example.xyzreader.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ayush on 3/12/17.
 */

public class ImageAspect extends android.support.v7.widget.AppCompatImageView {
    public ImageAspect(Context context) {
        super(context);
    }

    public ImageAspect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageAspect(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int threeTwoHeight=MeasureSpec.getSize(widthMeasureSpec)*2/3;
        int threeTwoHeightSpec=MeasureSpec.makeMeasureSpec(threeTwoHeight,MeasureSpec.EXACTLY);

    }
}
