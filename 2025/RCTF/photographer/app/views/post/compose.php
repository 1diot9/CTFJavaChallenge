<?php
$pageTitle = 'Publish Work';
$pageCSS = ['pages/compose'];
$pageJS = ['pages/compose'];
$navType = 'compose';
include __DIR__ . '/../layouts/header.php';
?>

<div class="compose-container">
    
    <div class="compose-wrapper">
        <form id="composeForm" class="compose-form">
            <input type="hidden" name="csrf_token" value="<?= e(Auth::generateCSRFToken()) ?>">
            <input type="hidden" name="photo_ids" id="photoIds" value="[]">
            
            <div class="compose-title-group">
                <input type="text" 
                       name="title" 
                       id="postTitle" 
                       placeholder="Give your work a title..." 
                       class="compose-title-input"
                       required>
            </div>
            
            <div class="compose-content-group">
                <textarea name="content" 
                          id="postContent" 
                          placeholder="Share the story behind this shoot, equipment used, shooting techniques..." 
                          class="compose-content-input"
                          rows="6"></textarea>
            </div>
            
            <div id="photoPreviewArea" class="compose-photo-preview"></div>
            
            <div class="compose-toolbar">
                <button type="button" class="toolbar-btn" title="Add Photos" onclick="document.getElementById('photoUpload').click()">
                    <i class="fas fa-image"></i>
                </button>
                <input type="file" id="photoUpload" accept="image/*" multiple style="display: none;">
                
                <div class="toolbar-info">
                    <span id="photoCount" class="photo-count">0 photos</span>
                </div>
            </div>
        </form>
    </div>
</div>

<script>
const csrfToken = '<?= e(Auth::generateCSRFToken()) ?>';
</script>

<?php include __DIR__ . '/../layouts/footer.php'; ?>

