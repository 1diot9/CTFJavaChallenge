<?php
class User {

    public static function findById($userId) {
        return DB::table('user')
            ->leftJoin('photo', 'user.background_photo_id', '=', 'photo.id')
            ->where('user.id', '=', $userId)
            ->first();
    }

    public static function findByEmail($email) {
        return DB::table('user')
            ->where('email', '=', $email)
            ->first();
    }

    public static function create($data) {
        $snowflake = new Snowflake();
        $userId = $snowflake->nextId();
        $now = date('Y-m-d H:i:s');

        $default_value = config('default_value.user');

        $userData = [
            'id' => $userId,
            'username' => $data['username'],
            'normalized_username' => self::normalizeUsername($data['username']),
            'password' => password_hash($data['password'], PASSWORD_BCRYPT),
            'email' => $data['email'],
            'bio' => $data['bio'] ?? '',
            'avatar_url' => $data['avatar_url'] ?? $default_value['avatar_url'],
            'type' => $data['type'] ?? $default_value['type'],
            'level' => $data['level'] ?? $default_value['level'],
            'created_at' => $now,
            'updated_at' => $now
        ];
        
        $result = DB::table('user')->insert($userData);
        
        if ($result) {
            return [
                'success' => true,
                'user_id' => (string)$userId
            ];
        }
        
        return [
            'success' => false,
            'message' => 'Failed to create user'
        ];
    }

    public static function update($userId, $data) {
        $data['updated_at'] = date('Y-m-d H:i:s');
        
        return DB::table('user')
            ->where('id', '=', $userId)
            ->update($data);
    }

    public static function verifyPassword($user, $password) {
        return password_verify($password, $user['password']);
    }

    public static function updatePassword($userId, $newPassword) {
        return self::update($userId, [
            'password' => password_hash($newPassword, PASSWORD_BCRYPT)
        ]);
    }

    public static function normalizeUsername($username) {
        return strtolower(preg_replace('/\s+/', '', $username));
    }

    public static function getPosts($userId, $limit = null, $offset = 0) {
        $query = DB::table('post')
            ->where('user_id', '=', $userId)
            ->orderBy('created_at', 'DESC');
        
        if ($limit !== null) {
            $query->limit($limit)->offset($offset);
        }
        
        return $query->get();
    }

    public static function emailExists($email) {
        $user = self::findByEmail($email);
        return $user !== null;
    }
}
