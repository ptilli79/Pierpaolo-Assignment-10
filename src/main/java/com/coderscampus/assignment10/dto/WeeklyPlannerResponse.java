package com.coderscampus.assignment10.dto;

import java.util.List;

public class WeeklyPlannerResponse {
	private WeeklyPlanner week;

	public WeeklyPlanner getWeek() {
		return week;
	}

	public void setWeek(WeeklyPlanner week) {
		this.week = week;
	}
	
	@Override
	public String toString() {
		return "WeeklyPlannerResponse [week=" + week + "]\n";
	}	
}
