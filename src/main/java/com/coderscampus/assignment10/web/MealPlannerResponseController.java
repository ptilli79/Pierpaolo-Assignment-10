package com.coderscampus.assignment10.web;


import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.coderscampus.assignment10.dto.DailyPlanner;
import com.coderscampus.assignment10.dto.WeeklyPlannerResponse;
import com.coderscampus.assignment10.repository.DailyPlannerRepository;
import com.coderscampus.assignment10.repository.WeeklyPlannerRepository;

//import ch.qos.logback.classic.Logger;



@RestController
public class MealPlannerResponseController {
	
	@Autowired
	private WeeklyPlannerRepository weekPlannerRepo;
	
	@Autowired
	private DailyPlannerRepository dayPlannerRepo;
	
	@Autowired
	private UriStringBuilder uriString;
	
	//@SuppressWarnings("unchecked")
	@GetMapping("/mealplanner/week")
	public ResponseEntity<WeeklyPlannerResponse> getWeekMeals(String targetCalories, String diet, String exclude){
		RestTemplate rt = new RestTemplate();
		//System.out.println(generateUriHelper(targetCalories, diet, exclude, "week"));
		ResponseEntity<WeeklyPlannerResponse> responseEntity = rt.getForEntity(generateUriHelper(targetCalories, diet, exclude, "week"), WeeklyPlannerResponse.class);
		//System.out.println(responseEntity.getBody().toString());
		weekPlannerRepo.save(responseEntity.getBody());
		return responseEntity;
	}
		
	@GetMapping("/mealplanner/day")
	public ResponseEntity<DailyPlanner> getDayMeals(String targetCalories, String diet, String exclude){
		RestTemplate rt = new RestTemplate();
		//System.out.println(generateUriHelper(targetCalories, diet, exclude, "week"));
		ResponseEntity<DailyPlanner> responseEntity = rt.getForEntity(generateUriHelper(targetCalories, diet, exclude, "day"), DailyPlanner.class);
		//System.out.println(responseEntity.getBody().toString());
		dayPlannerRepo.save(responseEntity.getBody());
		return responseEntity;
	}
	
	@GetMapping("/mealplanner/weeklyMealsRepo")
	public Map<Long, WeeklyPlannerResponse> getWeeklyMeals(){
		return weekPlannerRepo.getAll();
	}
	
	@GetMapping("/mealplanner/dailyMealsRepo")
	public Map<Long, DailyPlanner> getDailyMeals(){
		return dayPlannerRepo.getAll();
	}
	
	private URI generateUriHelper(String targetCalories, String diet, String exclude, String timeFrame) {
		String url = uriString.toString();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("timeFrame", timeFrame);
		
		if (!(targetCalories==null || targetCalories.length()==0)) {
			builder = builder.queryParam("targetCalories", Integer.parseInt(targetCalories));
		}
		if (!(diet==null || diet.length()==0)) {
			builder = builder.queryParam("diet", diet);
		}
		if (!(exclude==null || exclude.length()==0)) {
			builder = builder.queryParam("exclude", exclude);
		}
		return (builder.build().toUri());
	}
}

	
	
	
	
	

