package com.projects.cavany.dto;

import java.util.List;



public class AnalyzedInstructionDTO {

    private String name;
    private List<StepDTO> steps;
    
    // Getters and setters for all fields
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<StepDTO> getSteps() {
		return steps;
	}
	public void setSteps(List<StepDTO> steps) {
		this.steps = steps;
	}
}
