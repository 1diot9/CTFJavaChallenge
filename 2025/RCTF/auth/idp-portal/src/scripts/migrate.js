const mysql = require('mysql2/promise');
const fs = require('fs').promises;
const path = require('path');
const { getDatabaseConfig } = require('../config/config');

class Migrator {
    constructor() {
        this.migrationsDir = path.join(__dirname, "../migrations");
        this.connection = null;
        this.dbConfig = getDatabaseConfig();
    }

    async connect() {
        this.connection = await mysql.createConnection({
            host: this.dbConfig.host,
            port: this.dbConfig.port,
            user: this.dbConfig.user,
            password: this.dbConfig.password,
            database: this.dbConfig.database,
            multipleStatements: true
        });
    }

    async disconnect() {
        if (this.connection) {
            await this.connection.end();
        }
    }

    async ensureMigrationsTable() {
        await this.connection.query(`
      CREATE TABLE IF NOT EXISTS migrations (
        id INT AUTO_INCREMENT PRIMARY KEY,
        migration_name VARCHAR(255) UNIQUE NOT NULL,
        executed_at DATETIME DEFAULT CURRENT_TIMESTAMP
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    `);
    }

    async getExecutedMigrations() {
        const [rows] = await this.connection.query(
            'SELECT migration_name FROM migrations ORDER BY id ASC'
        );
        return rows.map(row => row.migration_name);
    }

    async markMigrationExecuted(migrationName) {
        await this.connection.query(
            'INSERT INTO migrations (migration_name) VALUES (?)',
            [migrationName]
        );
    }

    async removeMigrationRecord(migrationName) {
        await this.connection.query(
            'DELETE FROM migrations WHERE migration_name = ?',
            [migrationName]
        );
    }

    async getMigrationFiles() {
        const files = await fs.readdir(this.migrationsDir);
        return files
            .filter(f => f.endsWith('.js'))
            .sort();
    }

    async up() {
        await this.connect();
        
        await this.connection.query(`SET GLOBAL sql_mode = ''`);

        await this.ensureMigrationsTable();

        const allMigrations = await this.getMigrationFiles();
        const executed = await this.getExecutedMigrations();
        const pending = allMigrations.filter(m => !executed.includes(m));

        if (pending.length === 0) {
            console.log('No pending migrations');
            
            await this.connection.query(`SET GLOBAL sql_mode = 'ONLY_FULL_GROUP_BY,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION'`);
            
            await this.disconnect();
            return;
        }

        console.log(`Found ${pending.length} pending migration(s)\n`);

        for (const migrationFile of pending) {
            const migrationPath = path.join(this.migrationsDir, migrationFile);
            const migration = require(migrationPath);

            console.log(`Executing migration: ${migrationFile}`);

            try {
                await migration.up(this.connection);
                await this.markMigrationExecuted(migrationFile);
                console.log(`[Success] ${migrationFile} executed successfully\n`);
            } catch (error) {
                console.error(`[Failed] ${migrationFile} execution failed:`, error.message);
                await this.disconnect();
                throw error;
            }
        }
        
        await this.connection.query(`SET GLOBAL sql_mode = 'ONLY_FULL_GROUP_BY,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION'`);

        await this.disconnect();
    }

    async down() {
        await this.connect();
        await this.ensureMigrationsTable();

        const executed = await this.getExecutedMigrations();

        if (executed.length === 0) {
            await this.disconnect();
            return;
        }

        const lastMigration = executed[executed.length - 1];
        const migrationPath = path.join(this.migrationsDir, lastMigration);

        console.log(`Rolling back migration: ${lastMigration}`);

        try {
            const migration = require(migrationPath);
            await migration.down(this.connection);
            await this.removeMigrationRecord(lastMigration);
            console.log(`[Success] ${lastMigration} rolled back successfully`);
        } catch (error) {
            console.error(`[Failed] ${lastMigration} rollback failed:`, error.message);
            await this.disconnect();
            throw error;
        }

        await this.disconnect();
    }

    async status() {
        await this.connect();
        await this.ensureMigrationsTable();

        const allMigrations = await this.getMigrationFiles();
        const executed = await this.getExecutedMigrations();

        console.log('\nMigration Status:\n');
        console.log('Executed:');
        executed.forEach(m => console.log(`  [Executed] ${m}`));

        const pending = allMigrations.filter(m => !executed.includes(m));
        if (pending.length > 0) {
            console.log('\nPending:');
            pending.forEach(m => console.log(`  [Pending] ${m}`));
        }

        console.log(`\nTotal: ${allMigrations.length} | Executed: ${executed.length} | Pending: ${pending.length}\n`);

        await this.disconnect();
    }
}

const command = process.argv[2] || 'up';
const migrator = new Migrator();

(async () => {
    try {
        switch (command) {
            case 'up':
                await migrator.up();
                break;
            case 'down':
                await migrator.down();
                break;
            case 'status':
                await migrator.status();
                break;
            default:
                console.log('Usage: node migrate.js [up|down|status]');
                process.exit(1);
        }
    } catch (error) {
        console.error('Migration failed:', error);
        process.exit(1);
    }
})();

