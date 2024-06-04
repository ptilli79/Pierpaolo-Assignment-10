package com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class AnalyzedInstructionsCollection {
	@Id
	Long id;
	
	@Relationship(type = "HAS_ANALYZED_INSTRUCTIONS", direction = Relationship.Direction.OUTGOING)
    private List<AnalyzedInstruction> analyzedInstructions;
	
    public AnalyzedInstructionsCollection(Long id) {
    	this.id = id;  // Assuming RecipeDetails has an getId() method returning Long
	}
	
    //Getters and Setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<AnalyzedInstruction> getAnalyzedInstructions() {
		return analyzedInstructions;
	}
	public void setAnalyzedInstructions(List<AnalyzedInstruction> analyzedInstructions) {
		this.analyzedInstructions = analyzedInstructions;
	}
}
