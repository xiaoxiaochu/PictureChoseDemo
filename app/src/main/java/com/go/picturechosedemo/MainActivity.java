package com.go.picturechosedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by go on 2017/9/4.
 */

public class MainActivity extends AppCompatActivity {


   TextView textView;

   private Button tweet_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
       textView = findViewById(R.id.text_hello_layout);
       tweet_btn = findViewById(R.id.tweet_publish);

        textView.setText("another hello world");


        tweet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,TweetPublishActivity.class));
            }
        });
    }
}
