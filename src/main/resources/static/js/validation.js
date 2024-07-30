document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(event) {
            event.preventDefault(); // Prevent default form submission

            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                alert('Passwords do not match!');
                return;
            }

            if (!username || !email || !password) {
                alert('All fields are required!');
                return;
            }

            // If validation passes, submit the form
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfParameter = document.querySelector('meta[name="_csrf_parameter"]').getAttribute('content');

            const formData = new FormData(registerForm);
            formData.append(csrfParameter, csrfToken);

            fetch(registerForm.action, {
                method: 'POST',
                body: formData,
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                }
            })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/login?registered';
                } else {
                    return response.text();
                }
            })
            .then(errorMessage => {
                if (errorMessage) {
                    alert('Registration failed: ' + errorMessage);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred during registration');
            });
        });
    }

    const loginForm = document.querySelector('form[action="/login"]');
    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
            if (!username || !password) {
                event.preventDefault();
                alert('Username and password are required!');
            }
            // Login form submission is handled by Spring Security, so we don't need to add CSRF token manually
        });
    }
});