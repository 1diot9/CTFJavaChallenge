<?php
class PostController {
    
    public function compose() {
        if (!Auth::check()) {
            redirect('/login');
        }
        
        view('post/compose');
    }
    
    public function show($postId) {
        if (!Auth::check()) {
            redirect('/login');
        }
        
        $post = Post::findByIdWithAuthor($postId);
        
        if (!$post) {
            http_response_code(404);
            echo "Post not found";
            return;
        }
        
        $photos = Post::getPhotos($postId);
        $isOwner = Auth::id() == $post['user_id'];
        
        view('post/detail', [
            'post' => $post,
            'photos' => $photos,
            'isOwner' => $isOwner
        ]);
    }
    
    public function create() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        $title = $_POST['title'] ?? '';
        $content = $_POST['content'] ?? '';
        $photoIds = $_POST['photo_ids'] ?? [];
        $csrfToken = $_POST['csrf_token'] ?? '';
        
        if (!Auth::verifyCSRFToken($csrfToken)) {
            json(['success' => false, 'message' => 'Invalid request'], 403);
        }
        
        if (empty($title)) {
            json(['success' => false, 'message' => 'Please enter a title']);
        }
        
        if (!is_array($photoIds)) {
            $photoIds = json_decode($photoIds, true) ?: [];
        }
        
        $result = Post::create([
            'title' => $title,
            'content' => $content
        ]);
        
        if (!$result['success']) {
            json(['success' => false, 'message' => 'Failed to publish']);
        }
        
        Photo::attachPost($photoIds, $result['post_id']);
        
        json(['success' => true, 'message' => 'Published successfully', 'post_id' => $result['post_id']]);
    }
    
    public function delete() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        $postId = $_POST['post_id'] ?? '';
        $csrfToken = $_POST['csrf_token'] ?? '';
        
        if (!Auth::verifyCSRFToken($csrfToken)) {
            json(['success' => false, 'message' => 'Invalid request'], 403);
        }
        
        $post = Post::findById($postId);
        
        if (!$post) {
            json(['success' => false, 'message' => 'Post not found']);
        }
        
        if (!Post::belongsToCurrentUser($postId, Auth::id())) {
            json(['success' => false, 'message' => 'No permission to delete'], 403);
        }
        
        $result = Post::delete($postId);
        
        if ($result) {
            json(['success' => true, 'message' => 'Deleted successfully']);
        } else {
            json(['success' => false, 'message' => 'Failed to delete']);
        }
    }
}

