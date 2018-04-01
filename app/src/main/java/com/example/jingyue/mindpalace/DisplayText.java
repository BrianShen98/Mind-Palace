package com.example.jingyue.mindpalace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayText extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_screen);

        final Button button_display_txt = (Button) findViewById(R.id.display_txt);
        button_display_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayTXT();
            }
        });
    }
    private void displayTXT()
    {
        TextView textView1 = (TextView) findViewById(R.id.text1);
        TextView textView2 = (TextView) findViewById(R.id.text2);
        TextView textView3 = (TextView) findViewById(R.id.text3);
        TextView textView4 = (TextView) findViewById(R.id.text4);

        ImageView imgView1 = (ImageView) findViewById(R.id.img1);
        ImageView imgView2 = (ImageView) findViewById(R.id.img2);
        ImageView imgView3 = (ImageView) findViewById(R.id.img3);
        ImageView imgView4 = (ImageView) findViewById(R.id.img4);

        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);
        textView3.setVisibility(View.VISIBLE);
        textView4.setVisibility(View.VISIBLE);

        imgView1.setVisibility(View.INVISIBLE);
        imgView2.setVisibility(View.INVISIBLE);
        imgView3.setVisibility(View.INVISIBLE);
        imgView4.setVisibility(View.INVISIBLE);

    }

}