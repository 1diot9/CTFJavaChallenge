const express = require('express');
const router = express.Router();
const PortalController = require('../controllers/portalController');
const { requireAuth, requirePermission } = require('../middleware/auth');

router.get('/', (req, res) => {
    if (req.session && req.session.userId) {
        return res.redirect('/portal');
    }
    PortalController.index(req, res);
});

router.get('/portal', requireAuth, PortalController.portal);

module.exports = router;

