<?php
class Auth {
    private static $user = null;
    
    public static function init() {
        if (session_status() === PHP_SESSION_NONE) {
            session_name(config('session.name'));
            session_start();
        }
        
        if (isset($_SESSION['user_id'])) {
            self::$user = User::findById($_SESSION['user_id']);
        }
    }

    public static function login($email, $password) {
        $user = User::findByEmail($email);
        
        if ($user && User::verifyPassword($user, $password)) {
            $_SESSION['user_id'] = $user['id'];
            self::$user = $user;
            return true;
        }
        
        return false;
    }
    
    public static function register($data) {
        if (User::emailExists($data['email'])) {
            return ['success' => false, 'message' => 'Email already registered'];
        }
        
        return User::create($data);
    }
    
    public static function logout() {
        self::$user = null;
        session_destroy();
    }
    
    public static function check() {
        return self::$user !== null;
    }

    public static function id() {
        return $_SESSION['user_id'];
    }

    public static function user() {
        return self::$user;
    }

    public static function type() {
        return self::$user['type'];
    }
    
    public static function generateCSRFToken() {
        if (!isset($_SESSION['csrf_token'])) {
            $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
        }
        return $_SESSION['csrf_token'];
    }
    
    public static function verifyCSRFToken($token) {
        return isset($_SESSION['csrf_token']) && hash_equals($_SESSION['csrf_token'], $token);
    }
}

