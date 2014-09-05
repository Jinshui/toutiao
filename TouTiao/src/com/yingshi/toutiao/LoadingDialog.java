package com.yingshi.toutiao;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.yingshi.toutiao.util.DialogHelper;

public class LoadingDialog extends DialogFragment {
	public static final int DLG_LOADING = 1;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    return DialogHelper.createProgressDialog(getActivity().getApplicationContext(), DLG_LOADING, R.string.loading, null);
	}
}
