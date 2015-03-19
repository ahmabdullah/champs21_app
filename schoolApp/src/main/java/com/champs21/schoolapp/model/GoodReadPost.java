/**
 * 
 */
package com.champs21.schoolapp.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

/**
 * @author Tasvir
 *
 */
public class GoodReadPost {
	@SerializedName("post")
	private ArrayList<FreeVersionPost> post;
	@SerializedName("folder_name")
	private String folderName;
	
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getFolderName() {
		return folderName;
	}
	
	public void setPost(ArrayList<FreeVersionPost> post) {
		this.post = post;
	}
	public ArrayList<FreeVersionPost> getPost() {
		return post;
	}
}
