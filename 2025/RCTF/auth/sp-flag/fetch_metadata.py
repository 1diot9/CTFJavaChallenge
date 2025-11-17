#!/usr/bin/env python3
import os
import json
from saml2 import MetadataParser


def fetch_and_save_metadata():
    metadata_url = os.getenv('IDP_METADATA_URL')
    
    if not metadata_url:
        print("Error: Environment variable IDP_METADATA_URL not set")
        return False
    
    try:
        parser = MetadataParser(metadata_url=metadata_url)
        
        if not parser.load():
            print("Error: Failed to load metadata")
            return False
        
        print("Metadata fetched successfully")
        
        config = parser.to_dict()
        
        output_file = 'idp_config.json'
        
        config['metadata_url'] = metadata_url
        
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(config, f, indent=2, ensure_ascii=False)

        return True
        
    except Exception as e:
        print(f"Error: {e}")
        return False


if __name__ == '__main__':
    import sys
    success = fetch_and_save_metadata()
    sys.exit(0 if success else 1)
