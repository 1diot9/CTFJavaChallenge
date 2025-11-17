<?php

class CsrfProtection {
    private static $tokenName = 'csrf_token';
    
    /**
     * Generate CSRF token
     */
    public static function generateToken() {
        if (!isset($_SESSION[self::$tokenName])) {
            $_SESSION[self::$tokenName] = bin2hex(random_bytes(32));
        }
        return $_SESSION[self::$tokenName];
    }
    
    /**
     * Get current CSRF token
     */
    public static function getToken() {
        return self::generateToken();
    }
    
    /**
     * Validate CSRF token
     */
    public static function validateToken($token) {
        if (!isset($_SESSION[self::$tokenName])) {
            return false;
        }
        
        return hash_equals($_SESSION[self::$tokenName], $token);
    }
    
    /**
     * Validate CSRF token from request
     */
    public static function validateRequest() {
        $token = null;
        
        // Check header first
        if (isset($_SERVER['HTTP_X_CSRF_TOKEN'])) {
            $token = $_SERVER['HTTP_X_CSRF_TOKEN'];
        }
        // Then check POST data
        elseif (isset($_POST['csrf_token'])) {
            $token = $_POST['csrf_token'];
        }
        
        if (!$token || !self::validateToken($token)) {
            http_response_code(403);
            header('Content-Type: application/json; charset=utf-8');
            echo json_encode(['success' => false, 'message' => 'CSRF token validation failed']);
            exit;
        }
        
        return true;
    }
    
    /**
     * Refresh CSRF token
     */
    public static function refreshToken() {
        $_SESSION[self::$tokenName] = bin2hex(random_bytes(32));
        return $_SESSION[self::$tokenName];
    }
}

