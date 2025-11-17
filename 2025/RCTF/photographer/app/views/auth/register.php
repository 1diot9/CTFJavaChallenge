<?php
$pageTitle = 'Register';
$pageCSS = ['pages/auth'];
include __DIR__ . '/../layouts/header.php';
?>

<div class="auth-container">
    <div class="auth-box">
        <div class="auth-logo">
            <i class="fas fa-camera-retro"></i>
        </div>
        <div class="auth-header">
            <h1>Join Us</h1>
            <p>Start sharing your photography works</p>
        </div>
        
        <form id="registerForm" class="auth-form">
            <input type="hidden" name="csrf_token" value="<?= e(Auth::generateCSRFToken()) ?>">
            
            <div class="form-group">
                <label for="username">
                    <i class="fas fa-user"></i>
                    Username
                </label>
                <input type="text" id="username" name="username" required>
            </div>
            
            <div class="form-group">
                <label for="email">
                    <i class="fas fa-envelope"></i>
                    Email
                </label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="password">
                    <i class="fas fa-lock"></i>
                    Password
                </label>
                <input type="password" id="password" name="password" required minlength="6">
            </div>
            
            <div class="form-group">
                <label for="confirm_password">
                    <i class="fas fa-lock"></i>
                    Confirm Password
                </label>
                <input type="password" id="confirm_password" name="confirm_password" required minlength="6">
            </div>
            
            <button type="submit" class="btn-primary">Register</button>
            
            <div class="auth-footer">
                Already have an account? <a href="/login">Login now</a>
            </div>
        </form>
    </div>
</div>

<script>
document.getElementById('registerForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const btn = this.querySelector('button[type="submit"]');
    btn.disabled = true;
    btn.textContent = 'Registering...';
    
    try {
        const response = await fetch('/api/register', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            window.location.href = data.redirect;
        } else {
            alert(data.message);
            btn.disabled = false;
            btn.textContent = 'Register';
        }
    } catch (error) {
        alert('Registration failed, please try again');
        btn.disabled = false;
        btn.textContent = 'Register';
    }
});
</script>

<?php include __DIR__ . '/../layouts/footer.php'; ?>

