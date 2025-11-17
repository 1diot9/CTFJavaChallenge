import os
import json


class SAMLConfig:
    
    def __init__(self, config_file='idp_config.json'):
        self.config_file = config_file
        self.idp_entity_id = None
        self.idp_sso_url = None
        self.idp_sso_url_post = None
        self.idp_slo_url = None
        self.idp_slo_url_post = None
        self.idp_certificates = []
        self.name_id_formats = []
        
        self.sp_entity_id = os.getenv('SP_ENTITY_ID', 'http://auth-flag.rctf.rois.team:26000/')
        self.sp_acs_url = os.getenv('SP_ACS_URL', 'http://auth-flag.rctf.rois.team:26000/saml/acs')
        self.sp_slo_url = os.getenv('SP_SLO_URL', 'http://auth-flag.rctf.rois.team:26000/saml/slo')
        
        self.validate_time = os.getenv('SAML_VALIDATE_TIME', 'true').lower() == 'true'
        self.validate_audience = os.getenv('SAML_VALIDATE_AUDIENCE', 'true').lower() == 'true'
        self.validate_destination = os.getenv('SAML_VALIDATE_DESTINATION', 'true').lower() == 'true'
        self.time_tolerance = int(os.getenv('SAML_TIME_TOLERANCE', '300'))

        if os.path.exists(self.config_file):
            with open(self.config_file, 'r', encoding='utf-8') as f:
                idp_config = json.load(f)

            self.idp_entity_id = idp_config.get('entity_id')
            self.idp_sso_url = idp_config.get('sso_url')
            self.idp_sso_url_post = idp_config.get('sso_url_post')
            self.idp_slo_url = idp_config.get('slo_url')
            self.idp_slo_url_post = idp_config.get('slo_url_post')
            self.idp_certificates = idp_config.get('certificates', [])
            self.name_id_formats = idp_config.get('name_id_formats', [])
    

    def get_idp_cert(self):
        if self.idp_certificates:
            return self.idp_certificates[0]
        return None
    
    def get_sso_url(self, binding='redirect'):
        if binding.lower() == 'post' and self.idp_sso_url_post:
            return self.idp_sso_url_post
        return self.idp_sso_url
    
    def get_slo_url(self, binding='redirect'):
        if binding.lower() == 'post' and self.idp_slo_url_post:
            return self.idp_slo_url_post
        return self.idp_slo_url


saml_config = SAMLConfig()
