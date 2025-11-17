<?php
class Article {
    private $db;
    private $snowflake;
    
    public function __construct() {
        $this->db = Database::getInstance()->getConnection();
        $this->snowflake = new Snowflake();
    }
    
    public function create($userId, $title, $subtitle, $content) {
        $id = $this->snowflake->nextId();
        $sql = "INSERT INTO articles (id, user_id, title, subtitle, content, status) VALUES (?, ?, ?, ?, ?, 'pending')";
        $stmt = $this->db->prepare($sql);
        $stmt->execute([$id, $userId, $title, $subtitle, $content]);
        return $id;
    }
    
    public function update($id, $title, $subtitle, $content) {
        $sql = "UPDATE articles SET title = ?, subtitle = ?, content = ? WHERE id = ?";
        $stmt = $this->db->prepare($sql);
        return $stmt->execute([$title, $subtitle, $content, $id]);
    }
    
    public function delete($id) {
        $sql = "DELETE FROM articles WHERE id = ?";
        $stmt = $this->db->prepare($sql);
        return $stmt->execute([$id]);
    }
    
    public function findById($id) {
        $sql = "SELECT a.*, u.username 
                FROM articles a 
                JOIN users u ON a.user_id = u.id 
                WHERE a.id = ?";
        $stmt = $this->db->prepare($sql);
        $stmt->execute([$id]);
        return $stmt->fetch();
    }
    
    public function findAll($limit = 10, $offset = 0) {
        $sql = "SELECT a.*, u.username 
                FROM articles a 
                JOIN users u ON a.user_id = u.id 
                WHERE a.status = 'approved'
                ORDER BY a.created_at DESC 
                LIMIT ? OFFSET ?";
        $stmt = $this->db->prepare($sql);
        $stmt->bindValue(1, $limit, PDO::PARAM_INT);
        $stmt->bindValue(2, $offset, PDO::PARAM_INT);
        $stmt->execute();
        return $stmt->fetchAll();
    }
    
    public function findAllForAdmin($limit = 10, $offset = 0) {
        $sql = "SELECT a.*, u.username 
                FROM articles a 
                JOIN users u ON a.user_id = u.id 
                ORDER BY a.created_at DESC 
                LIMIT ? OFFSET ?";
        $stmt = $this->db->prepare($sql);
        $stmt->bindValue(1, $limit, PDO::PARAM_INT);
        $stmt->bindValue(2, $offset, PDO::PARAM_INT);
        $stmt->execute();
        return $stmt->fetchAll();
    }
    
    public function findByUserId($userId, $isAdmin = false) {
        $sql = "SELECT * FROM articles WHERE user_id = ? ORDER BY created_at DESC";
        $stmt = $this->db->prepare($sql);
        $stmt->execute([$userId]);
        return $stmt->fetchAll();
    }
    
    public function updateStatus($id, $status) {
        $sql = "UPDATE articles SET status = ? WHERE id = ?";
        $stmt = $this->db->prepare($sql);
        return $stmt->execute([$status, $id]);
    }
    
    public function incrementViews($id) {
        $sql = "UPDATE articles SET views = views + 1 WHERE id = ?";
        $stmt = $this->db->prepare($sql);
        return $stmt->execute([$id]);
    }
}

