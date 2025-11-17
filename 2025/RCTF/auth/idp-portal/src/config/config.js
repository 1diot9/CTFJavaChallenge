const fs = require('fs');
const path = require('path');
const yaml = require('yamljs');
const crypto = require('crypto');

const CONFIG_PATH = path.join(__dirname,  '../../config.yml');
let config = null;

try {
    if (!fs.existsSync(CONFIG_PATH)) {
        throw new Error(`Configuration file does not exist: ${CONFIG_PATH}`);
    }

    config = yaml.load(CONFIG_PATH);

    config.session.secret = crypto.randomBytes(32).toString('hex');
    config.server.invitationCode = crypto.randomBytes(16).toString('hex');


    if (process.env.NODE_ENV) {
        config.server.env = process.env.NODE_ENV;
    }

    if (process.env.PORT) {
        config.server.port = parseInt(process.env.PORT, 10);
    }

    if (process.env.CERT_PATH) {
        config.server.certPath = process.env.CERT_PATH;
    }

    if (process.env.PRIVATE_KEY_PATH) {
        config.server.privateKeyPath = process.env.PRIVATE_KEY_PATH;
    }

    if (process.env.DB_HOST) {
        config.database.host = process.env.DB_HOST;
    }

    if (process.env.DB_PORT) {
        config.database.port = parseInt(process.env.DB_PORT, 10);
    }

    if (process.env.DB_USER) {
        config.database.user = process.env.DB_USER;
    }

    if (process.env.DB_PASSWORD) {
        config.database.password = process.env.DB_PASSWORD;
    }

    if (process.env.DB_NAME) {
        config.database.database = process.env.DB_NAME;
    }


    if (process.env.SAML_ISSUER) {
        config.saml.issuer = process.env.SAML_ISSUER;
    }

} catch (error) {
    console.error(error);
    process.exit(1);
}

function getServerConfig() {
    return config.server;
}

function getDatabaseConfig() {
    return config.database;
}

function getSessionConfig() {
    return config.session;
}

function getSamlConfig() {
    return config.saml;
}

function getServicesConfig() {
    return config.services || [];
}

function getLoggingConfig() {
    return config.logging;
}

function getSecurityConfig() {
    return config.security;
}

function getInviteCode() {
    return config.server.invitationCode;
}

function isProduction() {
    return config.server.env === 'production';
}

function getCertPath() {
    return path.join(__dirname, '../..', config.server.certPath);
}

function getPrivateKeyPath() {
    return path.join(__dirname, '../..', config.server.privateKeyPath);
}


function getCertificate() {
    const certPath = getCertPath();
    if (!fs.existsSync(certPath)) {
        throw new Error(`Certificate file does not exist: ${certPath}`);
    }
    return fs.readFileSync(certPath, 'utf8');
}

function getPrivateKey() {
    const keyPath = getPrivateKeyPath();
    if (!fs.existsSync(keyPath)) {
        throw new Error(`Private key file does not exist: ${keyPath}`);
    }
    return fs.readFileSync(keyPath, 'utf8');
}

module.exports = {
    getServerConfig,
    getDatabaseConfig,
    getSessionConfig,
    getSamlConfig,
    getServicesConfig,
    getLoggingConfig,
    getSecurityConfig,
    getInviteCode,
    isProduction,
    getCertificate,
    getPrivateKey
};
