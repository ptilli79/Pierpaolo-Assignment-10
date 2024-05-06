package com.projects.cavany.dto.RecipeDetails.AnalyzedInstruction;

import java.util.List;

public class StepDTO {
    private int number;
    private String step;
    private List<IngredientDTO> ingredients;
    private List<EquipmentDTO> equipment;
    private LengthDTO length;
    
    // Getters and setters for all fields
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public List<IngredientDTO> getIngredients() {
		return ingredients;
	}
	public void setIngredients(List<IngredientDTO> ingredients) {
		this.ingredients = ingredients;
	}
	public List<EquipmentDTO> getEquipment() {
		return equipment;
	}
	public void setEquipment(List<EquipmentDTO> equipment) {
		this.equipment = equipment;
	}
	public LengthDTO getLength() {
		return length;
	}
	public void setLength(LengthDTO length) {
		this.length = length;
	}
}
