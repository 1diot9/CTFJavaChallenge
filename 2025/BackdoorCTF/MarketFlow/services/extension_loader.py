import secrets
from datetime import datetime
from models.database import get_db

class ExtensionManifest:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, name, version, handlers=None, validators=None, formatters=None, **kwargs):
        self.name = name
        self.version = version
        self.handlers = handlers or {}
        self.validators = validators or {}
        self.formatters = formatters or {}
        self.metadata = kwargs
    
    def register(self):
        from services.object_manager import CLASS_REGISTRY
        
        for handler_name, handler_class in self.handlers.items():
            if hasattr(handler_class, '__name__'):
                handler_class.schema_version = "1.0"
                handler_class.serializable = True
                CLASS_REGISTRY[handler_name] = handler_class
        
        for validator_name, validator_class in self.validators.items():
            if hasattr(validator_class, '__name__'):
                validator_class.schema_version = "1.0"
                validator_class.serializable = True
                CLASS_REGISTRY[validator_name] = validator_class
        
        for formatter_name, formatter_class in self.formatters.items():
            if hasattr(formatter_class, '__name__'):
                formatter_class.schema_version = "1.0"
                formatter_class.serializable = True
                CLASS_REGISTRY[formatter_name] = formatter_class
    
    def to_dict(self):
        return {
            'name': self.name,
            'version': self.version
        }

class ExtensionLoader:
    def __init__(self):
        self.loaded_extensions = {}
    
    def load_extension(self, manifest):
        if not isinstance(manifest, ExtensionManifest):
            raise ValueError("Invalid extension manifest")
        
        manifest.register()
        
        self.loaded_extensions[manifest.name] = manifest
        
        db = get_db()
        ext_id = secrets.token_hex(16)
        db.execute(
            "INSERT INTO extensions (id, name, manifest, loaded_at) VALUES (?, ?, ?, ?)",
            [ext_id, manifest.name, str(manifest.to_dict()), datetime.now().isoformat()]
        )
        db.commit()
        
        return ext_id
    
    def get_extension(self, name):
        return self.loaded_extensions.get(name)