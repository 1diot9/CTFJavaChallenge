from lxml import etree
import base64
import zlib
import uuid
from datetime import datetime
from urllib.parse import quote


class AuthnRequestBuilder:
    
    NAMESPACES = {
        'samlp': 'urn:oasis:names:tc:SAML:2.0:protocol',
        'saml': 'urn:oasis:names:tc:SAML:2.0:assertion'
    }
    
    def __init__(self, sp_entity_id, acs_url, idp_sso_url, 
                 force_authn=False, is_passive=False,
                 name_id_format='urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified'):
        self.sp_entity_id = sp_entity_id
        self.acs_url = acs_url
        self.idp_sso_url = idp_sso_url
        self.force_authn = force_authn
        self.is_passive = is_passive
        self.name_id_format = name_id_format
        self.request_id = None
        self.issue_instant = None
    
    def build_request(self):
        self.request_id = f"_{uuid.uuid4().hex}"
        self.issue_instant = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%S.%f')[:-3] + 'Z'
        
        nsmap = {
            'samlp': self.NAMESPACES['samlp'],
            'saml': self.NAMESPACES['saml']
        }
        
        root = etree.Element(
            f"{{{self.NAMESPACES['samlp']}}}AuthnRequest",
            nsmap=nsmap
        )
        
        root.set('ID', self.request_id)
        root.set('Version', '2.0')
        root.set('IssueInstant', self.issue_instant)
        root.set('Destination', self.idp_sso_url)
        root.set('AssertionConsumerServiceURL', self.acs_url)
        root.set('ProtocolBinding', 'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST')
        
        if self.force_authn:
            root.set('ForceAuthn', 'true')
        
        if self.is_passive:
            root.set('IsPassive', 'true')
        
        issuer = etree.SubElement(
            root,
            f"{{{self.NAMESPACES['saml']}}}Issuer"
        )
        issuer.text = self.sp_entity_id
        
        name_id_policy = etree.SubElement(
            root,
            f"{{{self.NAMESPACES['samlp']}}}NameIDPolicy"
        )
        name_id_policy.set('Format', self.name_id_format)
        name_id_policy.set('AllowCreate', 'true')
        
        requested_authn_context = etree.SubElement(
            root,
            f"{{{self.NAMESPACES['samlp']}}}RequestedAuthnContext"
        )
        requested_authn_context.set('Comparison', 'exact')
        
        authn_context_class_ref = etree.SubElement(
            requested_authn_context,
            f"{{{self.NAMESPACES['saml']}}}AuthnContextClassRef"
        )
        authn_context_class_ref.text = 'urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport'
        
        xml_string = etree.tostring(root, encoding='utf-8', xml_declaration=False)
        
        return xml_string
    
    def get_request_id(self):
        return self.request_id
    
    def get_redirect_url(self, relay_state=None):
        xml_string = self.build_request()
        
        deflated = zlib.compress(xml_string)[2:-4]
        
        encoded = base64.b64encode(deflated).decode('utf-8')
        
        saml_request = quote(encoded)
        
        url = f"{self.idp_sso_url}?SAMLRequest={saml_request}"
        
        if relay_state:
            url += f"&RelayState={quote(relay_state)}"
        
        return url
    
    def get_post_data(self, relay_state=None):
        xml_string = self.build_request()
        
        encoded = base64.b64encode(xml_string).decode('utf-8')
        
        data = {
            'SAMLRequest': encoded
        }
        
        if relay_state:
            data['RelayState'] = relay_state
        
        return data


class LogoutRequestBuilder:
    
    NAMESPACES = {
        'samlp': 'urn:oasis:names:tc:SAML:2.0:protocol',
        'saml': 'urn:oasis:names:tc:SAML:2.0:assertion'
    }
    
    def __init__(self, sp_entity_id, idp_slo_url, name_id, session_index=None):
        self.sp_entity_id = sp_entity_id
        self.idp_slo_url = idp_slo_url
        self.name_id = name_id
        self.session_index = session_index
        self.request_id = None
        self.issue_instant = None
    
    def build_request(self):
        self.request_id = f"_{uuid.uuid4().hex}"
        self.issue_instant = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%S.%f')[:-3] + 'Z'
        
        nsmap = {
            'samlp': self.NAMESPACES['samlp'],
            'saml': self.NAMESPACES['saml']
        }
        
        root = etree.Element(
            f"{{{self.NAMESPACES['samlp']}}}LogoutRequest",
            nsmap=nsmap
        )
        
        root.set('ID', self.request_id)
        root.set('Version', '2.0')
        root.set('IssueInstant', self.issue_instant)
        root.set('Destination', self.idp_slo_url)
        
        issuer = etree.SubElement(
            root,
            f"{{{self.NAMESPACES['saml']}}}Issuer"
        )
        issuer.text = self.sp_entity_id
        
        name_id_element = etree.SubElement(
            root,
            f"{{{self.NAMESPACES['saml']}}}NameID"
        )
        name_id_element.text = self.name_id
        
        if self.session_index:
            session_index_element = etree.SubElement(
                root,
                f"{{{self.NAMESPACES['samlp']}}}SessionIndex"
            )
            session_index_element.text = self.session_index
        
        xml_string = etree.tostring(root, encoding='utf-8', xml_declaration=False)
        
        return xml_string
    
    def get_redirect_url(self, relay_state=None):
        xml_string = self.build_request()
        
        deflated = zlib.compress(xml_string)[2:-4]
        
        encoded = base64.b64encode(deflated).decode('utf-8')
        
        saml_request = quote(encoded)
        
        url = f"{self.idp_slo_url}?SAMLRequest={saml_request}"
        
        if relay_state:
            url += f"&RelayState={quote(relay_state)}"
        
        return url
