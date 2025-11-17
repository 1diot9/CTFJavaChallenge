const pool = require('../config/database');
const bcrypt = require('bcrypt');

class User {
    static async create({ username, email, password, type, displayName, department, role = 'user' }) {
        const hashedPassword = await bcrypt.hash(password, 10);

        const [result] = await pool.query(
            `INSERT INTO users (username, email, password, type, display_name, department, role)
       VALUES (?, ?, ?, ?, ?, ?, ?)`,
            [username, email, hashedPassword, type, displayName, department, role]
        );

        return result.insertId;
    }

    static async findByUsername(username) {
        const [rows] = await pool.query(
            'SELECT * FROM users WHERE username = ? AND status = 1',
            [username]
        );
        return rows[0];
    }

    static async findByEmail(email) {
        const [rows] = await pool.query(
            'SELECT * FROM users WHERE email = ? AND status = 1',
            [email]
        );
        return rows[0];
    }

    static async findById(id) {
        const [rows] = await pool.query(
            'SELECT * FROM users WHERE id = ? AND status = 1',
            [id]
        );
        return rows[0];
    }

    static async verifyPassword(plainPassword, hashedPassword) {
        return bcrypt.compare(plainPassword, hashedPassword);
    }

    static async updateLastLogin(userId) {
        await pool.query(
            'UPDATE users SET last_login = NOW() WHERE id = ?',
            [userId]
        );
    }

    static sanitizeUser(user) {
        if (!user) return null;

        const { password, ...safeUser } = user;
        return safeUser;
    }
}

module.exports = User;

