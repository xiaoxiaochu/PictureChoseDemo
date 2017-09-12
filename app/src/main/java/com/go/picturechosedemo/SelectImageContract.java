package com.go.picturechosedemo;

/**
 * Created by go on 2017/9/5.
 */

public interface SelectImageContract {

    interface Operator {
        void requestCamera();

        void requestExternalStorage();

        void onBack();

        void setDataView(View view);
    }

    interface View {

        void onOpenCameraSuccess();

        void onCameraPermissionDenied();
    }
}
