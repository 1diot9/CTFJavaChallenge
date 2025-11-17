from lxml import etree
import requests
import re


class MetadataParser:
    
    NAMESPACES = {
        'md': 'urn:oasis:names:tc:SAML:2.0:metadata',
        'ds': 'http://www.w3.org/2000/09/xmldsig#'
    }
    
    def __init__(self, metadata_url=None, metadata_xml=None):
        self.metadata_url = metadata_url
        self.metadata_xml = metadata_xml
        self.document = None
        
    def load(self):
        try:
            if self.metadata_url:
                response = requests.get(self.metadata_url, timeout=10, verify=True)
                response.raise_for_status()
                self.metadata_xml = response.text
            
            if self.metadata_xml:
                if isinstance(self.metadata_xml, str):
                    self.metadata_xml = self.metadata_xml.encode('utf-8')
                parser = etree.XMLParser(resolve_entities=False, no_network=True)
                self.document = etree.fromstring(self.metadata_xml, parser)
                return True
            
            return False
        except Exception as e:
            return False
    
    def get_idp_sso_url(self, binding='redirect'):
        try:
            if self.document is None:
                return None
            
            idp_descriptors = self.document.xpath(
                '//md:IDPSSODescriptor',
                namespaces=self.NAMESPACES
            )
            
            if not idp_descriptors:
                return None
            
            idp_descriptor = idp_descriptors[0]
            
            if binding.lower() == 'redirect':
                binding_uri = 'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect'
            elif binding.lower() == 'post':
                binding_uri = 'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST'
            else:
                binding_uri = 'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect'
            
            sso_services = idp_descriptor.xpath(
                f'.//md:SingleSignOnService[@Binding="{binding_uri}"]',
                namespaces=self.NAMESPACES
            )
            
            if sso_services:
                return sso_services[0].get('Location')
            
            sso_services = idp_descriptor.xpath(
                './/md:SingleSignOnService',
                namespaces=self.NAMESPACES
            )
            
            if sso_services:
                return sso_services[0].get('Location')
            
            return None
        except Exception:
            return None
    
    def get_idp_slo_url(self, binding='redirect'):
        try:
            if self.document is None:
                return None
            
            idp_descriptors = self.document.xpath(
                '//md:IDPSSODescriptor',
                namespaces=self.NAMESPACES
            )
            
            if not idp_descriptors:
                return None
            
            idp_descriptor = idp_descriptors[0]
            
            if binding.lower() == 'redirect':
                binding_uri = 'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect'
            elif binding.lower() == 'post':
                binding_uri = 'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST'
            else:
                binding_uri = 'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect'
            
            slo_services = idp_descriptor.xpath(
                f'.//md:SingleLogoutService[@Binding="{binding_uri}"]',
                namespaces=self.NAMESPACES
            )
            
            if slo_services:
                return slo_services[0].get('Location')
            
            slo_services = idp_descriptor.xpath(
                './/md:SingleLogoutService',
                namespaces=self.NAMESPACES
            )
            
            if slo_services:
                return slo_services[0].get('Location')
            
            return None
        except Exception:
            return None
    
    def get_idp_certificates(self):
        try:
            if self.document is None:
                return []
            
            certificates = []
            
            cert_nodes = self.document.xpath(
                '//ds:X509Certificate',
                namespaces=self.NAMESPACES
            )
            
            for cert_node in cert_nodes:
                cert_text = cert_node.text
                if cert_text:
                    cert_text = cert_text.strip()
                    cert_text = re.sub(r'\s+', '', cert_text)
                    
                    pem_cert = f"-----BEGIN CERTIFICATE-----\n"
                    for i in range(0, len(cert_text), 64):
                        pem_cert += cert_text[i:i+64] + "\n"
                    pem_cert += "-----END CERTIFICATE-----"
                    
                    certificates.append(pem_cert)
            
            return certificates
        except Exception:
            return []
    
    def get_idp_entity_id(self):
        try:
            if self.document is None:
                return None
            
            entity_descriptors = self.document.xpath(
                '//md:EntityDescriptor',
                namespaces=self.NAMESPACES
            )
            
            if entity_descriptors:
                return entity_descriptors[0].get('entityID')
            
            return None
        except Exception:
            return None
    
    def get_supported_name_id_formats(self):
        try:
            if self.document is None:
                return []
            
            formats = []
            
            format_nodes = self.document.xpath(
                '//md:IDPSSODescriptor/md:NameIDFormat',
                namespaces=self.NAMESPACES
            )
            
            for format_node in format_nodes:
                if format_node.text:
                    formats.append(format_node.text.strip())
            
            return formats
        except Exception:
            return []
    
    def to_dict(self):
        return {
            'entity_id': self.get_idp_entity_id(),
            'sso_url': self.get_idp_sso_url(),
            'sso_url_post': self.get_idp_sso_url('post'),
            'slo_url': self.get_idp_slo_url(),
            'slo_url_post': self.get_idp_slo_url('post'),
            'certificates': self.get_idp_certificates(),
            'name_id_formats': self.get_supported_name_id_formats()
        }


