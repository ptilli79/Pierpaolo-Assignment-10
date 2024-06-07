package com.projects.cavany.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Repository;

import com.projects.cavany.domain.RecipeDetails.RecipeDetails;

import jakarta.transaction.Transactional;

@Repository
public class CustomRecipeDetailsRepositoryImpl implements CustomRecipeDetailsRepository {

    @Autowired
    private Neo4jTemplate neo4jTemplate;

    @Transactional
    @Override
    public void saveRecipeDetailsInBatch(List<RecipeDetails> recipeDetailsList) {
        neo4jTemplate.saveAll(recipeDetailsList);
    }
}
