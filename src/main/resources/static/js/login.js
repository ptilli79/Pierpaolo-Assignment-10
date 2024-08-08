document.addEventListener('DOMContentLoaded', function() {
    const loginDiv = document.getElementById('loginDiv');
    const registerDiv = document.getElementById('registerDiv');
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const showRegisterFormButton = document.getElementById('showRegisterForm');
    const showLoginFormButton = document.getElementById('showLoginForm');
    const loginErrorMessage = document.getElementById('login-error-message');
    const loginSuccessMessage = document.getElementById('login-success-message');
    const registerErrorMessage = document.getElementById('register-error-message');
    const registerSuccessMessage = document.getElementById('register-success-message');

    function showMessage(message, isError = true, formType = 'login') {
        let errorMessage, successMessage;
        if (formType === 'login') {
            errorMessage = loginErrorMessage;
            successMessage = loginSuccessMessage;
        } else {
            errorMessage = registerErrorMessage;
            successMessage = registerSuccessMessage;
        }

        if (isError) {
            errorMessage.textContent = message;
            errorMessage.style.display = 'block';
            successMessage.style.display = 'none';
            setTimeout(() => {
                errorMessage.classList.add('hidden');
                setTimeout(() => {
                    errorMessage.style.display = 'none';
                    errorMessage.classList.remove('hidden');
                }, 2000);
            }, 2000);
        } else {
            successMessage.textContent = message;
            successMessage.style.display = 'block';
            errorMessage.style.display = 'none';
            setTimeout(() => {
                successMessage.classList.add('hidden');
                setTimeout(() => {
                    successMessage.style.display = 'none';
                    successMessage.classList.remove('hidden');
                }, 2000);
            }, 2000);
        }
    }

    // Toggle between forms
    showRegisterFormButton.addEventListener('click', function() {
        loginDiv.style.display = 'none';
        registerDiv.style.display = 'block';
        loginErrorMessage.style.display = 'none';
        loginSuccessMessage.style.display = 'none';
    });

    showLoginFormButton.addEventListener('click', function() {
        registerDiv.style.display = 'none';
        loginDiv.style.display = 'block';
        registerErrorMessage.style.display = 'none';
        registerSuccessMessage.style.display = 'none';
    });

    // Handle login form submission
    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();
        
        const formData = new FormData(this);
        
        fetch('/custom_login', {
            method: 'POST',
            body: formData,
            redirect: 'follow'
        })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url;
            } else {
                return response.text();
            }
        })
        .then(result => {
            if (result) {
                const [status, message] = result.split(':', 2);
                if (status === 'error') {
                    showMessage(message, true, 'login');
                } else if (status === 'success') {
                    window.location.href = '/home';
                }
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showMessage('An error occurred. Please try again.', true, 'login');
        });
    });

    // Handle register form submission
    registerForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const formData = new FormData(this);

        fetch('/register', {
            method: 'POST',
            body: formData
        })
        .then(response => response.text())
        .then(result => {
            const [status, message] = result.split(':', 2);
            if (status === 'error') {
                showMessage(message, true, 'register');
            } else if (status === 'success') {
                showMessage(message, false, 'register');
                setTimeout(() => {
                    registerDiv.style.display = 'none';
                    loginDiv.style.display = 'block';
                    document.getElementById('username').value = formData.get('username');
                }, 2000);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showMessage('An error occurred. Please try again.', true, 'register');
        });
    });
});
