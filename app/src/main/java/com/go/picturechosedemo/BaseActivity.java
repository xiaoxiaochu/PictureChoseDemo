package com.go.picturechosedemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by go on 2017/9/4.
 */

public abstract class BaseActivity extends AppCompatActivity {



    private Fragment mFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (true) {
            setContentView(getContentView());

            initWindow();
          //  ButterKnife.bind(this);
            initWidget();
            initData();
        } else {
            finish();
        }



    }

    protected void addFragment(int frameLayoutId, Fragment fragment){
        if (fragment != null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
             if (fragment.isAdded()){
                 if (mFragment != null){
                     transaction.hide(mFragment).show(fragment);
                 }

                 transaction.show(fragment);
             } else {
                 if (mFragment != null) {
                     transaction.hide(mFragment).add(frameLayoutId, fragment);
                 } else {
                     transaction.add(frameLayoutId, fragment);
                 }
             }
            mFragment = fragment;
            transaction.commit();
        }

    }

    protected boolean initBundle(Bundle bundle) {
        return true;
    }


    protected abstract int  getContentView();


    protected void initWindow() {
    }

    protected void initWidget() {
    }

    protected void initData() {
    }

}
