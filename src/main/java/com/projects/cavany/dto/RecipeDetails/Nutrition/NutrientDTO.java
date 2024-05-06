package com.projects.cavany.dto.RecipeDetails.Nutrition;

public class NutrientDTO {
    private String name;
    private double amount;
    private String unit;
    private double percentOfDailyNeeds;

    // Constructors
    public NutrientDTO() {
    }

    public NutrientDTO(String name, double amount, String unit, double percentOfDailyNeeds) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.percentOfDailyNeeds = percentOfDailyNeeds;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPercentOfDailyNeeds() {
        return percentOfDailyNeeds;
    }

    public void setPercentOfDailyNeeds(double percentOfDailyNeeds) {
        this.percentOfDailyNeeds = percentOfDailyNeeds;
    }
}
