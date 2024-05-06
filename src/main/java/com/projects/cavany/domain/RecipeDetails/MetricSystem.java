package com.projects.cavany.domain.RecipeDetails;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node
public class MetricSystem {
	@Id @GeneratedValue
	private Long Id;
	@Property
    private double amount;
	@Property
    private String unitShort;
	@Property
    private String unitLong;
	
	//Getters and setters
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getUnitShort() {
		return unitShort;
	}
	public void setUnitShort(String unitShort) {
		this.unitShort = unitShort;
	}
	public String getUnitLong() {
		return unitLong;
	}
	public void setUnitLong(String unitLong) {
		this.unitLong = unitLong;
	}
}
