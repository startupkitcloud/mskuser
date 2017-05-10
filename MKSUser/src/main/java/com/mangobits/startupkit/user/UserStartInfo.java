package com.mangobits.startupkit.user;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserStartInfo {

	
	private String idUser;
	
	
	private String keyIOS;
	
	
	private String keyAndroid;
	
	
	private Double latitude;
	
	
	private Double longitude;
	
	
	private String language;


	public String getIdUser() {
		return idUser;
	}


	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}


	public Double getLatitude() {
		return latitude;
	}


	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}


	public Double getLongitude() {
		return longitude;
	}


	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}


	public String getKeyIOS() {
		return keyIOS;
	}


	public void setKeyIOS(String keyIOS) {
		this.keyIOS = keyIOS;
	}


	public String getKeyAndroid() {
		return keyAndroid;
	}


	public void setKeyAndroid(String keyAndroid) {
		this.keyAndroid = keyAndroid;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}
}
