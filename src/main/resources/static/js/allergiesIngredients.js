document.addEventListener('DOMContentLoaded', () => {
    const allergies = document.querySelectorAll('#allergies .preference');
    const ingredients = document.querySelectorAll('#excluded-ingredients .preference');
    const queryButton = document.getElementById('query-recipes');
    const noAllergyButton = document.querySelector('#allergies .preference[data-value="no-allergies"]');
  
    
    // Extracting query parameters from URL
	const queryParams = new URLSearchParams(window.location.search);
	const diets = queryParams.get('diets') || '';
	const glutenFree = queryParams.get('glutenFree') === 'true';
	const toggleContainer = document.getElementById("toggleContainer");

	// Assuming isWeeks is updated elsewhere based on the toggle state
	let isWeeks = false; // Default to days
	

	// Ingredient dictionary
	const ingredientDictionary = {
    'anchovies': ['salt packed anchovies', 'boquerones', 'canned anchovies', 'anchovy paste', 'anchovy', 'anchovies'],
    'eggs': ['hard boiled egg', 'egg yolk', 'eggs', 'egg whites', 'egg'],
    'fish': ['fish sauce', 'fish stock', 'fish', 'white fish', , 'white fish fillets', 'cod fillets', 'bluefish fillets', 'seabass fillets', 'swordfish', 'salmon', 'salmon fillets', 'halibut fillets', 'halibut', 'trout fillets', 'trout', 'monkfish fillets', 'monkfish']
    // Add more mappings as necessary
};

   
// Event listener for the toggle container
toggleContainer.addEventListener("click", function() {
    isWeeks = !isWeeks; // Toggle the state between weeks and days
    if (isWeeks) {
        toggleContainer.textContent = "Week(s)";
        toggleContainer.classList.remove('days');
        toggleContainer.classList.add('weeks');
    } else {
        toggleContainer.textContent = "Days";
        toggleContainer.classList.remove('weeks');
        toggleContainer.classList.add('days');
    }
});





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

// Function to store the selected preferences in the session storage
function storeSelectedPreferences() {
    const selectedAllergies = Array.from(allergies).filter(allergy => allergy.classList.contains('active')).map(allergy => allergy.getAttribute('data-value'));
    const selectedIngredients = Array.from(ingredients).filter(ingredient => ingredient.classList.contains('active')).map(ingredient => ingredient.getAttribute('data-value'));
    sessionStorage.setItem('selectedAllergies', JSON.stringify(selectedAllergies));
    sessionStorage.setItem('selectedIngredients', JSON.stringify(selectedIngredients));
}



// Event listener for the query button
queryButton.addEventListener('click', function() {
    // Combine selected ingredients and allergies into one array
    const allSelectedPreferences = [
        ...Array.from(ingredients).filter(pref => pref.classList.contains('active')),
        ...Array.from(allergies).filter(pref => pref.classList.contains('active') && pref.getAttribute('data-value') !== 'no-allergies')
    ];

    // Map to ingredient names, expand using dictionary, and join into a string
    const excludedIngredients = allSelectedPreferences
        .flatMap(pref => ingredientDictionary[pref.getAttribute('data-value')] || [pref.getAttribute('data-value')])
        .join(',');

    // Retrieve and validate the numeric time value entered by the user
    const timeNumber = parseInt(document.getElementById('timeAmount').value, 10);
    if (isNaN(timeNumber) || timeNumber < 1 || timeNumber > 28) {
        alert("Please enter a valid number of days or weeks (1-28).");
        return;
    }

    // Calculate the duration in days
    const durationInDays = isWeeks ? timeNumber * 7 : timeNumber;
    
    // Before using diets.join(','), ensure it's an array. If it's a string, split it into an array.
	//const dietsArray = typeof diets === 'string' ? diets.split(',') : diets;

    // Retrieve diets and glutenFree from the session storage
    const diets = sessionStorage.getItem('diets') || '';
    const glutenFree = sessionStorage.getItem('glutenFree') === 'true';

    // Store the additional query parameters in the session storage
    sessionStorage.setItem('days', durationInDays.toString());
    sessionStorage.setItem('excludedIngredients', excludedIngredients);

    // Log for debugging
    console.log(`Duration in Days: ${durationInDays}`);
    console.log(`Diets: ${diets}`);
    console.log(`Excluded Ingredients: ${excludedIngredients}`);
    console.log(`Gluten Free: ${glutenFree}`);
    
     // Store the selected preferences in the session storage
    storeSelectedPreferences();


    // Make a request to the backend to get the next page
    fetch('/results')
        .then(response => response.text())
        .then(html => {
            // Replace the current page with the received HTML
            document.open();
            document.write(html);
            document.close();
        })
        .catch(error => console.error('Error:', error));
});


    // Set the initial state of the query button based on allergies selection
    updateQueryButtonState();
    
    // Function to retrieve the selected preferences from the session storage
function retrieveSelectedPreferences() {
    const storedAllergies = sessionStorage.getItem('selectedAllergies');
    const storedIngredients = sessionStorage.getItem('selectedIngredients');
    if (storedAllergies) {
        JSON.parse(storedAllergies).forEach(allergy => {
            const allergyElement = document.querySelector(`.preference[data-value="${allergy}"]`);
            if (allergyElement) {
                allergyElement.classList.add('active');
            }
        });
    }
    if (storedIngredients) {
        JSON.parse(storedIngredients).forEach(ingredient => {
            const ingredientElement = document.querySelector(`.preference[data-value="${ingredient}"]`);
            if (ingredientElement) {
                ingredientElement.classList.add('active');
            }
        });
    }
    updateQueryButtonState();
}

// Call retrieveSelectedPreferences on page load
retrieveSelectedPreferences();
    
    
    
    
    
    
    
});
