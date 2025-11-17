
-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_admin TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Articles table
CREATE TABLE IF NOT EXISTS articles (
    id BIGINT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    content TEXT NOT NULL,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    views INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO users (username, email, password, is_admin) VALUES 
('admin', 'admin@rois.team', '$2y$10$w56aNQmssUmb5h9e.BdYpOXoU38suUJQ7ACZVhs2XTz8zLtGhDlpi', 1)
ON DUPLICATE KEY UPDATE is_admin = 1;

INSERT INTO `author`.`articles` (`id`, `user_id`, `title`, `subtitle`, `content`, `status`, `views`, `created_at`, `updated_at`) VALUES (2348385096957952, 1, 'Welcome to RCTF2025!', '', 'Welcome to RCTF2025!', 'approved', 1, '2025-11-15 10:00:00', '2025-11-15 10:00:00');
