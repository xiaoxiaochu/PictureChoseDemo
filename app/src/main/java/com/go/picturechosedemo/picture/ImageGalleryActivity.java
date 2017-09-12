package com.go.picturechosedemo.picture;

import android.support.v4.view.ViewPager;

import com.go.picturechosedemo.BaseActivity;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by go on 2017/9/11.
 */

public class ImageGalleryActivity extends BaseActivity implements ViewPager.OnPageChangeListener,
        EasyPermissions.PermissionCallbacks{


    @Override
    protected int getContentView() {
        return 0;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
