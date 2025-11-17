const crypto = require('crypto');

class XMLBuilder {
    static escapeXML(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&apos;');
    }

    static generateID() {
        return `_${crypto.randomBytes(21).toString('hex')}`;
    }

    static formatDate(date) {
        return date.toISOString();
    }

    static getCurrentTimestamp() {
        return this.formatDate(new Date());
    }

    static getFutureTimestamp(minutes) {
        const date = new Date();
        date.setMinutes(date.getMinutes() + minutes);
        return this.formatDate(date);
    }

    static buildAttributes(attributes) {
        if (!attributes || attributes.length === 0) {
            return '';
        }

        const attributeElements = attributes.map(attr => {
            const values = Array.isArray(attr.value) ? attr.value : [attr.value];
            const attributeValues = values.map(val => 
                '<saml:AttributeValue>' + this.escapeXML(val) + '</saml:AttributeValue>'
            ).join('');

            return '<saml:Attribute Name="' + this.escapeXML(attr.name) + '" NameFormat="' + (attr.nameFormat || 'urn:oasis:names:tc:SAML:2.0:attrname-format:basic') + '">' + attributeValues + '</saml:Attribute>';
        }).join('');

        return '<saml:AttributeStatement>' + attributeElements + '</saml:AttributeStatement>';
    }

    static buildAuthnStatement(authnInstant, sessionIndex) {
        return '<saml:AuthnStatement AuthnInstant="' + authnInstant + '" SessionIndex="' + sessionIndex + '">' +
               '<saml:AuthnContext>' +
               '<saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport</saml:AuthnContextClassRef>' +
               '</saml:AuthnContext>' +
               '</saml:AuthnStatement>';
    }

    static buildAssertion(options) {
        const {
            assertionID,
            issueInstant,
            issuer,
            nameID,
            nameIDFormat,
            recipient,
            audience,
            inResponseTo,
            sessionIndex,
            attributes,
            notBefore,
            notOnOrAfter,
            subjectNotOnOrAfter
        } = options;

        const authnStatement = this.buildAuthnStatement(issueInstant, sessionIndex);
        const attributeStatement = this.buildAttributes(attributes);
        
        const subjectConfirmationInResponseTo = inResponseTo ? ' InResponseTo="' + inResponseTo + '"' : '';

        const assertion = '<saml:Assertion xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion" ID="' + assertionID + '" Version="2.0" IssueInstant="' + issueInstant + '">' +
            '<saml:Issuer>' + this.escapeXML(issuer) + '</saml:Issuer>' +
            '<saml:Subject>' +
            '<saml:NameID Format="' + nameIDFormat + '">' + this.escapeXML(nameID) + '</saml:NameID>' +
            '<saml:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:bearer">' +
            '<saml:SubjectConfirmationData NotOnOrAfter="' + subjectNotOnOrAfter + '" Recipient="' + this.escapeXML(recipient) + '"' + subjectConfirmationInResponseTo + '/>' +
            '</saml:SubjectConfirmation>' +
            '</saml:Subject>' +
            '<saml:Conditions NotBefore="' + notBefore + '" NotOnOrAfter="' + notOnOrAfter + '">' +
            '<saml:AudienceRestriction>' +
            '<saml:Audience>' + this.escapeXML(audience) + '</saml:Audience>' +
            '</saml:AudienceRestriction>' +
            '</saml:Conditions>' +
            authnStatement +
            attributeStatement +
            '</saml:Assertion>';

        return assertion;
    }

    static buildResponseWithoutAssertion(options) {
        const {
            responseID,
            issueInstant,
            destination,
            issuer,
            inResponseTo,
            statusCode
        } = options;

        const inResponseToAttr = inResponseTo ? ' InResponseTo="' + inResponseTo + '"' : '';

        const response = '<?xml version="1.0" encoding="UTF-8"?>' +
            '<samlp:Response xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol" xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion" ID="' + responseID + '" Version="2.0" IssueInstant="' + issueInstant + '" Destination="' + this.escapeXML(destination) + '"' + inResponseToAttr + '>' +
            '<saml:Issuer>' + this.escapeXML(issuer) + '</saml:Issuer>' +
            '<samlp:Status>' +
            '<samlp:StatusCode Value="' + statusCode + '"/>' +
            '</samlp:Status>' +
            '</samlp:Response>';

        return response;
    }

    static buildMetadata(options) {
        const {
            entityID,
            certificate,
            ssoLocation,
            sloLocation,
            nameIDFormats
        } = options;

        const certBody = certificate
            .replace(/-----BEGIN CERTIFICATE-----/, '')
            .replace(/-----END CERTIFICATE-----/, '')
            .replace(/\s/g, '');

        const nameIDFormatElements = (nameIDFormats || ['urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified'])
            .map(format => '<md:NameIDFormat>' + format + '</md:NameIDFormat>')
            .join('');

        const metadata = '<?xml version="1.0" encoding="UTF-8"?>' +
            '<md:EntityDescriptor xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata" xmlns:ds="http://www.w3.org/2000/09/xmldsig#" entityID="' + this.escapeXML(entityID) + '">' +
            '<md:IDPSSODescriptor WantAuthnRequestsSigned="false" protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">' +
            '<md:KeyDescriptor use="signing">' +
            '<ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">' +
            '<ds:X509Data>' +
            '<ds:X509Certificate>' + certBody + '</ds:X509Certificate>' +
            '</ds:X509Data>' +
            '</ds:KeyInfo>' +
            '</md:KeyDescriptor>' +
            '<md:KeyDescriptor use="encryption">' +
            '<ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">' +
            '<ds:X509Data>' +
            '<ds:X509Certificate>' + certBody + '</ds:X509Certificate>' +
            '</ds:X509Data>' +
            '</ds:KeyInfo>' +
            '</md:KeyDescriptor>' +
            nameIDFormatElements +
            '<md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="' + this.escapeXML(ssoLocation) + '"/>' +
            '<md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="' + this.escapeXML(ssoLocation) + '"/>' +
            '<md:SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="' + this.escapeXML(sloLocation) + '"/>' +
            '</md:IDPSSODescriptor>' +
            '</md:EntityDescriptor>';

        return metadata;
    }

    static buildLogoutResponse(options) {
        const {
            responseID,
            issueInstant,
            destination,
            issuer,
            inResponseTo,
            statusCode
        } = options;

        const inResponseToAttr = inResponseTo ? ' InResponseTo="' + inResponseTo + '"' : '';
        const destinationAttr = destination ? ' Destination="' + this.escapeXML(destination) + '"' : '';

        const response = '<?xml version="1.0" encoding="UTF-8"?>' +
            '<samlp:LogoutResponse xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol" xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion" ID="' + responseID + '" Version="2.0" IssueInstant="' + issueInstant + '"' + destinationAttr + inResponseToAttr + '>' +
            '<saml:Issuer>' + this.escapeXML(issuer) + '</saml:Issuer>' +
            '<samlp:Status>' +
            '<samlp:StatusCode Value="' + (statusCode || 'urn:oasis:names:tc:SAML:2.0:status:Success') + '"/>' +
            '</samlp:Status>' +
            '</samlp:LogoutResponse>';

        return response;
    }
}

module.exports = XMLBuilder;
