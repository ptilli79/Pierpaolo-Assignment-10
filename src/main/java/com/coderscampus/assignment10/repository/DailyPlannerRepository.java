package com.coderscampus.assignment10.repository;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.coderscampus.assignment10.dto.DailyPlanner;

@Repository
public class DailyPlannerRepository {
	private Map<Long, DailyPlanner> dailyMealPlan = new HashMap<Long, DailyPlanner>();

	private Long index = 0L;
	
	public void save (DailyPlanner genericMealPlan) {
		dailyMealPlan.put(index, genericMealPlan);
		index++;
	}
	
	public DailyPlanner getWeeklyPlanById (Long dailyMealPlanId) {
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
