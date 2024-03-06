package com.projects.cavany.web;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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

import com.projects.cavany.domain.AnalyzedInstruction;
import com.projects.cavany.domain.Equipment;
import com.projects.cavany.domain.ExtendedIngredient;
import com.projects.cavany.domain.Ingredient;
import com.projects.cavany.domain.MetricSystem;
import com.projects.cavany.domain.ProductMatch;
import com.projects.cavany.domain.RecipeDetails;
import com.projects.cavany.domain.Step;
import com.projects.cavany.domain.WinePairing;
import com.projects.cavany.dto.AnalyzedInstructionDTO;
import com.projects.cavany.dto.BulkRecipeDetailsDTO;
import com.projects.cavany.dto.ComplexSearchResultItemDTO;
import com.projects.cavany.dto.ComplexSearchResultsDTO;
import com.projects.cavany.dto.DailyPlanner;
import com.projects.cavany.dto.EquipmentDTO;
import com.projects.cavany.dto.ExtendedIngredientDTO;
import com.projects.cavany.dto.IngredientDTO;
import com.projects.cavany.dto.Meal;
import com.projects.cavany.dto.MeasuresDTO;
import com.projects.cavany.dto.ProductMatchDTO;
import com.projects.cavany.dto.RandomSearchResponse;
import com.projects.cavany.dto.RecipeDetailsDTO;
import com.projects.cavany.dto.StepDTO;
import com.projects.cavany.dto.WeeklyPlanner;
import com.projects.cavany.dto.WeeklyPlannerResponse;
import com.projects.cavany.dto.WinePairingDTO;
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
    private static final int maxIds = 65000;
    private static final int batchSize = 50;
    String recipesFilePath = "C:\\Users\\pierp\\OneDrive\\Documentos\\MyRepository\\JavaBootcamp\\bootcamp-pierpaolo\\JavaBootcamp-Workspace\\Cavany\\output\\recipes.csv";
    private final List<String> diets = Arrays.asList("Whole30"); // You can add more diets as needed
    private final Map<String, List<String>> excludedIngredientsByCategory = new HashMap<>();

    // Constructor or @PostConstruct method
    public MealPlannerResponseController() {
        excludedIngredientsByCategory.put("Sugar", Arrays.asList("sugar", "high fructose corn syrup", "agave nectar"));
        excludedIngredientsByCategory.put("Fats", Arrays.asList("margarine", "lard", "avocado", "hass avocado", "avocado cubes", "avocados", "avocado mayo", "avocadoes"));
        excludedIngredientsByCategory.put("Canned Goods", Arrays.asList(
                "5 spice powder", "canned black beans", "canned diced tomatoes", "canned garbanzo beans",
                "canned green chiles", "canned kidney beans", "canned mushrooms", "canned pinto beans",
                "canned red kidney beans", "canned tomatoes", "canned tuna", "canned white beans",
                "canned white cannellini beans"
            ));
        excludedIngredientsByCategory.put("Liquors", Arrays.asList("amaretto", "bourbon", "brandy", "beer",
        		"champagne", "cognac", "gin", "grand marnier", "kahlua", "rum", "tequila", "vodka", "whiskey",
        		"white wine", "red wine", "dry vermouth", "sweet vermouth", "scotch", "irish cream", 
        		"marsala wine"));
        // Add more categories as necessary
    }
	
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
	        AtomicInteger recipesSaved = new AtomicInteger();

	        // Fetch existing recipe IDs from Neo4j
	        Set<Long> existingIds = recipeDetailsRepositoryNeo4j.findAllIds().stream()
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
	                recipesSaved.incrementAndGet(); // Increment recipes saved counter

	                if (recipesSaved.get() % 500 == 0) { // After every 500 recipes saved
	                    try {
	                        System.out.println("Saved 500 recipes, pausing...");
	                        Thread.sleep(10000); // Wait for 10 seconds
	                    } catch (InterruptedException e) {
	                        Thread.currentThread().interrupt();
	                    }
	                }
	            });
	            //generateRecipeService.generateRecipeCSV(recipeDetailsRepo.getAll(), recipesFilePath);
	        }
	    }   
	    
	    @GetMapping("/recipes/filtered")
	    public ResponseEntity<?> fetchFilteredRecipes(
	            @RequestParam(required = false) List<String> diets,
	            @RequestParam(required = false) List<String> excludedIngredientsFromRequest,
	            @RequestParam(defaultValue = "true") boolean glutenFree) {

	        // Prepare the lists of excluded ingredients from request and constructor categories
	        List<String> allExcludedIngredients = new ArrayList<>();
	        for (String category : excludedIngredientsByCategory.keySet()) {
	            allExcludedIngredients.addAll(excludedIngredientsByCategory.get(category));
	        }
	        if (excludedIngredientsFromRequest != null) {
	            allExcludedIngredients.addAll(excludedIngredientsFromRequest);
	        }
	        
	        // Default diets list if not provided
	        if (diets == null || diets.isEmpty()) {
	            diets = List.of("whole 30"); // Default diet if none provided
	        }
	     // Prepare query strings for logging
	        String dietsString = String.join("\", \"", diets);
	        String excludedIngredientsString = allExcludedIngredients.stream()
	                .map(String::trim)
	                .collect(Collectors.joining("\", \""));

	     // Print out the Cypher query for testing
	        String printableCypherQuery = String.format(
	            "MATCH (recipe:RecipeDetails)-[:HAS_INGREDIENT]->(ingredient:ExtendedIngredient) " +
	            "WHERE ANY(d IN ['%s'] WHERE d IN recipe.diets) AND recipe.glutenFree = %b " +
	            "WITH recipe, COLLECT(DISTINCT toLower(ingredient.name)) AS ingredients " +
	            "OPTIONAL MATCH (recipe)-[:HAS_PREPARATION_INSTRUCTIONS]->(:AnalyzedInstruction)-[:HAS_STEPS]->(:Step)-[:HAS_INGREDIENTS]->(stepIngredient:Ingredient) " +
	            "WITH recipe, ingredients, COLLECT(DISTINCT toLower(stepIngredient.name)) AS stepIngredients " +
	            "WITH recipe, ingredients + stepIngredients AS allIngredientsList " +
	            "UNWIND allIngredientsList AS ingredientNames " +
	            "WITH recipe, COLLECT(DISTINCT ingredientNames) AS distinctIngredients " +
	            "WHERE NONE(ing IN distinctIngredients WHERE ing IN ['%s']) " +
	            "RETURN DISTINCT recipe.Id",
	            dietsString, glutenFree, excludedIngredientsString
	        );
	        
	        //String formattedDietsString = String.format("[\"%s\"]", String.join("\", \"", diets));
	        //String formattedAllExcludedIngredientsString = String.format("[\"%s\"]", String.join("\", \"", allExcludedIngredients));
	        
	        System.out.println("Cypher Query: " + printableCypherQuery);
	        //System.out.println("Diets: " + formattedDietsString);
	        //System.out.println("GlutenFree: " + glutenFree);
	        //System.out.println("Excluded Ingredients: " + formattedAllExcludedIngredientsString);

	        // Execute the query to fetch filtered recipe IDs
	        // Implementation depends on your Neo4j repository setup
	        // Fetch filtered recipe IDs from the repository
	        List<Long> filteredRecipeIds = recipeDetailsRepositoryNeo4j.findFilteredRecipes(
	        		diets, glutenFree, allExcludedIngredients);

	        // Handle no results
	        if (filteredRecipeIds.isEmpty()) {
	            return ResponseEntity.ok(Collections.emptyList());
	        }

	        // Fetch recipe details for the filtered IDs (limit results as needed, assuming this method exists)
	        List<RecipeDetails> limitedRecipes = recipeDetailsRepositoryNeo4j.findLimitedRecipesByIds(filteredRecipeIds);

	        // Convert the RecipeDetails entities into a simpler format
	        // This step depends on how you want to display the recipes in your application
	        List<Map<String, Object>> recipesInfo = limitedRecipes.stream()
	                .map(recipe -> {
	                    Map<String, Object> recipeMap = new HashMap<>();
	                    // Map the necessary fields from RecipeDetails to recipeMap
	                    recipeMap.put("id", recipe.getId());
	                    recipeMap.put("title", recipe.getTitle());
	                    // Add other fields as needed...
	                    return recipeMap;
	                })
	                .collect(Collectors.toList());

	        // Return the limited recipes info
	        return ResponseEntity.ok(recipesInfo);
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
	        
	        extendedIngredients.add(ingredient);	        
	    }
	    recipe.setExtendedIngredients(extendedIngredients);
	    
	 // Placeholder for new logic: Convert AnalyzedInstruction from DTO to Entity
	    List<AnalyzedInstruction> analyzedInstructions = new ArrayList<>();
	    for (AnalyzedInstructionDTO instructionDTO : dto.getAnalyzedInstructions()) {
	        AnalyzedInstruction instruction = new AnalyzedInstruction();
	        instruction.setName(instructionDTO.getName());

	        List<Step> steps = new ArrayList<>();
	        for (StepDTO stepDTO : instructionDTO.getSteps()) {
	            Step step = new Step();
	            step.setNumber(stepDTO.getNumber());
	            step.setStep(stepDTO.getStep());

	            // Convert ingredients and equipment
	            List<Ingredient> ingredients = stepDTO.getIngredients().stream()
	                .map(ingredientDTO -> convertToIngredientEntity(ingredientDTO))
	                .collect(Collectors.toList());
	            step.setIngredients(ingredients);

	            List<Equipment> equipment = stepDTO.getEquipment().stream()
	                .map(equipmentDTO -> convertToEquipmentEntity(equipmentDTO))
	                .collect(Collectors.toList());
	            step.setEquipment(equipment);

	            steps.add(step);
	        }
	        instruction.setSteps(steps);
	        analyzedInstructions.add(instruction);
	    }
	    recipe.setAnalyzedInstructions(analyzedInstructions);
	    
	    // Convert WinePairing from DTO to Entity
	    WinePairingDTO winePairingDTO = dto.getWinePairing();
	    if (winePairingDTO != null) {
	        WinePairing winePairing = new WinePairing();
	        winePairing.setPairedWines(winePairingDTO.getPairedWines());
	        winePairing.setPairingText(winePairingDTO.getPairingText());

	        List<ProductMatch> productMatches = new ArrayList<>();
	        if (winePairingDTO.getProductMatches() != null) { // Check if product matches list is not null
	            for (ProductMatchDTO productMatchDTO : winePairingDTO.getProductMatches()) {
	                ProductMatch productMatch = convertToProductMatchEntity(productMatchDTO);
	                productMatches.add(productMatch);
	            }
	        }
	        winePairing.setProductMatches(productMatches);
	        
	        recipe.setWinePairing(winePairing);
	    }
	    
	    return recipe;
	}
	
	private String buildCypherQuery(List<String> diets, List<String> excludedIngredients) {
	    StringBuilder query = new StringBuilder("MATCH (recipe:RecipeDetails) ");

	    List<String> conditions = new ArrayList<>();
	    if (!diets.isEmpty()) {
	        conditions.add("ANY(diet IN recipe.diets WHERE diet IN [" + 
	            diets.stream().map(diet -> "\"" + diet + "\"").collect(Collectors.joining(", ")) + "])");
	    }

	    if (!excludedIngredients.isEmpty()) {
	        conditions.add("NONE(ingredient IN recipe.ingredients WHERE ingredient.name IN [" + 
	            excludedIngredients.stream().map(ingredient -> "\"" + ingredient + "\"").collect(Collectors.joining(", ")) + "])");
	    }

	    if (!conditions.isEmpty()) {
	        query.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
	    }

	    query.append("RETURN recipe");
	    return query.toString();
	}
	private Ingredient convertToIngredientEntity(IngredientDTO ingredientDTO) {
	    Ingredient ingredient = new Ingredient();
	    ingredient.setId(ingredientDTO.getId());
	    ingredient.setName(ingredientDTO.getName());
	    ingredient.setLocalizedName(ingredientDTO.getLocalizedName());
	    ingredient.setImage(ingredientDTO.getImage());
	    return ingredient;
	}

	private Equipment convertToEquipmentEntity(EquipmentDTO equipmentDTO) {
	    Equipment equipment = new Equipment();
	    equipment.setId(equipmentDTO.getId());
	    equipment.setName(equipmentDTO.getName());
	    equipment.setLocalizedName(equipmentDTO.getLocalizedName());
	    equipment.setImage(equipmentDTO.getImage());
	    return equipment;
	}
	
	private ProductMatch convertToProductMatchEntity(ProductMatchDTO dto) {
	    ProductMatch productMatch = new ProductMatch();
	    productMatch.setId(dto.getId());
	    productMatch.setTitle(dto.getTitle());
	    productMatch.setDescription(dto.getDescription());
	    productMatch.setPrice(dto.getPrice());
	    productMatch.setImageUrl(dto.getImageUrl());
	    productMatch.setAverageRating(dto.getAverageRating());
	    productMatch.setRatingCount(dto.getRatingCount());
	    productMatch.setScore(dto.getScore());
	    productMatch.setLink(dto.getLink());
	    // Add more fields if necessary
	    return productMatch;
	}
}

	
	
	
	
	

