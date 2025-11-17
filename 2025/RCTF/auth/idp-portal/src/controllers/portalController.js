const Service = require('../models/Service');
const User = require('../models/User');
const config = require('../config/config');

class PortalController {
    static index(req, res) {
        res.render('index', {
            title: 'Unified Identity Authentication Center'
        });
    }

    static async portal(req, res) {
        try {
            const user = await User.findById(req.session.userId);

            if (!user) {
                return res.redirect('/login');
            }

            const services = await Service.findEnabled();

            res.render('portal', {
                title: 'Application Service Center',
                user: User.sanitizeUser(user),
                services
            });
        } catch (error) {
            console.error('[Portal] Failed to load portal:', error.message);
            console.error(error);
            res.status(500).send('Server error');
        }
    }

    static async syncServicesFromConfig() {
        try {
            const services = config.getServicesConfig();

            if (services && services.length > 0) {
                console.log(`[Portal] Syncing ${services.length} service(s) from config...`);
                await Service.syncFromConfig(services);
                console.log('[Portal] Services synced successfully');
            }
        } catch (error) {
            console.error('[Portal] Failed to sync services from config:', error.message);
            console.error(error);
            throw error;
        }
    }
}

module.exports = PortalController;

