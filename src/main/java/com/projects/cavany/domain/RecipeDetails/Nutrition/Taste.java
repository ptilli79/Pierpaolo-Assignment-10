package com.projects.cavany.domain.RecipeDetails.Nutrition;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class Taste {
    @Id @GeneratedValue private Long id;
    private double sweetness;
    private double saltiness;
    private double sourness;
    private double bitterness;
    private double savoriness;
    private double fattiness;
    private double spiciness;
    
    // Getters and setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public double getSweetness() {
		return sweetness;
	}
	public void setSweetness(double sweetness) {
		this.sweetness = sweetness;
	}
	public double getSaltiness() {
		return saltiness;
	}
	public void setSaltiness(double saltiness) {
		this.saltiness = saltiness;
	}
	public double getSourness() {
		return sourness;
	}
	public void setSourness(double sourness) {
		this.sourness = sourness;
	}
	public double getBitterness() {
		return bitterness;
	}
	public void setBitterness(double bitterness) {
		this.bitterness = bitterness;
	}
	public double getSavoriness() {
		return savoriness;
	}
	public void setSavoriness(double savoriness) {
		this.savoriness = savoriness;
	}
	public double getFattiness() {
		return fattiness;
	}
	public void setFattiness(double fattiness) {
		this.fattiness = fattiness;
	}
	public double getSpiciness() {
		return spiciness;
	}
	public void setSpiciness(double spiciness) {
		this.spiciness = spiciness;
	}
}