package com.champs21.schoolapp.model;

import com.google.gson.annotations.SerializedName;

public class AddData {
	@SerializedName("ad_image")
	private String ad_image;
	@SerializedName("ad_image_link")
	private String ad_image_link;
	@SerializedName("ad_image_caption")
	private String ad_image_caption;

	public String getAd_image() {
		return ad_image;
	}

	public void setAd_image(String ad_image) {
		this.ad_image = ad_image;
	}

	public String getAd_image_link() {
		return ad_image_link;
	}

	public void setAd_image_link(String ad_image_link) {
		this.ad_image_link = ad_image_link;
	}

	public String getAd_image_caption() {
		return ad_image_caption;
	}

	public void setAd_image_caption(String ad_image_caption) {
		this.ad_image_caption = ad_image_caption;
	}

}
