package com.mangobits.msk.user;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;

@Entity(name="userInfo")
@Indexed
public class UserInfo {
	
	
	@Id
	@DocumentId
	private String idUser;
	
	
	private Boolean fgReceiveEmails;
	
	
	private Date lastLogin;
	
	
	
	public UserInfo(){
		
	}
	
	
	public UserInfo(String idUser){
		this.idUser = idUser;
	}



	public String getIdUser() {
		return idUser;
	}



	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}



	public Boolean getFgReceiveEmails() {
		return fgReceiveEmails;
	}



	public void setFgReceiveEmails(Boolean fgReceiveEmails) {
		this.fgReceiveEmails = fgReceiveEmails;
	}



	public Date getLastLogin() {
		return lastLogin;
	}



	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
}
