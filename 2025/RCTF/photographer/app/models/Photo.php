<?php
class Photo {

    public static function findById($photoId) {
        return DB::table('photo')
            ->where('id', '=', $photoId)
            ->first();
    }

    public static function create($data) {
        $snowflake = new Snowflake();
        $photoId = $snowflake->nextId();
        $now = date('Y-m-d H:i:s');
        
        $photoData = [
            'id' => $photoId,
            'user_id' => $data['user_id'],
            'post_id' => $data['post_id'] ?? null,
            'original_filename' => $data['original_filename'],
            'saved_filename' => $data['saved_filename'],
            'type' => $data['type'],
            'size' => $data['size'],
            'width' => $data['width'] ?? 0,
            'height' => $data['height'] ?? 0,
            'exif_make' => $data['exif_make'] ?? null,
            'exif_model' => $data['exif_model'] ?? null,
            'exif_exposure_time' => $data['exif_exposure_time'] ?? null,
            'exif_f_number' => $data['exif_f_number'] ?? null,
            'exif_iso' => $data['exif_iso'] ?? null,
            'exif_focal_length' => $data['exif_focal_length'] ?? null,
            'exif_date_taken' => $data['exif_date_taken'] ?? null,
            'exif_artist' => $data['exif_artist'] ?? null,
            'exif_copyright' => $data['exif_copyright'] ?? null,
            'exif_software' => $data['exif_software'] ?? null,
            'exif_orientation' => $data['exif_orientation'] ?? null,
            'created_at' => $now
        ];
        
        $result = DB::table('photo')->insert($photoData);
        
        if ($result) {
            return [
                'success' => true,
                'photo_id' => (string)$photoId
            ];
        }
        
        return [
            'success' => false,
            'message' => 'Failed to create photo record'
        ];
    }

    public static function delete($photoId) {
        return DB::table('photo')
            ->where('id', '=', $photoId)
            ->delete();
    }

    public static function getByUser($userId, $limit = null, $offset = 0) {
        $query = DB::table('photo')
            ->where('user_id', '=', $userId)
            ->orderBy('created_at', 'DESC');
        
        if ($limit !== null) {
            $query->limit($limit)->offset($offset);
        }
        
        return $query->get();
    }

    public static function attachPost($photoIds, $postId) {
        if (empty($photoIds) || !is_array($photoIds)) {
            return true;
        }

        foreach ($photoIds as $photoId) {
            if (empty($photoId)) continue;

            DB::table('photo')
                ->where('id', '=', (string)$photoId)
                ->where('user_id', '=', Auth::id())
                ->update(['post_id' => (string)$postId]);
        }

        return true;
    }

    public static function belongsToCurrentUser($photoId) {
        $photo = self::findById($photoId);
        return $photo && $photo['user_id'] == Auth::id();
    }

}
