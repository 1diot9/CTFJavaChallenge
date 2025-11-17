// Home page articles list async loading
let currentPage = 1;
let isLoading = false;

async function loadArticles() {
    if (isLoading) return;
    
    isLoading = true;
    const loadMoreBtn = document.getElementById('load-more');
    
    try {
        const response = await fetch(`/api/articles?page=${currentPage}`);
        const result = await response.json();
        
        if (result.success && result.data.length > 0) {
            const articlesList = document.getElementById('articles-list');
            
            // Remove loading indicator
            const loadingEl = articlesList.querySelector('.loading');
            if (loadingEl) loadingEl.remove();
            
            // Render articles
            result.data.forEach(article => {
                const articleHtml = createArticlePreview(article);
                articlesList.insertAdjacentHTML('beforeend', articleHtml);
            });
            
            // Show load more button
            if (result.data.length === 10) {
                loadMoreBtn.style.display = 'inline-block';
            } else {
                loadMoreBtn.style.display = 'none';
            }
            
            currentPage++;
        } else {
            // No more articles
            const articlesList = document.getElementById('articles-list');
            const loadingEl = articlesList.querySelector('.loading');
            if (loadingEl) {
                loadingEl.textContent = 'No articles yet';
            }
            loadMoreBtn.style.display = 'none';
        }
    } catch (error) {
        console.error('Failed to load articles:', error);
        alert('Failed to load, please try again later');
    } finally {
        isLoading = false;
    }
}

function createArticlePreview(article) {
    const excerpt = article.content.substring(0, 150).replace(/<[^>]*>/g, '');
    const date = new Date(article.created_at).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
    
    return `
        <div class="article-preview">
            <div class="article-preview-content">
                <div class="article-author">
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                        <circle cx="10" cy="6" r="3" stroke="currentColor" stroke-width="1.5"/>
                        <path d="M5 15a5 5 0 0 1 10 0" stroke="currentColor" stroke-width="1.5"/>
                    </svg>
                    <span>${escapeHtml(article.username)}</span>
                </div>
                <h2 class="article-preview-title">
                    <a href="/article/${article.id}">${escapeHtml(article.title)}</a>
                </h2>
                ${article.subtitle ? `<p class="article-preview-subtitle">${escapeHtml(article.subtitle)}</p>` : ''}
                <div class="article-preview-meta">
                    <span>${date}</span>
                    <span>${article.views} views</span>
                </div>
            </div>
        </div>
    `;
}

function escapeHtml(text) {
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// Load articles on page load
document.addEventListener('DOMContentLoaded', () => {
    loadArticles();
    
    // Load more button
    const loadMoreBtn = document.getElementById('load-more');
    loadMoreBtn.addEventListener('click', loadArticles);
});

