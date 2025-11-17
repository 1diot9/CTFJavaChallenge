// Login and register form handling

document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
});

async function handleLogin(e) {
    e.preventDefault();
    
    const messageEl = document.getElementById('message');
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    
    // Get form data
    const formData = new FormData(e.target);
    
    // Disable button
    submitBtn.disabled = true;
    submitBtn.textContent = 'Logging in...';
    messageEl.className = 'form-message';
    messageEl.style.display = 'none';
    
    try {
        const response = await fetch('/login', {
            method: 'POST',
            headers: CsrfHelper.getHeaders(),
            body: formData
        });
        
        const result = await response.json();
        
        if (result.success) {
            messageEl.className = 'form-message success';
            messageEl.textContent = result.message;
            messageEl.style.display = 'block';
            
            // Redirect to dashboard
            setTimeout(() => {
                window.location.href = '/dashboard';
            }, 1000);
        } else {
            messageEl.className = 'form-message error';
            messageEl.textContent = result.message;
            messageEl.style.display = 'block';
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        }
    } catch (error) {
        console.error('Login failed:', error);
        messageEl.className = 'form-message error';
        messageEl.textContent = 'Login failed, please try again later';
        messageEl.style.display = 'block';
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    }
}

async function handleRegister(e) {
    e.preventDefault();
    
    const messageEl = document.getElementById('message');
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    
    // Get form data
    const formData = new FormData(e.target);
    
    // Disable button
    submitBtn.disabled = true;
    submitBtn.textContent = 'Registering...';
    messageEl.className = 'form-message';
    messageEl.style.display = 'none';
    
    try {
        const response = await fetch('/register', {
            method: 'POST',
            headers: CsrfHelper.getHeaders(),
            body: formData
        });
        
        const result = await response.json();
        
        if (result.success) {
            messageEl.className = 'form-message success';
            messageEl.textContent = result.message;
            messageEl.style.display = 'block';
            
            // Redirect to dashboard
            setTimeout(() => {
                window.location.href = '/dashboard';
            }, 1000);
        } else {
            messageEl.className = 'form-message error';
            messageEl.textContent = result.message;
            messageEl.style.display = 'block';
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        }
    } catch (error) {
        console.error('Registration failed:', error);
        messageEl.className = 'form-message error';
        messageEl.textContent = 'Registration failed, please try again later';
        messageEl.style.display = 'block';
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    }
}

