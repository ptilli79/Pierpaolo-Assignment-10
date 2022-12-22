package com.coderscampus.assignment10.dto;

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
	@Override
	public String toString() {
		return "WeeklyPlanner [sunday=" + sunday + ", monday=" + monday + ", tuesday=" + tuesday + ", wednesday="
				+ wednesday + ", thursday=" + thursday + ", friday=" + friday + ", saturday=" + saturday + "]";
	}
	
	
	
	
}
