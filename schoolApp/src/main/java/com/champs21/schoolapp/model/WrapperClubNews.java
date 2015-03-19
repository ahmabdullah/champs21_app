package com.champs21.schoolapp.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class WrapperClubNews {


	private List<ClubNews> clubs;
	private String total;
	@SerializedName("has_next")
	private boolean hasnext;
	
	public List<ClubNews> getClubs() {
		return clubs;
	}
	public void setClubs(List<ClubNews> clubs) {
		this.clubs = clubs;
	}
	
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public boolean isHasnext() {
		return hasnext;
	}
	public void setHasnext(boolean hasnext) {
		this.hasnext = hasnext;
	}
	
	
}
