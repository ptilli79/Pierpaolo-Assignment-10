package com.projects.cavany.dto.RecipeDetails;

import java.util.List;

public class ExtendedIngredientDTO {
    private long id;
    private String aisle;
    private String image;
    private String consistency;
    private String name;
    private String nameClean;
    private String original;
    private String originalName;
    private double amount;
    private String unit;
    private List<String> meta;
    private MeasuresDTO measures;
    
    // Getters and setters for all fields
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAisle() {
		return aisle;
	}
	public void setAisle(String aisle) {
		this.aisle = aisle;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getConsistency() {
		return consistency;
	}
	public void setConsistency(String consistency) {
		this.consistency = consistency;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameClean() {
		return nameClean;
	}
	public void setNameClean(String nameClean) {
		this.nameClean = nameClean;
	}
	public String getOriginal() {
		return original;
	}
	public void setOriginal(String original) {
		this.original = original;
	}
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public List<String> getMeta() {
		return meta;
	}
	public void setMeta(List<String> meta) {
		this.meta = meta;
	}
	public MeasuresDTO getMeasures() {
		return measures;
	}
	public void setMeasures(MeasuresDTO measures) {
		this.measures = measures;
	}
    

}
