<?php 
$isEdit = isset($article);
$pageTitle = $isEdit ? 'Edit Article - ROIS Blog' : 'Write Article - ROIS Blog';
include __DIR__ . '/layout/header.php'; 
?>

<div class="editor-page">
    <div class="editor-container">
        <form id="article-form" class="article-form">
            <input type="hidden" id="article-id" value="<?php echo $isEdit ? $article['id'] : ''; ?>">
            
            <div class="form-group">
                <input 
                    type="text" 
                    id="title" 
                    name="title" 
                    class="input-title" 
                    placeholder="Article Title" 
                    value="<?php echo $isEdit ? $article['title']: ''; ?>"
                    required
                >
            </div>
            
            <div class="form-group">
                <input 
                    type="text" 
                    id="subtitle" 
                    name="subtitle" 
                    class="input-subtitle" 
                    placeholder="Subtitle (optional)" 
                    value="<?php echo $isEdit ? $article['subtitle'] : ''; ?>"
                >
            </div>
            
            <div class="form-group">
                <input
                    id="content" 
                    name="content" 
                    class="input-content" 
                    placeholder="Start writing your article..."
                    value="<?php echo $isEdit ? htmlspecialchars($article['content']) : ''; ?>"
                    required
                >
            </div>
            
            <div class="form-message" id="message"></div>
            
            <div class="form-actions">
                <button type="submit" class="btn-primary">
                    <?php echo $isEdit ? 'Update Article' : 'Publish Article'; ?>
                </button>
                <a href="/dashboard" class="btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script src="/assets/js/editor.js"></script>

<?php include __DIR__ . '/layout/footer.php'; ?>

