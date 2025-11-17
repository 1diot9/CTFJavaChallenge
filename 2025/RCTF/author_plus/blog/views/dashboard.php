<?php 
$pageTitle = 'My Articles - ROIS Blog';
include __DIR__ . '/layout/header.php'; 
?>

<div class="dashboard">
    <div class="container">
        <div class="dashboard-header">
            <h1>My Articles</h1>
            <a href="/articles/create" class="btn-primary">New Article</a>
        </div>
        
        <?php if (empty($articles)): ?>
            <div class="empty-state">
                <svg width="100" height="100" viewBox="0 0 100 100" fill="none">
                    <circle cx="50" cy="50" r="40" stroke="#ddd" stroke-width="2"/>
                    <path d="M35 50h30M50 35v30" stroke="#ddd" stroke-width="2"/>
                </svg>
                <h2>No articles yet</h2>
                <p>Start your first creation</p>
                <a href="/articles/create" class="btn-primary">Write Article</a>
            </div>
        <?php else: ?>
            <div class="articles-list">
                <?php foreach ($articles as $article): ?>
                    <div class="article-card">
                        <div class="article-card-content">
                            <h2 class="article-card-title">
                                <a href="/article/<?php echo $article['id']; ?>">
                                    <?php echo $article['title']; ?>
                                </a>
                            </h2>
                            <?php if (!empty($article['subtitle'])): ?>
                                <p class="article-card-subtitle">
                                    <?php echo$article['subtitle']; ?>
                                </p>
                            <?php endif; ?>
                            <div class="article-card-meta">
                                <span><?php echo date('Y-m-d', strtotime($article['created_at'])); ?></span>
                                <span><?php echo $article['views']; ?> views</span>
                                <span class="status-badge status-<?php echo $article['status']; ?>">
                                    <?php echo ucfirst($article['status']); ?>
                                </span>
                            </div>
                        </div>
                        <div class="article-card-actions">
                            <?php if ($article['status'] === 'pending'): ?>
                                <a href="/articles/edit/<?php echo $article['id']; ?>" class="btn-secondary btn-sm">Edit</a>
                            <?php endif; ?>
                            <button onclick="deleteArticle(<?php echo $article['id']; ?>)" class="btn-danger btn-sm">Delete</button>
                        </div>
                    </div>
                <?php endforeach; ?>
            </div>
        <?php endif; ?>
    </div>
</div>

<script src="/assets/js/dashboard.js"></script>

<?php include __DIR__ . '/layout/footer.php'; ?>

