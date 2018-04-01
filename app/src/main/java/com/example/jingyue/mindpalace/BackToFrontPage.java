package com.example.jingyue.mindpalace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class BackToFrontPage extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_screen);

        final Button button_back_to_front = (Button) findViewById(R.id.back_to_front);
        button_back_to_front.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(next);
            }
        });
    }


}
