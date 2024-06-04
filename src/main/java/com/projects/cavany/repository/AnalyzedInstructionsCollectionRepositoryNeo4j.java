package com.projects.cavany.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction.AnalyzedInstruction;
import com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction.AnalyzedInstructionsCollection;

@Repository
public interface AnalyzedInstructionsCollectionRepositoryNeo4j extends Neo4jRepository<AnalyzedInstructionsCollection, Long> {
	Optional<AnalyzedInstructionsCollection> findRecipeById(Long recipeId);

	@Query("MATCH (a:AnalyzedInstructionsCollection {id: $recipeId})-[r:HAS_ANALYZED_INSTRUCTIONS]->(step:AnalyzedInstruction)DETACH DELETE a, step")
	void deleteInstructionsById(Long recipeId);
	

	@Override
	AnalyzedInstructionsCollection save(AnalyzedInstructionsCollection newInstructions);
}
