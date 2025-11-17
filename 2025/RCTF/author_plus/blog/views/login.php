<?php 
$pageTitle = 'Login - ROIS Blog';
include __DIR__ . '/layout/header.php'; 
?>

<div class="auth-page">
    <div class="auth-container">
        <h1 class="auth-title">Welcome Back</h1>
        <p class="auth-subtitle">Login to continue your creative journey</p>
        
        <form id="login-form" class="auth-form">
            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" required autocomplete="email">
            </div>
            
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required autocomplete="current-password">
            </div>
            
            <div class="form-message" id="message"></div>
            
            <button type="submit" class="btn-primary btn-block">Login</button>
        </form>
        
        <p class="auth-footer">
            Don't have an account? <a href="/register">Sign up now</a>
        </p>
    </div>
</div>

<script src="/assets/js/auth.js"></script>

<?php include __DIR__ . '/layout/footer.php'; ?>

