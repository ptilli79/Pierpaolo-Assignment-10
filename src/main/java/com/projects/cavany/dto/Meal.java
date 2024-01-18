package com.projects.cavany.dto;

import java.util.Objects;

public class Meal {
	
	private Long id;
	private Long readyInMinutes;
	private String sourceUrl;
	private Long servings;
	private String title;
	private String imageType;
	
	//Getters & Setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getReadyInMinutes() {
		return readyInMinutes;
	}
	public void setReadyInMinutes(Long readyInMinutes) {
		this.readyInMinutes = readyInMinutes;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public Long getServings() {
		return servings;
	}
	public void setServings(Long servings) {
		this.servings = servings;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImageType() {
		return imageType;
	}
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}



//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}
	
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Meal other = (Meal) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
	
//	@Override
//	public String toString() {
//		return "Recipe [id=" + id + ", title=" + title + "]";
//	}
	
	@Override
	public String toString() {
		return "Meal [id=" + id + ", readyInMinutes=" + readyInMinutes + ", sourceUrl=" + sourceUrl + ", servings="
				+ servings + ", title=" + title + ", imageType=" + imageType + "]";
	}
		
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Meal other = (Meal) obj;
		return Objects.equals(id, other.id);
	}


}
