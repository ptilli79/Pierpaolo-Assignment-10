package com.projects.cavany.dto.MealPlanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class WeeklyPlanner {

	private DailyPlanner sunday;
	private DailyPlanner monday;
	private DailyPlanner tuesday;
	private DailyPlanner wednesday;
	private DailyPlanner thursday;
	private DailyPlanner friday;
	private DailyPlanner saturday;
	
	public DailyPlanner getSunday() {
		return sunday;
	}
	public void setSunday(DailyPlanner sunday) {
		this.sunday = sunday;
	}
	public DailyPlanner getMonday() {
		return monday;
	}
	public void setMonday(DailyPlanner monday) {
		this.monday = monday;
	}
	public DailyPlanner getTuesday() {
		return tuesday;
	}
	public void setTuesday(DailyPlanner tuesday) {
		this.tuesday = tuesday;
	}
	public DailyPlanner getWednesday() {
		return wednesday;
	}
	public void setWednesday(DailyPlanner wednesday) {
		this.wednesday = wednesday;
	}
	public DailyPlanner getThursday() {
		return thursday;
	}
	public void setThursday(DailyPlanner thursday) {
		this.thursday = thursday;
	}
	public DailyPlanner getFriday() {
		return friday;
	}
	public void setFriday(DailyPlanner friday) {
		this.friday = friday;
	}
	public DailyPlanner getSaturday() {
		return saturday;
	}
	public void setSaturday(DailyPlanner saturday) {
		this.saturday = saturday;
	}
	
    // Add a method to get the meals for a specific day
    public List<Meal> getMealsForDay(String day) {
        DailyPlanner dailyPlanner = null;
        switch (day.toLowerCase()) {
            case "sunday":
                dailyPlanner = sunday;
                break;
            case "monday":
                dailyPlanner = monday;
                break;
            case "tuesday":
                dailyPlanner = tuesday;
                break;
            case "wednesday":
                dailyPlanner = wednesday;
                break;
            case "thursday":
                dailyPlanner = thursday;
                break;
            case "friday":
                dailyPlanner = friday;
                break;
            case "saturday":
                dailyPlanner = saturday;
                break;
        }
        if (dailyPlanner != null) {
            return dailyPlanner.getMeals();
        }
        return Collections.emptyList(); // Return an empty list if the day's planner is not found
    }
    
    // Get all available days of the week
    @JsonIgnore
    public List<String> getAllDaysOfWeek() {
        List<String> daysOfWeek = new ArrayList<>();
        if (sunday != null) daysOfWeek.add("sunday");
        if (monday != null) daysOfWeek.add("monday");
        if (tuesday != null) daysOfWeek.add("tuesday");
        if (wednesday != null) daysOfWeek.add("wednesday");
        if (thursday != null) daysOfWeek.add("thursday");
        if (friday != null) daysOfWeek.add("friday");
        if (saturday != null) daysOfWeek.add("saturday");
        return daysOfWeek;
    }
	
	@Override
	public String toString() {
		return "WeeklyPlanner [sunday=" + sunday + ", monday=" + monday + ", tuesday=" + tuesday + ", wednesday="
				+ wednesday + ", thursday=" + thursday + ", friday=" + friday + ", saturday=" + saturday + "]";
	}
	
	
	
	
}
