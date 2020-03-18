package com.mangobits.startupkit.user.terms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.Id;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="terms")
@Indexed
public class Terms {
	
	
	@Id
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
