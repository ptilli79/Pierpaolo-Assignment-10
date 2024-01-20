package com.projects.cavany.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.projects.cavany.dto.Meal;
import com.projects.cavany.dto.RecipeDetailsDTO;
import com.projects.cavany.dto.WeeklyPlanner;
import com.projects.cavany.dto.WeeklyPlannerResponse;
import com.projects.cavany.repository.RecipeDetailsRepository;
import com.projects.cavany.repository.WeeklyPlannerRepository;
import com.projects.cavany.web.UriStringBuilder;

@Service
public class MealPlannerService {
	
	@Autowired
	private WeeklyPlannerRepository weekPlannerRepo;

    @Autowired
    private RecipeDetailsRepository recipeDetailsInfo;
    
	@Autowired
	private UriStringBuilder uriString;
	
	@Autowired
	private RestTemplate restTemplate;
	
    private int maxRequestsPerSecond = 4; // Change this value as needed
    private long rateLimitIntervalMillis = 250; // 1000 milliseconds (1 second)

    public WeeklyPlannerResponse processAndSavePlanner(WeeklyPlannerResponse plannerResponse) {
        if (plannerResponse != null && plannerResponse.getWeek() != null) {
            WeeklyPlanner weeklyPlanner = plannerResponse.getWeek();
            List<RecipeDetailsDTO> savedRecipes = new ArrayList<>();

            int requestsMade = 0;

            // Iterate over all available days of the week
            for (String day : weeklyPlanner.getAllDaysOfWeek()) {
                List<Meal> meals = weeklyPlanner.getMealsForDay(day);

                for (Meal meal : meals) {
                    // Check if we've reached the rate limit, and if so, wait before making the next request
                    if (requestsMade >= maxRequestsPerSecond) {
                        try {
                            Thread.sleep(rateLimitIntervalMillis);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        requestsMade = 0; // Reset the request counter
                    }
                    String recipeId = Long.toString(meal.getId());
                    ResponseEntity<RecipeDetailsDTO> recipeResponseEntity = restTemplate.getForEntity(uriString.toStringRecipeInformation(recipeId), RecipeDetailsDTO.class);

                    if (recipeResponseEntity.getStatusCode().is2xxSuccessful()) {
                        RecipeDetailsDTO recipeDetails = recipeResponseEntity.getBody();
                        if (recipeDetails != null) {
                            savedRecipes.add(recipeDetails);
                        }
                    }
                    requestsMade++;
                }
            }

            // Save the list of recipes to your repository
            recipeDetailsInfo.saveAll(savedRecipes);

            // Optionally, remove the "allDaysOfWeek" field from the planner response
            // weeklyPlanner.getAllDaysOfWeek().clear();

            // Save the planner response to your repository (if needed)
            weekPlannerRepo.save(plannerResponse);

            return plannerResponse;
        } else {
            // Handle the case where the planner response is null or meals are not found
            return null; // Or provide an appropriate response
        }
    }
    

}
