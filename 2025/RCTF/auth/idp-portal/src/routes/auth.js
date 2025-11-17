const express = require('express');
const router = express.Router();
const { body } = require('express-validator');
const AuthController = require('../controllers/authController');
const { requireAuth, redirectIfAuthenticated } = require('../middleware/auth');

const registerValidation = [
    body('username')
        .trim()
        .isLength({ min: 3, max: 50 })
        .withMessage('Username must be between 3-50 characters')
        .matches(/^[a-zA-Z0-9_]+$/)
        .withMessage('Username can only contain letters, numbers, and underscores'),

    body('email')
        .trim()
        .isEmail()
        .withMessage('Please enter a valid email address')
        .normalizeEmail(),

    body('password')
        .isLength({ min: 6 })
        .withMessage('Password must be at least 6 characters'),

    body('confirmPassword')
        .custom((value, { req }) => value === req.body.password)
        .withMessage('Passwords do not match'),

    body('type'),

    body('invitationCode')
        .if(body('type').equals('0'))
        .notEmpty()
        .withMessage('Invited users must provide an invitation code')
];

const loginValidation = [
    body('username')
        .trim()
        .notEmpty()
        .withMessage('Please enter username'),

    body('password')
        .notEmpty()
        .withMessage('Please enter password')
];

router.get('/register', redirectIfAuthenticated, AuthController.showRegister);

router.post('/register', redirectIfAuthenticated, registerValidation, AuthController.register);

router.get('/login', redirectIfAuthenticated, AuthController.showLogin);

router.post('/login', redirectIfAuthenticated, loginValidation, AuthController.login);

router.get('/logout', AuthController.logout);
router.post('/logout', AuthController.logout);

router.get('/api/me', requireAuth, AuthController.getCurrentUser);

module.exports = router;

