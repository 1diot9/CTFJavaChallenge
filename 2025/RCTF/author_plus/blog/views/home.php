<?php 
$pageTitle = 'Home - ROIS Blog';
include __DIR__ . '/layout/header.php'; 
?>

<div class="hero">
    <div class="container">
        <h1 class="hero-title">Stay Curious</h1>
        <p class="hero-subtitle">Discover fascinating stories, ideas, and expertise from every corner of the world</p>
    </div>
</div>

<main class="main-content">
    <div class="container">
        <div class="articles-grid" id="articles-list">
            <div class="loading">Loading...</div>
        </div>
        <div class="load-more-container">
            <button id="load-more" class="btn-secondary" style="display: none;">Load More</button>
        </div>
    </div>
</main>

<script src="/assets/js/home.js"></script>

<?php include __DIR__ . '/layout/footer.php'; ?>

