package com.projects.cavany.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import com.projects.cavany.domain.RecipeDetails.WinePairing;
import com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction.AnalyzedInstruction;

public interface AnalyzedInstructionRepositoryNeo4j extends Neo4jRepository<AnalyzedInstruction, Long>{
    @Query("MERGE (ai:AnalyzedInstruction {id: randomUUID()}) " +
            "ON CREATE SET ai.name = CASE WHEN $instruction.name IS NULL OR $instruction.name = '' THEN 'N/A' ELSE $instruction.name END " +
            "ON MATCH SET ai.name = CASE WHEN $instruction.name IS NULL OR $instruction.name = '' THEN 'N/A' ELSE $instruction.name END " +
            "RETURN ai")
     AnalyzedInstruction mergeInstruction(@Param("instruction") AnalyzedInstruction instruction);
    
    @Query("MATCH (r:RecipeDetails {Id: $recipeId})-[rel:HAS_PREPARATION_INSTRUCTIONS]->(ai:AnalyzedInstruction) DETACH DELETE ai")
    void deleteInstructionsByRecipeId(Long recipeId);

	List<AnalyzedInstruction> save(List<AnalyzedInstruction> newInstructions);
	
    @Query("MATCH (a:AnalyzedInstructionsCollection) RETURN max(a.id)")
    Optional<Long> findMaxId();
}
