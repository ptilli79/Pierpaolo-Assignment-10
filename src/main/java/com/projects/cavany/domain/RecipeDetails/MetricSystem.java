package com.projects.cavany.domain.RecipeDetails;

import java.util.UUID;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node
public class MetricSystem {
    @Id @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID uuid;  // This should be UUID instead of Long
    @Property
    private Long Id;  // This keeps the original ID from the external API.
	@Property
    private double amount;
	@Property
    private String unitShort;
	@Property
    private String unitLong;
	
	//Getters and setters
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
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
