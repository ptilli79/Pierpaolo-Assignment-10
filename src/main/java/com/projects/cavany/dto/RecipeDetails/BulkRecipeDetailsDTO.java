package com.projects.cavany.dto.RecipeDetails;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BulkRecipeDetailsDTO extends RecipeDetailsDTO {
	
	    private String report;
	    private TipsDTO tips;
	    private Integer openLicense;
	    private Double suspiciousDataScore;
	    private Integer approved;
	    @JsonIgnore
	    private List<Object> unknownIngredients;
	    private List<String> userTags;

	    // Getters and setters for new fields
	    public String getReport() {
	        return report;
	    }

	    public void setReport(String report) {
	        this.report = report;
	    }

	    public TipsDTO getTips() {
	        return tips;
	    }

	    public void setTips(TipsDTO tips) {
	        this.tips = tips;
	    }

	    public Integer getOpenLicense() {
	        return openLicense;
	    }

	    public void setOpenLicense(Integer openLicense) {
	        this.openLicense = openLicense;
	    }

	    public Double getSuspiciousDataScore() {
	        return suspiciousDataScore;
	    }

	    public void setSuspiciousDataScore(Double suspiciousDataScore) {
	        this.suspiciousDataScore = suspiciousDataScore;
	    }

	    public Integer getApproved() {
	        return approved;
	    }

	    public void setApproved(Integer approved) {
	        this.approved = approved;
	    }

	    public List<Object> getUnknownIngredients() {
	        return unknownIngredients;
	    }

	    public void setUnknownIngredients(List<Object> unknownIngredients) {
	        this.unknownIngredients = unknownIngredients;
	    }

	    public List<String> getUserTags() {
	        return userTags;
	    }

	    public void setUserTags(List<String> userTags) {
	        this.userTags = userTags;
	    }

	    // Inner class for TipsDTO
	    public static class TipsDTO {
	        private List<String> health;
	        private List<String> price;
	        private List<String> cooking;
	        private List<String> green;
	        
	     // Getters and setters
			public List<String> getHealth() {
				return health;
			}
			public void setHealth(List<String> health) {
				this.health = health;
			}
			public List<String> getPrice() {
				return price;
			}
			public void setPrice(List<String> price) {
				this.price = price;
			}
			public List<String> getCooking() {
				return cooking;
			}
			public void setCooking(List<String> cooking) {
				this.cooking = cooking;
			}
			public List<String> getGreen() {
				return green;
			}
			public void setGreen(List<String> green) {
				this.green = green;
			}
	    }

	    // ... possibly other nested classes and methods ...

}
