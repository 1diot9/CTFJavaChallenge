const { SignedXml } = require('xml-crypto');

class XMLSigner {
    constructor(privateKey, certificate) {
        this.privateKey = privateKey;
        this.certificate = certificate;
    }

    signAssertion(assertionXml, assertionID) {
        const xml = assertionXml.replace(/<\?xml[^?]*\?>\s*/g, '');
        
        const sig = new SignedXml();
        sig.privateKey = this.privateKey;
        
        sig.addReference({
            xpath: `//*[@ID="${assertionID}"]`,
            transforms: [
                'http://www.w3.org/2000/09/xmldsig#enveloped-signature',
                'http://www.w3.org/2001/10/xml-exc-c14n#'
            ],
            digestAlgorithm: 'http://www.w3.org/2001/04/xmlenc#sha256'
        });
        
        sig.canonicalizationAlgorithm = 'http://www.w3.org/2001/10/xml-exc-c14n#';
        sig.signatureAlgorithm = 'http://www.w3.org/2001/04/xmldsig-more#rsa-sha256';
        
        sig.keyInfoProvider = {
            getKeyInfo: () => {
                const certBody = this.certificate
                    .replace(/-----BEGIN CERTIFICATE-----/, '')
                    .replace(/-----END CERTIFICATE-----/, '')
                    .replace(/\s/g, '');
                
                return '<X509Data><X509Certificate>' + certBody + '</X509Certificate></X509Data>';
            }
        };
        
        sig.computeSignature(xml, {
            location: { reference: "//*[local-name(.)='Issuer']", action: 'after' }
        });
        
        return sig.getSignedXml();
    }

    signResponse(responseXml, responseID, signedAssertion) {
        const statusCloseTag = '</samlp:Status>';
        const insertPos = responseXml.indexOf(statusCloseTag);
        
        if (insertPos === -1) {
            throw new Error('Cannot find Status element');
        }

        const beforeAssertion = responseXml.substring(0, insertPos + statusCloseTag.length);
        const afterAssertion = responseXml.substring(insertPos + statusCloseTag.length);

        return beforeAssertion + signedAssertion + afterAssertion;
    }
}

module.exports = XMLSigner;
