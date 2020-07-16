package com.mangobits.startupkit.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mangobits.startupkit.core.address.AddressInfo;
import com.mangobits.startupkit.core.annotation.MSKEntity;
import com.mangobits.startupkit.core.annotation.MSKId;
import com.mangobits.startupkit.core.photo.GalleryItem;
import com.mangobits.startupkit.core.user.GeneralUser;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import javax.json.bind.annotation.JsonbDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@MSKEntity(name="user")
public class User implements GeneralUser {

	@MSKId
    private String id;

	private String idFacebook;

	private String idGoogle;

	private String idApple;

	private String phone;

	private Long phoneNumber;

	private String document;

	private Integer phoneCountryCode;

	private String name;

	private String idObj;

	private String nameObj;

	private String code;

	private String email;

	private String cpf;

	private String keyIOS;

	private String keyAndroid;

	private AddressInfo lastAddress;

	@JsonbDateFormat(value = "yyyy-MM-dd HH:mm:ss")
	private Date lastLogin;

	private String password;

	@BsonIgnore
	private String oldPassword;

	private String salt;

	@JsonbDateFormat(value = "yyyy-MM-dd")
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

	private String token;

	private String type;

	private String idAvatar;

	private Date tokenExpirationDate;

	private UserStatusEnum status;

	private Map<String, String> info;

	private List<GalleryItem> gallery;

	public User(){
		
	}

	public User(String id){
		this.id = id;
	}

	@Override
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

	@Override
	public Long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
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

	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	@Override
	public String getKeyIOS() {
		return keyIOS;
	}

	public void setKeyIOS(String keyIOS) {
		this.keyIOS = keyIOS;
	}

	@Override
	public String getKeyAndroid() {
		return keyAndroid;
	}

	public void setKeyAndroid(String keyAndroid) {
		this.keyAndroid = keyAndroid;
	}

	public AddressInfo getLastAddress() {
		return lastAddress;
	}

	public void setLastAddress(AddressInfo lastAddress) {
		this.lastAddress = lastAddress;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getBirthDateStr() {
		return birthDateStr;
	}

	public void setBirthDateStr(String birthDateStr) {
		this.birthDateStr = birthDateStr;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getTokenExpirationDate() {
		return tokenExpirationDate;
	}

	public void setTokenExpirationDate(Date tokenExpirationDate) {
		this.tokenExpirationDate = tokenExpirationDate;
	}

	public UserStatusEnum getStatus() {
		return status;
	}

	public void setStatus(UserStatusEnum status) {
		this.status = status;
	}

	public Map<String, String> getInfo() {
		return info;
	}

	public void setInfo(Map<String, String> info) {
		this.info = info;
	}

	public List<GalleryItem> getGallery() {
		return gallery;
	}

	public void setGallery(List<GalleryItem> gallery) {
		this.gallery = gallery;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIdObj() {
		return idObj;
	}

	public void setIdObj(String idObj) {
		this.idObj = idObj;
	}

	public String getNameObj() {
		return nameObj;
	}

	public void setNameObj(String nameObj) {
		this.nameObj = nameObj;
	}

	public String getIdAvatar() {
		return idAvatar;
	}

	public void setIdAvatar(String idAvatar) {
		this.idAvatar = idAvatar;
	}

	public String getIdApple() {
		return idApple;
	}

	public void setIdApple(String idApple) {
		this.idApple = idApple;
	}
}
