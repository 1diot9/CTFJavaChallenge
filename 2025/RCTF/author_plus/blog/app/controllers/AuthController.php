<?php
require_once __DIR__ . '/../models/User.php';

class AuthController {
    public function showLogin() {
        if (isset($_SESSION['user_id'])) {
            header('Location: /dashboard');
            exit;
        }
        require __DIR__ . '/../../views/login.php';
    }
    
    public function showRegister() {
        if (isset($_SESSION['user_id'])) {
            header('Location: /dashboard');
            exit;
        }
        require __DIR__ . '/../../views/register.php';
    }
    
    public function login() {
        CsrfProtection::validateRequest();
        header('Content-Type: application/json; charset=utf-8');
        
        $email = $_POST['email'] ?? '';
        $password = $_POST['password'] ?? '';
        
        if (empty($email) || empty($password)) {
            echo json_encode(['success' => false, 'message' => 'Please fill in all fields']);
            return;
        }
        
        $userModel = new User();
        $user = $userModel->findByEmail($email);
        
        if (!$user || !password_verify($password, $user['password'])) {
            echo json_encode(['success' => false, 'message' => 'Invalid email or password']);
            return;
        }

        session_regenerate_id(true);
        
        $_SESSION['user_id'] = $user['id'];
        $_SESSION['username'] = $user['username'];
        $_SESSION['is_admin'] = $user['is_admin'] ? true : false;
        
        echo json_encode(['success' => true, 'message' => 'Login successful']);
    }
    
    public function register() {
        CsrfProtection::validateRequest();
        header('Content-Type: application/json; charset=utf-8');
        
        $username = $_POST['username'] ?? '';
        $email = $_POST['email'] ?? '';
        $password = $_POST['password'] ?? '';
        $confirmPassword = $_POST['confirm_password'] ?? '';
        
        // Validation
        if (empty($username) || empty($email) || empty($password)) {
            echo json_encode(['success' => false, 'message' => 'Please fill in all fields']);
            return;
        }

        if (preg_match('/[<>\'"\x20\t\r\n]/', $username)){
            echo json_encode(['success' => false, 'message' => 'Username contains invalid characters']);
            return;
        }
        
        if ($password !== $confirmPassword) {
            echo json_encode(['success' => false, 'message' => 'Passwords do not match']);
            return;
        }
        
        if (strlen($password) < 6) {
            echo json_encode(['success' => false, 'message' => 'Password must be at least 6 characters']);
            return;
        }
        
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            echo json_encode(['success' => false, 'message' => 'Invalid email format']);
            return;
        }
        
        $userModel = new User();
        
        // Check if email already exist
        if ($userModel->findByEmail($email)) {
            echo json_encode(['success' => false, 'message' => 'Email already registered']);
            return;
        }

        
        // Create user
        $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
        $userId = $userModel->create($username, $email, $hashedPassword);
        
        if ($userId) {
            session_regenerate_id(true);

            $_SESSION['user_id'] = $userId;
            $_SESSION['username'] = $username;
            $_SESSION['is_admin'] = false;
            echo json_encode(['success' => true, 'message' => 'Registration successful']);
        } else {
            echo json_encode(['success' => false, 'message' => 'Registration failed, please try again']);
        }
    }
    
    public function logout() {
        session_destroy();
        header('Location: /');
        exit;
    }
}

