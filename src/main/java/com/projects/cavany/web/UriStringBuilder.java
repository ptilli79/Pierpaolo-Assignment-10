package com.projects.cavany.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spoonacular.urls")
public class UriStringBuilder {
        private String base;
        private String mealPlan;
        private String recipeInformation;
        private String apiKey;
        
		public String getBase() {
			return base;
		}
		public void setBase(String base) {
			this.base = base;
		}
		
		public String getMealPlan() {
			return mealPlan;
		}
		public void setMealPlan(String mealPlan) {
			this.mealPlan = mealPlan;
		}
		public String getRecipeInformation() {
			return recipeInformation;
		}
		
		public void setRecipeInformation(String recipeInformation) {
			this.recipeInformation = recipeInformation;
		}
		
		public String getApiKey() {
			return apiKey;
		}
		
		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
		}
		
		@Override
		public String toString() {
			return base + mealPlan + "?apiKey=" + apiKey;
		}
		
		public String toStringRecipeInformation(String recipeId) {
			return base + recipeInformation + "/" + recipeId + "/information?apiKey=" + apiKey;
			
		}
}
