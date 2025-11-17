<?php
$pageTitle = e($post['title']);
$pageCSS = ['pages/post', 'components/photo-viewer'];
$pageJS = ['components/photo-viewer'];
$navType = 'post';
include __DIR__ . '/../layouts/header.php';
?>

<div class="post-detail-container">
    
    <div class="post-detail-content">
        <article class="post-article">
            <header class="post-header">
                <h1><?= e($post['title']) ?></h1>
                <div class="post-meta">
                    <div class="author-info">
                        <img src="<?= e($post['avatar_url']) ?>" alt="<?= e($post['username']) ?>" class="author-avatar">
                        <div>
                            <div class="author-name">
                                <?= e($post['username']) ?>
                                <span class="user-level-badge">Lv.<?= $post['level'] ?><?= getUserLevelTitle($post['level']) ?></span>
                            </div>
                            <div class="post-time"><?= timeAgo($post['created_at']) ?></div>
                        </div>
                    </div>
                </div>
            </header>
            
            <?php if (!empty($post['content'])): ?>
                <div class="post-body">
                    <p><?= nl2br(e($post['content'])) ?></p>
                </div>
            <?php endif; ?>
            
            <?php if (!empty($photos)): ?>
                <div class="post-gallery">
                    <?php foreach ($photos as $photo): ?>
                        <div class="gallery-item" onclick="viewPhoto('<?= e($photo['id']) ?>')">
                            <img src="/uploads/photos/<?= e($photo['saved_filename']) ?>" 
                                 alt="<?= e($photo['original_filename']) ?>">
                            <div class="photo-overlay">
                                <i class="fas fa-search-plus"></i>
                            </div>
                        </div>
                    <?php endforeach; ?>
                </div>
            <?php endif; ?>
        </article>
    </div>
</div>

<script>
const csrfToken = '<?= e(Auth::generateCSRFToken()) ?>';
const postId = '<?= e($post['id']) ?>';
const currentUserId = '<?= Auth::id() ?>';
const isOwner = <?= $isOwner ? 'true' : 'false' ?>;

function viewPhoto(photoId) {
    if (typeof photoViewer !== 'undefined') {
        photoViewer.open(photoId, {
            showDelete: isOwner,
            currentUserId: currentUserId,
            onDelete: function() {
                window.location.reload();
            }
        });
    }
}

async function deleteCurrentPost() {
    if (!confirm('Are you sure you want to delete this post?')) return;
    
    const formData = new FormData();
    formData.append('post_id', postId);
    formData.append('csrf_token', csrfToken);
    
    try {
        const response = await fetch('/api/posts/delete', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Deleted successfully');
            setTimeout(() => {
                window.location.href = '/space';
            }, 1000);
        } else {
            showNotification(data.message);
        }
    } catch (error) {
        showNotification('Failed to delete');
    }
}
</script>

<?php include __DIR__ . '/../layouts/footer.php'; ?>

