package org.startupkit.user.terms;

import org.startupkit.core.annotation.MSKEntity;
import org.startupkit.core.annotation.MSKId;

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
