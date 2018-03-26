package com.henrique.camerawifi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class VideoSurfaceView extends SurfaceView {
    private static final float MAX_ASPECT_RATIO_DEFORMATION_PERCENT = 0.01f;
    private float videoAspectRatio;
    public static final float DEFAULT_MIN_SCALE = 1.0f;


    public VideoSurfaceView(Context context) {
        super(context);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setVideoWidthHeightRatio(float widthHeightRatio) {
        if (this.videoAspectRatio != widthHeightRatio) {
            this.videoAspectRatio = widthHeightRatio;
            requestLayout();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (this.videoAspectRatio != 0.0f) {
            float aspectDeformation = (this.videoAspectRatio / (((float) width) / ((float) height))) - DEFAULT_MIN_SCALE;
            if (aspectDeformation > MAX_ASPECT_RATIO_DEFORMATION_PERCENT) {
                height = (int) (((float) width) / this.videoAspectRatio);
            } else if (aspectDeformation < -0.01f) {
                width = (int) (((float) height) * this.videoAspectRatio);
            }
        }
        setMeasuredDimension(width, height);
    }
}
