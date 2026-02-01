import os
import json
from datetime import datetime

CACHE_STORAGE = {}

class CacheConfiguration:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, cache_key, objects=None, ttl=3600, persistence=None, **kwargs):
        self.cache_key = cache_key
        self.objects = objects or []
        self.ttl = ttl
        self.persistence = persistence
        self.options = kwargs
        
        if isinstance(self.persistence, dict) and '_type' in self.persistence:
            from services.object_manager import ObjectManager
            manager = ObjectManager()
            self.persistence = manager.deserialize(self.persistence)
    
    def to_dict(self):
        return {
            'cache_key': self.cache_key,
            'ttl': self.ttl
        }

class PersistenceAdapter:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, storage_path, mode='append', **kwargs):
        self.storage_path = storage_path
        self.mode = mode
        self.options = kwargs
    
    def write(self, data):
        full_path = os.path.join('/var/tmp/sessionmaze/cache', self.storage_path)
        os.makedirs(os.path.dirname(full_path), exist_ok=True)
        
        write_mode = 'a' if self.mode == 'append' else 'w'
        
        items_to_write = data
        if not isinstance(data, list):
            items_to_write = [data]
            
        with open(full_path, write_mode) as f:
            for item in items_to_write:
                if isinstance(item, dict) or isinstance(item, list):
                    f.write(json.dumps(item) + '\n')
                else:
                    f.write(str(item) + '\n')
    
    def to_dict(self):
        return {
            'storage_path': self.storage_path,
            'mode': self.mode
        }

class CacheService:
    def __init__(self):
        self.storage = CACHE_STORAGE
    
    def prime(self, config):
        from services.object_manager import ObjectManager
        manager = ObjectManager()
        
        if not isinstance(config, CacheConfiguration):
            raise ValueError("Invalid cache configuration")
        
        cached_items = []
        for obj_data in config.objects:
            if isinstance(obj_data, dict) and '_type' in obj_data:
                obj = manager.deserialize(obj_data)
                cached_items.append(obj)
            else:
                cached_items.append(obj_data)
        
        self.storage[config.cache_key] = {
            'items': cached_items,
            'timestamp': datetime.now().isoformat(),
            'ttl': config.ttl
        }
        
        if config.persistence and isinstance(config.persistence, PersistenceAdapter):
            config.persistence.write(cached_items)
    
    def get(self, key):
        return self.storage.get(key)
    
    def invalidate(self, key):
        if key in self.storage:
            del self.storage[key]