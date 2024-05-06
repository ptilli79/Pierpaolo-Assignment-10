package com.projects.cavany.dto.RecipeDetails.Nutrition;

public class TasteDTO {
    private double sweetness;
    private double saltiness;
    private double sourness;
    private double bitterness;
    private double savoriness;
    private double fattiness;
    private double spiciness;

    // Constructors
    public TasteDTO() {
    }

    public TasteDTO(double sweetness, double saltiness, double sourness, double bitterness, double savoriness, double fattiness, double spiciness) {
        this.sweetness = sweetness;
        this.saltiness = saltiness;
        this.sourness = sourness;
        this.bitterness = bitterness;
        this.savoriness = savoriness;
        this.fattiness = fattiness;
        this.spiciness = spiciness;
    }

    // Getters and setters
    public double getSweetness() {
        return sweetness;
    }

    public void setSweetness(double sweetness) {
        this.sweetness = sweetness;
    }

    public double getSaltiness() {
        return saltiness;
    }

    public void setSaltiness(double saltiness) {
        this.saltiness = saltiness;
    }

    public double getSourness() {
        return sourness;
    }

    public void setSourness(double sourness) {
        this.sourness = sourness;
    }

    public double getBitterness() {
        return bitterness;
    }

    public void setBitterness(double bitterness) {
        this.bitterness = bitterness;
    }

    public double getSavoriness() {
        return savoriness;
    }

    public void setSavoriness(double savoriness) {
        this.savoriness = savoriness;
    }

    public double getFattiness() {
        return fattiness;
    }

    public void setFattiness(double fattiness) {
        this.fattiness = fattiness;
    }

    public double getSpiciness() {
        return spiciness;
    }

    public void setSpiciness(double spiciness) {
        this.spiciness = spiciness;
    }
}