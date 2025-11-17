exports.up = async function(connection) {
    await connection.query(`
    CREATE TABLE IF NOT EXISTS users (
      id INT AUTO_INCREMENT PRIMARY KEY,
      username VARCHAR(50) UNIQUE NOT NULL,
      email VARCHAR(100) UNIQUE NOT NULL,
      password VARCHAR(255) NOT NULL,
      type TINYINT NOT NULL DEFAULT 1 COMMENT '0:invited user(with permission), 1:self-registered(no permission)',
      display_name VARCHAR(100) DEFAULT NULL,
      avatar VARCHAR(255) DEFAULT NULL,
      department VARCHAR(100) DEFAULT NULL,
      role VARCHAR(50) DEFAULT 'user',
      status TINYINT NOT NULL DEFAULT 1 COMMENT '0:disabled, 1:enabled',
      last_login DATETIME DEFAULT NULL,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      INDEX idx_username (username),
      INDEX idx_email (email),
      INDEX idx_type (type),
      INDEX idx_status (status)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='users table'
  `);

    await connection.query(`
    CREATE TABLE IF NOT EXISTS services (
      id INT AUTO_INCREMENT PRIMARY KEY,
      service_id VARCHAR(50) UNIQUE NOT NULL,
      name VARCHAR(100) NOT NULL,
      description TEXT DEFAULT NULL,
      icon VARCHAR(50) DEFAULT NULL,
      protocol VARCHAR(20) NOT NULL COMMENT 'oauth2 or saml',
      config TEXT NOT NULL COMMENT 'JSON format configuration',
      enabled TINYINT NOT NULL DEFAULT 1,
      sort_order INT DEFAULT 0,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      INDEX idx_service_id (service_id),
      INDEX idx_protocol (protocol),
      INDEX idx_enabled (enabled)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='application services table'
  `);
};

exports.down = async function(connection) {
    await connection.query('DROP TABLE IF EXISTS services');
    await connection.query('DROP TABLE IF EXISTS users');
    await connection.query('DROP TABLE IF EXISTS migrations');
};

