<?php
require_once __DIR__ . '/../models/Article.php';

class ArticleController {
    private function checkAuth() {
        if (!isset($_SESSION['user_id'])) {
            header('Location: /login');
            exit;
        }
    }
    
    public function dashboard() {
        $this->checkAuth();
        
        $articleModel = new Article();
        
        $articles = $articleModel->findByUserId($_SESSION['user_id']);
        
        require __DIR__ . '/../../views/dashboard.php';
    }
    
    public function create() {
        $this->checkAuth();
        require __DIR__ . '/../../views/article-form.php';
    }
    
    public function store() {
        $this->checkAuth();
        CsrfProtection::validateRequest();
        header('Content-Type: application/json; charset=utf-8');
        
        $title = $_POST['title'] ?? '';
        $subtitle = $_POST['subtitle'] ?? '';
        $content = $_POST['content'] ?? '';
        
        if (empty($title) || empty($content)) {
            echo json_encode(['success' => false, 'message' => 'Title and content cannot be empty']);
            return;
        }
        
        $articleModel = new Article();
        $articleId = $articleModel->create(
            $_SESSION['user_id'],
            htmlspecialchars($title),
            htmlspecialchars($subtitle),
            $content
        );
        
        if ($articleId) {
            echo json_encode(['success' => true, 'message' => 'Article submitted for review', 'article_id' => $articleId]);
        } else {
            echo json_encode(['success' => false, 'message' => 'Submit failed, please try again']);
        }
    }
    
    public function edit($id) {
        $this->checkAuth();
        
        $articleModel = new Article();
        $article = $articleModel->findById($id);
        $isAdmin = isset($_SESSION['is_admin']) && $_SESSION['is_admin'];
        
        if (!$article || (!$isAdmin && $article['user_id'] != $_SESSION['user_id'])) {
            http_response_code(403);
            echo "Access denied";
            return;
        }
        
        // Prevent editing reviewed articles (applies to everyone including admins)
        if ($article['status'] !== 'pending') {
            http_response_code(403);
            echo "Cannot edit articles that have been reviewed";
            return;
        }
        
        require __DIR__ . '/../../views/article-form.php';
    }
    
    public function update($id) {
        $this->checkAuth();
        CsrfProtection::validateRequest();
        header('Content-Type: application/json; charset=utf-8');
        
        $articleModel = new Article();
        $article = $articleModel->findById($id);
        $isAdmin = isset($_SESSION['is_admin']) && $_SESSION['is_admin'];
        
        if (!$article || (!$isAdmin && $article['user_id'] != $_SESSION['user_id'])) {
            echo json_encode(['success' => false, 'message' => 'Permission denied']);
            return;
        }
        
        // Prevent updating reviewed articles (applies to everyone including admins)
        if ($article['status'] !== 'pending') {
            echo json_encode(['success' => false, 'message' => 'Cannot edit articles that have been reviewed']);
            return;
        }
        
        $title = $_POST['title'] ?? '';
        $subtitle = $_POST['subtitle'] ?? '';
        $content = $_POST['content'] ?? '';
        
        if (empty($title) || empty($content)) {
            echo json_encode(['success' => false, 'message' => 'Title and content cannot be empty']);
            return;
        }

        
        $success = $articleModel->update(
            $id,
            htmlspecialchars($title),
            htmlspecialchars($subtitle),
            $content
        );
        
        if ($success) {
            echo json_encode(['success' => true, 'message' => 'Update successful']);
        } else {
            echo json_encode(['success' => false, 'message' => 'Update failed']);
        }
    }
    
    public function delete($id) {
        $this->checkAuth();
        CsrfProtection::validateRequest();
        header('Content-Type: application/json; charset=utf-8');
        
        $articleModel = new Article();
        $article = $articleModel->findById($id);
        $isAdmin = isset($_SESSION['is_admin']) && $_SESSION['is_admin'];
        
        if (!$article || (!$isAdmin && $article['user_id'] != $_SESSION['user_id'])) {
            echo json_encode(['success' => false, 'message' => 'Permission denied']);
            return;
        }
        
        $success = $articleModel->delete($id);
        
        if ($success) {
            echo json_encode(['success' => true, 'message' => 'Delete successful']);
        } else {
            echo json_encode(['success' => false, 'message' => 'Delete failed']);
        }
    }
    
    // API endpoints
    public function apiList() {
        header('Content-Type: application/json; charset=utf-8');
        
        $page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
        $limit = 10;
        $offset = ($page - 1) * $limit;
        
        $articleModel = new Article();
        $articles = $articleModel->findAll($limit, $offset);
        
        echo json_encode(['success' => true, 'data' => $articles]);
    }
    
    public function apiShow($id) {
        header('Content-Type: application/json; charset=utf-8');
        
        $articleModel = new Article();
        $article = $articleModel->findById($id);
        
        if (!$article) {
            echo json_encode(['success' => false, 'message' => 'Article not found']);
            return;
        }
        
        // Check permissions
        $isAdmin = isset($_SESSION['is_admin']) && $_SESSION['is_admin'];
        $isAuthor = isset($_SESSION['user_id']) && $_SESSION['user_id'] == $article['user_id'];
        $isApproved = $article['status'] === 'approved';
        
        // Only allow access if article is approved, user is author, or user is admin
        if (!$isApproved && !$isAuthor && !$isAdmin) {
            echo json_encode(['success' => false, 'message' => 'Access denied - Article not available']);
            return;
        }
        
        echo json_encode(['success' => true, 'data' => $article]);
    }
    
    // Show audit page for specific article (admin only)
    public function audit($id) {
        $this->checkAuth();
        
        $isAdmin = isset($_SESSION['is_admin']) && $_SESSION['is_admin'];
        if (!$isAdmin) {
            http_response_code(403);
            echo "Admin access required";
            return;
        }
        
        $articleModel = new Article();
        $article = $articleModel->findById($id);
        
        if (!$article) {
            http_response_code(404);
            echo "Article not found";
            return;
        }
        
        // Only allow auditing pending articles
        if ($article['status'] !== 'pending') {
            http_response_code(403);
            echo "This article has already been reviewed and cannot be audited again";
            return;
        }
        
        require __DIR__ . '/../../views/article-audit.php';
    }
    
    // Approve article (admin only)
    public function approve($id) {
        header('Content-Type: application/json; charset=utf-8');

        echo json_encode(['success' => false, 'message' => 'Error']);
    }
    
    // Reject article (admin only)
    public function reject($id) {
        $this->checkAuth();
        CsrfProtection::validateRequest();
        header('Content-Type: application/json; charset=utf-8');
        
        $isAdmin = isset($_SESSION['is_admin']) && $_SESSION['is_admin'];
        if (!$isAdmin) {
            echo json_encode(['success' => false, 'message' => 'Admin access required']);
            return;
        }
        
        // Check if article is pending
        $articleModel = new Article();
        $article = $articleModel->findById($id);
        
        if (!$article) {
            echo json_encode(['success' => false, 'message' => 'Article not found']);
            return;
        }
        
        if ($article['status'] !== 'pending') {
            echo json_encode(['success' => false, 'message' => 'Article has already been reviewed']);
            return;
        }
        
        $success = $articleModel->updateStatus($id, 'rejected');
        
        if ($success) {
            echo json_encode(['success' => true, 'message' => 'Article rejected']);
        } else {
            echo json_encode(['success' => false, 'message' => 'Rejection failed']);
        }
    }
}

