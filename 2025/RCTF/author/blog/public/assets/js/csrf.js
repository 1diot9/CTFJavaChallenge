// CSRF Token Helper
const CsrfHelper = {
    /**
     * Get CSRF token from meta tag
     */
    getToken() {
        const metaTag = document.querySelector('meta[name="csrf-token"]');
        return metaTag ? metaTag.getAttribute('content') : '';
    },
    
    /**
     * Add CSRF token to headers
     */
    getHeaders() {
        return {
            'X-CSRF-TOKEN': this.getToken()
        };
    },
    
    /**
     * Add CSRF token to FormData
     */
    addToFormData(formData) {
        formData.append('csrf_token', this.getToken());
        return formData;
    }
};

