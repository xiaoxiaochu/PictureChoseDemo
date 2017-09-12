package com.go.picturechosedemo;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by go on 2017/9/7.
 */

public class DialogHelper {

    public static AlertDialog.Builder getDialog(Context context) {
        return new AlertDialog.Builder(context, R.style.App_Theme_Dialog_Alert);
    }
    /**
     * 获取一个验证对话框
     */
    public static AlertDialog.Builder getConfirmDialog(
            Context context,
            String title,
            String message,
            String positiveText,
            String negativeText,
            boolean cancelable,
            DialogInterface.OnClickListener positiveListener,
            DialogInterface.OnClickListener negativeListener) {
        return getDialog(context)
                .setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener);
    }
}
