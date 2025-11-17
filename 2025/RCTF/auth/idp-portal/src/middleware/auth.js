const jwt = require('jsonwebtoken');
const User = require('../models/User');

function requireAuth(req, res, next) {
    if (!req.session || !req.session.userId) {
        if (req.xhr || req.headers.accept?.includes('application/json')) {
            return res.status(401).json({ error: 'Not logged in' });
        }
        return res.redirect('/login');
    }
    next();
}

function requirePermission(req, res, next) {
    if (!req.session || !req.session.userId) {
        if (req.xhr || req.headers.accept?.includes('application/json')) {
            return res.status(401).json({ error: 'Not logged in' });
        }
        return res.redirect('/login');
    }

    if (req.session.userType !== 0) {
        if (req.xhr || req.headers.accept?.includes('application/json')) {
            return res.status(403).json({ error: 'Access denied. Please contact the administrator to invite you to register' });
        }
        return res.render('error', {
            title: 'Access Denied',
            message: 'You do not have permission to access application services. Please contact the administrator to invite you to register.',
            user: req.user
        });
    }

    next();
}


async function loadUser(req, res, next) {
    if (req.session && req.session.userId) {
        try {
            const user = await User.findById(req.session.userId);
            if (user) {
                req.user = User.sanitizeUser(user);
            } else {
                req.session.destroy();
            }
        } catch (error) {
            console.error('[Auth] Failed to load user:', error.message);
        }
    }
    next();
}

function redirectIfAuthenticated(req, res, next) {
    if (req.session && req.session.userId) {
        return res.redirect('/portal');
    }
    next();
}

module.exports = {
    requireAuth,
    requirePermission,
    loadUser,
    redirectIfAuthenticated
};

