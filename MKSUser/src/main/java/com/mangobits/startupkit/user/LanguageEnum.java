package com.mangobits.startupkit.user;

public enum LanguageEnum {

	PORTUGUESE_BRAZIL("pt", "pt_BR", "Português"),
	ENGLISH("en", "en_US", "Inglês");
	
	private String locale;
	
	private String language;
	
	private String desc;
	
	LanguageEnum(String language, String locale, String desc){
		this.language = language;
		this.locale = locale;
		this.desc = desc;
	}
	
	
	public static String localeByLanguage(String language){
		
		String locale = null;
		
		language = language.split("(_|\\-)")[0];
		
		if(language != null){
		
			for(LanguageEnum item : values()){
				
				if(item.getLanguage().toLowerCase().equals(language)){
					locale = item.getLocale();
					break;
				}
			}
		}
		
		if(locale == null){
			locale = ENGLISH.getLocale();
		}
		
		return locale;
	}


	public String getLocale() {
		return locale;
	}


	public String getLanguage() {
		return language;
	}


	public String getDesc() {
		return desc;
	}
}
