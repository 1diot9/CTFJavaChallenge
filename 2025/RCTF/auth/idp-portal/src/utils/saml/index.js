const RequestParser = require('./requestParser');
const ResponseBuilder = require('./responseBuilder');
const XMLBuilder = require('./xmlBuilder');

class SAMLIdentityProvider {
    constructor(config) {
        this.config = config;
        this.responseBuilder = new ResponseBuilder(config);
        this.processedRequestIds = new Map();
    }

    parseAuthnRequest(samlRequest, binding = 'redirect') {
        const xml = RequestParser.decodeSAMLRequest(samlRequest, binding);
        const parsed = RequestParser.parseAuthnRequest(xml);
        
        if (parsed.issueInstant) {
            const timestampValidation = RequestParser.validateTimestamp(parsed.issueInstant);
            if (!timestampValidation.valid) {
                throw new Error(timestampValidation.message);
            }
        }

        return parsed;
    }

    parseLogoutRequest(samlRequest, binding = 'redirect') {
        const xml = RequestParser.decodeSAMLRequest(samlRequest, binding);
        const parsed = RequestParser.parseLogoutRequest(xml);
        
        if (parsed.issueInstant) {
            const timestampValidation = RequestParser.validateTimestamp(parsed.issueInstant);
            if (!timestampValidation.valid) {
                throw new Error(timestampValidation.message);
            }
        }

        return parsed;
    }

    validateRequestId(requestId, issueInstant) {
        if (!requestId) {
            return { valid: false, message: 'Missing Request ID' };
        }

        const MAX_AGE = 5 * 60 * 1000;
        const now = Date.now();

        if (issueInstant) {
            const issueTime = new Date(issueInstant).getTime();
            if (isNaN(issueTime)) {
                return { valid: false, message: 'Invalid IssueInstant' };
            }

            if (now - issueTime > MAX_AGE) {
                return { valid: false, message: 'SAML request has expired' };
            }

            if (issueTime > now + 60000) {
                return { valid: false, message: 'SAML request time is invalid' };
            }
        }

        if (this.processedRequestIds.has(requestId)) {
            return { valid: false, message: 'Duplicate SAML request (replay protection)' };
        }

        this.processedRequestIds.set(requestId, now);

        const CLEANUP_AGE = 10 * 60 * 1000;
        for (const [id, timestamp] of this.processedRequestIds.entries()) {
            if (now - timestamp > CLEANUP_AGE) {
                this.processedRequestIds.delete(id);
            }
        }

        return { valid: true };
    }

    createLoginResponse(user, options) {
        const attributes = [
            { name: 'uid', value: String(user.uid || user.id || ''), nameFormat: 'urn:oasis:names:tc:SAML:2.0:attrname-format:basic' },
            { name: 'username', value: String(user.username || ''), nameFormat: 'urn:oasis:names:tc:SAML:2.0:attrname-format:basic' },
            { name: 'email', value: String(user.email || ''), nameFormat: 'urn:oasis:names:tc:SAML:2.0:attrname-format:basic' },
            { name: 'displayName', value: String(user.displayName || user.display_name || user.username || ''), nameFormat: 'urn:oasis:names:tc:SAML:2.0:attrname-format:basic' },
            { name: 'role', value: String(user.role || 'user'), nameFormat: 'urn:oasis:names:tc:SAML:2.0:attrname-format:basic' },
            { name: 'department', value: String(user.department || ''), nameFormat: 'urn:oasis:names:tc:SAML:2.0:attrname-format:basic' }
        ];

        const responseOptions = {
            inResponseTo: options.requestId,
            destination: options.acsUrl || options.destination,
            nameID: user.email || user.username || String(user.id || ''),
            nameIDFormat: options.nameIDFormat || 'urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified',
            attributes: attributes,
            sessionIndex: options.sessionIndex,
            audience: options.entityId || options.audience
        };

        return this.responseBuilder.buildLoginResponse(responseOptions);
    }

    createLogoutResponse(options) {
        return this.responseBuilder.buildLogoutResponse(options);
    }

    getMetadata(options) {
        return this.responseBuilder.buildMetadata(options);
    }

    static encodeResponse(xml) {
        return ResponseBuilder.encodeResponse(xml);
    }

    static decodeRequest(samlRequest, binding = 'redirect') {
        return RequestParser.decodeSAMLRequest(samlRequest, binding);
    }
}

module.exports = SAMLIdentityProvider;

