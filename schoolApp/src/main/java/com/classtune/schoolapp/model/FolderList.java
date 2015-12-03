/**
 * 
 */
package com.classtune.schoolapp.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

/**
 * @author Amit
 *
 */
public class FolderList {
	@SerializedName("folder")
	private ArrayList<Folder> folderList;
	
	public void setFolderList(ArrayList<Folder> folderList) {
		this.folderList = folderList;
	}
	public ArrayList<Folder> getFolderList() {
		return folderList;
	}
}
