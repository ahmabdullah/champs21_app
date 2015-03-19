package com.champs21.schoolapp.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class UserWrapper {
	
	
	/*@SerializedName("countries")
	private ArrayList<String> countryList;*/
	@SerializedName("free_id")
	private String freeId;
	@SerializedName("user_type")
	private int userType;
	@SerializedName("user")
	private User user;
	@SerializedName("paid_user")
	private UserPaidInfo info;
	
	@SerializedName("weekend")
	private ArrayList<Integer> weekends;
	@SerializedName("session")
	private String session;
	@SerializedName("children")
	private ArrayList<UserPaidInfo> children;
	@SerializedName("is_login")
	private boolean isLoggedIn;
	@SerializedName("is_register")
	private boolean isRegistered;
	
	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public ArrayList<Integer> getWeekends() {
		return weekends;
	}
	public void setWeekends(ArrayList<Integer> weekends) {
		this.weekends = weekends;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	
	public void setChildren(ArrayList<UserPaidInfo> children) {
		this.children = children;
	}
	public ArrayList<UserPaidInfo> getChildren() {
		return children;
	}
	
	
	/*public ArrayList<String> getCountryList() {
		return countryList;
	}
	public void setCountryList(ArrayList<String> countryList) {
		this.countryList = countryList;
	}*/
	public String getFreeId() {
		return freeId;
	}
	public void setFreeId(String freeId) {
		this.freeId = freeId;
	}
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}
	public boolean isRegistered() {
		return isRegistered;
	}
	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}
	public UserPaidInfo getInfo() {
		return info;
	}
	public void setInfo(UserPaidInfo info) {
		this.info = info;
	}
}
