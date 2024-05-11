package com.projects.cavany.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;


import com.projects.cavany.domain.RecipeDetails.WinePairing;

public interface WinePairingRepositoryNeo4j extends Neo4jRepository<WinePairing, Long>{
    @Query("MERGE (wp:WinePairing {pairingText: coalesce($winePairing.pairingText, '')}) " +
            "ON CREATE SET wp.pairedWines = CASE WHEN $winePairing.pairedWines IS NULL THEN [] ELSE $winePairing.pairedWines END " +
            "ON MATCH SET wp.pairedWines = CASE WHEN $winePairing.pairedWines IS NULL THEN [] ELSE $winePairing.pairedWines END " +
            "RETURN wp")
     WinePairing mergeWinePairing(@Param("winePairing") WinePairing winePairing);
    
    @Query("MATCH (r:RecipeDetails {Id: $recipeId})-[rel:HAS_WINE_PAIRING]->(wp:WinePairing) DETACH DELETE wp")
    void deleteWinePairingByRecipeId(Long recipeId);
    

}
