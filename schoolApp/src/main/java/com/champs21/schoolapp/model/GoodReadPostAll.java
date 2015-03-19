/**
 * 
 */
package com.champs21.schoolapp.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

/**
 * @author Amit
 *
 */
public class GoodReadPostAll {
	@SerializedName("post")
	private ArrayList<GoodReadPost> goodreadPostList;
	
	public void setGoodreadPostList(ArrayList<GoodReadPost> goodreadPostList) {
		this.goodreadPostList = goodreadPostList;
	}
	public ArrayList<GoodReadPost> getGoodreadPostList() {
		return goodreadPostList;
	}
}
