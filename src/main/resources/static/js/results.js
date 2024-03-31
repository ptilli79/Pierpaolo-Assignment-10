document.addEventListener('DOMContentLoaded', () => {
    fetchRecipeData();
});

function fetchRecipeData() {
    const queryParams = new URLSearchParams(window.location.search);
    const diets = queryParams.get('diets') || '';
    const excludedIngredients = queryParams.get('excludedIngredients') || '';
    const glutenFree = queryParams.get('glutenFree') === 'true';
    const apiQuery = `/recipes/filtered?diets=${encodeURIComponent(diets)}&excludedIngredientsFromRequest=${excludedIngredients}&glutenFree=${glutenFree}`;

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

    // Enhance color assignments to accommodate more groups
    const colors = ['#ffcccc', '#ccffcc', '#ccccff', '#ffcc99', '#99ccff', '#cc99ff', '#ff99cc']; // Extended color palette
    
    let recipeCounter = 0; // Keep track of the recipe count to group every 21 recipes

    Object.entries(data.week).forEach(([weekNumber, info]) => {
        if (!info.meals || info.meals.length === 0) return;

        info.meals.forEach(meal => {
            const card = document.createElement('div');
            card.className = 'recipe-card';
            
            // Calculate the group index based on the recipe counter
            const groupIndex = Math.floor(recipeCounter / 21); // Grouping every 21 recipes
            const backgroundColor = colors[groupIndex % colors.length]; // Cycle through colors array
            
            card.style.backgroundColor = backgroundColor; // Apply group-specific background color
            card.style.border = '1px solid #ccc';
            card.style.marginBottom = '10px';
            card.style.padding = '10px';

            const titleText = document.createElement('p');
            titleText.innerHTML = `<strong>Title:</strong> ${meal.title}`;
            card.appendChild(titleText);

            const idText = document.createElement('p');
            idText.innerHTML = `<strong>ID:</strong> ${meal.id}`;
            card.appendChild(idText);

            container.appendChild(card);

            recipeCounter++; // Increment the recipe counter after processing each recipe
        });
    });
}









function handleError(error) {
    console.error('Fetch error:', error);
    document.getElementById('recipeResults').innerHTML = `<p>Error: ${error.message}</p>`;
}
