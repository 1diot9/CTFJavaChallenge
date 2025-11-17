const { JSDOM } = require('jsdom');
const createDOMPurify = require('dompurify');

// Create DOMPurify instance
const window = new JSDOM('').window;
const DOMPurify = createDOMPurify(window);


/**
 * Sanitize HTML content
 * @param {string} dirty - Unsanitized HTML
 * @returns {string} Clean HTML
 */
function sanitize(dirty) {
    return DOMPurify.sanitize(dirty);
}

/**
 * Double sanitization for extra security
 * @param {string} dirty - Unsanitized HTML
 * @returns {string} Double-cleaned HTML
 */
function doubleSanitize(dirty) {
    // First pass
    let clean = sanitize(dirty);
    // Second pass
    clean = sanitize(clean);
    return clean;
}

// Read from stdin
let inputData = '';

process.stdin.setEncoding('utf8');

process.stdin.on('data', (chunk) => {
    inputData += chunk;
});

process.stdin.on('end', () => {
    try {
        // Parse input JSON
        const input = JSON.parse(inputData);
        const dirty = input.content || '';
        
        // Double sanitize
        const clean = doubleSanitize(dirty);
        
        // Output result as JSON
        const result = {
            success: true,
            content: clean
        };
        
        console.log(JSON.stringify(result));
        process.exit(0);
    } catch (error) {
        // Output error as JSON
        const result = {
            success: false,
            error: error.message
        };
        
        console.error(JSON.stringify(result));
        process.exit(1);
    }
});

// Handle timeout (10 seconds)
setTimeout(() => {
    console.error(JSON.stringify({
        success: false,
        error: 'Sanitization timeout'
    }));
    process.exit(1);
}, 10000);

