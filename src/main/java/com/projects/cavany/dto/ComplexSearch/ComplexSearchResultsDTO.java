package com.projects.cavany.dto.ComplexSearch;

import java.util.List;

public class ComplexSearchResultsDTO {
	

	    private List<ComplexSearchResultItemDTO> results;
	    private int offset;
	    private int number;
	    private int totalResults;

	    public ComplexSearchResultsDTO() {
	        // Default constructor
	    }

	    public ComplexSearchResultsDTO(List<ComplexSearchResultItemDTO> results, int offset, int number, int totalResults) {
	        this.results = results;
	        this.offset = offset;
	        this.number = number;
	        this.totalResults = totalResults;
	    }
	    
	    // Getters and setters

		public List<ComplexSearchResultItemDTO> getResults() {
			return results;
		}

		public void setResults(List<ComplexSearchResultItemDTO> results) {
			this.results = results;
		}

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}

		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}

		public int getTotalResults() {
			return totalResults;
		}

		public void setTotalResults(int totalResults) {
			this.totalResults = totalResults;
		}
	    
	}


