package com.go.picturechosedemo;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by go on 2017/9/4.
 */

public interface TweetPublishContract {

    interface Operator {
        void setDataView(View view, String defaultContent, String[] defaultImages, About.Share about,String localImg);

        void publish();

        void onBack();

        void loadData();

        void onSaveInstanceState(Bundle outState);

        void onRestoreInstanceState(Bundle savedInstanceState);
    }

    interface View {
        Context getContext();

        String getContent();

        void setContent(String content, boolean needSelectionEnd);

        void setAbout(About.Share about, boolean needCommit);

        boolean needCommit();

        String[] getImages();

        void setImages(String[] paths);

        void finish();

        Operator getOperator();

        boolean onBackPressed();
    }
}
