package com.projects.cavany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jAuditing
@EnableNeo4jRepositories(basePackages = "com.projects.cavany.repository")
public class CavanyApp {

	public static void main(String[] args) {
		SpringApplication.run(CavanyApp.class, args);
	}
}
	