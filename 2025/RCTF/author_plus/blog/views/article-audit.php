<?php 
$pageTitle = 'Audit Article - ROIS Blog';
include __DIR__ . '/layout/header.php'; 
?>

<div class="article-audit-page">
    <div class="container">
        <div class="audit-header">
            <h1>Article Audit</h1>
            <div class="audit-breadcrumb">
                <a href="/dashboard">Dashboard</a>
                <span>/</span>
                <a href="/article/<?php echo $article['id']; ?>">Article</a>
                <span>/</span>
                <span>Audit</span>
            </div>
        </div>
        
        <div class="audit-content">
            <!-- Article Information Card -->
            <div class="article-info-card">
                <div class="info-header">
                    <h2>Article Information</h2>
                    <span class="status-badge status-<?php echo $article['status']; ?>">
                        <?php echo ucfirst($article['status']); ?>
                    </span>
                </div>
                
                <div class="info-body">
                    <div class="info-row">
                        <label>Title:</label>
                        <div class="info-value"><?php echo $article['title']; ?></div>
                    </div>
                    
                    <?php if (!empty($article['subtitle'])): ?>
                    <div class="info-row">
                        <label>Subtitle:</label>
                        <div class="info-value"><?php echo $article['subtitle']; ?></div>
                    </div>
                    <?php endif; ?>
                    
                    <div class="info-row">
                        <label>Author:</label>
                        <div class="info-value"><?php echo $article['username']; ?></div>
                    </div>
                    
                    <div class="info-row">
                        <label>Created At:</label>
                        <div class="info-value"><?php echo date('F j, Y H:i', strtotime($article['created_at'])); ?></div>
                    </div>
                    
                    <div class="info-row">
                        <label>Views:</label>
                        <div class="info-value"><?php echo $article['views']; ?></div>
                    </div>
                </div>
            </div>
            
            <!-- Audit Actions Card -->
            <div class="audit-actions-card">
                <h2>Audit Actions</h2>
                <p class="audit-description">
                    <a href="/article/<?php echo $article['id']; ?>" target="_blank" style="color: #007bff; text-decoration: none;">
                        Click here to view the full article
                    </a>
                    , then decide whether to approve or reject it.
                </p>
                
                <div class="action-buttons">
                    <button onclick="approveArticle(<?php echo $article['id']; ?>)" class="btn-approve">
                        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                            <path d="M5 10l3 3 7-7" stroke="currentColor" stroke-width="2" fill="none"/>
                        </svg>
                        Approve Article
                    </button>
                    <button onclick="rejectArticle(<?php echo $article['id']; ?>)" class="btn-reject">
                        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                            <path d="M6 6l8 8M14 6l-8 8" stroke="currentColor" stroke-width="2"/>
                        </svg>
                        Reject Article
                    </button>
                    <a href="/article/<?php echo $article['id']; ?>" class="btn-secondary">
                        Back to Article
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
.article-audit-page {
    padding: 40px 20px;
    min-height: calc(100vh - 60px);
    background: #f5f5f5;
}

.audit-header {
    max-width: 1000px;
    margin: 0 auto 30px;
}

.audit-header h1 {
    font-size: 2rem;
    color: #1a1a1a;
    margin-bottom: 10px;
}

.audit-breadcrumb {
    display: flex;
    align-items: center;
    gap: 10px;
    color: #666;
    font-size: 0.9rem;
}

.audit-breadcrumb a {
    color: #007bff;
    text-decoration: none;
}

.audit-breadcrumb a:hover {
    text-decoration: underline;
}

.audit-content {
    max-width: 1000px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.article-info-card,
.audit-actions-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.info-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 2px solid #f0f0f0;
}

.info-header h2,
.audit-actions-card h2 {
    font-size: 1.3rem;
    color: #1a1a1a;
    margin: 0;
}

.status-badge {
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 0.85rem;
    font-weight: 500;
}

.status-pending {
    background: #fff3cd;
    color: #856404;
}

.status-approved {
    background: #d4edda;
    color: #155724;
}

.status-rejected {
    background: #f8d7da;
    color: #721c24;
}

.info-body {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.info-row {
    display: grid;
    grid-template-columns: 150px 1fr;
    gap: 16px;
}

.info-row label {
    font-weight: 600;
    color: #555;
}

.info-value {
    color: #1a1a1a;
}

.audit-description {
    color: #666;
    margin-bottom: 24px;
    line-height: 1.6;
}

.action-buttons {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
}

.action-buttons button,
.action-buttons a {
    padding: 12px 24px;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    font-size: 1rem;
    font-weight: 500;
    transition: all 0.3s ease;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    text-decoration: none;
}

.btn-approve {
    background: #28a745;
    color: white;
}

.btn-approve:hover {
    background: #218838;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
}

.btn-reject {
    background: #dc3545;
    color: white;
}

.btn-reject:hover {
    background: #c82333;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(220, 53, 69, 0.3);
}

.btn-secondary {
    background: #6c757d;
    color: white;
}

.btn-secondary:hover {
    background: #5a6268;
}

.btn-sm {
    padding: 8px 16px;
    font-size: 0.9rem;
}

@media (max-width: 768px) {
    .article-audit-page {
        padding: 20px 10px;
    }
    
    .audit-header h1 {
        font-size: 1.5rem;
    }
    
    .info-row {
        grid-template-columns: 1fr;
        gap: 8px;
    }
    
    .info-row label {
        font-size: 0.9rem;
    }
    
    .action-buttons {
        flex-direction: column;
    }
    
    .action-buttons button,
    .action-buttons a {
        width: 100%;
        justify-content: center;
    }
}
</style>

<script src="/assets/js/article-audit.js"></script>

<?php include __DIR__ . '/layout/footer.php'; ?>

