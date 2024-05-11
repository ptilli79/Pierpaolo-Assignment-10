package com.projects.cavany.domain.RecipeDetails;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.projects.cavany.domain.RecipeDetails.AnalyzedInstruction.AnalyzedInstruction;
import com.projects.cavany.domain.RecipeDetails.Nutrition.Nutrition;
import com.projects.cavany.dto.RecipeDetails.ExtendedIngredientDTO;
import com.projects.cavany.dto.RecipeDetails.AnalyzedInstruction.AnalyzedInstructionDTO;
import com.projects.cavany.dto.RecipeDetails.WinePairing.WinePairingDTO;


@Node
public class RecipeDetails {
	@Id 
    private Long Id;
    @Property
    private String title;
    @Property
    private boolean vegetarian;
    @Property
    private boolean vegan;
    @Property
    private boolean glutenFree;
    @Property
    private boolean dairyFree;
    @Property
    private boolean veryHealthy;
    @Property
    private boolean cheap;
    @Property
    private boolean veryPopular;
    @Property
    private boolean sustainable;
    @Property
    private boolean lowFodmap;
    @Property
    private int weightWatcherSmartPoints;
    @Property
    private String gaps;
    @Property
    private int preparationMinutes;
    @Property
    private int cookingMinutes;
    @Property
    private int aggregateLikes;
    @Property
    private int healthScore;
    @Property
    private String creditsText;
    @Property
    private String sourceName;
    @Property
    private double pricePerServing;
    @Property
    private int readyInMinutes;
    @Property
    private int servings;
    @Property
    private String sourceUrl;
    @Property
    private String image;
    @Property
    private String imageType;
    @Property
    private String summary;
    @Property
    private List<String> cuisines;
    @Property
    private List<String> dishTypes;
    @Property
    private List<String> diets;
    @Property
    private List<String> occasions;
    @Property
    private String instructions;
    @Property
    private long originalId;
    @Property
    private double spoonacularScore;
    @Property
    private String spoonacularSourceUrl;
    // Getters and setters for all fields
    // Consider relationships for complex types
    // For example, List<String> could be managed as related nodes
    @Relationship(type = "HAS_INGREDIENT", direction = Relationship.Direction.OUTGOING)
    private List<ExtendedIngredient> extendedIngredients;
    
    @Relationship(type = "HAS_WINE_PAIRING", direction = Relationship.Direction.OUTGOING)
    private WinePairing winePairing;
    
    @Relationship(type = "HAS_PREPARATION_INSTRUCTIONS", direction = Relationship.Direction.OUTGOING)
    private List<AnalyzedInstruction> analyzedInstructions;
    
    @Relationship(type = "HAS_NUTRITION", direction = Relationship.Direction.OUTGOING)
    private Nutrition nutrition;
    
    
    // Constructors, getters, and setters
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isVegetarian() {
		return vegetarian;
	}
	public void setVegetarian(boolean vegetarian) {
		this.vegetarian = vegetarian;
	}
	public boolean isVegan() {
		return vegan;
	}
	public void setVegan(boolean vegan) {
		this.vegan = vegan;
	}
	public boolean isGlutenFree() {
		return glutenFree;
	}
	public void setGlutenFree(boolean glutenFree) {
		this.glutenFree = glutenFree;
	}
	public boolean isDairyFree() {
		return dairyFree;
	}
	public void setDairyFree(boolean dairyFree) {
		this.dairyFree = dairyFree;
	}
	public boolean isVeryHealthy() {
		return veryHealthy;
	}
	public void setVeryHealthy(boolean veryHealthy) {
		this.veryHealthy = veryHealthy;
	}
	public boolean isCheap() {
		return cheap;
	}
	public void setCheap(boolean cheap) {
		this.cheap = cheap;
	}
	public boolean isVeryPopular() {
		return veryPopular;
	}
	public void setVeryPopular(boolean veryPopular) {
		this.veryPopular = veryPopular;
	}
	public boolean isSustainable() {
		return sustainable;
	}
	public void setSustainable(boolean sustainable) {
		this.sustainable = sustainable;
	}
	public boolean isLowFodmap() {
		return lowFodmap;
	}
	public void setLowFodmap(boolean lowFodmap) {
		this.lowFodmap = lowFodmap;
	}
	public int getWeightWatcherSmartPoints() {
		return weightWatcherSmartPoints;
	}
	public void setWeightWatcherSmartPoints(int weightWatcherSmartPoints) {
		this.weightWatcherSmartPoints = weightWatcherSmartPoints;
	}
	public String getGaps() {
		return gaps;
	}
	public void setGaps(String gaps) {
		this.gaps = gaps;
	}
	public int getPreparationMinutes() {
		return preparationMinutes;
	}
	public void setPreparationMinutes(int preparationMinutes) {
		this.preparationMinutes = preparationMinutes;
	}
	public int getCookingMinutes() {
		return cookingMinutes;
	}
	public void setCookingMinutes(int cookingMinutes) {
		this.cookingMinutes = cookingMinutes;
	}
	public int getAggregateLikes() {
		return aggregateLikes;
	}
	public void setAggregateLikes(int aggregateLikes) {
		this.aggregateLikes = aggregateLikes;
	}
	public int getHealthScore() {
		return healthScore;
	}
	public void setHealthScore(int healthScore) {
		this.healthScore = healthScore;
	}
	public String getCreditsText() {
		return creditsText;
	}
	public void setCreditsText(String creditsText) {
		this.creditsText = creditsText;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public double getPricePerServing() {
		return pricePerServing;
	}
	public void setPricePerServing(double pricePerServing) {
		this.pricePerServing = pricePerServing;
	}
	public int getReadyInMinutes() {
		return readyInMinutes;
	}
	public void setReadyInMinutes(int readyInMinutes) {
		this.readyInMinutes = readyInMinutes;
	}
	public int getServings() {
		return servings;
	}
	public void setServings(int servings) {
		this.servings = servings;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getImageType() {
		return imageType;
	}
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public List<String> getCuisines() {
		return cuisines;
	}
	public void setCuisines(List<String> cuisines) {
		this.cuisines = cuisines;
	}
	public List<String> getDishTypes() {
		return dishTypes;
	}
	public void setDishTypes(List<String> dishTypes) {
		this.dishTypes = dishTypes;
	}
	public List<String> getDiets() {
		return diets;
	}
	public void setDiets(List<String> diets) {
		this.diets = diets;
	}
	public List<String> getOccasions() {
		return occasions;
	}
	public void setOccasions(List<String> occasions) {
		this.occasions = occasions;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	public long getOriginalId() {
		return originalId;
	}
	public void setOriginalId(long originalId) {
		this.originalId = originalId;
	}
	public double getSpoonacularScore() {
		return spoonacularScore;
	}
	public void setSpoonacularScore(double spoonacularScore) {
		this.spoonacularScore = spoonacularScore;
	}
	public String getSpoonacularSourceUrl() {
		return spoonacularSourceUrl;
	}
	public void setSpoonacularSourceUrl(String spoonacularSourceUrl) {
		this.spoonacularSourceUrl = spoonacularSourceUrl;
	}
	public List<ExtendedIngredient> getExtendedIngredients() {
		return extendedIngredients;
	}
	public void setExtendedIngredients(List<ExtendedIngredient> extendedIngredients) {
		this.extendedIngredients = extendedIngredients;
	}
	public List<AnalyzedInstruction> getAnalyzedInstructions() {
		return analyzedInstructions;
	}
	public void setAnalyzedInstructions(List<AnalyzedInstruction> analyzedInstructions) {
		this.analyzedInstructions = analyzedInstructions;
	}
	public WinePairing getWinePairing() {
		return winePairing;
	}
	public void setWinePairing(WinePairing winePairing) {
		this.winePairing = winePairing;
	}
    public Nutrition getNutrition() {
        return nutrition;
    }
    public void setNutrition(Nutrition nutrition) {
        this.nutrition = nutrition;
    }
}
