<?php 
$pageTitle = 'Register - ROIS Blog';
include __DIR__ . '/layout/header.php'; 
?>

<div class="auth-page">
    <div class="auth-container">
        <h1 class="auth-title">Join Us</h1>
        <p class="auth-subtitle">Start your creative journey</p>
        
        <form id="register-form" class="auth-form">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" required autocomplete="username">
            </div>
            
            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" required autocomplete="email">
            </div>
            
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required autocomplete="new-password">
            </div>
            
            <div class="form-group">
                <label for="confirm_password">Confirm Password</label>
                <input type="password" id="confirm_password" name="confirm_password" required autocomplete="new-password">
            </div>
            
            <div class="form-message" id="message"></div>
            
            <button type="submit" class="btn-primary btn-block">Register</button>
        </form>
        
        <p class="auth-footer">
            Already have an account? <a href="/login">Login now</a>
        </p>
    </div>
</div>

<script src="/assets/js/auth.js"></script>

<?php include __DIR__ . '/layout/footer.php'; ?>

