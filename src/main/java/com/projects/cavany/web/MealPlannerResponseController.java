package com.projects.cavany.web;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.projects.cavany.dto.BulkRecipeDetailsDTO;
import com.projects.cavany.dto.ComplexSearchResultItemDTO;
import com.projects.cavany.dto.ComplexSearchResultsDTO;
import com.projects.cavany.dto.DailyPlanner;
import com.projects.cavany.dto.Meal;
import com.projects.cavany.dto.RandomSearchResponse;
import com.projects.cavany.dto.RecipeDetailsDTO;
import com.projects.cavany.dto.WeeklyPlanner;
import com.projects.cavany.dto.WeeklyPlannerResponse;
import com.projects.cavany.repository.DailyPlannerRepository;
import com.projects.cavany.repository.RecipeDetailsRepository;
import com.projects.cavany.repository.WeeklyPlannerRepository;
import com.projects.cavany.service.GenerateRecipeService;
import com.projects.cavany.service.MealPlannerService;

//import ch.qos.logback.classic.Logger;
@RestController
public class MealPlannerResponseController {
	
	@Autowired
	private WeeklyPlannerRepository weekPlannerRepo;
	
	@Autowired
	private DailyPlannerRepository dayPlannerRepo;
	
	@Autowired
	private RecipeDetailsRepository recipeDetailsRepo;
	
    @Autowired
    private MealPlannerService mealPlannerService;
    
    @Autowired
    private GenerateRecipeService generateRecipeService;
	
	@Autowired
	private UriStringBuilder uriString;
	
    @Autowired
    private RecipeDetailsRepository recipeDetailsRepository;
	
    private int maxRequestsPerSecond = 4; // Change this value as needed
    private long rateLimitIntervalMillis = 1000; // 1000 milliseconds (1 second)
    private static final int maxIds = 5000;
    private static final int batchSize = 50;
    String recipesFilePath = "C:\\Users\\pierp\\OneDrive\\Documentos\\MyRepository\\JavaBootcamp\\bootcamp-pierpaolo\\JavaBootcamp-Workspace\\Cavany\\output\\recipes.csv";

	
	//@SuppressWarnings("unchecked")
	@GetMapping("/mealplanner/week")
	public WeeklyPlannerResponse getWeekMeals(String targetCalories, String diet, String exclude) {
	    RestTemplate rt = new RestTemplate();
	    System.out.println(generateUriHelper(uriString.toString(), targetCalories, diet, exclude, "week"));
	    ResponseEntity<WeeklyPlannerResponse> responseEntity = rt.getForEntity(generateUriHelper(uriString.toString(), targetCalories, diet, exclude, "week"), WeeklyPlannerResponse.class);
	    WeeklyPlannerResponse plannerResponse = responseEntity.getBody();
	    
        // Instantiate the MealPlannerService here and process the planner response
        mealPlannerService.processAndSavePlanner(plannerResponse);
        // You can also return the saved WeeklyPlannerResponse or another appropriate response
        return plannerResponse;
	}
	
	@RequestMapping("/recipes/random")
	public ResponseEntity<RandomSearchResponse> fetchAndSaveRandomRecipes(String targetCalories, String diet, String exclude, Integer number, Integer offset) {
	    RestTemplate rt = new RestTemplate();

	    // Create headers with "Accept: application/json"
	    HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

	    // Set your API key as a query parameter
	    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uriString.toStringRandomSearch());
	    // Replace with your actual API key

	    if (!(diet == null || diet.length() == 0)) {
	        builder.queryParam("diet", diet);
	    }
	    if (!(targetCalories == null || targetCalories.length() == 0)) {
	        builder.queryParam("targetCalories", Integer.parseInt(targetCalories));
	    }
	    if (!(exclude == null || exclude.length() == 0)) {
	        builder.queryParam("exclude", exclude);
	    }
	    if (!(number == null)) {
	        builder.queryParam("number", number);
	    }
	    if (!(offset == null)) {
	        builder.queryParam("offset", offset);
	    }

	    builder.queryParam("apiKey", uriString.getApiKey());

	    // Create a request entity with the headers
	    HttpEntity<?> requestEntity = new HttpEntity<>(headers);

	    // Send the request with the request entity
	    System.out.println(builder.toUriString());
	    ResponseEntity<RandomSearchResponse> responseEntity = rt.exchange(
	            builder.toUriString(),
	            HttpMethod.GET,
	            requestEntity,
	            RandomSearchResponse.class // Specify the response type
	    );

	    RandomSearchResponse randomSearchResponse = responseEntity.getBody();

	    try {
	        // Iterate through the received list of recipes and save each one
	        List<RecipeDetailsDTO> recipes = randomSearchResponse.getRecipes(); // Get the list of recipes from the response

	        for (RecipeDetailsDTO recipe : recipes) {
	            // Check if the recipe with this ID exists in the repository
	            Long recipeId = recipe.getId();
	            RecipeDetailsDTO existingRecipe = recipeDetailsRepository.getRecipeById(recipeId);

	            if (existingRecipe == null) {
	                // Recipe with the given ID doesn't exist in the repository, save it
	                recipeDetailsRepository.save(recipe);
	            }
	        }
	        
	        generateRecipeService.generateRecipeCSV(recipeDetailsRepo.getAll(), recipesFilePath);
	        
	        return ResponseEntity.ok(randomSearchResponse);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(randomSearchResponse);
	    }
	}



//	        try {
	            // Iterate through the received list of recipes and save each one
//	            for (RecipeDetailsDTO recipe : recipes) {
	                // Check if the recipe with this ID exists in the repository
//	                Long recipeId = recipe.getId();
//	                RecipeDetailsDTO existingRecipe = recipeDetailsRepository.getRecipeById(recipeId);

//	                if (existingRecipe == null) {
	                    // Recipe with the given ID doesn't exist in the repository, save it
//	                    recipeDetailsRepository.save(recipe);
//	                }
//	            }
//	            return ResponseEntity.ok("Recipes saved successfully.");
//	        } catch (Exception e) {
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving recipes.");
//	        }

	
	
	
	
	@RequestMapping("/complex/search")
	public ComplexSearchResultsDTO getSearch(String targetCalories, String diet, String exclude, Integer number, Integer offset) throws IOException {
	    RestTemplate rt = new RestTemplate();

	    // Create headers with "Accept: application/json"
	    HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

	    // Set your API key as a query parameter
	    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uriString.toStringComplexSearch());
	             // Replace with your actual API key
	    
	    if (!(diet == null || diet.length() == 0)) {
	        builder.queryParam("diet", diet);
	    }
		if (!(targetCalories==null || targetCalories.length()==0)) {
			builder.queryParam("targetCalories", Integer.parseInt(targetCalories));
		}
	    if (!(exclude == null || exclude.length() == 0)) {
	        builder.queryParam("exclude", exclude);
	    }
	    if (!(number == null)) {
	        builder.queryParam("number", number);
	    }
	    if (!(offset == null)) {
	        builder.queryParam("offset", offset);
	    }
	    		
	    builder.queryParam("apiKey", uriString.getApiKey());

	    // Create a request entity with the headers
	    HttpEntity<?> requestEntity = new HttpEntity<>(headers);

	    // Send the request with the request entity
	    System.out.println(builder.toUriString());
	    ResponseEntity<ComplexSearchResultsDTO> responseEntity = rt.exchange(
	        builder.toUriString(),
	        HttpMethod.GET,
	        requestEntity, 
	        ComplexSearchResultsDTO.class
	    );
	    ComplexSearchResultsDTO complexSearchResponse = responseEntity.getBody();
	    
	    //now get the recipe information of each recipe and saved it in the repo
	    int requestsMade = 0;
        // Save each recipe's information into your repository
	    List<ComplexSearchResultItemDTO> results = complexSearchResponse.getResults();
	    for (ComplexSearchResultItemDTO result : results) {
	        Long recipeId = result.getId();
	        // Check if the recipe with this ID exists in the repository
	        RecipeDetailsDTO existingRecipe = recipeDetailsRepo.getRecipeById(recipeId);

	        if (existingRecipe == null) {
                if (requestsMade >= maxRequestsPerSecond) {
                    try {
                        Thread.sleep(rateLimitIntervalMillis);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    requestsMade = 0; // Reset the request counter
                }
	            // Recipe with the given ID doesn't exist in the repository, fetch its details
	            ResponseEntity<RecipeDetailsDTO> recipeResponseEntity = rt.exchange(
	                uriString.toStringRecipeInformation(Long.toString(recipeId)),
	                HttpMethod.GET,
	                requestEntity,
	                RecipeDetailsDTO.class
	            );

	            if (recipeResponseEntity.getStatusCode().is2xxSuccessful()) {
	                RecipeDetailsDTO recipeDetails = recipeResponseEntity.getBody();
	                if (recipeDetails != null) {
	                    // Save the recipe in the repository
	                    recipeDetailsRepo.save(recipeDetails);
	                }
	            }
	            requestsMade++;
	        }
	    }
	    
	    generateRecipeService.generateRecipeCSV(recipeDetailsRepo.getAll(), recipesFilePath);
	    
        return complexSearchResponse;
	}
	
	   @GetMapping("/recipes/informationBulk")
	    public ResponseEntity<BulkRecipeDetailsDTO[]> getRecipes(@RequestParam("ids") String ids) {
	        String url = uriString.toStringRecipeBulkInformation() +"?ids="+ ids + "&apiKey=" + uriString.getApiKey();
	        RestTemplate restTemplate = new RestTemplate();
	        System.out.println(url);
	        ResponseEntity<BulkRecipeDetailsDTO[]> response = restTemplate.getForEntity(url, BulkRecipeDetailsDTO[].class);
	        
	        // New Logic to save recipes and check if they already exist
	        List<BulkRecipeDetailsDTO> recipesToSave = new ArrayList<>(Arrays.asList(response.getBody()));
	        for (BulkRecipeDetailsDTO recipe : recipesToSave) {
	            RecipeDetailsDTO existingRecipe = recipeDetailsRepository.getRecipeById(recipe.getId());
	            if (existingRecipe == null) {
	                recipeDetailsRepository.save(recipe);
	            } else {
	                System.out.println("Recipe with ID " + recipe.getId() + " already exists in the repository.");
	            }
	        }

	        return response;
	    }
	
	    @GetMapping("/recipes/fetchRecipes")
	    public void fetchRecipes() throws IOException {
	        RestTemplate restTemplate = new RestTemplate();
	        int requestsMade = 0;

	        for (int i = 0; i < maxIds; i += batchSize) {
	            StringBuilder ids = new StringBuilder();
	            for (int j = i; j < i + batchSize && j < maxIds; j++) {
	                if (j > i) ids.append(",");
	                ids.append(j);
	            }

	            String url = uriString.toStringRecipeBulkInformation() + "?ids=" + ids.toString() + "&apiKey=" + uriString.getApiKey();

	            if (requestsMade >= maxRequestsPerSecond) {
	                try {
	                    Thread.sleep(rateLimitIntervalMillis);
	                } catch (InterruptedException e) {
	                    Thread.currentThread().interrupt();
	                }
	                requestsMade = 0;
	            }
	            System.out.println(url);
	            ResponseEntity<BulkRecipeDetailsDTO[]> response = restTemplate.getForEntity(url, BulkRecipeDetailsDTO[].class);
	            requestsMade++;

	            // Handle the response - save to repository
	            Arrays.stream(response.getBody()).forEach(recipe -> {
	                RecipeDetailsDTO existingRecipe = recipeDetailsRepository.getRecipeById(recipe.getId());
	                if (existingRecipe == null) {
	                    recipeDetailsRepository.save(recipe);
	                } else {
	                    System.out.println("Recipe with ID " + recipe.getId() + " already exists in the repository.");
	                }
	            });
	            generateRecipeService.generateRecipeCSV(recipeDetailsRepo.getAll(), recipesFilePath);
	        }
	    }   
	    
	    
	@RequestMapping("/mealplanner/day")
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
	                recipeDetailsRepo.save(recipeDetails);
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
		return recipeDetailsRepo.getAll();
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

	
	
	
	
	

