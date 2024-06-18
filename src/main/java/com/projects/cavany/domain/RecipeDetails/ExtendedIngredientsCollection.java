package com.projects.cavany.domain.RecipeDetails;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class ExtendedIngredientsCollection {
	@Id
	Long id;
	
	@Relationship(type = "HAS_INGREDIENT", direction = Relationship.Direction.OUTGOING)
    private List<ExtendedIngredient> extendedIngredients;
	
    public ExtendedIngredientsCollection(Long id) {
    	this.id = id;  // Assuming RecipeDetails has a getId() method returning Long
	}
	
    //Getters and Setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public List<ExtendedIngredient> getExtendedIngredients() {
		return extendedIngredients;
	}

	public void setExtendedIngredients(List<ExtendedIngredient> extendedIngredients) {
		this.extendedIngredients = extendedIngredients;
	}


}
