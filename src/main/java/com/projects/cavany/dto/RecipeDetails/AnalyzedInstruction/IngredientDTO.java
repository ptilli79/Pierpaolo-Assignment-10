package com.projects.cavany.dto.RecipeDetails.AnalyzedInstruction;

public class IngredientDTO {
	
	    private long id;
	    private String name;
	    private String localizedName;
	    private String image;
	    
	    // Getters and setters for all fields
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getLocalizedName() {
			return localizedName;
		}
		public void setLocalizedName(String localizedName) {
			this.localizedName = localizedName;
		}
		public String getImage() {
			return image;
		}
		public void setImage(String image) {
			this.image = image;
		}    
}


