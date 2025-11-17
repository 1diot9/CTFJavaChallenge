const express = require('express');
const router = express.Router();
const SAMLController = require('../controllers/samlController');

router.get('/metadata', SAMLController.metadata);

router.get('/sso', SAMLController.sso);
router.post('/sso', SAMLController.sso);

router.get('/slo', SAMLController.slo);
router.post('/slo', SAMLController.slo);

router.get('/idp/:serviceId', SAMLController.idpInitiatedSSO);

module.exports = router;

