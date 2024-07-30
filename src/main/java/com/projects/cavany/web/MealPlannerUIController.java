package com.projects.cavany.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projects.cavany.domain.Security.RoleUser;
import com.projects.cavany.domain.Security.WebUser;
import com.projects.cavany.repository.WebUserRepositoryNeo4j;
import com.projects.cavany.service.UserDataInitializer;

import jakarta.servlet.http.HttpSession;

@Controller
public class MealPlannerUIController {


    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private WebUserRepositoryNeo4j webUserRepositoryNeo4j;
    
    private final AuthenticationManager authenticationManager;
    
    @Autowired
    public MealPlannerUIController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    
    @GetMapping("/select-exclusions")
    public String showExclusionsPage() {
        return "index";
    }
    
    @GetMapping("/allergies")
    public String showAllergiesPage() {
        return "allergies-and-ingredients";
    }

    @GetMapping("/results")
    public String showResultsPage() {
        return "results";
    }

//    @GetMapping("/register")
//    public String showRegistrationForm(Model model) {
//        model.addAttribute("user", new WebUser());
//        return "register";
//    }

    @GetMapping("/check-username")
    @ResponseBody
    public ResponseEntity<Boolean> checkUsernameExists(@RequestParam String username) {
        boolean exists = webUserRepositoryNeo4j.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }
    
    @PostMapping("/custom_login")
    public ResponseEntity<String> performLogin(@RequestParam String username, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok("success:Login successful");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("error:Invalid username or password");
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public String registerUser(@ModelAttribute WebUser user, @RequestParam String confirmPassword) {
        if (!user.getPassword().equals(confirmPassword)) {
            return "error:Passwords do not match";
        }
        
        if (webUserRepositoryNeo4j.existsByUsername(user.getUsername())) {
            return "error:Username already exists";
        }
        
        try {
            WebUser createdUser = userDataInitializer.createUser(
                user.getUsername(), 
                user.getEmail(), 
                user.getPassword(), 
                RoleUser.ROLE_USER, 
                Collections.singletonList("read")
            );
            
            return "success:User successfully created. Please log in.";
        } catch (Exception e) {
            return "error:An error occurred during registration. Please try again.";
        }
    }



    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        return "home";
    }
    
    @GetMapping("/logout")
    public String logoutPage() {
        return "logout";
    }
    
    
}

