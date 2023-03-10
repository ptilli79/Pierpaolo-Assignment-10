package com.coderscampus.assignment10.repository;


import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.coderscampus.assignment10.dto.WeeklyPlannerResponse;


@Repository
public class WeeklyPlannerRepository {
	private Map<Long, WeeklyPlannerResponse> weeklyMealPlan = new HashMap<Long, WeeklyPlannerResponse>();
	private Long index = 0L;
	
	public void save (WeeklyPlannerResponse genericMealPlan) {
		weeklyMealPlan.put(index, genericMealPlan);
		index++;
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
