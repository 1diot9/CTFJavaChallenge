const SAMLIdentityProvider = require('../utils/saml');
const config = require('../config/config');

class SAMLService {
    static getIdentityProvider() {
        if (!this.idp) {
            const certificate = config.getCertificate();
            const privateKey = config.getPrivateKey();
            const samlConfig = config.getSamlConfig();

            this.idp = new SAMLIdentityProvider({
                issuer: process.env.SAML_ISSUER || samlConfig.issuer,
                certificate: certificate,
                privateKey: privateKey
            });
        }
        return this.idp;
    }

    static async generateSAMLResponse(user, options = {}) {
        const idp = this.getIdentityProvider();

        const userInfo = {
            id: user.id,
            uid: String(user.id || ''),
            username: user.username || '',
            email: user.email || user.username || '',
            displayName: user.display_name || user.username || '',
            display_name: user.display_name || user.username || '',
            role: user.role || 'user',
            department: user.department || ''
        };

        const responseOptions = {
            requestId: options.requestId,
            acsUrl: options.acsUrl || options.acs_url,
            destination: options.acsUrl || options.acs_url,
            entityId: options.entityId || options.entity_id,
            audience: options.entityId || options.entity_id,
            nameIDFormat: options.nameIDFormat || 'urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified'
        };

        const responseXml = idp.createLoginResponse(userInfo, responseOptions);
        return SAMLIdentityProvider.encodeResponse(responseXml);
    }

    static async parseSAMLRequest(samlRequest, binding = 'redirect') {
        try {
            const idp = this.getIdentityProvider();
            const parsed = idp.parseAuthnRequest(samlRequest, binding);

            return {
                requestId: parsed.requestId,
                acsUrl: parsed.acsUrl,
                entityId: parsed.issuer,
                issueInstant: parsed.issueInstant,
                destination: parsed.destination,
                protocolBinding: parsed.protocolBinding,
                forceAuthn: parsed.forceAuthn,
                isPassive: parsed.isPassive,
                nameIDFormat: parsed.nameIDFormat,
                allowCreate: parsed.allowCreate
            };
        } catch (error) {
            console.error('[SAML Service] Failed to parse SAML request:', error.message);
            return null;
        }
    }

    static async validateSAMLRequest(samlRequest) {
        if (!samlRequest) {
            return { valid: false, message: 'Missing SAML request' };
        }

        try {
            const parsed = await this.parseSAMLRequest(samlRequest);
            if (!parsed) {
                return { valid: false, message: 'Invalid SAML request format' };
            }
            return { valid: true, data: parsed };
        } catch (error) {
            console.error('[SAML Service] SAML request validation failed:', error.message);
            return { valid: false, message: error.message };
        }
    }

    static async validateRequestId(requestId, issueInstant) {
        const idp = this.getIdentityProvider();
        return idp.validateRequestId(requestId, issueInstant);
    }

    static generateMetadata() {
        try {
            const idp = this.getIdentityProvider();
            const serverConfig = config.getServerConfig();
            const samlConfig = config.getSamlConfig();
            const baseUrl = process.env.BASE_URL || `http://${serverConfig.host}:${serverConfig.port}`;

            return idp.getMetadata({
                entityID: process.env.SAML_ISSUER || samlConfig.issuer,
                ssoLocation: `${baseUrl}/saml/sso`,
                sloLocation: `${baseUrl}/saml/slo`,
                nameIDFormats: ['urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified']
            });
        } catch (error) {
            console.error('[SAML Service] Failed to generate metadata:', error.message);
            console.error(error);
            throw error;
        }
    }

    static async generateSAMLLogoutResponse(options = {}) {
        try {
            const idp = this.getIdentityProvider();

            const logoutResponseOptions = {
                inResponseTo: options.inResponseTo,
                destination: options.destination || options.acsUrl
            };

            const responseXml = idp.createLogoutResponse(logoutResponseOptions);
            return responseXml;
        } catch (error) {
            console.error('[SAML Service] Failed to generate logout response:', error.message);
            console.error(error);
            throw error;
        }
    }

    static encodeSAMLResponse(samlResponse) {
        return SAMLIdentityProvider.encodeResponse(samlResponse);
    }

    static decodeSAMLRequest(samlRequest) {
        try {
            return SAMLIdentityProvider.decodeRequest(samlRequest, 'redirect');
        } catch (error) {
            console.error('[SAML Service] Failed to decode SAML request:', error.message);
            return null;
        }
    }
}

module.exports = SAMLService;
