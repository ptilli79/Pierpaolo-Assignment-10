package com.projects.cavany.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.csrf()
        	.and()
            .authorizeHttpRequests(authorize -> authorize
            	.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/login", "/register", "/error", "/index.html", "/allergies", "/recipes/filtered", "/allergies-and-ingredients.html", "/results", "/select-exclusions").permitAll()
                .requestMatchers(
                    "/mealplanner/week",
                    "/recipes/random",
                    "/complex/search",
                    "/recipes/informationBulk",
                    "/recipes/fetchRecipes",
                    "/mealplanner/day",
                    "/recipe/{recipeId}",
                    "/mealplanner/weeklyMealsRepo",
                    "/mealplanner/dailyMealsRepo",
                    "/recipe/detailsRepo",
                    "/home"
                ).hasRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin()
            .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll();
                

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
}

