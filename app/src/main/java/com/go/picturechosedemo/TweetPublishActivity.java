package com.go.picturechosedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.go.picturechosedemo.view.RichEditText;
import com.go.picturechosedemo.view.TweetPicturesPreviewer;


/**
 * Created by go on 2017/9/4.
 */

public class TweetPublishActivity extends BaseActivity implements View.OnClickListener{

    RichEditText mEditContent;

    TweetPicturesPreviewer mLayImages;

    ImageView  iv_picture;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    protected int getContentView() {
        return R.layout.activity_tweet_publish;
    }


    @Override
    protected void initData() {
        super.initData();
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        mEditContent = findViewById(R.id.edit_content);
        mLayImages = findViewById(R.id.recycler_images);
        iv_picture = findViewById(R.id.iv_picture);

       iv_picture.setOnClickListener(this);
    }


    @Override
    protected void initWindow() {
        super.initWindow();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_picture :
                hideSoftKeyboard();
                mLayImages.onLoadMoreClick();
                Toast.makeText(TweetPublishActivity.this,"makeText",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void hideSoftKeyboard() {
        mEditContent.clearFocus();
        ((InputMethodManager)TweetPublishActivity.this.getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                mEditContent.getWindowToken(), 0);
    }
}
