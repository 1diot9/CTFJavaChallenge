
<?php
$pageTitle="Photographer Sharing Platform - Capture Beautiful Moments";
$pageCSS = ['pages/home'];
$navType = 'home';
$bodyClass = 'home-body';
include __DIR__ . '/layouts/header.php';
?>

    <section class="hero-section">
        <div class="hero-content">
            <h1 class="hero-title">Share Your Photography Works</h1>
            <p class="hero-subtitle">Capture beautiful moments and showcase your creative talent</p>
            <div class="hero-buttons">
                <a href="/register" class="btn-hero-primary">
                    <i class="fas fa-rocket"></i>
                    Get Started
                </a>
                <a href="#features" class="btn-hero-secondary">
                    <i class="fas fa-info-circle"></i>
                    Learn More
                </a>
            </div>
        </div>
        <div class="hero-image">
            <div class="hero-card">
                <i class="fas fa-images hero-icon"></i>
            </div>
        </div>
    </section>

    <section id="features" class="features-section">
        <div class="section-container">
            <h2 class="section-title">Why Choose Us</h2>
            <p class="section-subtitle">A photography sharing platform built for photographers</p>
            
            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-camera-retro"></i>
                    </div>
                    <h3>Professional Display</h3>
                    <p>Beautiful gallery layout that perfectly presents each of your works</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-info-circle"></i>
                    </div>
                    <h3>EXIF Information</h3>
                    <p>Automatically extract photo parameters and display shooting details and equipment information</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-blog"></i>
                    </div>
                    <h3>Blog Posts</h3>
                    <p>Tell the story behind each photo with words and images</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-user-circle"></i>
                    </div>
                    <h3>Personal Space</h3>
                    <p>Independent personal homepage with customizable background and avatar</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-shield-alt"></i>
                    </div>
                    <h3>Secure & Reliable</h3>
                    <p>Comprehensive permission management to protect your works and privacy</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">
                        <i class="fas fa-mobile-alt"></i>
                    </div>
                    <h3>Responsive Design</h3>
                    <p>Perfect browsing experience on any device</p>
                </div>
            </div>
        </div>
    </section>

    <section class="process-section">
        <div class="section-container">
            <h2 class="section-title">Get Started in Three Steps</h2>
            <p class="section-subtitle">Quickly start your photography sharing journey</p>
            
            <div class="process-steps">
                <div class="process-step">
                    <div class="step-number">1</div>
                    <div class="step-content">
                        <h3>Register Account</h3>
                        <p>Fill in your email and password to instantly create your photographer account</p>
                    </div>
                </div>
                
                <div class="process-step">
                    <div class="step-number">2</div>
                    <div class="step-content">
                        <h3>Upload Works</h3>
                        <p>Upload photos and the system automatically extracts EXIF information</p>
                    </div>
                </div>
                
                <div class="process-step">
                    <div class="step-number">3</div>
                    <div class="step-content">
                        <h3>Publish Posts</h3>
                        <p>Write blog posts to share creative inspiration and shooting techniques</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="cta-section">
        <div class="cta-content">
            <h2>Ready to Showcase Your Works?</h2>
            <p>Join us and share your creations with millions of photographers</p>
            <a href="/register" class="btn-cta">
                <i class="fas fa-user-plus"></i>
                Free Registration
            </a>
        </div>
    </section>

    <footer class="home-footer">
        <div class="footer-container">
            <div class="footer-brand">
                <i class="fas fa-camera"></i>
                <span>Photographer Platform</span>
            </div>
            <div class="footer-links">
                <a href="/login">Login</a>
                <a href="/register">Register</a>
            </div>
            <div class="footer-copyright">
                © 2025 Photographer Sharing Platform. All rights reserved.
            </div>
        </div>
    </footer>

    <script>
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });
    </script>
</body>
</html>

<?php include __DIR__ . '/layouts/footer.php'; ?>
