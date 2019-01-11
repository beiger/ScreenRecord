package com.bing.example.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bing.example.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class PermissionDialog extends DialogFragment {
	private static final String TAG = PermissionDialog.class.getName();

	private String[] mPermissions;

	public void setPermissions(String[] permissions) {
		mPermissions = permissions;
	}

	public static void show(FragmentManager fm, String[] permissions) {
		PermissionDialog dialog = (PermissionDialog) fm.findFragmentByTag(TAG);
		if (dialog == null) {
			dialog = new PermissionDialog();
		}
		dialog.setPermissions(permissions);
		dialog.show(fm, TAG);
	}


	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_permisson, null);
		TextView textView = view.findViewById(R.id.permission);
		textView.setText(getPermissionsString(mPermissions));
		return new AlertDialog.Builder(getContext())
				.setView(view)
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(R.string.open_permission, (dialog, which) -> toSelfSetting(getContext())).create();
	}

	public static void toSelfSetting(Context context) {
		Intent mIntent = new Intent();
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
		context.startActivity(mIntent);
	}


	private String getPermissionsString(String[] permissions) {
		if (permissions == null || permissions.length <= 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < permissions.length; i++) {
			if (i < permissions.length - 1) {
				result.append("· ").append(getPermissionGroupLabel(permissions[i])).append("\n");
			} else {
				result.append("· ").append(getPermissionGroupLabel(permissions[i]));
			}
		}
		return result.toString();
	}

	private String getPermissionGroupLabel(String permissionName) {
		String label = null;
		try {
			PermissionInfo permissionInfo;
			permissionInfo = getActivity().getPackageManager().getPermissionInfo(permissionName, 0);
			PackageItemInfo groupInfo = getActivity().getPackageManager().getPermissionGroupInfo(permissionInfo.group, 0);
			label = (String) groupInfo.loadLabel(getActivity().getPackageManager());
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return label;
	}
}
