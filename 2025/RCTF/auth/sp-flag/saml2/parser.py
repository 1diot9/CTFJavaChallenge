import base64
from lxml import etree
from .validator import SignatureValidator


class SAMLResponseParser:
    
    NAMESPACES = {
        'samlp': 'urn:oasis:names:tc:SAML:2.0:protocol',
        'saml': 'urn:oasis:names:tc:SAML:2.0:assertion',
        'ds': 'http://www.w3.org/2000/09/xmldsig#'
    }
    
    def __init__(self, saml_response_b64, cert_text, 
                 validate_time=True,
                 validate_audience=False,
                 expected_audience=None,
                 validate_destination=False,
                 expected_destination=None,
                 time_tolerance=300):
        self.saml_response_b64 = saml_response_b64
        self.cert_text = cert_text
        self.document = None
        self.assertions = []
        self.validate_time = validate_time
        self.validate_audience = validate_audience
        self.expected_audience = expected_audience
        self.validate_destination = validate_destination
        self.expected_destination = expected_destination
        self.time_tolerance = time_tolerance
        self._processed_assertion_ids = set()
        
    def parse(self):
        try:
            decoded = base64.b64decode(self.saml_response_b64)
            parser = etree.XMLParser(resolve_entities=False, no_network=True)
            self.document = etree.fromstring(decoded, parser)
            return True
        except Exception as e:
            return False
    
    def validate_signature(self):
        if self.document is None:
            return False
            
        validator = SignatureValidator(
            self.document, 
            self.cert_text,
            validate_time=self.validate_time,
            validate_audience=self.validate_audience,
            expected_audience=self.expected_audience,
            validate_destination=self.validate_destination,
            expected_destination=self.expected_destination,
            time_tolerance=self.time_tolerance
        )
        return validator.validate()
    
    def get_nameid(self):
        if self.document is None:
            return None
            
        assertions = self.document.xpath(
            '//saml:Assertion',
            namespaces=self.NAMESPACES
        )
        
        if not assertions:
            return None
        
        assertion = assertions[0]
        nameid_nodes = assertion.xpath(
            './/saml:NameID',
            namespaces=self.NAMESPACES
        )
        
        if nameid_nodes:
            return nameid_nodes[0].text
        
        return None
    
    def get_attributes(self):
        if self.document is None:
            return {}
            
        attributes = {}
        
        assertions = self.document.xpath(
            '//saml:Assertion',
            namespaces=self.NAMESPACES
        )
        
        if not assertions:
            return attributes
        
        assertion = assertions[0]
        attr_nodes = assertion.xpath(
            './/saml:Attribute',
            namespaces=self.NAMESPACES
        )
        
        for attr in attr_nodes:
            name = attr.get('Name')
            values = attr.xpath(
                './/saml:AttributeValue',
                namespaces=self.NAMESPACES
            )
            if values:
                attributes[name] = values[0].text
        
        return attributes
    
    def is_valid(self):
        if not self.parse():
            return False
        
        if not self.validate_signature():
            return False
        
        if not self._check_assertion_uniqueness():
            return False
        
        return True
    
    def _check_assertion_uniqueness(self):
        try:
            assertion_nodes = self.document.xpath(
                '//saml:Assertion',
                namespaces=self.NAMESPACES
            )
            
            for assertion in assertion_nodes:
                assertion_id = assertion.get('ID')
                if assertion_id:
                    if assertion_id in self._processed_assertion_ids:
                        return False
                    self._processed_assertion_ids.add(assertion_id)
            
            return True
        except Exception:
            return False
    
    def get_assertion_ids(self):
        try:
            assertion_nodes = self.document.xpath(
                '//saml:Assertion',
                namespaces=self.NAMESPACES
            )
            
            ids = []
            for assertion in assertion_nodes:
                assertion_id = assertion.get('ID')
                if assertion_id:
                    ids.append(assertion_id)
            
            return ids
        except Exception:
            return []
