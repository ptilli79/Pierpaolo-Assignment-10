package com.projects.cavany.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;


//@Node
public class Measures {
	@Id @GeneratedValue 
	private Long Id;
	
	//@Relationship(type = "HAS_IMPERIAL_UNITS", direction = Relationship.Direction.OUTGOING)
    private MetricSystem us;
	
	//@Relationship(type = "HAS_METRIC_UNITS", direction = Relationship.Direction.OUTGOING)
    private MetricSystem metric;
    
    // Getters and setters for all fields
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		this.Id = id;
	}
	public MetricSystem getUs() {
		return us;
	}
	public void setUs(MetricSystem us) {
		this.us = us;
	}
	public MetricSystem getMetric() {
		return metric;
	}
	public void setMetric(MetricSystem metric) {
		this.metric = metric;
	}
    
}
