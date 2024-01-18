package com.coderscampus.assignment10.dto;

import java.util.List;

public class WinePairingDTO {

    private List<String> pairedWines;
    private String pairingText;
    private List<ProductMatchDTO> productMatches;
    
    // Getters and setters for all fields
	public List<String> getPairedWines() {
		return pairedWines;
	}
	public void setPairedWines(List<String> pairedWines) {
		this.pairedWines = pairedWines;
	}
	public String getPairingText() {
		return pairingText;
	}
	public void setPairingText(String pairingText) {
		this.pairingText = pairingText;
	}
	public List<ProductMatchDTO> getProductMatches() {
		return productMatches;
	}
	public void setProductMatches(List<ProductMatchDTO> productMatches) {
		this.productMatches = productMatches;
	}
}
