package com.projects.cavany.repository;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

import com.projects.cavany.dto.DailyPlanner;

@Repository
public class DailyPlannerRepository {
	private Map<Long, DailyPlanner> dailyMealPlan = new HashMap<Long, DailyPlanner>();

	private Long index = 0L;
	
	public DailyPlanner save (DailyPlanner genericMealPlan) {
		dailyMealPlan.put(index, genericMealPlan);
		index++;
		return(getDailyPlanById(index-1));
	}
	
	public DailyPlanner getDailyPlanById (Long dailyMealPlanId) {
		return dailyMealPlan.get(dailyMealPlanId);
	}
	
	public Map<Long, DailyPlanner> getAll () {
		return dailyMealPlan;
	}

	@Override
	public String toString() {
		return "DailyPlannerRepository [dailyMealPlan=" + dailyMealPlan + "]";
	}
}
