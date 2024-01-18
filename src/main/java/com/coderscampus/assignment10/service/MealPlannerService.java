package com.coderscampus.assignment10.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.coderscampus.assignment10.dto.Meal;
import com.coderscampus.assignment10.dto.RecipeDetailsDTO;
import com.coderscampus.assignment10.dto.WeeklyPlanner;
import com.coderscampus.assignment10.dto.WeeklyPlannerResponse;
import com.coderscampus.assignment10.repository.RecipeDetailsRepository;
import com.coderscampus.assignment10.repository.WeeklyPlannerRepository;
import com.coderscampus.assignment10.web.UriStringBuilder;

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
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
