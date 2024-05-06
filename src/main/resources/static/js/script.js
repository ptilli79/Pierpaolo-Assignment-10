console.log("Script loaded");
document.addEventListener('DOMContentLoaded', () => {
    console.log("DOM fully loaded and parsed");
    const preferences = document.querySelectorAll('.preference');
    let selectedPreferences = [];
    const submitButton = document.getElementById('submit-preferences');

    // Function to update the state of the submit button and preferences
    function updateState() {
        // Enable or disable the submit button based on the number of selected preferences
        submitButton.disabled = selectedPreferences.length === 0;

        // Enable or disable preferences and hover effect based on the number of selected preferences
        preferences.forEach(pref => {
            if (selectedPreferences.length >= 4) {
                if (!pref.classList.contains('active')) {
                    pref.classList.add('disable-hover', 'disabled');
                    pref.removeEventListener('click', preferenceClickHandler); // Disable further clicks
                }
            } else {
                pref.classList.remove('disable-hover', 'disabled');
                pref.addEventListener('click', preferenceClickHandler); // Re-enable clicks
            }
        });
    }

    // Handler for preference click events
    function preferenceClickHandler() {
        const value = this.getAttribute('data-value');

        // Toggle 'active' class and update selectedPreferences
        if (selectedPreferences.includes(value)) {
            // Remove the preference from the array if it's already there
            selectedPreferences = selectedPreferences.filter(item => item !== value);
            this.classList.remove('active');
        } else if (selectedPreferences.length < 4) {
            // Add the preference to the array if we have less than 4
            selectedPreferences.push(value);
            this.classList.add('active');
        }

        // Update the button and preferences states
        updateState();
    }

    // Initially set up preferences with event listeners and disable the submit button
    preferences.forEach(pref => {
        pref.addEventListener('click', preferenceClickHandler);
    });
    updateState();

    // Submit button click event
//    submitButton.addEventListener('click', function() {
//        const query = selectedPreferences.map(pref => `excludedIngredients=${encodeURIComponent(pref)}`).join('&');
//        window.location.href = `/recipes/filtered?${query}`;
//    });

        // Enable or disable preferences and hover effect based on the number of selected preferences
        preferences.forEach(pref => {
            if (selectedPreferences.length >= 4) {
                if (!pref.classList.contains('active')) {
                    pref.classList.add('disable-hover', 'disabled');
                    pref.removeEventListener('click', preferenceClickHandler); // Disable further clicks
                }
            } else {
                pref.classList.remove('disable-hover', 'disabled');
                pref.addEventListener('click', preferenceClickHandler); // Re-enable clicks
            }
        });


	// Submit button click event
	submitButton.addEventListener('click', function() {
    if (selectedPreferences.length > 0) {
        const diets = selectedPreferences.join(','); // Convert array to comma-separated string
        const glutenFree = selectedPreferences.includes('gluten-free'); // Check if 'gluten-free' is selected

        // Store the query parameters in the session storage
        sessionStorage.setItem('diets', diets);
        sessionStorage.setItem('glutenFree', glutenFree.toString());
        
        // Store the selected preferences in the session storage
        storeSelectedPreferences();

        // Make a request to the backend to get the next page
		window.location.href = '/allergies';
    }
});

// ... (existing code)

// Function to store the selected preferences in the session storage
function storeSelectedPreferences() {
    sessionStorage.setItem('selectedPreferences', JSON.stringify(selectedPreferences));
}

// Function to retrieve the selected preferences from the session storage
function retrieveSelectedPreferences() {
    const storedPreferences = sessionStorage.getItem('selectedPreferences');
    if (storedPreferences) {
        selectedPreferences = JSON.parse(storedPreferences);
        selectedPreferences.forEach(pref => {
            const prefElement = document.querySelector(`.preference[data-value="${pref}"]`);
            if (prefElement) {
                prefElement.classList.add('active');
            }
        });
        updateState();
    }
}

// Call retrieveSelectedPreferences on page load
retrieveSelectedPreferences();

// ... (existing code)




});


