package com.projects.cavany.dto.RecipeDetails;

public class MeasuresDTO {

    private UsMetricDTO us;
    private UsMetricDTO metric;
    
    // Getters and setters for all fields
	public UsMetricDTO getUs() {
		return us;
	}
	public void setUs(UsMetricDTO us) {
		this.us = us;
	}
	public UsMetricDTO getMetric() {
		return metric;
	}
	public void setMetric(UsMetricDTO metric) {
		this.metric = metric;
	}  
}
