<?php
class UserController {
    
    public function space() {
        if (!Auth::check()) {
            redirect('/login');
        }
        
        view('user/space');
    }
    
    public function spacePosts() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        $userId = Auth::id();
        $posts = User::getPosts($userId);
        
        foreach ($posts as &$post) {
            $post['id'] = (string)$post['id'];
            $post['user_id'] = (string)$post['user_id'];
            $post['photo_list'] = Post::getPhotos($post['id']);
        }
        
        json(['success' => true, 'posts' => $posts]);
    }
    
    public function spacePhotos() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        $photos = Photo::getByUser(Auth::id());
        
        foreach ($photos as &$photo) {
            $photo['id'] = (string)$photo['id'];
        }
        
        json(['success' => true, 'photos' => $photos]);
    }
    
    public function showSettings() {
        if (!Auth::check()) {
            redirect('/login');
        }
        
        view('user/settings');
    }
    
    public function updateUsername() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        $username = $_POST['username'] ?? '';
        $csrfToken = $_POST['csrf_token'] ?? '';
        
        if (!Auth::verifyCSRFToken($csrfToken)) {
            json(['success' => false, 'message' => 'Invalid request'], 403);
        }
        
        if (empty($username)) {
            json(['success' => false, 'message' => 'Username cannot be empty']);
        }
        
        $result = User::update(Auth::id(), [
            'username' => $username,
            'normalized_username' => User::normalizeUsername($username)
        ]);
        
        if ($result) {
            json(['success' => true, 'message' => 'Username updated successfully']);
        } else {
            json(['success' => false, 'message' => 'Update failed']);
        }
    }
    
    public function updatePassword() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        $oldPassword = $_POST['old_password'] ?? '';
        $newPassword = $_POST['new_password'] ?? '';
        $csrfToken = $_POST['csrf_token'] ?? '';
        
        if (!Auth::verifyCSRFToken($csrfToken)) {
            json(['success' => false, 'message' => 'Invalid request'], 403);
        }
        
        if (empty($oldPassword) || empty($newPassword)) {
            json(['success' => false, 'message' => 'Please fill in all fields']);
        }
        
        if (strlen($newPassword) < 6) {
            json(['success' => false, 'message' => 'New password must be at least 6 characters']);
        }
        
        $user = Auth::user();
        
        if (!User::verifyPassword($user, $oldPassword)) {
            json(['success' => false, 'message' => 'Old password is incorrect']);
        }
        
        $result = User::updatePassword(Auth::id(), $newPassword);
        
        if ($result) {
            json(['success' => true, 'message' => 'Password updated successfully']);
        } else {
            json(['success' => false, 'message' => 'Update failed']);
        }
    }
    
    public function updateBio() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        $bio = $_POST['bio'] ?? '';
        $csrfToken = $_POST['csrf_token'] ?? '';
        
        if (!Auth::verifyCSRFToken($csrfToken)) {
            json(['success' => false, 'message' => 'Invalid request'], 403);
        }
        
        $result = User::update(Auth::id(), ['bio' => $bio]);
        
        if ($result) {
            json(['success' => true, 'message' => 'Bio updated successfully']);
        } else {
            json(['success' => false, 'message' => 'Update failed']);
        }
    }
    
    public function updateAvatar() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        if (!isset($_FILES['avatar']) || $_FILES['avatar']['error'] !== UPLOAD_ERR_OK) {
            json(['success' => false, 'message' => 'Please select an image']);
        }
        
        $file = $_FILES['avatar'];
        
        if (!isValidImage($file)) {
            json(['success' => false, 'message' => 'Invalid image file']);
        }
        
        $ext = pathinfo($file['name'], PATHINFO_EXTENSION);
        $filename = 'avatar_' . Auth::id() . '_' . time() . '.' . $ext;
        $uploadPath = config('upload.path') . '/avatars';
        
        if (!is_dir($uploadPath)) {
            mkdir($uploadPath, 0755, true);
        }
        
        $filePath = $uploadPath . '/' . $filename;
        
        if (!move_uploaded_file($file['tmp_name'], $filePath)) {
            json(['success' => false, 'message' => 'File upload failed']);
        }
        
        $avatarUrl = '/uploads/avatars/' . $filename;
        
        $result = User::update(Auth::id(), ['avatar_url' => $avatarUrl]);
        
        if ($result) {
            json(['success' => true, 'message' => 'Avatar updated successfully', 'avatar_url' => $avatarUrl]);
        } else {
            json(['success' => false, 'message' => 'Update failed']);
        }
    }
    
    public function getPhotos() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        $photos = Photo::getByUser(Auth::id());
        
        foreach ($photos as &$photo) {
            $photo['id'] = (string)$photo['id'];
        }
        
        json(['success' => true, 'photos' => $photos]);
    }
    
    public function setBackground() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        $photoId = $_POST['photo_id'] ?? '';
        $csrfToken = $_POST['csrf_token'] ?? '';
        
        if (!Auth::verifyCSRFToken($csrfToken)) {
            json(['success' => false, 'message' => 'Invalid request'], 403);
        }
        
        if (!empty($photoId) && !Photo::belongsToCurrentUser($photoId, Auth::id())) {
            json(['success' => false, 'message' => 'Photo not found']);
        }
        
        $result = User::update(Auth::id(), [
            'background_photo_id' => $photoId ?: null
        ]);
        
        if ($result) {
            json(['success' => true, 'message' => 'Background set successfully']);
        } else {
            json(['success' => false, 'message' => 'Setting failed']);
        }
    }
}

