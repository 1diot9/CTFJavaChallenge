<?php
$pageAuthor = htmlspecialchars($article['username']);
$pageTitle = $article['title'] . ' - ROIS Blog';
include __DIR__ . '/layout/header.php'; 
?>
<script src="/assets/js/purify.min.js"></script>
<script src="/assets/js/xss-shield.js"></script>

<article class="article-page">
    <div class="article-container">
        <header class="article-header">
            <h1 class="article-title" id="article-title"><?php echo $article['title']; ?></h1>
            <?php if (!empty($article['subtitle'])): ?>
                <p class="article-subtitle" id="article-subtitle"><?php echo $article['subtitle']; ?></p>
            <?php endif; ?>
            <div class="article-meta">
                <span class="author">
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                        <circle cx="10" cy="6" r="3" stroke="currentColor" stroke-width="1.5"/>
                        <path d="M5 15a5 5 0 0 1 10 0" stroke="currentColor" stroke-width="1.5"/>
                    </svg>
                    <span title="<?php echo $pageAuthor; ?>"><?php echo $pageAuthor ?></span>
                </span>
                <span class="date"><?php echo date('F j, Y', strtotime($article['created_at'])); ?></span>
                <span class="views"><?php echo $article['views']; ?> views</span>
            </div>
        </header>

        <div class="article-content" id="article-content">
            <div class="loading">Loading article content...</div>
        </div>
        
        <?php if (isset($_SESSION['is_admin']) && $_SESSION['is_admin'] && $article['status'] === 'pending'): ?>
            <!-- Admin audit notice -->
            <div class="article-audit-notice">
                <div class="audit-notice-content">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <circle cx="12" cy="12" r="10" stroke="#ff9800" stroke-width="2"/>
                        <path d="M12 8v4M12 16h.01" stroke="#ff9800" stroke-width="2"/>
                    </svg>
                    <div>
                        <h3>This article is pending review</h3>
                        <p>Click the button below to go to the audit page to review this article</p>
                    </div>
                </div>
                <a id="audit" href="/article/<?php echo $article['id']; ?>/audit" class="btn-audit">Go to Audit Page</a>
            </div>
        <?php endif; ?>
    </div>
</article>


<style>
.article-audit-notice {
    margin-top: 40px;
    padding: 24px;
    background: #fff8e1;
    border: 2px solid #ff9800;
    border-radius: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 20px;
}

.audit-notice-content {
    display: flex;
    gap: 16px;
    align-items: flex-start;
}

.audit-notice-content h3 {
    margin: 0 0 8px 0;
    color: #e65100;
    font-size: 1.1rem;
}

.audit-notice-content p {
    margin: 0;
    color: #f57c00;
    font-size: 0.95rem;
}

.btn-audit {
    background: #ff9800;
    color: white;
    padding: 12px 24px;
    border-radius: 8px;
    text-decoration: none;
    white-space: nowrap;
    font-weight: 500;
    transition: background 0.3s ease;
}

.btn-audit:hover {
    background: #f57c00;
}

@media (max-width: 768px) {
    .article-audit-notice {
        flex-direction: column;
        align-items: stretch;
    }
    
    .btn-audit {
        text-align: center;
    }
}
</style>

<script src="/assets/js/article.js"></script>
<?php include __DIR__ . '/layout/footer.php'; ?>

