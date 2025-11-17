const express = require('express');
const session = require('express-session');
const bodyParser = require('body-parser');
const path = require('path');
const helmet = require('helmet');
const cors = require('cors');
const morgan = require('morgan');
const ejs = require('ejs');

const config = require('./config/config');

const authRoutes = require('./routes/auth');
const portalRoutes = require('./routes/portal');
const samlRoutes = require('./routes/saml');

const { loadUser } = require('./middleware/auth');

const PortalController = require('./controllers/portalController');

const app = express();

const serverConfig = config.getServerConfig();
const sessionConfig = config.getSessionConfig();
const loggingConfig = config.getLoggingConfig();
const securityConfig = config.getSecurityConfig();

if (securityConfig.helmet.enabled) {
    app.use(helmet({
        contentSecurityPolicy: false
    }));
}

if (securityConfig.cors.enabled) {
    app.use(cors({
        origin: securityConfig.cors.origin,
        credentials: securityConfig.cors.credentials
    }));
}

if (!config.isProduction()) {
    app.use(morgan(loggingConfig.format || 'dev'));
}

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use(session({
    secret: sessionConfig.secret,
    resave: sessionConfig.resave,
    saveUninitialized: sessionConfig.saveUninitialized,
    cookie: {
        secure: config.isProduction(),
        httpOnly: sessionConfig.cookie.httpOnly,
        maxAge: sessionConfig.cookie.maxAge
    }
}));

app.use(express.static(path.join(__dirname, 'public')));

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

app.use(loadUser);

app.use('/', portalRoutes);
app.use('/', authRoutes);
app.use('/saml', samlRoutes);

app.use((req, res) => {
    res.status(404).render('error', {
        title: 'Page Not Found',
        message: 'The page you are looking for does not exist',
        user: req.user
    });
});

app.use((err, req, res, next) => {
    console.error('[Error] Server error:', err);
    res.status(500).render('error', {
        title: 'Server Error',
        message: config.isProduction()
            ? 'Internal server error, please try again later'
            : err.message,
        user: req.user
    });
});

const PORT = serverConfig.port;
const HOST = serverConfig.host;

async function startServer() {
    try {
        await PortalController.syncServicesFromConfig();

        app.listen(PORT, () => {

            console.log( '='.repeat(40));
            console.log('IDP Application Service Center Started');
            console.log(`Access URL: http://${HOST}:${PORT}`);
            console.log('SAML Metadata: http://' + HOST + ':' + PORT + '/saml/metadata');
            console.log('User invitation code: ' + config.getInviteCode());

        });
    } catch (error) {
        console.error('[Error] Server startup failed:', error);
        process.exit(1);
    }
}

startServer();

module.exports = app;

