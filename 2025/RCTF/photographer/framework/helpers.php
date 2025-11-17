<?php
function view($viewName, $data = []) {
    extract($data);
    $viewFile = __DIR__ . '/../app/views/' . $viewName . '.php';
    
    if (file_exists($viewFile)) {
        require $viewFile;
    } else {
        die("View file not found: $viewName");
    }
}

function redirect($path) {
    header("Location: $path");
    exit;
}

function json($data, $statusCode = 200) {
    http_response_code($statusCode);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode($data, JSON_UNESCAPED_UNICODE);
    exit;
}

function e($string) {
    return htmlspecialchars($string ?? '', ENT_QUOTES, 'UTF-8');
}

function config($key) {
    static $config = null;
    
    if ($config === null) {
        $config = require __DIR__ . '/../app/config/config.php';
    }
    
    $keys = explode('.', $key);
    $value = $config;
    
    foreach ($keys as $k) {
        if (isset($value[$k])) {
            $value = $value[$k];
        } else {
            return null;
        }
    }
    
    return $value;
}

function getUserLevelTitle($level) {
    $levels = config('user_levels');
    return $levels[$level] ?? $levels[1];
}

function getUserTypeName($type) {
    $types = config('user_types');
    return $types[$type] ?? $types[0];
}

function timeAgo($datetime) {
    $timestamp = strtotime($datetime);
    $diff = time() - $timestamp;
    
    if ($diff < 60) {
        return 'Just now';
    } elseif ($diff < 3600) {
        return floor($diff / 60) . ' minutes ago';
    } elseif ($diff < 86400) {
        return floor($diff / 3600) . ' hours ago';
    } elseif ($diff < 2592000) {
        return floor($diff / 86400) . ' days ago';
    } else {
        return date('Y-m-d', $timestamp);
    }
}

function isValidImage($file) {
    $allowedExtensions = config('upload.allowed_extensions');
    
    $ext = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
    if (!in_array($ext, $allowedExtensions)) {
        return false;
    }
    
    if ($file['size'] > config('upload.max_size')) {
        return false;
    }
    
    $imageInfo = @getimagesize($file['tmp_name']);
    if ($imageInfo === false) {
        return false;
    }
    
    return true;
}

function extractExif($filePath) {
    $exif = [
        'make' => null,
        'model' => null,
        'exposure_time' => null,
        'f_number' => null,
        'iso' => null,
        'focal_length' => null,
        'date_taken' => null,
        'width' => 0,
        'height' => 0,
        'artist' => null,
        'copyright' => null,
        'software' => null,
        'orientation' => null
    ];
    
    $imageInfo = @getimagesize($filePath);
    if ($imageInfo !== false) {
        $exif['width'] = $imageInfo[0];
        $exif['height'] = $imageInfo[1];
    }
    
    if (function_exists('exif_read_data')) {
        $exifData = @exif_read_data($filePath);
        
        if ($exifData !== false) {
            $exif['make'] = $exifData['Make'] ?? null;
            $exif['model'] = $exifData['Model'] ?? null;
            $exif['exposure_time'] = $exifData['ExposureTime'] ?? null;
            $exif['f_number'] = $exifData['FNumber'] ?? null;
            $exif['iso'] = $exifData['ISOSpeedRatings'] ?? null;
            $exif['focal_length'] = $exifData['FocalLength'] ?? null;
            $exif['date_taken'] = $exifData['DateTimeOriginal'] ?? null;
            $exif['artist'] = $exifData['Artist'] ?? null;
            $exif['copyright'] = $exifData['Copyright'] ?? null;
            $exif['software'] = $exifData['Software'] ?? null;
            $exif['orientation'] = $exifData['Orientation'] ?? null;
        }
    }
    
    return $exif;
}

