package com.coderscampus.assignment10.dto;

public class UsMetricDTO {
    private double amount;
    private String unitShort;
    private String unitLong;
    
    // Getters and setters for all fields
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getUnitShort() {
		return unitShort;
	}
	public void setUnitShort(String unitShort) {
		this.unitShort = unitShort;
	}
	public String getUnitLong() {
		return unitLong;
	}
	public void setUnitLong(String unitLong) {
		this.unitLong = unitLong;
	} 
}
