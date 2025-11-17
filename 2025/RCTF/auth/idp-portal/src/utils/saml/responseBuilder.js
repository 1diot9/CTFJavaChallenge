const XMLBuilder = require('./xmlBuilder');
const XMLSigner = require('./xmlSigner');

class ResponseBuilder {
    constructor(config) {
        this.issuer = config.issuer;
        this.certificate = config.certificate;
        this.privateKey = config.privateKey;
        this.signer = new XMLSigner(this.privateKey, this.certificate);
    }

    buildLoginResponse(options) {
        const {
            inResponseTo,
            destination,
            nameID,
            nameIDFormat = 'urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified',
            attributes = [],
            sessionIndex,
            audience
        } = options;

        const responseID = XMLBuilder.generateID();
        const assertionID = XMLBuilder.generateID();
        const issueInstant = XMLBuilder.getCurrentTimestamp();
        const notBefore = issueInstant;
        const notOnOrAfter = XMLBuilder.getFutureTimestamp(5);
        const subjectNotOnOrAfter = XMLBuilder.getFutureTimestamp(5);
        const finalSessionIndex = sessionIndex || XMLBuilder.generateID();

        const responseWithoutAssertion = XMLBuilder.buildResponseWithoutAssertion({
            responseID,
            issueInstant,
            destination,
            issuer: this.issuer,
            inResponseTo,
            statusCode: 'urn:oasis:names:tc:SAML:2.0:status:Success'
        });

        const assertion = XMLBuilder.buildAssertion({
            assertionID,
            issueInstant,
            issuer: this.issuer,
            nameID,
            nameIDFormat,
            recipient: destination,
            audience: audience || destination,
            inResponseTo,
            sessionIndex: finalSessionIndex,
            attributes,
            notBefore,
            notOnOrAfter,
            subjectNotOnOrAfter
        });

        const signedAssertion = this.signer.signAssertion(assertion, assertionID);

        const signedResponse = this.signer.signResponse(
            responseWithoutAssertion,
            responseID,
            signedAssertion
        );

        return signedResponse;
    }

    buildLogoutResponse(options) {
        const {
            inResponseTo,
            destination,
            statusCode = 'urn:oasis:names:tc:SAML:2.0:status:Success'
        } = options;

        const responseID = XMLBuilder.generateID();
        const issueInstant = XMLBuilder.getCurrentTimestamp();

        return XMLBuilder.buildLogoutResponse({
            responseID,
            issueInstant,
            destination,
            issuer: this.issuer,
            inResponseTo,
            statusCode
        });
    }

    buildMetadata(options) {
        const {
            entityID,
            ssoLocation,
            sloLocation,
            nameIDFormats
        } = options;

        return XMLBuilder.buildMetadata({
            entityID: entityID || this.issuer,
            certificate: this.certificate,
            ssoLocation,
            sloLocation,
            nameIDFormats
        });
    }

    static encodeResponse(xml) {
        return Buffer.from(xml, 'utf8').toString('base64');
    }
}

module.exports = ResponseBuilder;

