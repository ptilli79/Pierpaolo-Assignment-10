package com.projects.cavany.domain.RecipeDetails;

import java.util.UUID;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node
public class ProductMatch {
    @Id @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID uuid;  // This should be UUID instead of Long
    @Property
    private Long id;  // This keeps the original ID from the external API.
	@Property
    private String title;
	@Property
    private String description;
	@Property
    private String price;
	@Property
    private String imageUrl;
	@Property
    private double averageRating;
	@Property
    private double ratingCount;
	@Property
    private double score;
	@Property
    private String link;
    
    //Getters and setters for all fields
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public double getAverageRating() {
		return averageRating;
	}
	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}
	public double getRatingCount() {
		return ratingCount;
	}
	public void setRatingCount(double ratingCount) {
		this.ratingCount = ratingCount;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
}
