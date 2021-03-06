package org.startupkit.user.freezer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.startupkit.core.annotation.MSKEntity;
import org.startupkit.core.annotation.MSKId;

import java.util.Date;

@MSKEntity(name="userFreezer")
public class UserFreezer {

	@MSKId
    private String id;

	private String idFacebook;

	private String idGoogle;

	private String phone;

	private Long phoneNumber;
	
	private Integer phoneCountryCode;

	private String name;

	private String email;

	private String cpf;

	private String keyIOS;

	private String keyAndroid;

	private String password;

	@JsonIgnore
	private String salt;

	private Date birthDate;

	private String birthDateStr;

	private String gender;

	private String language;

	@JsonIgnore
	private String locale;

	@JsonIgnore
	private Date creationDate;

	private Boolean phoneConfirmed;

	private Boolean emailConfirmed;

	private Boolean userConfirmed;

	public UserFreezer(){
		
	}
	
	
	public UserFreezer(String id){
		this.id = id;
	}





	public String getId() {
		return id;
	}





	public void setId(String id) {
		this.id = id;
	}





	public String getIdFacebook() {
		return idFacebook;
	}





	public void setIdFacebook(String idFacebook) {
		this.idFacebook = idFacebook;
	}





	public String getIdGoogle() {
		return idGoogle;
	}





	public void setIdGoogle(String idGoogle) {
		this.idGoogle = idGoogle;
	}





	public String getPhone() {
		return phone;
	}





	public void setPhone(String phone) {
		this.phone = phone;
	}





	public Long getPhoneNumber() {
		return phoneNumber;
	}





	public void setPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}





	public Integer getPhoneCountryCode() {
		return phoneCountryCode;
	}





	public void setPhoneCountryCode(Integer phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
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





	public String getPassword() {
		return password;
	}





	public void setPassword(String password) {
		this.password = password;
	}





	public Date getBirthDate() {
		return birthDate;
	}





	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}





	public String getGender() {
		return gender;
	}





	public void setGender(String gender) {
		this.gender = gender;
	}





	public String getLanguage() {
		return language;
	}





	public void setLanguage(String language) {
		this.language = language;
	}





	public String getLocale() {
		return locale;
	}





	public void setLocale(String locale) {
		this.locale = locale;
	}





	public Date getCreationDate() {
		return creationDate;
	}





	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	public String getBirthDateStr() {
		return birthDateStr;
	}


	public void setBirthDateStr(String birthDateStr) {
		this.birthDateStr = birthDateStr;
	}


	public Boolean getPhoneConfirmed() {
		return phoneConfirmed;
	}


	public void setPhoneConfirmed(Boolean phoneConfirmed) {
		this.phoneConfirmed = phoneConfirmed;
	}


	public Boolean getEmailConfirmed() {
		return emailConfirmed;
	}


	public void setEmailConfirmed(Boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}


	public Boolean getUserConfirmed() {
		return userConfirmed;
	}


	public void setUserConfirmed(Boolean userConfirmed) {
		this.userConfirmed = userConfirmed;
	}


	public String getSalt() {
		return salt;
	}


	public void setSalt(String salt) {
		this.salt = salt;
	}


	public String getCpf() {
		return cpf;
	}


	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
}
