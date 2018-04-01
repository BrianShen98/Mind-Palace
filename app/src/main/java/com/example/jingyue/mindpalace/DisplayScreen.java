package com.example.jingyue.mindpalace;

/*import android.graphics.PointF;
import android.os.Bundle;

public class DisplayScreen extends MainActivity{
    PointF pointStart1;
    PointF pointStart2;

    private LineView original;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        original = (LineView)findViewById(R.id.lineView);
        pointStart1 = new PointF(10, 10);
        pointStart2 = new PointF(10,10);
        original.setPointA(pointStart1);
        original.setPointB(pointStart2);

        original.draw();
    }

}
*/

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class DisplayScreen extends Activity {
    LineView drawView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawView = new LineView(this);
        drawView.setBackgroundColor(Color.WHITE);
        setContentView(drawView);

    }
}