document.addEventListener('DOMContentLoaded', () => {
    const queryButton = document.getElementById('query-recipes');
    const toggleContainer = document.getElementById("toggleContainer");
    let isWeeks = false;

    // Check if this is a direct page load/reload or if diets were cleared
    if (!sessionStorage.getItem('navigatedFrom') || sessionStorage.getItem('dietsCleared') === 'true') {
        clearSelections();
    }
    sessionStorage.removeItem('navigatedFrom');
    sessionStorage.removeItem('dietsCleared');

    function clearSelections() {
        sessionStorage.removeItem('allergiesState');
        document.querySelectorAll('.preference').forEach(pref => {
            pref.classList.remove('active', 'disabled');
        });
    }

    // Restore state if it exists
    const savedState = sessionStorage.getItem('allergiesState');
    if (savedState) {
        document.querySelector('.preferences-layout').innerHTML = savedState;
    }

    toggleContainer.addEventListener("click", function() {
        isWeeks = !isWeeks;
        toggleContainer.textContent = isWeeks ? "Week(s)" : "Days";
        toggleContainer.classList.toggle('days', !isWeeks);
        toggleContainer.classList.toggle('weeks', isWeeks);
    });

    function updateQueryButtonState() {
        const anyAllergiesSelected = document.querySelector('#allergies .preference.active') !== null;
        queryButton.disabled = !anyAllergiesSelected;
    }

    document.querySelector('.preferences-layout').addEventListener('click', function(e) {
        if (e.target.classList.contains('preference')) {
            if (e.target.closest('#allergies')) {
                const noAllergyButton = document.querySelector('#allergies .preference[data-value="no-allergies"]');
                if (e.target === noAllergyButton) {
                    if (e.target.classList.contains('active')) {
                        // Deactivating "No Allergies"
                        e.target.classList.remove('active');
                        document.querySelectorAll('#allergies .preference:not([data-value="no-allergies"])').forEach(allergy => {
                            allergy.classList.remove('disabled');
                        });
                    } else {
                        // Activating "No Allergies"
                        e.target.classList.add('active');
                        document.querySelectorAll('#allergies .preference:not([data-value="no-allergies"])').forEach(allergy => {
                            allergy.classList.remove('active');
                            allergy.classList.add('disabled');
                        });
                    }
                } else if (!noAllergyButton.classList.contains('active')) {
                    // Only allow toggling other allergies if "No Allergies" is not active
                    e.target.classList.toggle('active');
                }
                // If "No Allergies" is active, do nothing when clicking other allergies
            } else {
                // This is for non-allergy preferences (e.g., excluded ingredients)
                e.target.classList.toggle('active');
            }
            updateQueryButtonState();
            // Save state after each interaction
            sessionStorage.setItem('allergiesState', document.querySelector('.preferences-layout').innerHTML);
        }
    });

    queryButton.addEventListener('click', function() {
        const selectedAllergies = Array.from(document.querySelectorAll('#allergies .preference.active')).map(allergy => allergy.getAttribute('data-value'));
        const selectedIngredients = Array.from(document.querySelectorAll('#excluded-ingredients .preference.active')).map(ingredient => ingredient.getAttribute('data-value'));
        const allSelectedPreferences = [...selectedAllergies, ...selectedIngredients];
        const excludedIngredients = allSelectedPreferences.join(',');
        const timeNumber = parseInt(document.getElementById('timeAmount').value, 10);
        if (isNaN(timeNumber) || timeNumber < 1 || timeNumber > 28) {
            alert("Please enter a valid number of days or weeks (1-28).");
            return;
        }
        const durationInDays = isWeeks ? timeNumber * 7 : timeNumber;
        sessionStorage.setItem('days', durationInDays.toString());
        sessionStorage.setItem('excludedIngredients', excludedIngredients);
        sessionStorage.setItem('navigatedFrom', 'allergies');
        window.location.href = '/results';
    });

    updateQueryButtonState();
});

function goToDietsPage() {
    sessionStorage.setItem('navigatedFrom', 'allergies');
    window.location.href = '/select-exclusions';
}