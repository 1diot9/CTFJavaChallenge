const zlib = require('zlib');
const { DOMParser } = require('@xmldom/xmldom')

class RequestParser {
    static decodeSAMLRequest(samlRequest, binding = 'redirect') {
        try {
            const buffer = Buffer.from(samlRequest, 'base64');
            
            if (binding === 'redirect') {
                return zlib.inflateRawSync(buffer).toString('utf8');
            } else {
                return buffer.toString('utf8');
            }
        } catch (error) {
            throw new Error('SAML request decoding failed: ' + error.message);
        }
    }

    static getElementText(doc, tagName) {
        const elements = doc.getElementsByTagNameNS('*', tagName);
        if (elements && elements.length > 0 && elements[0].firstChild) {
            return elements[0].firstChild.nodeValue;
        }
        return null;
    }

    static getAttributeValue(doc, attributeName) {
        const root = doc.documentElement;
        if (root && root.hasAttribute(attributeName)) {
            return root.getAttribute(attributeName);
        }
        return null;
    }

    static parseAuthnRequest(xml) {
        try {
            const parser = new DOMParser();
            const doc = parser.parseFromString(xml, 'text/xml');
            const root = doc.documentElement;

            const requestId = root.getAttribute('ID');
            const destination = root.getAttribute('Destination');
            const issueInstant = root.getAttribute('IssueInstant');
            const acsUrl = root.getAttribute('AssertionConsumerServiceURL');
            const protocolBinding = root.getAttribute('ProtocolBinding');
            const forceAuthn = root.getAttribute('ForceAuthn') === 'true';
            const isPassive = root.getAttribute('IsPassive') === 'true';

            const issuerElements = doc.getElementsByTagNameNS('*', 'Issuer');
            const issuer = issuerElements && issuerElements.length > 0 && issuerElements[0].firstChild 
                ? issuerElements[0].firstChild.nodeValue 
                : null;

            const nameIDPolicyElements = doc.getElementsByTagNameNS('*', 'NameIDPolicy');
            let nameIDFormat = null;
            let allowCreate = true;
            
            if (nameIDPolicyElements && nameIDPolicyElements.length > 0) {
                const policyNode = nameIDPolicyElements[0];
                nameIDFormat = policyNode.getAttribute('Format');
                const allowCreateAttr = policyNode.getAttribute('AllowCreate');
                if (allowCreateAttr) {
                    allowCreate = allowCreateAttr === 'true';
                }
            }

            return {
                requestId,
                issuer,
                acsUrl,
                destination,
                issueInstant,
                protocolBinding,
                forceAuthn,
                isPassive,
                nameIDFormat,
                allowCreate
            };
        } catch (error) {
            throw new Error('SAML request parsing failed: ' + error.message);
        }
    }

    static parseLogoutRequest(xml) {
        try {
            const parser = new DOMParser();
            const doc = parser.parseFromString(xml, 'text/xml');
            const root = doc.documentElement;

            const requestId = root.getAttribute('ID');
            const destination = root.getAttribute('Destination');
            const issueInstant = root.getAttribute('IssueInstant');

            const issuerElements = doc.getElementsByTagNameNS('*', 'Issuer');
            const issuer = issuerElements && issuerElements.length > 0 && issuerElements[0].firstChild 
                ? issuerElements[0].firstChild.nodeValue 
                : null;

            const nameIDElements = doc.getElementsByTagNameNS('*', 'NameID');
            const nameID = nameIDElements && nameIDElements.length > 0 && nameIDElements[0].firstChild 
                ? nameIDElements[0].firstChild.nodeValue 
                : null;

            const sessionIndexElements = doc.getElementsByTagNameNS('*', 'SessionIndex');
            const sessionIndex = sessionIndexElements && sessionIndexElements.length > 0 && sessionIndexElements[0].firstChild 
                ? sessionIndexElements[0].firstChild.nodeValue 
                : null;

            return {
                requestId,
                issuer,
                destination,
                issueInstant,
                nameID,
                sessionIndex
            };
        } catch (error) {
            throw new Error('SAML logout request parsing failed: ' + error.message);
        }
    }

    static validateTimestamp(issueInstant, clockSkewMinutes = 5) {
        if (!issueInstant) {
            return { valid: true };
        }

        const issueTime = new Date(issueInstant).getTime();
        if (isNaN(issueTime)) {
            return { valid: false, message: 'Invalid IssueInstant format' };
        }

        const now = Date.now();
        const maxAge = clockSkewMinutes * 60 * 1000;
        const clockSkew = clockSkewMinutes * 60 * 1000;

        if (now - issueTime > maxAge) {
            return { valid: false, message: 'SAML request has expired' };
        }

        if (issueTime > now + clockSkew) {
            return { valid: false, message: 'SAML request time is invalid' };
        }

        return { valid: true };
    }
}

module.exports = RequestParser;
