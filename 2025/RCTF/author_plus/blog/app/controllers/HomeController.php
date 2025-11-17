<?php
require_once __DIR__ . '/../models/Article.php';

class HomeController {
    public function index() {
        require __DIR__ . '/../../views/home.php';
    }
    
    public function show($id) {
        $articleModel = new Article();
        $article = $articleModel->findById($id);
        
        if (!$article) {
            http_response_code(404);
            echo "Article not found";
            return;
        }
        
        $isAdmin = isset($_SESSION['is_admin']) && $_SESSION['is_admin'];
        $isAuthor = isset($_SESSION['user_id']) && $_SESSION['user_id'] == $article['user_id'];
        $isApproved = $article['status'] === 'approved';
        
        if (!$isApproved && !$isAuthor && !$isAdmin) {
            http_response_code(403);
            echo "Access denied - Article not available";
            return;
        }
        
        if ($isApproved) {
            $articleModel->incrementViews($id);
        }
        
        require __DIR__ . '/../../views/article.php';
    }
}

