const mysql = require('mysql2/promise');
const { getDatabaseConfig } = require('./config');

const dbConfig = getDatabaseConfig();

const pool = mysql.createPool({
    host: dbConfig.host,
    port: dbConfig.port,
    user: dbConfig.user,
    password: dbConfig.password,
    database: dbConfig.database,
    waitForConnections: dbConfig.waitForConnections,
    connectionLimit: dbConfig.connectionLimit,
    queueLimit: dbConfig.queueLimit,
    enableKeepAlive: true,
    keepAliveInitialDelay: 0
});

pool.getConnection()
    .then(connection => {
        console.log('[Database] Connection pool initialized successfully');
        connection.release();
    })
    .catch(err => {
        console.error('[Database] Failed to connect to database:', err.message);
        console.error('[Database] Connection details:', {
            host: dbConfig.host,
            port: dbConfig.port,
            user: dbConfig.user,
            database: dbConfig.database
        });
    });

module.exports = pool;

