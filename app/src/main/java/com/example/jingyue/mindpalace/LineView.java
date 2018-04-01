package com.example.jingyue.mindpalace;

/*import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class LineView extends View {

    private Paint paint = new Paint();
    private PointF pointA, pointB;
    public LineView(Context context) {
        super(context);
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);
        canvas.drawLine(pointA.x,pointA.y,pointB.x,pointB.y,paint);
        super.onDraw(canvas);
    }

    public void setPointA(PointF point)
    {
        pointA = point;
    }

    public void setPointB(PointF point)
    {
        pointB = point;
    }

    public void draw()
    {
        invalidate();
        requestLayout();
    }
}
*/

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.support.annotation.Nullable;
import android.view.View;

public class LineView extends View {
    Paint paint = new Paint();

    private void init() {
        paint.setColor(Color.BLACK);
    }

    public LineView(Context context) {
        super(context);
        init();
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawLine(0, 0, 20, 20, paint);
        canvas.drawLine(20, 0, 0, 20, paint);
    }

}