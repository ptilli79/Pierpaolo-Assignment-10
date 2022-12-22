package com.coderscampus.assignment10.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spoonacular.urls")
public class UriStringBuilder {
	

        private String base;
        private String mealplan;
        private String apiKey;
        
		public String getBase() {
			return base;
		}
		public void setBase(String base) {
			this.base = base;
		}
		public String getMealplan() {
			return mealplan;
		}
		public void setMealplan(String mealplan) {
			this.mealplan = mealplan;
		}
		
		public String getApiKey() {
			return apiKey;
		}
		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
		}
		@Override
		public String toString() {
			return base + mealplan + "?apiKey=" + apiKey;
		}
		
		

    

}
