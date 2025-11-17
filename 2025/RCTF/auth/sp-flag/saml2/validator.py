from lxml import etree
from cryptography import x509
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import padding
import base64
import copy
from datetime import datetime, timedelta


class SignatureValidator:
    
    NAMESPACES = {
        'samlp': 'urn:oasis:names:tc:SAML:2.0:protocol',
        'saml': 'urn:oasis:names:tc:SAML:2.0:assertion',
        'ds': 'http://www.w3.org/2000/09/xmldsig#'
    }
    
    def __init__(self, document, cert_text, 
                 validate_time=True, 
                 validate_audience=False, 
                 expected_audience=None,
                 validate_destination=False,
                 expected_destination=None,
                 time_tolerance=300):
        self.document = document
        self.cert_text = cert_text
        self.public_key = None
        self.validate_time = validate_time
        self.validate_audience = validate_audience
        self.expected_audience = expected_audience
        self.validate_destination = validate_destination
        self.expected_destination = expected_destination
        self.time_tolerance = time_tolerance
        self._load_certificate()
    
    def _load_certificate(self):
        try:
            cert_data = self.cert_text.replace('-----BEGIN CERTIFICATE-----', '')
            cert_data = cert_data.replace('-----END CERTIFICATE-----', '')
            cert_data = cert_data.replace('\n', '').replace('\r', '')
            cert_bytes = base64.b64decode(cert_data)
            cert = x509.load_der_x509_certificate(cert_bytes, default_backend())
            self.public_key = cert.public_key()
        except Exception as e:
            pass
    
    def validate(self):
        if self.public_key is None:
            return False
        
        response_signature = self._find_response_signature()
        if response_signature is not None:
            if not self._verify_signature(response_signature):
                return False
        else:
            assertion_signatures = self._find_assertion_signatures()
            if not assertion_signatures:
                return False
            
            for sig_node in assertion_signatures:
                if not self._verify_signature(sig_node):
                    return False
        
        if self.validate_time:
            if not self._validate_time_conditions():
                return False
        
        if self.validate_audience:
            if not self._validate_audience_restriction():
                return False
        
        if self.validate_destination:
            if not self._validate_destination():
                return False
        
        return True
    
    def _find_response_signature(self):
        try:
            response_nodes = self.document.xpath(
                '//samlp:Response',
                namespaces=self.NAMESPACES
            )
            
            if not response_nodes:
                return None
            
            response = response_nodes[0]
            
            signature_nodes = response.xpath(
                './ds:Signature',
                namespaces=self.NAMESPACES
            )
            
            if signature_nodes:
                return signature_nodes[0]
            
            return None
        except Exception:
            return None
    
    def _find_assertion_signatures(self):
        try:
            assertion_nodes = self.document.xpath(
                '//saml:Assertion',
                namespaces=self.NAMESPACES
            )
            
            if not assertion_nodes:
                return []
            
            signatures = []
            for assertion in assertion_nodes:
                sig_nodes = assertion.xpath(
                    './ds:Signature',
                    namespaces=self.NAMESPACES
                )
                signatures.extend(sig_nodes)
            
            return signatures
        except Exception:
            return []
    
    def _canonicalize(self, element):
        try:
            c14n_bytes = etree.tostring(
                element,
                method='c14n',
                exclusive=True,
                with_comments=False
            )
            return c14n_bytes
        except Exception as e:
            try:
                c14n_bytes = etree.tostring(
                    element,
                    method='c14n',
                    with_comments=False
                )
                return c14n_bytes
            except Exception:
                return None
    
    def _get_digest_method(self, reference_node):
        try:
            digest_method_node = reference_node.xpath(
                './ds:DigestMethod',
                namespaces=self.NAMESPACES
            )
            if digest_method_node:
                algorithm = digest_method_node[0].get('Algorithm', '')
                if 'sha256' in algorithm.lower() or 'sha-256' in algorithm.lower():
                    return hashes.SHA256()
                elif 'sha1' in algorithm.lower() or 'sha-1' in algorithm.lower():
                    return hashes.SHA1()
                elif 'sha512' in algorithm.lower() or 'sha-512' in algorithm.lower():
                    return hashes.SHA512()
            return hashes.SHA256()
        except Exception:
            return hashes.SHA256()
    
    def _get_signature_method(self, signed_info_node):
        try:
            signature_method_node = signed_info_node.xpath(
                './ds:SignatureMethod',
                namespaces=self.NAMESPACES
            )
            if signature_method_node:
                algorithm = signature_method_node[0].get('Algorithm', '')
                if 'sha256' in algorithm.lower() or 'sha-256' in algorithm.lower():
                    return hashes.SHA256()
                elif 'sha1' in algorithm.lower() or 'sha-1' in algorithm.lower():
                    return hashes.SHA1()
                elif 'sha512' in algorithm.lower() or 'sha-512' in algorithm.lower():
                    return hashes.SHA512()
            return hashes.SHA256()
        except Exception:
            return hashes.SHA256()
    
    def _apply_transforms(self, element, transforms_nodes):
        try:
            element_copy = copy.deepcopy(element)
            
            for transform in transforms_nodes:
                algorithm = transform.get('Algorithm', '')
                
                if 'enveloped-signature' in algorithm:
                    sig_nodes = element_copy.xpath(
                        './/*[local-name()="Signature" and namespace-uri()="http://www.w3.org/2000/09/xmldsig#"]'
                    )
                    for sig in sig_nodes:
                        parent = sig.getparent()
                        if parent is not None:
                            parent.remove(sig)
            
            return element_copy
        except Exception as e:
            return element
    
    def _calculate_digest(self, element, hash_algorithm):
        try:
            from cryptography.hazmat.primitives.hashes import Hash
            
            c14n_bytes = self._canonicalize(element)
            if c14n_bytes is None:
                return None
            
            digest = Hash(hash_algorithm, backend=default_backend())
            digest.update(c14n_bytes)
            digest_value = digest.finalize()
            
            return digest_value
        except Exception as e:
            return None
    
    def _verify_reference(self, reference_node, signature_node):
        try:
            uri = reference_node.get('URI')
            if not uri:
                return False
            
            ref_id = uri[1:] if uri.startswith('#') else uri
            
            signed_elements = self.document.xpath(
                f'//*[@ID="{ref_id}"]'
            )
            
            if not signed_elements:
                return False
            
            signed_element = signed_elements[0]
            
            transforms_nodes = reference_node.xpath(
                './ds:Transforms/ds:Transform',
                namespaces=self.NAMESPACES
            )
            
            transformed_element = self._apply_transforms(signed_element, transforms_nodes)
            
            digest_hash = self._get_digest_method(reference_node)
            
            calculated_digest = self._calculate_digest(transformed_element, digest_hash)
            if calculated_digest is None:
                return False
            
            digest_value_nodes = reference_node.xpath(
                './ds:DigestValue',
                namespaces=self.NAMESPACES
            )
            
            if not digest_value_nodes:
                return False
            
            expected_digest = base64.b64decode(digest_value_nodes[0].text.strip())
            
            return calculated_digest == expected_digest
            
        except Exception as e:
            return False
    
    def _verify_signature(self, signature_node):
        try:
            signed_info_nodes = signature_node.xpath(
                './ds:SignedInfo',
                namespaces=self.NAMESPACES
            )
            
            if not signed_info_nodes:
                return False
            
            signed_info = signed_info_nodes[0]
            
            reference_nodes = signed_info.xpath(
                './ds:Reference',
                namespaces=self.NAMESPACES
            )
            
            if not reference_nodes:
                return False
            
            for reference_node in reference_nodes:
                if not self._verify_reference(reference_node, signature_node):
                    return False
            
            signed_info_c14n = self._canonicalize(signed_info)
            if signed_info_c14n is None:
                return False
            
            hash_algorithm = self._get_signature_method(signed_info)
            
            sig_value_nodes = signature_node.xpath(
                './ds:SignatureValue',
                namespaces=self.NAMESPACES
            )
            
            if not sig_value_nodes:
                return False
            
            signature_value_text = sig_value_nodes[0].text
            if not signature_value_text:
                return False
            
            signature_value = base64.b64decode(signature_value_text.strip())
            
            try:
                self.public_key.verify(
                    signature_value,
                    signed_info_c14n,
                    padding.PKCS1v15(),
                    hash_algorithm
                )
                return True
            except Exception as e:
                return False
                
        except Exception as e:
            return False
    
    def _validate_time_conditions(self):
        try:
            conditions_nodes = self.document.xpath(
                '//saml:Conditions',
                namespaces=self.NAMESPACES
            )
            
            if not conditions_nodes:
                return True
            
            now = datetime.utcnow()
            tolerance = timedelta(seconds=self.time_tolerance)
            
            for conditions in conditions_nodes:
                not_before = conditions.get('NotBefore')
                if not_before:
                    not_before_time = self._parse_time(not_before)
                    if not_before_time and now < (not_before_time - tolerance):
                        return False
                
                not_on_or_after = conditions.get('NotOnOrAfter')
                if not_on_or_after:
                    not_on_or_after_time = self._parse_time(not_on_or_after)
                    if not_on_or_after_time and now > (not_on_or_after_time + tolerance):
                        return False
            
            confirmation_data_nodes = self.document.xpath(
                '//saml:SubjectConfirmationData',
                namespaces=self.NAMESPACES
            )
            
            for confirmation_data in confirmation_data_nodes:
                not_on_or_after = confirmation_data.get('NotOnOrAfter')
                if not_on_or_after:
                    not_on_or_after_time = self._parse_time(not_on_or_after)
                    if not_on_or_after_time and now > (not_on_or_after_time + tolerance):
                        return False
            
            return True
        except Exception:
            return False
    
    def _validate_audience_restriction(self):
        """
        """
        try:
            if not self.expected_audience:
                return True
            
            audience_nodes = self.document.xpath(
                '//saml:Audience',
                namespaces=self.NAMESPACES
            )
            
            if not audience_nodes:
                return False
            
            for audience in audience_nodes:
                if audience.text and audience.text.strip() == self.expected_audience:
                    return True
            
            return False
        except Exception:
            return False
    
    def _validate_destination(self):
        """
        """
        try:
            if not self.expected_destination:
                return True
            
            response_nodes = self.document.xpath(
                '//samlp:Response',
                namespaces=self.NAMESPACES
            )
            
            for response in response_nodes:
                destination = response.get('Destination')
                if destination and destination != self.expected_destination:
                    return False
            
            confirmation_data_nodes = self.document.xpath(
                '//saml:SubjectConfirmationData',
                namespaces=self.NAMESPACES
            )
            
            for confirmation_data in confirmation_data_nodes:
                recipient = confirmation_data.get('Recipient')
                if recipient and recipient != self.expected_destination:
                    return False
            
            return True
        except Exception:
            return False
    
    def _parse_time(self, time_str):
        """
        """
        try:
            formats = [
                '%Y-%m-%dT%H:%M:%S.%fZ',  # 2025-11-14T20:56:00.965Z
                '%Y-%m-%dT%H:%M:%SZ',     # 2025-11-14T20:56:00Z
                '%Y-%m-%dT%H:%M:%S',      # 2025-11-14T20:56:00
            ]
            
            for fmt in formats:
                try:
                    return datetime.strptime(time_str, fmt)
                except ValueError:
                    continue
            
            return None
        except Exception:
            return None
    
    def get_assertion_ids(self):
        """
        """
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

