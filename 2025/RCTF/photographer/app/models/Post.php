<?php
class Post {

    public static function findById($postId) {
        return DB::table('post')
            ->where('id', '=', $postId)
            ->first();
    }

    public static function findByIdWithAuthor($postId) {
        return DB::table('post')
            ->join('user', 'post.user_id', '=', 'user.id')
            ->select([
                'post.*',
                'user.username',
                'user.avatar_url',
                'user.level'
            ])
            ->where('post.id', '=', $postId)
            ->first();
    }

    public static function create($data) {
        $snowflake = new Snowflake();
        $postId = $snowflake->nextId();
        $now = date('Y-m-d H:i:s');
        
        $postData = [
            'id' => $postId,
            'user_id' => Auth::id(),
            'title' => $data['title'],
            'content' => $data['content'] ?? '',
            'created_at' => $now,
            'updated_at' => $now
        ];
        
        $result = DB::table('post')->insert($postData);
        
        if ($result) {
            return [
                'success' => true,
                'post_id' => (string)$postId
            ];
        }
        
        return [
            'success' => false,
            'message' => 'Failed to create post'
        ];
    }

    public static function delete($postId) {
        return DB::table('post')
            ->where('id', '=', $postId)
            ->delete();
    }

    public static function getPhotos($postId) {
        $photos = DB::table('photo')
            ->select(['id', 'saved_filename','original_filename'])
            ->where('post_id', '=', $postId)
            ->orderBy('created_at', 'ASC')
            ->get();
        
        return $photos;
    }

    public static function belongsToCurrentUser($postId) {
        $post = self::findById($postId);
        return $post && $post['user_id'] == Auth::id();
    }
}
