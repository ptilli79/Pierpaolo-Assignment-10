package com.projects.cavany.dto.RecipeDetails;

import org.springframework.lang.Nullable;

public class UsMetricDTO {
    private Double amount;
    private String unitShort;
    private String unitLong;
    

    // Getters and setters for all fields
    @Nullable // Indicates this can be null
    public Double getAmount() { // Changed return type to Double
        return amount;
    }
    public void setAmount(Double amount) { // Allow null values
        this.amount = amount;
    }

    @Nullable // Indicates this can be null
    public String getUnitShort() {
        return unitShort;
    }
    public void setUnitShort(String unitShort) {
        this.unitShort = unitShort;
    }

    @Nullable // Indicates this can be null
    public String getUnitLong() {
        return unitLong;
    }
    public void setUnitLong(String unitLong) {
        this.unitLong = unitLong;
    } 
}
