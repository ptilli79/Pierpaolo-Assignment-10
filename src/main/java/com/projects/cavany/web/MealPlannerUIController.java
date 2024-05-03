package com.projects.cavany.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MealPlannerUIController {

    @GetMapping("/select-exclusions")
    public String showExclusionsPage() {
        // This returns the HTML file name without '.html' extension.
        // Spring Boot will automatically look for this file in 'src/main/resources/static'
        //return "redirect:/index.html";
        return "index";// Assuming your HTML file is named 'index.html'
    }
    
    
    @GetMapping("/allergies")
    public String showAllergiesPage() {
        return "allergies-and-ingredients";
    }

    @GetMapping("/results")
    public String showResultsPage() {
        return "results";
    }
}

