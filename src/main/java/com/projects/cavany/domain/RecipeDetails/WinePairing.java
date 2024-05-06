package com.projects.cavany.domain.RecipeDetails;

import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class WinePairing {
	@Id @GeneratedValue
	private Long Id;
	@Property
    private List<String> pairedWines;
	@Property
    private String pairingText;
	
    @Relationship(type = "HAS_PRODUCT_MATCH", direction = Relationship.Direction.OUTGOING)
    private List<ProductMatch> productMatches;
    
    // Getters and setters for all fields
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public List<String> getPairedWines() {
		return pairedWines;
	}
	public void setPairedWines(List<String> pairedWines) {
		this.pairedWines = pairedWines;
	}
	public String getPairingText() {
		return pairingText;
	}
	public void setPairingText(String pairingText) {
		this.pairingText = pairingText;
	}
	public List<ProductMatch> getProductMatches() {
		return productMatches;
	}
	public void setProductMatches(List<ProductMatch> productMatches) {
		this.productMatches = productMatches;
	}
}
