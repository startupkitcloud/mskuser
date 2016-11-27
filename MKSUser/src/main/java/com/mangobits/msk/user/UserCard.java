package com.mangobits.msk.user;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCard {

	
	private String idUser;
	
	
	private String nameUser;
	
	
	

	public String getIdUser() {
		return idUser;
	}




	public String getNameUser() {
		return nameUser;
	}




	public void setNameUser(String nameUser) {
		this.nameUser = nameUser;
	}




	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}
}
