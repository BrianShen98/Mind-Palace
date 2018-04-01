package com.example.jingyue.mindpalace;



import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;

public class LineView extends View
{
    Path path;
    Paint paint;
    float length;

    public LineView(Context context)
    {
        super(context);
    }

    public LineView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LineView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void init(int start_x,int start_y,int end_x,int end_y)
    {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);

        path = new Path();
        path.moveTo(start_x, start_y);
        path.lineTo(end_x, end_y);


        // Measure the path
        PathMeasure measure = new PathMeasure(path, false);
        length = measure.getLength();

        float[] intervals = new float[]{length, length};

        ObjectAnimator animator = ObjectAnimator.ofFloat(LineView.this, "phase", 1.0f, 0.0f);
        animator.setDuration(1000);
        animator.start();
    }

    //is called by animtor object
    public void setPhase(float phase)
    {
        Log.d("LineView","setPhase called with:" + String.valueOf(phase));
        paint.setPathEffect(createPathEffect(length, phase, 0.0f));
        invalidate();//will calll onDraw
    }

    private static PathEffect createPathEffect(float pathLength, float phase, float offset)
    {
        return new DashPathEffect(new float[] { pathLength, pathLength },
                Math.max(phase * pathLength, offset));
    }

    @Override
    public void onDraw(Canvas c)
    {
        super.onDraw(c);
        c.drawPath(path, paint);
    }
}
