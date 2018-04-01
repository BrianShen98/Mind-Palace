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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

public class DisplayScreen extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_screen);


        //4 lines
        LineView line1 = (LineView)findViewById(R.id.path1);
        line1.bringToFront();
        LineView line2 = (LineView)findViewById(R.id.path2);
        LineView line3 = (LineView)findViewById(R.id.path3);
        LineView line4 = (LineView)findViewById(R.id.path4);


        line1.init(420,650,305,350);
        line2.init(660,650,770,340);
        line3.init(430,890,305,1200);
        line4.init(660,880,770,1195);


    }
}