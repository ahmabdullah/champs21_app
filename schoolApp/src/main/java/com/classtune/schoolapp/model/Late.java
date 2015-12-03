package com.classtune.schoolapp.model;

import com.classtune.schoolapp.adapters.CalendarAdapter.CalenderEventType;
import com.google.gson.annotations.SerializedName;

public class Late implements CalenderEvent{
	@SerializedName("date")
	private String date;
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public CalenderEventType getType() {
		// TODO Auto-generated method stub
		return CalenderEventType.LATE;
	}

	
}
