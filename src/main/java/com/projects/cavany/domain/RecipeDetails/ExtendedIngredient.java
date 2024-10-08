package com.projects.cavany.domain.RecipeDetails;

import java.util.List;
import java.util.UUID;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class ExtendedIngredient {
    @Id @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID uuid;  // This should be UUID instead of Long
    @Property
    private Long id;  // This keeps the original ID from the external API.
	@Property
    private String aisle;
	@Property
    private String image;
	@Property
    private String consistency;
	@Property
    private String name;
	@Property
    private String nameClean;
	@Property
    private String original;
	@Property
    private String originalName;
	@Property
    private double amount;
	@Property
    private String unit;
	@Property
    private List<String> meta;

	//@Relationship(type = "HAS_IMPERIAL_UNITS", direction = Relationship.Direction.OUTGOING)
    //private MetricSystem us;
	//@Relationship(type = "HAS_METRIC_UNITS", direction = Relationship.Direction.OUTGOING)
    //private MetricSystem metric;
	//Getters and Setters
	//public UUID getUuid() {
	//	return uuid;
	//}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
//	public MetricSystem getUs() {
//		return us;
//	}
//	public void setUs(MetricSystem us) {
//		this.us = us;
//	}
//	public MetricSystem getMetric() {
//		return metric;
//	}
//	public void setMetric(MetricSystem metric) {
//		this.metric = metric;
//	}
	
	//public Measures getMeasures() {
	//	return measures;
	//}
	//public void setMeasures(Measures measures) {
	//	this.measures = measures;
	//}
}
