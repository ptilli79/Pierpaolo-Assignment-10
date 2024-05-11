package com.projects.cavany.web;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.projects.cavany.domain.RecipeDetails.ExtendedIngredient;
import com.projects.cavany.domain.RecipeDetails.MetricSystem;
import com.projects.cavany.domain.RecipeDetails.ProductMatch;
import com.projects.cavany.domain.RecipeDetails.RecipeDetails;
import com.projects.cavany.domain.RecipeDetails.WinePairing;
import com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction.AnalyzedInstruction;
import com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction.Equipment;
import com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction.Ingredient;
import com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction.Step;
import com.projects.cavany.domain.RecipeDetails.Nutrition.CaloricBreakdown;
import com.projects.cavany.domain.RecipeDetails.Nutrition.IngredientNutrition;
import com.projects.cavany.domain.RecipeDetails.Nutrition.NutrientEntity;
import com.projects.cavany.domain.RecipeDetails.Nutrition.Nutrition;
import com.projects.cavany.domain.RecipeDetails.Nutrition.NutritionProperty;
import com.projects.cavany.domain.RecipeDetails.Nutrition.WeightPerServing;
import com.projects.cavany.dto.*;
import com.projects.cavany.dto.ComplexSearch.ComplexSearchResultItemDTO;
import com.projects.cavany.dto.ComplexSearch.ComplexSearchResultsDTO;
import com.projects.cavany.dto.ComplexSearch.RandomSearchResponse;
import com.projects.cavany.dto.MealPlanner.DailyPlanner;
import com.projects.cavany.dto.MealPlanner.WeeklyPlannerResponse;
import com.projects.cavany.dto.RecipeDetails.BulkRecipeDetailsDTO;
import com.projects.cavany.dto.RecipeDetails.ExtendedIngredientDTO;
import com.projects.cavany.dto.RecipeDetails.MeasuresDTO;
import com.projects.cavany.dto.RecipeDetails.RecipeDetailsDTO;
import com.projects.cavany.dto.RecipeDetails.RecipeWithIngredientsDTOFromEntity;
import com.projects.cavany.dto.RecipeDetails.AnalyzedInstruction.AnalyzedInstructionDTO;
import com.projects.cavany.dto.RecipeDetails.AnalyzedInstruction.EquipmentDTO;
import com.projects.cavany.dto.RecipeDetails.AnalyzedInstruction.IngredientDTO;
import com.projects.cavany.dto.RecipeDetails.AnalyzedInstruction.StepDTO;
import com.projects.cavany.dto.RecipeDetails.Nutrition.CaloricBreakdownDTO;
import com.projects.cavany.dto.RecipeDetails.Nutrition.IngredientNutritionDTO;
import com.projects.cavany.dto.RecipeDetails.Nutrition.NutrientDTO;
import com.projects.cavany.dto.RecipeDetails.Nutrition.NutritionDTO;
import com.projects.cavany.dto.RecipeDetails.Nutrition.NutritionPropertyDTO;
import com.projects.cavany.dto.RecipeDetails.Nutrition.WeightPerServingDTO;
import com.projects.cavany.dto.RecipeDetails.WinePairing.ProductMatchDTO;
import com.projects.cavany.dto.RecipeDetails.WinePairing.WinePairingDTO;
import com.projects.cavany.repository.DailyPlannerRepository;
import com.projects.cavany.repository.ExtendedIngredientRepositoryNeo4j;
import com.projects.cavany.repository.RecipeDetailsRepository;
import com.projects.cavany.repository.RecipeDetailsRepositoryNeo4j;
import com.projects.cavany.repository.WeeklyPlannerRepository;
import com.projects.cavany.service.GenerateRecipeService;
import com.projects.cavany.service.MealPlannerService;
import com.projects.cavany.service.RecipeDetailsService;

import org.springframework.web.client.HttpServerErrorException;

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
    private RecipeDetailsService recipeDetailsService;
    
  
	
    private int maxRequestsPerSecond = 4; // Change this value as needed
    private long rateLimitIntervalMillis = 1000; // 1000 milliseconds (1 second)
    private static final int maxIds = 20000;
    private static final int batchSize = 50;
    String recipesFilePath = "C:\\Users\\pierp\\OneDrive\\Documentos\\MyRepository\\JavaBootcamp\\bootcamp-pierpaolo\\JavaBootcamp-Workspace\\Cavany\\output\\recipes.csv";
    private final List<String> diets = Arrays.asList("Whole30"); // You can add more diets as needed
    private final Map<String, List<String>> excludedIngredientsByCategory = new HashMap<>();

    // Constructor or @PostConstruct method
 // Constructor or @PostConstruct method
    public MealPlannerResponseController() {
        excludedIngredientsByCategory.put("Sugar", Arrays.asList(
            "sugar", "high fructose corn syrup", "agave nectar", "stevia", "molasses", "maple syrup", "confectioner's swerve", 
            "sugar syrup", "sukrin sweetener", "sweet tea"));

        excludedIngredientsByCategory.put("Fats", Arrays.asList(
            "margarine", "lard", "avocado", "hass avocado", "avocado cubes", "avocados", "avocado mayo", "avocadoes", 
            "bacon fat", "butter", "buttermilk", "coconut butter", "coconut cream", "duck fat", "light butter"));

        excludedIngredientsByCategory.put("Canned Goods", Arrays.asList(
            "5 spice powder", "canned black beans", "canned diced tomatoes", "canned garbanzo beans", "canned green chiles", 
            "canned kidney beans", "canned mushrooms", "canned pinto beans", "canned red kidney beans", "canned tomatoes", 
            "canned tuna", "canned white beans", "canned white cannellini beans", "cannellini beans", "Pork & Beans"));

        excludedIngredientsByCategory.put("Liquors", Arrays.asList(
            "amaretto", "bourbon", "brandy", "beer", "champagne", "cognac", "gin", "grand marnier", "kahlua", "rum", 
            "tequila", "vodka", "whiskey", "white wine", "red wine", "dry vermouth", "sweet vermouth", "scotch", 
            "irish cream", "marsala wine", "beer", "ginger beer", "liquor", "vermouth", "wine", "sparkling wine"));

        excludedIngredientsByCategory.put("Baking Goods", Arrays.asList(
            "angel food cake mix", "baking bar", "biscuit mix", "biscuits", "bittersweet chocolate", "brownie mix", 
            "candy canes", "candy coating", "candy melts", "chocolate ice cream", "cinnamon stick", "cocoa nibs", 
            "cocoa powder", "corn bread mix", "dark chocolate candy bars", "dessert oats", "flour", "flour tortillas", 
            "fudge", "fudge topping", "gelatin", "gf chocolate cake mix", "graham cracker crumbs", "graham cracker pie crust", 
            "instant chocolate pudding mix", "instant espresso powder", "instant lemon pudding mix", "instant yeast", 
            "marshmallow fluff", "marshmallows", "pie crust", "puff pastry", "self-rising flour", "shortbread cookies", 
            "shortcrust pastry", "white cake mix", "yellow cake mix"));

        // Add more categories based on the new ingredients
        excludedIngredientsByCategory.put("Spices & Seasonings", Arrays.asList(
            "angostura bitters", "black pepper", "cajun seasoning", "caraway seed", "cardamom pods", "celery salt", 
            "celery seed", "chana dal", "cracked pepper", "dill", "dried dill", "fenugreek leaf", "fenugreek seeds", 
            "ground lamb", "ground mace", "ground veal", "herbes de provence", "hot dog", "old bay seasoning", 
            "peppercorns", "red pepper flakes", "rub", "rum extract", "saffron threads", "sage", "sage leaves", 
            "saltine crackers", "seasoned rice vinegar", "sesame seeds", "summer savory", "sweet paprika", "taco seasoning mix"));

        // Continue adding new categories as needed
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
	    //public void fetchRecipes() throws IOException {
	    public void fetchRecipes(@RequestParam(required = false) Long startId) throws IOException {

	        RestTemplate restTemplate = new RestTemplate();
	        int requestsMade = 0;
	        AtomicInteger recipesSaved = new AtomicInteger();
	        Set<Long> existingIds = recipeDetailsRepositoryNeo4j.findAllIds().stream().collect(Collectors.toSet());
	        Optional<Long> maxIdInDb = recipeDetailsRepositoryNeo4j.findMaxId();
	        //long startId = maxIdInDb.isPresent() ? maxIdInDb.get() + 1 : 0;
	        long startIdValue = startId != null ? startId : (maxIdInDb.isPresent() ? maxIdInDb.get() + 1 : 0);

	        final int maxRetries = 3;

	        for (long i = startIdValue; i < maxIds; i += batchSize) {
	            StringBuilder ids = new StringBuilder();
	            for (long j = i; j < i + batchSize && j < maxIds; j++) {
	                if (ids.length() > 0) {
	                    ids.append(",");
	                }
	                ids.append(j);
	            }
	            if (ids.length() == 0) continue;
	       

	            

	            String url = uriString.toStringRecipeBulkInformation() + "?ids=" + ids.toString() + "&includeNutrition=true&apiKey=" + uriString.getApiKey();
	            ResponseEntity<BulkRecipeDetailsDTO[]> response = null;
	            int retryCount = 0;

	            while (retryCount < maxRetries) {
	                if (requestsMade >= maxRequestsPerSecond) {
	                    try {
	                        Thread.sleep(rateLimitIntervalMillis);
	                    } catch (InterruptedException e) {
	                        Thread.currentThread().interrupt();
	                    }
	                    requestsMade = 0;
	                }

	                try {
	                    System.out.println("Requesting URL: " + url);
	                    response = restTemplate.getForEntity(url, BulkRecipeDetailsDTO[].class);
	                    requestsMade++;
	                    break; // Request successful
	                } catch (ResourceAccessException | HttpClientErrorException | HttpServerErrorException e) {
	                    System.err.println("Error accessing URL: " + url + ". Error: " + e.getMessage());
	                    retryCount++;
	                    if (retryCount >= maxRetries) {
	                        System.err.println("Max retry attempts reached for URL: " + url);
	                        break;
	                    }
	                    try {
	                        System.out.println("Retrying after error... (" + retryCount + "/" + maxRetries + ")");
	                        Thread.sleep(10000); // Wait for 10 seconds before retrying
	                    } catch (InterruptedException ex) {
	                        Thread.currentThread().interrupt();
	                    }
	                }
	            }

	            if (response != null && response.getBody() != null) {
	                Arrays.stream(response.getBody()).forEach(recipeDetailsDTO -> {
	                    RecipeDetails recipe = convertToRecipeEntity(recipeDetailsDTO);
	                    //recipeDetailsRepositoryNeo4j.save(recipe);
	                    recipeDetailsService.saveOrUpdateRecipeDetails(recipe);
	                    
	                    recipesSaved.incrementAndGet();

	                    if (recipesSaved.get() % 500 == 0) {
	                        try {
	                            System.out.println("Saved 500 recipes, pausing...");
	                            Thread.sleep(10000); // Wait for 10 seconds
	                        } catch (InterruptedException e) {
	                            Thread.currentThread().interrupt();
	                        }
	                    }
	                });
	            }
	        }
	    }
   
	    @GetMapping("/recipes/filtered")
	    public ResponseEntity<Map<String, Object>> fetchFilteredRecipes(
	            @RequestParam(required = false) List<String> diets,
	            @RequestParam(required = false) List<String> excludedIngredientsFromRequest,
	            @RequestParam(defaultValue = "false") boolean glutenFree,
	            @RequestParam(defaultValue = "false") boolean dairyFree,
	            @RequestParam(defaultValue = "14") int days) {

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
	        String dietsPrintable = "[\"" + String.join("\", \"", diets) + "\"]";
	        String excludedIngredientsPrintable = "[\"" + allExcludedIngredients.stream()
	                .map(String::trim)
	                .collect(Collectors.joining("\", \"")) + "\"]";

	        // Print out the Cypher query for testing
	        String printableCypherQuery = String.format(
	            "MATCH (recipe:RecipeDetails)-[:HAS_INGREDIENT]->(ingredient:ExtendedIngredient) " +
	            "WHERE ALL(d IN %s WHERE d IN recipe.diets) " +
	            "WITH recipe, COLLECT(DISTINCT toLower(ingredient.name)) AS rawIngredients, " +
	            "COLLECT(DISTINCT toLower(ingredient.nameClean)) AS cleanIngredients " +
	            "OPTIONAL MATCH (recipe)-[:HAS_PREPARATION_INSTRUCTIONS]->(:AnalyzedInstruction)-[:HAS_STEPS]->(:Step)-[:HAS_INGREDIENTS]->(stepIngredient:Ingredient) " +
	            "WITH recipe, rawIngredients, cleanIngredients, " +
	            "COLLECT(DISTINCT toLower(stepIngredient.name)) AS stepRawIngredients, " +
	            "COLLECT(DISTINCT toLower(stepIngredient.nameClean)) AS stepCleanIngredients " +
	            "WITH recipe, rawIngredients + cleanIngredients + stepRawIngredients + stepCleanIngredients AS allIngredientsList " +
	            "UNWIND allIngredientsList AS ingredientNames " +
	            "WITH recipe, COLLECT(DISTINCT ingredientNames) AS distinctIngredients " +
	            "WHERE NONE(ing IN distinctIngredients WHERE ing IN %s) " +
	            "RETURN DISTINCT recipe.Id",
	            dietsPrintable, excludedIngredientsPrintable
	        );
	        
	        System.out.println("Cypher Query: " + printableCypherQuery);
	        System.out.println("Days: " + days);
	        System.out.println("Diets: " + diets);
	        System.out.println("GlutenFree: " + glutenFree);
	        System.out.println("dairyFree: " + dairyFree);
	        System.out.println("Excluded Ingredients: " + allExcludedIngredients);

	        // Execute the query to fetch filtered recipe IDs
	        // Implementation depends on your Neo4j repository setup
	        // Fetch filtered recipe IDs from the repository
	        List<Long> filteredRecipeIds = recipeDetailsRepositoryNeo4j.findFilteredRecipes(
	        		diets, glutenFree, dairyFree, allExcludedIngredients);

	        // If no recipes match the filters, return an empty meal plan
	        if (filteredRecipeIds.isEmpty()) {
	            return ResponseEntity.ok(Map.of("week", Collections.emptyMap()));
	        }


	        // Initialize daysOfWeek array
	        String[] daysOfWeek = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
	        int mealsPerDay = 3; // This can be adjusted as needed
	        int totalMealsNeeded = days * mealsPerDay;
	        List<String> dishTypes = new ArrayList<>(Arrays.asList("lunch", "dinner", "main course", "main dish", "morning meal", "breakfast", "brunch"));
	        // Fetch recipes with ingredients
	     // Fetch raw data
	        System.out.println("Filtered Recipe IDs: " + filteredRecipeIds.size());
	        List<RecipeWithIngredientsDTOFromEntity> recipesWithIngredients = generateRecipeService.findLimitedRecipesWithIngredients(filteredRecipeIds,dishTypes);
	        System.out.println("Recipes With Ingredients: " + recipesWithIngredients.size());

	        
	        // Shuffle for randomness
	        Collections.shuffle(recipesWithIngredients);
	        
	        List<RecipeWithIngredientsDTOFromEntity> selectedRecipes;

	        // Select a subset of recipes if there are more recipes than needed

	        if (recipesWithIngredients.size() > totalMealsNeeded) {
	            selectedRecipes = new ArrayList<>(recipesWithIngredients.subList(0, totalMealsNeeded));
	        } else {
	            selectedRecipes = new ArrayList<>(recipesWithIngredients);
	        }

	     // Initialize recipeIterator from selectedRecipes
	        Iterator<RecipeWithIngredientsDTOFromEntity> recipeIterator = selectedRecipes.iterator();

	        // Logic to distribute selected recipes across the specified days
	        Map<String, Object> weeklyMealPlan = new LinkedHashMap<>();
	        
	        
	        System.out.println("Selected Recipes With Ingredients: " + selectedRecipes.size());



	        // Used to keep track of the current week for resetting meal counter
	        int currentWeek = 1;
	        int mealCounter = 1; // Start counting meals from 1 for each week

	        for (int dayIndex = 0; dayIndex < days; dayIndex++) {
	            // Calculate week number for the current day
	            int weekNumber = (dayIndex / 7) + 1;
	            
	            // Reset meal counter if a new week starts
	            if (weekNumber > currentWeek) {
	                mealCounter = 1; // Reset meal counter for the new week
	                currentWeek = weekNumber;
	            }
	            
	            List<Map<String, Object>> mealsForDay = new ArrayList<>();
	            for (int j = 0; j < mealsPerDay && recipeIterator.hasNext(); j++) {
	                RecipeWithIngredientsDTOFromEntity recipeDTO = recipeIterator.next();
	                Map<String, Object> meal = new HashMap<>();
	                meal.put("id", recipeDTO.getRecipeId());
	                meal.put("title", recipeDTO.getRecipeTitle());
	                List<String> uniqueIngredients = new ArrayList<>(new LinkedHashSet<>(recipeDTO.getRecipeIngredients()));
	                meal.put("ingredients", uniqueIngredients);
	                
	                // Set week number and meal number within the week
	                meal.put("weekNumber", weekNumber);
	                meal.put("mealNumber", mealCounter++); // Increment meal counter after setting

	                mealsForDay.add(meal);
	            }
	            
	            String dayOfWeek = daysOfWeek[dayIndex % 7]; // Use modulo to cycle through days of the week
	            weeklyMealPlan.put("Week " + weekNumber + " " + dayOfWeek, Map.of("meals", mealsForDay)); // Append week number and day of the week
	        }

	        // The rest of your method that returns or processes the weeklyMealPlan map

	        return ResponseEntity.ok(Map.of("week", weeklyMealPlan));

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
		
	@GetMapping("/recipes/{recipeId}")
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
	        if (measuresDto.getUs() != null) {
	        	Double usAmount = measuresDto.getUs().getAmount(); // Now you're safely accessing getAmount
	            usMetric.setAmount((usAmount != null) ? usAmount.doubleValue() : 0.0); // Default to 0.0 if null
	        	//usMetric.setAmount(measuresDto.getUs().getAmount());
	        	usMetric.setUnitShort(measuresDto.getUs().getUnitShort());
	        	usMetric.setUnitLong(measuresDto.getUs().getUnitLong());
	        }
	           
	        MetricSystem metricMetric = new MetricSystem();
	        if (measuresDto.getMetric() != null) {
	        	Double metricAmount = measuresDto.getMetric().getAmount(); // Again, safely accessing getAmount
	            metricMetric.setAmount((metricAmount != null) ? metricAmount.doubleValue() : 0.0); // Default to 0.0 if null
	        	//metricMetric.setAmount(measuresDto.getMetric().getAmount());
	        	metricMetric.setUnitShort(measuresDto.getMetric().getUnitShort());
	        	metricMetric.setUnitLong(measuresDto.getMetric().getUnitLong());
	        }

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
	    // Convert Nutrition from DTO to Entity
	    NutritionDTO nutritionDTO = dto.getNutrition();
	    if (nutritionDTO != null) {
	        Nutrition nutrition = new Nutrition();
	        
	        // Convert nutrients
	        List<NutrientEntity> nutrients = nutritionDTO.getNutrients().stream()
	            .map(this::convertToNutrientEntity)
	            .collect(Collectors.toList());
	        nutrition.setNutrients(nutrients);
	        
	        // Convert properties
	        List<NutritionProperty> properties = nutritionDTO.getProperties().stream()
	            .map(this::convertToNutritionPropertyEntity)
	            .collect(Collectors.toList());
	        nutrition.setProperties(properties);
	        
	        // Convert ingredients
	        List<IngredientNutrition> ingredients = nutritionDTO.getIngredients().stream()
	            .map(this::convertToIngredientNutritionEntity)
	            .collect(Collectors.toList());
	        nutrition.setIngredients(ingredients);
	        
	        // Convert caloric breakdown
	        CaloricBreakdownDTO caloricBreakdownDTO = nutritionDTO.getCaloricBreakdown();
	        if (caloricBreakdownDTO != null) {
	            CaloricBreakdown caloricBreakdown = convertToCaloricBreakdownEntity(caloricBreakdownDTO);
	            nutrition.setCaloricBreakdown(caloricBreakdown);
	        }
	        
	        // Convert weight per serving
	        WeightPerServingDTO weightPerServingDTO = nutritionDTO.getWeightPerServing();
	        if (weightPerServingDTO != null) {
	            WeightPerServing weightPerServing = convertToWeightPerServingEntity(weightPerServingDTO);
	            nutrition.setWeightPerServing(weightPerServing);
	        }
	        
	        recipe.setNutrition(nutrition);
	    }
	    
	    return recipe;
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
	
	private NutrientEntity convertToNutrientEntity(NutrientDTO dto) {
	    NutrientEntity nutrient = new NutrientEntity();
	    nutrient.setName(dto.getName());
	    nutrient.setAmount(dto.getAmount());
	    nutrient.setUnit(dto.getUnit());
	    nutrient.setPercentOfDailyNeeds(dto.getPercentOfDailyNeeds());
	    return nutrient;
	}

	private NutritionProperty convertToNutritionPropertyEntity(NutritionPropertyDTO dto) {
	    NutritionProperty property = new NutritionProperty();
	    property.setName(dto.getName());
	    property.setAmount(dto.getAmount());
	    property.setUnit(dto.getUnit());
	    return property;
	}

	private IngredientNutrition convertToIngredientNutritionEntity(IngredientNutritionDTO dto) {
	    IngredientNutrition ingredientNutrition = new IngredientNutrition();
	    ingredientNutrition.setIngredientId(dto.getId());
	    ingredientNutrition.setName(dto.getName());
	    ingredientNutrition.setAmount(dto.getAmount());
	    ingredientNutrition.setUnit(dto.getUnit());
	    
	    List<NutrientEntity> nutrients = dto.getNutrients().stream()
	        .map(this::convertToNutrientEntity)
	        .collect(Collectors.toList());
	    ingredientNutrition.setNutrients(nutrients);
	    
	    return ingredientNutrition;
	}

	private CaloricBreakdown convertToCaloricBreakdownEntity(CaloricBreakdownDTO dto) {
	    CaloricBreakdown caloricBreakdown = new CaloricBreakdown();
	    caloricBreakdown.setPercentProtein(dto.getPercentProtein());
	    caloricBreakdown.setPercentFat(dto.getPercentFat());
	    caloricBreakdown.setPercentCarbs(dto.getPercentCarbs());
	    return caloricBreakdown;
	}

	private WeightPerServing convertToWeightPerServingEntity(WeightPerServingDTO dto) {
	    WeightPerServing weightPerServing = new WeightPerServing();
	    weightPerServing.setAmount(dto.getAmount());
	    weightPerServing.setUnit(dto.getUnit());
	    return weightPerServing;
	}

}

	
	
	
	
	

