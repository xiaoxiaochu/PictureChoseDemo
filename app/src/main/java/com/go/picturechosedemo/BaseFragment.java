package com.go.picturechosedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import butterknife.ButterKnife;

/**
 * Created by go on 2017/9/6.
 */

public abstract class BaseFragment extends Fragment {
    
    protected Context mContext;
    protected View mRoot;
    protected Bundle mBundle;
    protected RequestManager mImgLoader;
    protected LayoutInflater mInflater;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        initBundle(mBundle);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);
        if (mRoot != null) {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null)
                parent.removeView(mRoot);
        } else {
            mRoot = inflater.inflate(getLayoutId(), container, false);
            mInflater = inflater;
            // Do something
            onBindViewBefore(mRoot);
            // Bind view
            initWidget(mRoot);
         //   ButterKnife.bind(this, mRoot);
            // Get savedInstanceState
            if (savedInstanceState != null)
                onRestartInstance(savedInstanceState);
            // Init

            initData();
        }
        return mRoot;
    }

    protected void initWidget(View root) {

    }


    protected void initBundle(Bundle mBundle) {
    }


    protected abstract int getLayoutId();


    protected void onBindViewBefore(View root) {
        // ...
    }

    protected void initData(){

    }



    protected <T extends View> T findView(int viewId){
        return  mRoot.findViewById(viewId);
    }




    /**
     * 获取一个图片加载管理器
     *
     * @return RequestManager
     */
    public synchronized RequestManager getImgLoader() {
        if (mImgLoader == null)
            mImgLoader = Glide.with(this);
        return mImgLoader;
    }

    protected void onRestartInstance(Bundle bundle) {

    }
}
