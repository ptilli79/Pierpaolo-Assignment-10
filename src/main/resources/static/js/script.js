document.addEventListener('DOMContentLoaded', () => {
    const preferences = document.querySelectorAll('.preference');
    const submitButton = document.getElementById('submit-preferences');

    // Check if this is a direct page load/reload
    if (!sessionStorage.getItem('navigatedFrom')) {
        clearSelections();
        sessionStorage.setItem('dietsCleared', 'true');
    }
    sessionStorage.removeItem('navigatedFrom');

    function clearSelections() {
        sessionStorage.removeItem('indexState');
        document.querySelectorAll('.preference').forEach(pref => {
            pref.classList.remove('active', 'disabled');
        });
    }

    function updateState() {
        const activePreferences = document.querySelectorAll('.preference.active');
        submitButton.disabled = activePreferences.length === 0;
        preferences.forEach(pref => {
            if (activePreferences.length >= 4) {
                if (!pref.classList.contains('active')) {
                    pref.classList.add('disable-hover', 'disabled');
                }
            } else {
                pref.classList.remove('disable-hover', 'disabled');
            }
        });
    }

    document.querySelector('.preferences').addEventListener('click', function(e) {
        if (e.target.classList.contains('preference')) {
            if (e.target.classList.contains('active')) {
                e.target.classList.remove('active');
            } else if (document.querySelectorAll('.preference.active').length < 4) {
                e.target.classList.add('active');
            }
            updateState();
            // Save state after each interaction
            sessionStorage.setItem('indexState', document.querySelector('.preferences').innerHTML);
        }
    });

    submitButton.addEventListener('click', function() {
        const selectedPreferences = Array.from(document.querySelectorAll('.preference.active'))
            .map(pref => pref.getAttribute('data-value'));
        if (selectedPreferences.length > 0) {
            sessionStorage.setItem('diets', selectedPreferences.join(','));
            sessionStorage.setItem('glutenFree', selectedPreferences.includes('gluten free').toString());
            sessionStorage.setItem('navigatedFrom', 'index');
            window.location.href = '/allergies';
        }
    });

    // Restore state if it exists
    const savedState = sessionStorage.getItem('indexState');
    if (savedState) {
        document.querySelector('.preferences').innerHTML = savedState;
    }

    updateState();
});