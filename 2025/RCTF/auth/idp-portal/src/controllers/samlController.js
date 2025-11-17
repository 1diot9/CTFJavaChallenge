const SAMLService = require('../services/samlService');
const Service = require('../models/Service');
const User = require('../models/User');
const config = require('../config/config');

class SAMLController {
    static metadata(req, res) {
        const metadata = SAMLService.generateMetadata();
        res.set('Content-Type', 'text/xml');
        res.send(metadata);
    }

    static async sso(req, res) {
        const { SAMLRequest, RelayState } = req.query;

        if (!req.session || !req.session.userId) {
            const loginUrl = `/login?SAMLRequest=${encodeURIComponent(SAMLRequest || '')}&RelayState=${encodeURIComponent(RelayState || '')}`;
            return res.redirect(loginUrl);
        }

        if (req.session.userType !== 0) {
            return res.status(403).render('error', {
                title: 'Access Denied',
                message: 'You do not have permission to use SAML services.',
                user: req.user
            });
        }

        try {
            const user = await User.findById(req.session.userId);
            if (!user) {
                return res.redirect('/login');
            }

            let acsUrl = null;
            let entityId = null;
            let requestId = null;
            let matchedService = null;

            if (SAMLRequest) {
                const validation = await SAMLService.validateSAMLRequest(SAMLRequest);
                if (!validation.valid) {
                    return res.status(400).render('error', {
                        title: 'Invalid SAML Request',
                        message: validation.message,
                        user: req.user
                    });
                }

                const parsed = validation.data;
                if (parsed) {
                    acsUrl = parsed.acsUrl;
                    entityId = parsed.entityId;
                    requestId = parsed.requestId;

                    const requestIdValidation = await SAMLService.validateRequestId(
                        requestId, 
                        parsed.issueInstant
                    );
                    if (!requestIdValidation.valid) {
                        return res.status(400).render('error', {
                            title: 'SAML Request Validation Failed',
                            message: requestIdValidation.message,
                            user: req.user
                        });
                    }

                    matchedService = await Service.findByAcsUrl(acsUrl);
                    if (!matchedService) {
                        return res.status(403).render('error', {
                            title: 'Service Not Authorized',
                            message: 'This service is not registered in the system or has been disabled',
                            user: req.user
                        });
                    }

                    if (matchedService.config.entity_id && 
                        matchedService.config.entity_id !== entityId) {
                        return res.status(403).render('error', {
                            title: 'Service Identity Mismatch',
                            message: 'Entity ID validation failed',
                            user: req.user
                        });
                    }
                }
            }

            if (!acsUrl && RelayState) {
                const service = await Service.findByServiceId(RelayState);
                if (service && service.protocol === 'saml' && service.enabled) {
                    acsUrl = service.config.acs_url;
                    entityId = service.config.entity_id;
                    matchedService = service;
                } else {
                    return res.status(400).render('error', {
                        title: 'Service Not Found',
                        message: 'The specified service was not found or has been disabled',
                        user: req.user
                    });
                }
            }

            if (!acsUrl) {
                return res.status(400).render('error', {
                    title: 'Parameter Error',
                    message: 'Missing required SAML parameters',
                    user: req.user
                });
            }

            const encodedResponse = await SAMLService.generateSAMLResponse(user, {
                acsUrl,
                entityId,
                requestId
            });

            res.render('saml_post', {
                title: 'Logging in...',
                acsUrl,
                samlResponse: encodedResponse,
                relayState: RelayState || ''
            });

        } catch (error) {
            console.error('[SAML] SSO processing failed:', error.message);
            console.error(error);
            return res.status(500).render('error', {
                title: 'Server Error',
                message: 'An error occurred while processing the SAML request. Please try again later',
                user: req.user
            });
        }
    }

    static async slo(req, res) {
        const { SAMLRequest, RelayState } = req.query;

        try {
            if (req.session) {
                req.session.destroy();
            }

            if (SAMLRequest) {
                const decoded = SAMLService.decodeSAMLRequest(SAMLRequest);

                const requestIdMatch = decoded?.match(/ID="([^"]+)"/);
                const destinationMatch = decoded?.match(/Destination="([^"]+)"/);

                const logoutResponseUrl = await SAMLService.generateSAMLLogoutResponse({
                    inResponseTo: requestIdMatch ? requestIdMatch[1] : undefined,
                    destination: destinationMatch ? destinationMatch[1] : undefined
                });

                if (typeof logoutResponseUrl === 'string') {
                    if (logoutResponseUrl.startsWith('http')) {
                        return res.redirect(logoutResponseUrl);
                    } else {
                        const encodedResponse = SAMLService.encodeSAMLResponse(logoutResponseUrl);
                        if (destinationMatch && destinationMatch[1]) {
                            const redirectUrl = `${destinationMatch[1]}?SAMLResponse=${encodeURIComponent(encodedResponse)}${RelayState ? `&RelayState=${encodeURIComponent(RelayState)}` : ''}`;
                            return res.redirect(redirectUrl);
                        }
                    }
                }
            }

            res.redirect('/');

        } catch (error) {
            console.error('[SAML] SLO processing failed:', error.message);
            console.error(error);
            res.redirect('/');
        }
    }

    static async idpInitiatedSSO(req, res) {
        const { serviceId } = req.params;

        if (!req.session || !req.session.userId) {
            return res.redirect(`/login?serviceId=${serviceId}`);
        }

        if (req.session.userType !== 0) {
            return res.status(403).render('error', {
                title: 'Access Denied',
                message: 'You do not have permission to access this service. Please contact the administrator to invite you to register.',
                user: req.user
            });
        }

        try {
            const service = await Service.findByServiceId(serviceId);
            if (!service || !service.enabled) {
                return res.status(404).send('Service does not exist or has been disabled');
            }

            if (service.protocol !== 'saml') {
                return res.status(400).send('This service does not support SAML protocol');
            }

            const user = await User.findById(req.session.userId);
            if (!user) {
                return res.redirect('/login');
            }

            const encodedResponse = await SAMLService.generateSAMLResponse(user, {
                acsUrl: service.config.acs_url,
                entityId: service.config.entity_id
            });

            res.render('saml_post', {
                title: `Logging in to ${service.name}...`,
                acsUrl: service.config.acs_url,
                samlResponse: encodedResponse,
                relayState: ''
            });

        } catch (error) {
            console.error('[SAML] IDP-initiated SSO failed:', error.message);
            console.error(error);
            res.status(500).send('SAML processing failed');
        }
    }
}

module.exports = SAMLController;

