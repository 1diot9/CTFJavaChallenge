const config = require('../config/config');

const User = require('../models/User');
const { validationResult } = require('express-validator');


class AuthController {
    static showRegister(req, res) {
        res.render('register', {
            title: 'User Registration',
            errors: [],
            formData: {}
        });
    }

    static async register(req, res) {
        const errors = validationResult(req);

        if (!errors.isEmpty()) {
            return res.render('register', {
                title: 'User Registration',
                errors: errors.array(),
                formData: req.body
            });
        }

        const { username, email, password, type, invitationCode, displayName, department } = req.body;

        try {
            const existingUser = await User.findByUsername(username);
            if (existingUser) {
                return res.render('register', {
                    title: 'User Registration',
                    errors: [{ msg: 'Username already exists' }],
                    formData: req.body
                });
            }

            const existingEmail = await User.findByEmail(email);
            if (existingEmail) {
                return res.render('register', {
                    title: 'User Registration',
                    errors: [{ msg: 'Email already registered' }],
                    formData: req.body
                });
            }


            if (parseInt(type) === 0) {
                if (!invitationCode || invitationCode !== config.getInviteCode()) {
                    return res.render('register', {
                        title: 'User Registration',
                        errors: [{ msg: 'Invalid invitation code' }],
                        formData: req.body
                    });
                }

            }

            req.session.userId = await User.create({
                username,
                email,
                password,
                type,
                displayName: displayName || username,
                department,
                role: 'user'
            });
            req.session.username = username;
            req.session.userType = type;
            req.session.userRole = 'user';

            res.redirect('/portal');
        } catch (error) {
            console.error('[Auth] User registration failed:', error.message);
            console.error(error);
            res.render('register', {
                title: 'User Registration',
                errors: [{ msg: 'Registration failed, please try again later' }],
                formData: req.body
            });
        }
    }

    static showLogin(req, res) {
        const samlRequest = req.query.SAMLRequest || '';
        const relayState = req.query.RelayState || '';
        const oauth2Params = {
            clientId: req.query.client_id || '',
            redirectUri: req.query.redirect_uri || '',
            responseType: req.query.response_type || '',
            state: req.query.state || '',
            scope: req.query.scope || ''
        };

        res.render('login', {
            title: 'User Login',
            errors: [],
            formData: {},
            samlRequest,
            relayState,
            oauth2Params
        });
    }

    static async login(req, res) {
        const { username, password } = req.body;
        const samlRequest = req.body.samlRequest || '';
        const relayState = req.body.relayState || '';

        try {
            const user = await User.findByUsername(username);

            if (!user) {
                return res.render('login', {
                    title: 'User Login',
                    errors: [{ msg: 'Invalid username or password' }],
                    formData: { username },
                    samlRequest,
                    relayState,
                    oauth2Params: {}
                });
            }

            const isValidPassword = await User.verifyPassword(password, user.password);

            if (!isValidPassword) {
                return res.render('login', {
                    title: 'User Login',
                    errors: [{ msg: 'Invalid username or password' }],
                    formData: { username },
                    samlRequest,
                    relayState,
                    oauth2Params: {}
                });
            }

            await User.updateLastLogin(user.id);

            req.session.userId = user.id;
            req.session.username = user.username;
            req.session.userType = user.type;
            req.session.userRole = user.role;
            req.session.userEmail = user.email;
            req.session.displayName = user.display_name || user.username;

            if (samlRequest) {
                return res.redirect(`/saml/sso?SAMLRequest=${encodeURIComponent(samlRequest)}&RelayState=${encodeURIComponent(relayState)}`);
            }

            const oauth2ClientId = req.body.oauth2ClientId || req.query.client_id;
            if (oauth2ClientId) {
                const params = new URLSearchParams({
                    client_id: oauth2ClientId,
                    redirect_uri: req.body.oauth2RedirectUri || req.query.redirect_uri,
                    response_type: req.body.oauth2ResponseType || req.query.response_type || 'code',
                    state: req.body.oauth2State || req.query.state || '',
                    scope: req.body.oauth2Scope || req.query.scope || 'profile'
                });
                return res.redirect(`/oauth/authorize?${params.toString()}`);
            }

            res.redirect('/portal');
        } catch (error) {
            console.error('[Auth] User login failed:', error.message);
            console.error(error);
            res.render('login', {
                title: 'User Login',
                errors: [{ msg: 'Login failed, please try again later' }],
                formData: { username },
                samlRequest,
                relayState,
                oauth2Params: {}
            });
        }
    }

    static async logout(req, res) {
        req.session.destroy((err) => {
            if (err) {
                console.error('[Auth] Session destroy failed:', err.message);
            }
            res.redirect('/');
        });
    }

    static async getCurrentUser(req, res) {
        try {
            const user = await User.findById(req.session.userId);

            if (!user) {
                return res.status(404).json({ error: 'User does not exist' });
            }

            res.json({
                success: true,
                data: User.sanitizeUser(user)
            });
        } catch (error) {
            console.error('[Auth] Get current user failed:', error.message);
            console.error(error);
            res.status(500).json({ error: 'Server error' });
        }
    }
}

module.exports = AuthController;

