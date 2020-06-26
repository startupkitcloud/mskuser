package com.mangobits.startupkit.user.terms;

import com.mangobits.startupkit.core.annotation.MSKEntity;
import com.mangobits.startupkit.core.annotation.MSKId;

@MSKEntity(name="terms")
public class Terms {

	@MSKId
	private String language;

	private String terms;
	
	
	public Terms(){
		
	}
	
	
	public Terms(String language){
		this.language = language;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getTerms() {
		return terms;
	}


	public void setTerms(String terms) {
		this.terms = terms;
	}
}
