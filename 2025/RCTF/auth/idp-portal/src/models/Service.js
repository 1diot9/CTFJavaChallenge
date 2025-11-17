const pool = require('../config/database');

class Service {
    static async create({ serviceId, name, description, icon, protocol, config, enabled = true, sortOrder = 0 }) {
        const configJson = JSON.stringify(config);

        const [result] = await pool.query(
            `INSERT INTO services (service_id, name, description, icon, protocol, config, enabled, sort_order)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
            [serviceId, name, description, icon, protocol, configJson, enabled ? 1 : 0, sortOrder]
        );

        return result.insertId;
    }

    static async findByServiceId(serviceId) {
        const [rows] = await pool.query(
            'SELECT * FROM services WHERE service_id = ?',
            [serviceId]
        );

        if (rows[0]) {
            rows[0].config = JSON.parse(rows[0].config);
        }

        return rows[0];
    }

    static async findByAcsUrl(acsUrl) {
        const [rows] = await pool.query(
            'SELECT * FROM services WHERE protocol = ? AND enabled = 1',
            ['saml']
        );

        for (const row of rows) {
            const config = JSON.parse(row.config);
            if (config.acs_url === acsUrl) {
                row.config = config;
                return row;
            }
        }

        return null;
    }

    static async findById(id) {
        const [rows] = await pool.query(
            'SELECT * FROM services WHERE id = ?',
            [id]
        );

        if (rows[0]) {
            rows[0].config = JSON.parse(rows[0].config);
        }

        return rows[0];
    }

    static async findAll(filters = {}) {
        let query = 'SELECT * FROM services WHERE 1=1';
        const params = [];

        if (filters.enabled !== undefined) {
            query += ' AND enabled = ?';
            params.push(filters.enabled);
        }

        if (filters.protocol) {
            query += ' AND protocol = ?';
            params.push(filters.protocol);
        }

        query += ' ORDER BY sort_order ASC, name ASC';

        const [rows] = await pool.query(query, params);

        return rows.map(row => ({
            ...row,
            config: JSON.parse(row.config)
        }));
    }

    static async findEnabled() {
        return this.findAll({ enabled: 1 });
    }

    static async update(id, data) {
        const fields = [];
        const values = [];

        if (data.name !== undefined) {
            fields.push('name = ?');
            values.push(data.name);
        }

        if (data.description !== undefined) {
            fields.push('description = ?');
            values.push(data.description);
        }

        if (data.icon !== undefined) {
            fields.push('icon = ?');
            values.push(data.icon);
        }

        if (data.protocol !== undefined) {
            fields.push('protocol = ?');
            values.push(data.protocol);
        }

        if (data.config !== undefined) {
            fields.push('config = ?');
            values.push(JSON.stringify(data.config));
        }

        if (data.enabled !== undefined) {
            fields.push('enabled = ?');
            values.push(data.enabled ? 1 : 0);
        }

        if (data.sortOrder !== undefined) {
            fields.push('sort_order = ?');
            values.push(data.sortOrder);
        }

        if (fields.length === 0) {
            return;
        }

        values.push(id);
        await pool.query(
            `UPDATE services SET ${fields.join(', ')} WHERE id = ?`,
            values
        );
    }

    static async delete(id) {
        await pool.query('DELETE FROM services WHERE id = ?', [id]);
    }

    static async updateStatus(id, enabled) {
        await pool.query(
            'UPDATE services SET enabled = ? WHERE id = ?',
            [enabled ? 1 : 0, id]
        );
    }

    static async syncFromConfig(services) {
        for (const service of services) {
            const existing = await this.findByServiceId(service.id);

            if (existing) {
                await this.update(existing.id, {
                    name: service.name,
                    description: service.description,
                    icon: service.icon,
                    protocol: service.protocol,
                    config: service.config,
                    enabled: service.enabled
                });
            } else {
                await this.create({
                    serviceId: service.id,
                    name: service.name,
                    description: service.description,
                    icon: service.icon,
                    protocol: service.protocol,
                    config: service.config,
                    enabled: service.enabled
                });
            }
        }
    }
}

module.exports = Service;

