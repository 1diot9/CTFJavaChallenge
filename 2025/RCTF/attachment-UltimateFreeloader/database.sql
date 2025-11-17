-- Create database
CREATE DATABASE IF NOT EXISTS happy_shopping DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE happy_shopping;

-- Users table
CREATE TABLE users (
                       id VARCHAR(36) PRIMARY KEY COMMENT 'User ID using UUID',
                       username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
                       password VARCHAR(255) NOT NULL COMMENT 'Password',
                       email VARCHAR(100) NOT NULL UNIQUE COMMENT 'Email',
                       balance DECIMAL(10,2) NOT NULL DEFAULT 10.00 COMMENT 'Balance',
                       create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
                       update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
                       INDEX idx_username (username),
                       INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Users table';

-- Products table
CREATE TABLE products (
                          id VARCHAR(36) PRIMARY KEY COMMENT 'Product ID using UUID',
                          name VARCHAR(100) NOT NULL COMMENT 'Product name',
                          description VARCHAR(500) COMMENT 'Product description',
                          price DECIMAL(10,2) NOT NULL COMMENT 'Unit price',
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
                          INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Products table';

-- Coupons table
CREATE TABLE coupons (
                         id VARCHAR(36) PRIMARY KEY COMMENT 'Coupon ID using UUID',
                         user_id VARCHAR(36) NOT NULL COMMENT 'User ID',
                         name VARCHAR(100) NOT NULL COMMENT 'Coupon name',
                         discount_amount DECIMAL(10,2) NOT NULL COMMENT 'Discount amount',
                         is_used BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Is used',
                         expire_time TIMESTAMP NOT NULL COMMENT 'Expiration time',
                         create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
                         update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
                         INDEX idx_user_id (user_id),
                         INDEX idx_expire_time (expire_time),
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Coupons table';

-- Orders table
CREATE TABLE orders (
                        id VARCHAR(36) PRIMARY KEY COMMENT 'Order ID using UUID',
                        user_id VARCHAR(36) NOT NULL COMMENT 'User ID',
                        product_id VARCHAR(36) NOT NULL COMMENT 'Product ID',
                        quantity INT NOT NULL COMMENT 'Quantity',
                        original_price DECIMAL(10,2) NOT NULL COMMENT 'Original price',
                        discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Discount amount',
                        final_price DECIMAL(10,2) NOT NULL COMMENT 'Final price',
                        coupon_id VARCHAR(36) COMMENT 'Used coupon ID',
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Order status',
                        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
                        update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
                        INDEX idx_user_id (user_id),
                        INDEX idx_product_id (product_id),
                        INDEX idx_status (status),
                        INDEX idx_create_time (create_time),
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                        FOREIGN KEY (coupon_id) REFERENCES coupons(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Orders table';

-- Insert initial product data (using UUID)
INSERT INTO products (id, name, description, price) VALUES
                                                        ('550e8400-e29b-41d4-a716-446655440001', 'Little Potato', 'Just A Little Potato,come to buy it!', 5.50),
                                                        ('550e8400-e29b-41d4-a716-446655440002', 'Sweet Potato', 'Sweet!DiGua!', 8.80),
                                                        ('550e8400-e29b-41d4-a716-446655440003', 'Fish Fish', 'Deer~Deer~Fish~Fish~', 4.20),
                                                        ('550e8400-e29b-41d4-a716-446655440004', 'Large Potato', 'VeryVery Large and most expensive', 10.00);

ALTER TABLE coupons MODIFY expire_time DATETIME NOT NULL COMMENT 'Expiration time';