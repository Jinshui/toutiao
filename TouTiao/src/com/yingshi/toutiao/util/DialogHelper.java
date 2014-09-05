package com.yingshi.toutiao.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yingshi.toutiao.R;

public class DialogHelper {
    public static Dialog createProgressDialog(Context context, int dlgId, int resId, CancelListener listener) {
        Dialog dialog = new LoadingDialog(context, dlgId, android.R.style.Theme_Translucent_NoTitleBar, listener);
        dialog.setContentView(R.layout.progress_dialog);
        TextView textView = (TextView) dialog.findViewById(R.id.progressText);
        ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        textView.setText(resId);
        progressBar.setIndeterminate(true);
        dialog.setCancelable(true);
        return dialog;
    }

    public static interface CancelListener{
        public void onDialogCancelled(int id);
    }

    public static class LoadingDialog extends Dialog{
        int mId;
        CancelListener mListener;
        public LoadingDialog(Context context, int id, int theme, CancelListener listener) {
            super(context, theme);
            mId = id;
            mListener = listener;
        }

        public void onBackPressed(){
            super.onBackPressed();
            if(mListener != null)
                mListener.onDialogCancelled(mId);
        }
    }


    public static AlertDialog createDialog(Context ctx, int msg, int title, int icon, int btn1, OnClickListener btn1Listener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx)
        .setMessage(msg);
        if(title > 0)
            dialog.setTitle(title);
        if(icon > 0)
            dialog.setIcon(icon);
        if(btn1 > 0)
            dialog.setPositiveButton(btn1, btn1Listener);
        return dialog.create();
    }

    public static AlertDialog createDialog(Context ctx, int msg, int title, int icon, int btn1, OnClickListener btn1Listener, int btn2, OnClickListener btn2Listener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx)
        .setMessage(msg);
        if(title > 0)
            dialog.setTitle(title);
        if(icon > 0)
            dialog.setIcon(icon);
        if(btn1 > 0)
            dialog.setPositiveButton(btn1, btn1Listener);
        if(btn2 > 0)
            dialog.setNegativeButton(btn2, btn2Listener);
        return dialog.create();
    }

    public static AlertDialog createDialog(Context ctx, String msg, String title, int icon, int btn1, OnClickListener btn1Listener, int btn2, OnClickListener btn2Listener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx)
        .setMessage(msg);
        if(!TextUtils.isEmpty(title))
            dialog.setTitle(title);
        if(icon > 0)
            dialog.setIcon(icon);
        if(btn1 > 0)
            dialog.setPositiveButton(btn1, btn1Listener);
        if(btn2 > 0)
            dialog.setNegativeButton(btn2, btn2Listener);
        return dialog.create();
    }

    public static AlertDialog createDialog(Context ctx, int msg, int title, int icon, int btn1, OnClickListener btn1Listener, int btn2, OnClickListener btn2Listener, View customView){
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        if(customView != null)
            dialog.setView(customView);
        if(msg > 0)
            dialog.setMessage(msg);
        if(title > 0)
            dialog.setTitle(title);
        if(icon > 0)
            dialog.setIcon(icon);
        if(btn1 > 0)
            dialog.setPositiveButton(btn1, btn1Listener);
        if(btn2 > 0)
            dialog.setNegativeButton(btn2, btn2Listener);
        return dialog.create();
    }
}
