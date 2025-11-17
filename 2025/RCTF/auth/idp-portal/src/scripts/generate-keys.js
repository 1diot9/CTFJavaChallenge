const forge = require('node-forge');
const fs = require('fs');
const path = require('path');

const configDir = path.join(__dirname, '../config/pem');
const certPath = path.join(configDir, 'idp-cert.pem');
const keyPath = path.join(configDir, 'idp-key.pem');

if (!fs.existsSync(configDir)) {
    fs.mkdirSync(configDir, { recursive: true });
}

if (fs.existsSync(certPath) && fs.existsSync(keyPath)) {
    console.log('Keys already exist');
    process.exit(0);
}

console.log('Generating RSA key pair and X.509 certificate...');

try {
    const keys = forge.pki.rsa.generateKeyPair(2048);
    const cert = forge.pki.createCertificate();

    cert.publicKey = keys.publicKey;
    cert.serialNumber = '01';
    cert.validity.notBefore = new Date();
    cert.validity.notAfter = new Date();
    cert.validity.notAfter.setFullYear(cert.validity.notBefore.getFullYear() + 10);

    const attrs = [{
        name: 'commonName',
        value: 'idp.localhost'
    }, {
        name: 'countryName',
        value: 'CN'
    }, {
        shortName: 'ST',
        value: 'Beijing'
    }, {
        name: 'localityName',
        value: 'Beijing'
    }, {
        name: 'organizationName',
        value: 'IdP Portal'
    }, {
        shortName: 'OU',
        value: 'IT'
    }];

    cert.setSubject(attrs);
    cert.setIssuer(attrs);
    cert.sign(keys.privateKey, forge.md.sha256.create());

    const pemCert = forge.pki.certificateToPem(cert);
    const pemKey = forge.pki.privateKeyToPem(keys.privateKey);

    fs.writeFileSync(keyPath, pemKey);
    fs.writeFileSync(certPath, pemCert);

    console.log('Keys generated successfully');
    console.log(`Private key: ${keyPath}`);
    console.log(`Certificate: ${certPath}`);
} catch (error) {
    console.error('Key generation failed:', error.message);
    process.exit(1);
}

