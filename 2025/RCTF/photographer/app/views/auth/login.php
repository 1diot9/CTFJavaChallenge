<?php
$pageTitle = 'Login';
$pageCSS = ['pages/auth'];
include __DIR__ . '/../layouts/header.php';
?>

<div class="auth-container">
    <div class="auth-box">
        <div class="auth-logo">
            <i class="fas fa-camera-retro"></i>
        </div>
        <div class="auth-header">
            <h1>Photography Sharing Platform</h1>
            <p>Capture beautiful moments with your lens</p>
        </div>
        
        <form id="loginForm" class="auth-form">
            <input type="hidden" name="csrf_token" value="<?= e(Auth::generateCSRFToken()) ?>">
            
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
                <input type="password" id="password" name="password" required>
            </div>
            
            <button type="submit" class="btn-primary">Login</button>
            
            <div class="auth-footer">
                Don't have an account? <a href="/register">Register now</a>
            </div>
        </form>
    </div>
</div>

<script>
document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const btn = this.querySelector('button[type="submit"]');
    btn.disabled = true;
    btn.textContent = 'Logging in...';
    
    try {
        const response = await fetch('/api/login', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            window.location.href = data.redirect;
        } else {
            alert(data.message);
            btn.disabled = false;
            btn.textContent = 'Login';
        }
    } catch (error) {
        alert('Login failed, please try again');
        btn.disabled = false;
        btn.textContent = 'Login';
    }
});
</script>

<?php include __DIR__ . '/../layouts/footer.php'; ?>

