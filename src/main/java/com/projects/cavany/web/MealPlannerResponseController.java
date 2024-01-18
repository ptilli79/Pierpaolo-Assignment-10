package com.projects.cavany.web;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.projects.cavany.dto.DailyPlanner;
import com.projects.cavany.dto.Meal;
import com.projects.cavany.dto.RecipeDetailsDTO;
import com.projects.cavany.dto.WeeklyPlanner;
import com.projects.cavany.dto.WeeklyPlannerResponse;
import com.projects.cavany.repository.DailyPlannerRepository;
import com.projects.cavany.repository.RecipeDetailsRepository;
import com.projects.cavany.repository.WeeklyPlannerRepository;
import com.projects.cavany.service.MealPlannerService;

//import ch.qos.logback.classic.Logger;
@RestController
public class MealPlannerResponseController {
	
		@Autowired
		private WeeklyPlannerRepository weekPlannerRepo;
	
	@Autowired
	private DailyPlannerRepository dayPlannerRepo;
	
	@Autowired
	private RecipeDetailsRepository recipeDetailsInfo;
	
    @Autowired
    private MealPlannerService mealPlannerService;
	
	@Autowired
	private UriStringBuilder uriString;
	
	//@SuppressWarnings("unchecked")
	@RequestMapping("/mealplanner/week")
	public WeeklyPlannerResponse getWeekMeals(String targetCalories, String diet, String exclude) {
	    RestTemplate rt = new RestTemplate();
	    ResponseEntity<WeeklyPlannerResponse> responseEntity = rt.getForEntity(generateUriHelper(uriString.toString(), targetCalories, diet, exclude, "week"), WeeklyPlannerResponse.class);
	    WeeklyPlannerResponse plannerResponse = responseEntity.getBody();
	    
        // Instantiate the MealPlannerService here and process the planner response
        mealPlannerService.processAndSavePlanner(plannerResponse);
        // You can also return the saved WeeklyPlannerResponse or another appropriate response
        return plannerResponse;
	}
		
	@GetMapping("/mealplanner/day")
	public DailyPlanner getDayMeals(String targetCalories, String diet, String exclude){
		RestTemplate rt = new RestTemplate();
		//System.out.println(generateUriHelper(targetCalories, diet, exclude, "week"));
		ResponseEntity<DailyPlanner> responseEntity = rt.getForEntity(generateUriHelper(uriString.toString(), targetCalories, diet, exclude, "day"), DailyPlanner.class);
		//System.out.println(responseEntity.getBody().toString());
		dayPlannerRepo.save(responseEntity.getBody());
		return dayPlannerRepo.save(responseEntity.getBody());
	}
	
	@GetMapping("/recipe/{recipeId}")
	public ResponseEntity<?> getRecipeInformation(@PathVariable String recipeId) {
	    try {
	        RestTemplate rt = new RestTemplate();
	        ResponseEntity<RecipeDetailsDTO> responseEntity = rt.getForEntity(uriString.toStringRecipeInformation(recipeId), RecipeDetailsDTO.class);

	        if (responseEntity.getStatusCode().is2xxSuccessful()) {
	            RecipeDetailsDTO recipeDetails = responseEntity.getBody();
	            if (recipeDetails != null) {
	                recipeDetailsInfo.save(recipeDetails);
	                return ResponseEntity.ok(recipeDetails);
	            }
	        }

	        // Handle the case where the recipe is not found or the request fails
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body("{\"status\":\"failure\", \"code\":404,\"message\":\"A recipe with the id " + recipeId + " does not exist.\"}");
	    } catch (HttpClientErrorException.NotFound ex) {
	        // Handle the 404 error gracefully
	        return ResponseEntity.notFound().build();
	    } catch (Exception ex) {
	        // Handle other exceptions as needed
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	
	@GetMapping("/mealplanner/weeklyMealsRepo")
	public Map<Long, WeeklyPlannerResponse> getWeeklyMeals(){
		return weekPlannerRepo.getAll();
	}
	
	@GetMapping("/mealplanner/dailyMealsRepo")
	public Map<Long, DailyPlanner> getDailyMeals(){
		return dayPlannerRepo.getAll();
	}
	
	@GetMapping("/recipe/detailsRepo")
	public Map<Long, RecipeDetailsDTO> getRecipes(){
		return recipeDetailsInfo.getAll();
	}
	
	private URI generateUriHelper(String uriString, String targetCalories, String diet, String exclude, String timeFrame) {
		//String url = uriString.toString();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uriString);
		builder.queryParam("timeFrame", timeFrame);
		
		if (!(targetCalories==null || targetCalories.length()==0)) {
			builder = builder.queryParam("targetCalories", Integer.parseInt(targetCalories));
		}
		if (!(diet==null || diet.length()==0)) {
			builder = builder.queryParam("diet", diet);
		}
		if (!(exclude==null || exclude.length()==0)) {
			builder = builder.queryParam("exclude", exclude);
		}
		return (builder.build().toUri());
	}
}

	
	
	
	
	

