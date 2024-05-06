package com.projects.cavany.repository;


import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

import com.projects.cavany.dto.MealPlanner.WeeklyPlannerResponse;


@Repository
public class WeeklyPlannerRepository {
	private Map<Long, WeeklyPlannerResponse> weeklyMealPlan = new HashMap<Long, WeeklyPlannerResponse>();
	private Long index = 0L;
	
	public WeeklyPlannerResponse save (WeeklyPlannerResponse genericMealPlan) {
		weeklyMealPlan.put(index, genericMealPlan);
		index++;
		return(getWeeklyPlanById(index-1));
	}
	
	public WeeklyPlannerResponse getWeeklyPlanById (Long weeklyMealPlanId) {
		return weeklyMealPlan.get(weeklyMealPlanId);
	}
	
	public Map<Long, WeeklyPlannerResponse> getAll () {
		return weeklyMealPlan;
	}

	@Override
	public String toString() {
		return "WeeklyPlannerRepository [weeklyMealPlan=" + weeklyMealPlan + "]";
	}
	
	
}
