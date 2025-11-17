<?php
/**
 * HTML Sanitizer using Node.js DOMPurify
 * Provides double-pass sanitization for enhanced security
 */
class Sanitizer {
    private $nodePath;
    private $scriptPath;
    
    public function __construct() {
        $this->scriptPath = __DIR__ . '/../sanitize.js';
        
    }
    
    public function sanitize($content) {
        // Prepare input data
        $input = json_encode(['content' => $content]);
        
        // Prepare command
        $command = sprintf(
            'node "%s"',
            $this->scriptPath
        );
        
        // Execute Node.js script
        $descriptors = [
            0 => ['pipe', 'r'],  // stdin
            1 => ['pipe', 'w'],  // stdout
            2 => ['pipe', 'w']   // stderr
        ];
        
        $process = proc_open($command, $descriptors, $pipes);
        
        if (!is_resource($process)) {
            throw new Exception('Failed to start sanitization process');
        }
        
        // Write input to stdin
        fwrite($pipes[0], $input);
        fclose($pipes[0]);
        
        // Read output from stdout
        $output = stream_get_contents($pipes[1]);
        fclose($pipes[1]);
        
        // Read errors from stderr
        $errors = stream_get_contents($pipes[2]);
        fclose($pipes[2]);
        
        // Close process
        $returnCode = proc_close($process);
        
        // Parse result
        $result = json_decode($output, true);
        
        if ($returnCode !== 0 || !$result || !$result['success']) {
            $errorMsg = $result['error'] ?? $errors ?? 'Unknown error';
            throw new Exception('Sanitization failed: ' . $errorMsg);
        }
        
        return $result['content'];
    }
    
    /**
     * Sanitize content with fallback to PHP htmlspecialchars
     * 
     * @param string $content Content to sanitize
     * @return string Sanitized content
     */
    public function sanitizeWithFallback($content) {
        try {
            return $this->sanitize($content);
        } catch (Exception $e) {
            // Fallback to PHP built-in sanitization
            error_log('DOMPurify sanitization failed: ' . $e->getMessage());
            return htmlspecialchars($content, ENT_QUOTES | ENT_HTML5, 'UTF-8');
        }
    }
}

