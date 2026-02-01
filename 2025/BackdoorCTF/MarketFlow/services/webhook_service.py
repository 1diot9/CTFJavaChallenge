import re
import requests
from urllib.parse import urlparse

def is_safe_url(url):
    parsed = urlparse(url)
    host = parsed.hostname
    
    if not host:
        return False
    
    localhost_patterns = [
        r'^localhost$',
        r'^127\.',
        r'^0\.0\.0\.0$',
        r'^::1$',
        r'^::$',
        r'^0:0:0:0:0:0:0:1$'
    ]
    
    for pattern in localhost_patterns:
        if re.match(pattern, host, re.IGNORECASE):
            return False
    
    private_ranges = [
        r'^10\.',
        r'^172\.(1[6-9]|2[0-9]|3[01])\.',
        r'^192\.168\.',
        r'^169\.254\.',
        r'^fc00:',
        r'^fe80:'
    ]
    
    for pattern in private_ranges:
        if re.match(pattern, host, re.IGNORECASE):
            return False
    
    return True

class WebhookForwarder:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, target_url, method='POST', headers=None, **kwargs):
        self.target_url = target_url
        self.method = method.upper()
        self.headers = headers or {}
        self.options = kwargs
    
    def forward(self, data):
        if not is_safe_url(self.target_url):
            raise ValueError("URL not allowed: localhost or private IP detected")
        
        try:
            if self.method == 'GET':
                response = requests.get(
                    self.target_url,
                    headers=self.headers,
                    timeout=5
                )
            else:
                response = requests.post(
                    self.target_url,
                    json=data,
                    headers=self.headers,
                    timeout=5
                )
            
            return {
                'status_code': response.status_code,
                'body': response.text[:1000]
            }
        except Exception as e:
            return {
                'error': str(e)
            }
    
    def to_dict(self):
        return {
            'target_url': self.target_url,
            'method': self.method
        }