document.addEventListener('DOMContentLoaded', () => {
    const allergies = document.querySelectorAll('#allergies .preference');
    const ingredients = document.querySelectorAll('#excluded-ingredients .preference');
    const queryButton = document.getElementById('query-recipes');
    const noAllergyButton = document.querySelector('#allergies .preference[data-value="no-allergies"]');
    
    // Extracting query parameters from URL
	const queryParams = new URLSearchParams(window.location.search);
	const diets = queryParams.get('diets') || '';
	const glutenFree = queryParams.get('glutenFree') === 'true';
	

	// Ingredient dictionary
	const ingredientDictionary = {
    'anchovies': ['salt packed anchovies', 'boquerones', 'canned anchovies', 'anchovy paste', 'anchovy', 'anchovies'],
    // Add more mappings as necessary
};

    // Function to update the state of the query button based on selected allergies
    function updateQueryButtonState() {
        const anyAllergiesSelected = Array.from(allergies).some(pref => pref.classList.contains('active'));
        // The button's disabled state now depends solely on whether any allergies are selected
        queryButton.disabled = !anyAllergiesSelected;
    }

    // Function to disable or enable allergy preferences
    function toggleAllergies(enable) {
        allergies.forEach(allergy => {
            if (allergy !== noAllergyButton) {
                allergy.classList.toggle('disabled', !enable);
                if (!enable) allergy.classList.remove('active');
            }
        });
    }

    // Event listener for allergy preferences
    allergies.forEach(allergy => {
        allergy.addEventListener('click', function() {
            if (this === noAllergyButton) {
                if (this.classList.contains('active')) {
                    this.classList.remove('active');
                    toggleAllergies(true);
                } else {
                    this.classList.add('active');
                    toggleAllergies(false);
                }
            } else {
                noAllergyButton.classList.remove('active');
                toggleAllergies(true);
                this.classList.toggle('active');
            }
            updateQueryButtonState();
        });
    });

    // Event listener for excluded ingredient preferences
    ingredients.forEach(ingredient => {
        ingredient.addEventListener('click', function() {
            this.classList.toggle('active');
            // No call to updateQueryButtonState here as ingredient selection does not affect the "Query Recipes" button
        });
    });

// Event listener for the query button
queryButton.addEventListener('click', function() {
     // Use the ingredient dictionary to expand selected ingredients into their variants
    const expandedSelectedIngredients = Array.from(ingredients).filter(pref => pref.classList.contains('active')).flatMap(pref => {
        const ingredient = pref.getAttribute('data-value');
        // Expand the ingredient using the dictionary if available, otherwise just use the ingredient itself
        return ingredientDictionary[ingredient] || [ingredient];
    });

    // Combine expanded ingredients with selected allergies (excluding 'no-allergies')
    const selectedAllergies = Array.from(allergies).filter(pref => pref.classList.contains('active') && pref.getAttribute('data-value') !== 'no-allergies').map(pref => pref.getAttribute('data-value'));
    let excludedIngredientsFromRequest = expandedSelectedIngredients.join(',');
    if (selectedAllergies.length > 0) {
        excludedIngredientsFromRequest = excludedIngredientsFromRequest ? `${excludedIngredientsFromRequest},${selectedAllergies.join(',')}` : selectedAllergies.join(',');
    }
	
	// Redirect to results.html with parameters
    const searchParams = new URLSearchParams({
        diets: diets,
        excludedIngredients: encodeURIComponent(excludedIngredientsFromRequest),
        glutenFree: glutenFree
    }).toString();
    
    
    // Log the parameters
	console.log(excludedIngredientsFromRequest);

	// Temporarily delay the redirection for debugging
	setTimeout(() => {
    	const searchParams = new URLSearchParams({
        	diets: diets,
        	excludedIngredients: encodeURIComponent(excludedIngredientsFromRequest),
        	glutenFree: glutenFree
    	}).toString();
    
    	window.location.href = `results.html?${searchParams}`;
	}, 2000); // Delay for 2 seconds
   



});




    // Set the initial state of the query button based on allergies selection
    updateQueryButtonState();
});
