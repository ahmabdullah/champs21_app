package com.champs21.schoolapp.callbacks;

import android.widget.TextView;

import com.champs21.schoolapp.model.Folder;
import com.champs21.schoolapp.viewhelpers.CreateFolderDialog;

public interface ChooseFolderDilogClickListener {

	public void addFolderBtnClicked();
	public void folderClicked(Folder f);
	
}
