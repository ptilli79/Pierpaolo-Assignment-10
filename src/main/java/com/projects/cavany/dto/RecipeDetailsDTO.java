package com.projects.cavany.dto;

import java.util.List;

public class RecipeDetailsDTO {

    private boolean vegetarian;
    private boolean vegan;
    private boolean glutenFree;
    private boolean dairyFree;
    private boolean veryHealthy;
    private boolean cheap;
    private boolean veryPopular;
    private boolean sustainable;
    private boolean lowFodmap;
    private int weightWatcherSmartPoints;
    private String gaps;
    private int preparationMinutes;
    private int cookingMinutes;
    private int aggregateLikes;
    private int healthScore;
    private String creditsText;
    private String sourceName;
    private double pricePerServing;
    private List<ExtendedIngredientDTO> extendedIngredients;
    private long id;
    private String title;
    private int readyInMinutes;
    private int servings;
    private String sourceUrl;
    private String image;
    private String imageType;
    private String summary;
    private List<String> cuisines;
    private List<String> dishTypes;
    private List<String> diets;
    private List<String> occasions;
    private WinePairingDTO winePairing;
    private String instructions;
    private List<AnalyzedInstructionDTO> analyzedInstructions;
    private long originalId;
    private double spoonacularScore;
    private String spoonacularSourceUrl;
    
    // Getters and setters for all fields
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
	public List<ExtendedIngredientDTO> getExtendedIngredients() {
		return extendedIngredients;
	}
	public void setExtendedIngredients(List<ExtendedIngredientDTO> extendedIngredients) {
		this.extendedIngredients = extendedIngredients;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public WinePairingDTO getWinePairing() {
		return winePairing;
	}
	public void setWinePairing(WinePairingDTO winePairing) {
		this.winePairing = winePairing;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	public List<AnalyzedInstructionDTO> getAnalyzedInstructions() {
		return analyzedInstructions;
	}
	public void setAnalyzedInstructions(List<AnalyzedInstructionDTO> analyzedInstructions) {
		this.analyzedInstructions = analyzedInstructions;
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
}
