package com.example.jingyue.mindpalace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class NextPage extends AppCompatActivity{

    public class BackToFrontPage extends AppCompatActivity{

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display_screen);

            final Button button_next_page = (Button) findViewById(R.id.next_page);
            button_next_page.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {



                }
            });
        }


    }

}
