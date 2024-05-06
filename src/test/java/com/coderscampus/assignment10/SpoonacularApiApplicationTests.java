package com.coderscampus.assignment10;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.projects.cavany.dto.MealPlanner.DailyPlanner;


@SpringBootTest
class SpoonacularApiApplicationTests {
	
	@Test
	public void callSpoonacularApiExample () throws JSONException, IOException {
		RestTemplate rt = new RestTemplate();
			
		//String url = "https://api.spoonacular.com/mealplanner/generate?apiKey=fe298f5d285846f9b08f5f2ec0e07e3f";
		String url = "https://api.spoonacular.com/mealplanner/generate?apiKey=596c89fa6ffc4bf189a9b396e7ad3854";
		//URI uri = UriComponentsBuilder.fromHttpUrl("https://www.alphavantage.co/query")
		//URI uri1 = UriComponentsBuilder.fromHttpUrl("https://api.spoonacular.com/recipes/complexSearch?apiKey=fe298f5d285846f9b08f5f2ec0e07e3f")
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("timeFrame", "day").build();
		//URI uri1 = UriComponentsBuilder.fromHttpUrl("https://api.spoonacular.com/mealplanner/generate?apiKey=fe298f5d285846f9b08f5f2ec0e07e3f")
									  //.queryParam("timeFrame", "day")
									  //.queryParam("symbol", "TSLA")
									  //.queryParam("interval", "5min")
									  //.queryParam("apikey", "fe298f5d285846f9b08f5f2ec0e07e3f")
									  //.build()
									  //.toUri();
		
		//String response = rt.getForObject(builder.toUriString(), String.class);
		ResponseEntity<String> response = rt.getForEntity(builder.toUriString(), String.class);
		ResponseEntity<DailyPlanner> response2 = rt.getForEntity(builder.toUriString(), DailyPlanner.class);
		System.out.println(response2.getBody().toString());
		//ObjectMapper objectMapper1 = new ObjectMapper();
		//DailyPlanner dailyPlanner = objectMapper1.readValue(response.getBody(), DailyPlanner.class);
		//System.out.println(dailyPlanner.toString());
		assertEquals(true,response.hasBody());
		
		//ObjectMapper objectMapper = new ObjectMapper();
		//System.out.println(jsonObj.toString(4));
		
//        JsonNode node = nodeGenerator(jsonObj);
//        JSONObject flat = new JSONObject();
//        node.fields().forEachRemaining(o -> {
//            if (!o.getValue().isContainerNode())
//				try {
//					flat.put(o.getKey(), o.getValue());
//				} catch (JSONException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			else {
//                parser(o.getValue(), flat);
//            }
//        });
//        System.out.println(flat);
//        System.out.println(flat.get("sunday"));
//        DailyPlanner dailyPlanner = objectMapper.readValue(flat.toString(), DailyPlanner.class);
//        System.out.println(dailyPlanner);
    }

    public static JsonNode nodeGenerator(JSONObject input) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(input.toString());
    }

    public static void parser(JsonNode node, JSONObject flat) {
        if (node.isArray()) {
            ArrayNode array = node.deepCopy();
            array.forEach(u ->
                    u.fields().forEachRemaining(o -> {
                        if (!o.getValue().isContainerNode())
							try {
								flat.put(o.getKey(), o.getValue());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						else {
                            parser(o.getValue(), flat);
                        }
                    }));
        } else {
            node.fields().forEachRemaining(o -> {
				try {
					flat.put(o.getKey(), o.getValue());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
        }
    }
}
