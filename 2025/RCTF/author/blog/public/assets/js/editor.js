// Article editor

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('article-form');
    form.addEventListener('submit', handleSubmit);
});

async function handleSubmit(e) {
    e.preventDefault();
    
    const messageEl = document.getElementById('message');
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    const articleId = document.getElementById('article-id').value;
    
    // Get form data
    const formData = new FormData(e.target);
    
    // Disable button
    submitBtn.disabled = true;
    submitBtn.textContent = articleId ? 'Updating...' : 'Publishing...';
    messageEl.className = 'form-message';
    messageEl.style.display = 'none';
    
    // Determine URL
    const url = articleId ? `/articles/update/${articleId}` : '/articles/store';
    
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: CsrfHelper.getHeaders(),
            body: formData
        });
        
        const result = await response.json();
        
        if (result.success) {
            messageEl.className = 'form-message success';
            messageEl.textContent = result.message;
            messageEl.style.display = 'block';
            
            // Redirect to article detail or dashboard
            setTimeout(() => {
                if (result.article_id) {
                    window.location.href = `/article/${result.article_id}`;
                } else {
                    window.location.href = '/dashboard';
                }
            }, 1000);
        } else {
            messageEl.className = 'form-message error';
            messageEl.textContent = result.message;
            messageEl.style.display = 'block';
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        }
    } catch (error) {
        console.error('Submit failed:', error);
        messageEl.className = 'form-message error';
        messageEl.textContent = 'Submit failed, please try again later';
        messageEl.style.display = 'block';
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    }
}

