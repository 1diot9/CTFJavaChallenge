<?php
class PhotoController {
    
    public function upload() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        if (!isset($_FILES['photos']) || empty($_FILES['photos']['name'][0])) {
            json(['success' => false, 'message' => 'Please select photos']);
        }
        
        $files = $_FILES['photos'];
        $uploadedPhotos = [];
        $snowflake = new Snowflake();
        $uploadPath = config('upload.path') . '/photos';
        
        if (!is_dir($uploadPath)) {
            mkdir($uploadPath, 0755, true);
        }
        
        $fileCount = count($files['name']);
        
        for ($i = 0; $i < $fileCount; $i++) {
            if ($files['error'][$i] !== UPLOAD_ERR_OK) {
                continue;
            }
            
            $file = [
                'name' => $files['name'][$i],
                'type' => $files['type'][$i],
                'tmp_name' => $files['tmp_name'][$i],
                'error' => $files['error'][$i],
                'size' => $files['size'][$i]
            ];
            
            if (!isValidImage($file)) {
                continue;
            }
            
            $ext = pathinfo($file['name'], PATHINFO_EXTENSION);
            $photoId = $snowflake->nextId();
            $savedFilename = $photoId . '.' . $ext;
            $filePath = $uploadPath . '/' . $savedFilename;
            
            if (!move_uploaded_file($file['tmp_name'], $filePath)) {
                continue;
            }
            
            $exifData = extractExif($filePath);
            
            $result = Photo::create([
                'user_id' => Auth::id(),
                'original_filename' => $file['name'],
                'saved_filename' => $savedFilename,
                'type' => $file['type'],
                'size' => $file['size'],
                'width' => $exifData['width'],
                'height' => $exifData['height'],
                'exif_make' => $exifData['make'],
                'exif_model' => $exifData['model'],
                'exif_exposure_time' => $exifData['exposure_time'],
                'exif_f_number' => $exifData['f_number'],
                'exif_iso' => $exifData['iso'],
                'exif_focal_length' => $exifData['focal_length'],
                'exif_date_taken' => $exifData['date_taken'],
                'exif_artist' => $exifData['artist'],
                'exif_copyright' => $exifData['copyright'],
                'exif_software' => $exifData['software'],
                'exif_orientation' => $exifData['orientation']
            ]);
            
            if ($result['success']) {
                $uploadedPhotos[] = [
                    'id' => $result['photo_id'],
                    'filename' => $savedFilename,
                    'original_filename' => $file['name'],
                    'url' => '/uploads/photos/' . $savedFilename
                ];
            }
        }
        
        if (empty($uploadedPhotos)) {
            json(['success' => false, 'message' => 'Photo upload failed']);
        }
        
        json(['success' => true, 'photos' => $uploadedPhotos]);
    }
    
    public function info($photoId) {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        $photo = Photo::findById($photoId);
        
        if (!$photo) {
            json(['success' => false, 'message' => 'Photo not found'], 404);
        }

        json(['success' => true, 'photo' => $photo]);
    }
    
    public function delete() {
        if (!Auth::check()) {
            json(['success' => false, 'message' => 'Not logged in'], 401);
        }
        
        $photoId = $_POST['photo_id'] ?? '';
        $csrfToken = $_POST['csrf_token'] ?? '';
        
        if (!Auth::verifyCSRFToken($csrfToken)) {
            json(['success' => false, 'message' => 'Invalid request'], 403);
        }
        
        $photo = Photo::findById($photoId);
        
        if (!$photo) {
            json(['success' => false, 'message' => 'Photo not found']);
        }
        
        if (!Photo::belongsToCurrentUser($photoId, Auth::id())) {
            json(['success' => false, 'message' => 'No permission to delete'], 403);
        }
        
        $filePath = config('upload.path') . '/photos/' . $photo['saved_filename'];
        if (file_exists($filePath)) {
            unlink($filePath);
        }
        
        $result = Photo::delete($photoId);
        
        if ($result) {
            json(['success' => true, 'message' => 'Deleted successfully']);
        } else {
            json(['success' => false, 'message' => 'Failed to delete']);
        }
    }
}

