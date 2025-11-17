<?php
class AuthController {
    
    public function showLogin() {
        if (Auth::check()) {
            redirect('/space');
        }
        view('auth/login');
    }
    
    public function login() {
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            redirect('/login');
        }
        
        $email = $_POST['email'] ?? '';
        $password = $_POST['password'] ?? '';
        $csrfToken = $_POST['csrf_token'] ?? '';
        
        if (!Auth::verifyCSRFToken($csrfToken)) {
            json(['success' => false, 'message' => 'Invalid request'], 403);
        }
        
        if (empty($email) || empty($password)) {
            json(['success' => false, 'message' => 'Please fill in all fields']);
        }
        
        if (Auth::login($email, $password)) {
            json(['success' => true, 'redirect' => '/space']);
        } else {
            json(['success' => false, 'message' => 'Invalid email or password']);
        }
    }
    
    public function showRegister() {
        if (Auth::check()) {
            redirect('/space');
        }
        view('auth/register');
    }
    
    public function register() {
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            redirect('/register');
        }
        
        $username = $_POST['username'] ?? '';
        $email = $_POST['email'] ?? '';
        $password = $_POST['password'] ?? '';
        $confirmPassword = $_POST['confirm_password'] ?? '';
        $csrfToken = $_POST['csrf_token'] ?? '';
        
        if (!Auth::verifyCSRFToken($csrfToken)) {
            json(['success' => false, 'message' => 'Invalid request'], 403);
        }
        
        if (empty($username) || empty($email) || empty($password)) {
            json(['success' => false, 'message' => 'Please fill in all fields']);
        }
        
        if ($password !== $confirmPassword) {
            json(['success' => false, 'message' => 'Passwords do not match']);
        }
        
        if (strlen($password) < 6) {
            json(['success' => false, 'message' => 'Password must be at least 6 characters']);
        }
        
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            json(['success' => false, 'message' => 'Invalid email format']);
        }
        
        $result = Auth::register([
            'username' => $username,
            'email' => $email,
            'password' => $password
        ]);
        
        if ($result['success']) {
            Auth::login($email, $password);
            json(['success' => true, 'redirect' => '/space']);
        } else {
            json(['success' => false, 'message' => $result['message']]);
        }
    }
    
    public function logout() {
        Auth::logout();
        redirect('/login');
    }
}

