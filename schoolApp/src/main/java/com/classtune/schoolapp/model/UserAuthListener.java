package com.classtune.schoolapp.model;

public interface UserAuthListener {

	public void onAuthenticationStart();
	public void onAuthenticationSuccessful();
	public void onAuthenticationFailed(String msg);
	public void onPaswordChanged();
	
}
