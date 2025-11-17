// Dashboard - article management functions

async function deleteArticle(articleId) {
    if (!confirm('Are you sure you want to delete this article? This action cannot be undone.')) {
        return;
    }
    
    try {
        const response = await fetch(`/articles/delete/${articleId}`, {
            method: 'POST',
            headers: CsrfHelper.getHeaders()
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert(result.message);
            location.reload();
        } else {
            alert(result.message);
        }
    } catch (error) {
        console.error('Delete failed:', error);
        alert('Delete failed, please try again later');
    }
}

