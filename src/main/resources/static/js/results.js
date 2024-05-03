document.addEventListener('DOMContentLoaded', () => {
    fetchRecipeData();
});

function fetchRecipeData() {
    // Retrieve query parameters from the session storage
    const diets = sessionStorage.getItem('diets') || '';
    const excludedIngredients = sessionStorage.getItem('excludedIngredients') || '';
    const glutenFree = sessionStorage.getItem('glutenFree') === 'true';
    const days = sessionStorage.getItem('days') || '14'; // Default to 14 days if not specified

    // If the parameters were already URL-encoded when passed in the URL, you don't need to encode them again here.
    const apiQuery = `/recipes/filtered?diets=${diets}&excludedIngredientsFromRequest=${excludedIngredients}&glutenFree=${glutenFree}&days=${days}`;

    fetch(apiQuery)
        .then(handleResponse)
        .then(displayRecipes)
        .catch(handleError);
}


function handleResponse(response) {
    if (!response.ok) throw new Error('Network response was not ok');
    return response.json();
}

function displayRecipes(data) {
    const container = document.getElementById('recipeResults');
    container.innerHTML = ''; // Clear previous results

    if (!data.week || Object.keys(data.week).length === 0) {
        container.innerHTML = '<p>No recipes found for this week.</p>';
        return;
    }

    // Define a set of colors to cycle through for each week
    const colors = ['#ffcccc', '#ccffcc', '#ccccff', '#ffcc99', '#99ccff', '#cc99ff', '#ff99cc'];
    
    // Track the current week number
    let currentWeek = 1;
    // Keep track of the number of days processed to determine the week number
    let daysProcessed = 0;
    
    // Iterate over each day in the week data
    Object.entries(data.week).forEach(([dayOfWeek, info]) => {
        daysProcessed++;
        
        // Calculate the current week based on the number of days processed
        currentWeek = Math.ceil(daysProcessed / 7);
        
        // Determine the color for the current week
        const weekColor = colors[(currentWeek - 1) % colors.length];

        // Process each meal within the day
        info.meals.forEach(meal => {
            const card = document.createElement('div');
            card.className = 'recipe-card';
            card.style.backgroundColor = weekColor; // Apply the color

            // Title and ID
            const titleText = document.createElement('p');
            titleText.innerHTML = `<strong>Title:</strong> ${meal.title}`;
            card.appendChild(titleText);

            const idText = document.createElement('p');
            idText.innerHTML = `<strong>ID:</strong> ${meal.id}`;
            card.appendChild(idText);

            // Ingredients list (if available)
            if (meal.ingredients) {
                const ingredientsText = document.createElement('p');
                ingredientsText.innerHTML = `<strong>Ingredients:</strong> ${meal.ingredients.join(', ')}`;
                card.appendChild(ingredientsText);
            }

			 // Week number and meal number
    		const weekMealInfoText = document.createElement('p');
    		weekMealInfoText.innerHTML = `<strong>Week:</strong> ${meal.weekNumber}, <strong>Meal:</strong> ${meal.mealNumber}`;
    		card.appendChild(weekMealInfoText);
            container.appendChild(card);
        });

        // Output for debugging
        console.log(`Week ${currentWeek} (Day: ${dayOfWeek}): Color ${weekColor}`);
    });
}

function handleError(error) {
    console.error('Fetch error:', error);
    document.getElementById('recipeResults').innerHTML = `<p>Error: ${error.message}</p>`;
}

// ... (existing code)

function goToAllergiesPage() {
    // Make a request to the backend to get the allergies page
    fetch('/allergies')
        .then(response => response.text())
        .then(html => {
            // Replace the current page with the received HTML
            document.open();
            document.write(html);
            document.close();
        })
        .catch(error => console.error('Error:', error));
}
