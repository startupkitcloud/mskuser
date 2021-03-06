package org.startupkit.user;

import javax.json.bind.annotation.JsonbDateFormat;
import java.util.Date;


public class UserCard {

	private String id;

	private String name;

	private String nameObj;

	private String type;

	private UserStatusEnum status;

	@JsonbDateFormat(value = "yyyy-MM-dd HH:mm:ss")
	private Date creationDate;

	private String idObj;

	private String email;

	private String phone;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getNameObj() {
		return nameObj;
	}

	public void setNameObj(String nameObj) {
		this.nameObj = nameObj;
	}

	public String getIdObj() {
		return idObj;
	}

	public void setIdObj(String idObj) {
		this.idObj = idObj;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public UserStatusEnum getStatus() {
		return status;
	}

	public void setStatus(UserStatusEnum status) {
		this.status = status;
	}
	
}
