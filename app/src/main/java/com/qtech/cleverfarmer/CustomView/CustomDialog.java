package com.qtech.cleverfarmer.CustomView;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

/**
 * Created by xiaobailong24 on 2016/5/14.
 * Class:
 * Describe:
 * Version:
 */
public class CustomDialog extends AlertDialog {

    public CustomDialog(Context context) {
        super(context);
    }

    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        super.setOnCancelListener(listener);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void setView(View view) {
        super.setView(view);
    }
}
