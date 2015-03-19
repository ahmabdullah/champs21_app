package com.champs21.schoolapp.callbacks;

import android.widget.TextView;

import com.champs21.schoolapp.viewhelpers.CreateFolderDialog;

public interface onCreateFolderDialogButtonClickListener {

	public void onCreateBtnClick(CreateFolderDialog createFolderDialog, String folderName, TextView tv_error);
	public void onCancelBtnClick(CreateFolderDialog createFolderDialog);
	
}
