// Article detail page async content loading
(async () => {
    try {
        const articleId = window.location.pathname.match(/^\/article\/([^/]+)$/)[1];
        const response = await fetch(`/api/articles/${articleId}`);
        const result = await response.json();

        if (result.success) {
            const article = result.data;
            document.getElementById('article-content').innerHTML = article.content;
        } else {
            throw new Error(result.message || 'Load failed');
        }
    } catch (error) {
        console.error('Failed to load article content:', error);
        document.getElementById('article-content').innerHTML =
            '<p style="color: #c94040; text-align: center;">Failed to load, please refresh the page</p>';
    }
})();
