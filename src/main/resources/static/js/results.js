document.addEventListener('DOMContentLoaded', () => {
    fetchRecipeData();
});

function fetchRecipeData() {
    const diets = sessionStorage.getItem('diets') || '';
    const excludedIngredients = sessionStorage.getItem('excludedIngredients') || '';
    const glutenFree = sessionStorage.getItem('glutenFree') === 'true';
    const days = sessionStorage.getItem('days') || '14';
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
    container.innerHTML = '';
    if (!data.week || Object.keys(data.week).length === 0) {
        container.innerHTML = '<p>No recipes found for this week.</p>';
        return;
    }
    const colors = ['#ffcccc', '#ccffcc', '#ccccff', '#ffcc99', '#99ccff', '#cc99ff', '#ff99cc'];
    let currentWeek = 1;
    let daysProcessed = 0;
    
    Object.entries(data.week).forEach(([dayOfWeek, info]) => {
        daysProcessed++;
        currentWeek = Math.ceil(daysProcessed / 7);
        const weekColor = colors[(currentWeek - 1) % colors.length];
        info.meals.forEach(meal => {
            const card = document.createElement('div');
            card.className = 'recipe-card';
            card.style.backgroundColor = weekColor; 
            const titleText = document.createElement('p');
            titleText.innerHTML = `<strong>Title:</strong> ${meal.title}`;
            card.appendChild(titleText);
            const idText = document.createElement('p');
            idText.innerHTML = `<strong>ID:</strong> ${meal.id}`;
            card.appendChild(idText);
            if (meal.ingredients) {
                const ingredientsText = document.createElement('p');
                ingredientsText.innerHTML = `<strong>Ingredients:</strong> ${meal.ingredients.join(', ')}`;
                card.appendChild(ingredientsText);
            }
            const weekMealInfoText = document.createElement('p');
            weekMealInfoText.innerHTML = `<strong>Week:</strong> ${meal.weekNumber}, <strong>Meal:</strong> ${meal.mealNumber}`;
            card.appendChild(weekMealInfoText);
            container.appendChild(card);
        });
        console.log(`Week ${currentWeek} (Day: ${dayOfWeek}): Color ${weekColor}`);
    });
}

function handleError(error) {
    console.error('Fetch error:', error);
    document.getElementById('recipeResults').innerHTML = `<p>Error: ${error.message}</p>`;
}

function goToAllergiesPage() {
    window.location.href = '/allergies';
}