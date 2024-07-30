document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    const errorMessage = document.getElementById('error-message');

    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.style.display = 'block';
    }

    registerForm.addEventListener('submit', function(event) {
        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (!username || !email || !password || !confirmPassword) {
            event.preventDefault();
            showError('All fields are required.');
            return;
        }

        if (password !== confirmPassword) {
            event.preventDefault();
            showError('Passwords do not match.');
            return;
        }

        // If all validations pass, the form will be submitted
    });
});