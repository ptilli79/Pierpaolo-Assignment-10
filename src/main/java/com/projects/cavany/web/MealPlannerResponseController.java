package com.projects.cavany.web;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.projects.cavany.domain.ExtendedIngredient;
import com.projects.cavany.domain.Measures;
import com.projects.cavany.domain.MetricSystem;
import com.projects.cavany.domain.RecipeDetails;
import com.projects.cavany.dto.BulkRecipeDetailsDTO;
import com.projects.cavany.dto.ComplexSearchResultItemDTO;
import com.projects.cavany.dto.ComplexSearchResultsDTO;
import com.projects.cavany.dto.DailyPlanner;
import com.projects.cavany.dto.ExtendedIngredientDTO;
import com.projects.cavany.dto.Meal;
import com.projects.cavany.dto.MeasuresDTO;
import com.projects.cavany.dto.RandomSearchResponse;
import com.projects.cavany.dto.RecipeDetailsDTO;
import com.projects.cavany.dto.WeeklyPlanner;
import com.projects.cavany.dto.WeeklyPlannerResponse;
import com.projects.cavany.repository.DailyPlannerRepository;
import com.projects.cavany.repository.ExtendedIngredientRepositoryNeo4j;
import com.projects.cavany.repository.RecipeDetailsRepository;
import com.projects.cavany.repository.RecipeDetailsRepositoryNeo4j;
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
    
    @Autowired
    private RecipeDetailsRepositoryNeo4j recipeDetailsRepositoryNeo4j;
    
    @Autowired
    private ExtendedIngredientRepositoryNeo4j extendedIngredientRepositoryNeo4j;
	
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
	        String url = uriString.toStringRecipeBulkInformation() +"?ids="+ ids + "&includeNutrition=true&apiKey=" + uriString.getApiKey();
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

	        // Fetch existing recipe IDs from Neo4j
	        Set<Long> existingIds = recipeDetailsRepositoryNeo4j.findAll().stream()
	        	    .map(RecipeDetails::getId)
	        	    .collect(Collectors.toSet());
	        
	        Optional<Long> maxIdInDb = recipeDetailsRepositoryNeo4j.findMaxId();
	        long startId = maxIdInDb.isPresent() ? maxIdInDb.get() + 1 : 0;

	        for (long i = startId; i < maxIds; i += batchSize) {
	            StringBuilder ids = new StringBuilder();
	            for (long j = i; j < i + batchSize && j < maxIds; j++) {
	                if (!existingIds.contains((long) j)) {
	                    if (ids.length() > 0) ids.append(",");
	                    ids.append(j);
	                }
	            }
	            if (ids.length() == 0) continue; // Skip API call if all IDs exist

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
	            Arrays.stream(response.getBody()).forEach(recipeDetailsDTO -> {
	                RecipeDetails recipe = convertToRecipeEntity(recipeDetailsDTO);
	                recipeDetailsRepositoryNeo4j.save(recipe);
	            });
	            //generateRecipeService.generateRecipeCSV(recipeDetailsRepo.getAll(), recipesFilePath);
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
	
	private RecipeDetails convertToRecipeEntity(RecipeDetailsDTO dto) {
	    RecipeDetails recipe = new RecipeDetails();
	    // Set properties from DTO to Recipe entity
	    recipe.setId(dto.getId());
	    recipe.setTitle(dto.getTitle());
	    recipe.setVegan(dto.isVegan());
	    recipe.setVegetarian(dto.isVegetarian());
	    recipe.setGlutenFree(dto.isGlutenFree());
	    recipe.setDairyFree(dto.isDairyFree());
	    recipe.setVeryHealthy(dto.isVeryHealthy());
	    recipe.setCheap(dto.isCheap());
	    recipe.setVeryPopular(dto.isVeryPopular());
	    recipe.setSustainable(dto.isSustainable());
	    recipe.setLowFodmap(dto.isLowFodmap());
	    recipe.setWeightWatcherSmartPoints(dto.getWeightWatcherSmartPoints());
	    recipe.setGaps(dto.getGaps());
	    recipe.setPreparationMinutes(dto.getPreparationMinutes());
	    recipe.setCookingMinutes(dto.getCookingMinutes());
	    recipe.setAggregateLikes(dto.getAggregateLikes());
	    recipe.setHealthScore(dto.getHealthScore());
	    recipe.setCreditsText(dto.getCreditsText());
	    recipe.setSourceName(dto.getSourceName());
	    recipe.setPricePerServing(dto.getPricePerServing());
	    recipe.setReadyInMinutes(dto.getReadyInMinutes());
	    recipe.setServings(dto.getServings());
	    recipe.setSourceUrl(dto.getSourceUrl());
	    recipe.setImage(dto.getImage());
	    recipe.setImageType(dto.getImageType());
	    recipe.setSummary(dto.getSummary());
	    recipe.setCuisines(dto.getCuisines());
	    recipe.setDishTypes(dto.getDishTypes());
	    recipe.setDiets(dto.getDiets());
	    recipe.setOccasions(dto.getOccasions());
	    // Convert WinePairingDTO if necessary or handle it according to your logic
	    recipe.setInstructions(dto.getInstructions());
	    recipe.setOriginalId(dto.getOriginalId());
	    recipe.setSpoonacularScore(dto.getSpoonacularScore());
	    recipe.setSpoonacularSourceUrl(dto.getSpoonacularSourceUrl());
	    
	 // Convert extendedIngredients from DTO to Entity
	    List<ExtendedIngredient> extendedIngredients = new ArrayList<>();
	    for (ExtendedIngredientDTO ingredientDTO : dto.getExtendedIngredients()) {
	        ExtendedIngredient ingredient = new ExtendedIngredient();
	        MeasuresDTO measuresDto = ingredientDTO.getMeasures(); // Get the MeasuresDto from ExtendedIngredientDTO
	        if (measuresDto != null) {
	           
	        MetricSystem usMetric = new MetricSystem();
	        usMetric.setAmount(measuresDto.getUs().getAmount());
	        usMetric.setUnitShort(measuresDto.getUs().getUnitShort());
	        usMetric.setUnitLong(measuresDto.getUs().getUnitLong());
	           
	        MetricSystem metricMetric = new MetricSystem();
	        metricMetric.setAmount(measuresDto.getMetric().getAmount());
	        metricMetric.setUnitShort(measuresDto.getMetric().getUnitShort());
	        metricMetric.setUnitLong(measuresDto.getMetric().getUnitLong());
	       

	            // Now set the measures in ingredient
	        ingredient.setUs(usMetric);
	        ingredient.setMetric(metricMetric);
	        }
	        ingredient.setId(ingredientDTO.getId());
	        ingredient.setAisle(ingredientDTO.getAisle());
	        ingredient.setImage(ingredientDTO.getImage());
	        ingredient.setConsistency(ingredientDTO.getConsistency());
	        ingredient.setName(ingredientDTO.getName());
	        ingredient.setNameClean(ingredientDTO.getNameClean());
	        ingredient.setOriginal(ingredientDTO.getOriginal());
	        ingredient.setOriginalName(ingredientDTO.getOriginalName());
	        ingredient.setAmount(ingredientDTO.getAmount());
	        ingredient.setUnit(ingredientDTO.getUnit());
	        ingredient.setMeta(ingredientDTO.getMeta());
	        // Set relationship if necessary
	        // ingredient.setRecipeDetails(associatedRecipeDetails); // Set this if you are associating with a RecipeDetails entity
	        extendedIngredients.add(ingredient);

	        //Insert here logic to convert MeasuresDto to Measures
	        
	    }

	    recipe.setExtendedIngredients(extendedIngredients);
	    return recipe;
	}
}

	
	
	
	
	

