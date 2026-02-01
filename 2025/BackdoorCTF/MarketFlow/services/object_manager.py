from models.user import User, UserPreferences
from models.campaign import Campaign, CampaignSchedule, TargetAudience
from models.analytics import Analytics, ReportConfiguration, MetricAggregator, AnalyticsProcessor
from models.content import Content, ContentMetadata, TemplateSpecification
from services.webhook_service import WebhookForwarder
from services.cache_service import CacheConfiguration, PersistenceAdapter # <-- ADD THIS IMPORT

CLASS_REGISTRY = {
    'User': User,
    'UserPreferences': UserPreferences,
    'Campaign': Campaign,
    'CampaignSchedule': CampaignSchedule,
    'TargetAudience': TargetAudience,
    'Analytics': Analytics,
    'ReportConfiguration': ReportConfiguration,
    'MetricAggregator': MetricAggregator,
    'AnalyticsProcessor': AnalyticsProcessor,
    'Content': Content,
    'ContentMetadata': ContentMetadata,
    'TemplateSpecification': TemplateSpecification,
    'WebhookForwarder': WebhookForwarder,
    'CacheConfiguration': CacheConfiguration, 
    'PersistenceAdapter': PersistenceAdapter 
}

class ObjectManager:
    def __init__(self):
        self.registry = CLASS_REGISTRY
    
    def deserialize(self, data):
        if not isinstance(data, dict):
            return data
        
        obj_type = data.get('_type')
        
        if not obj_type:
            return data
        
        if obj_type not in self.registry:
            raise ValueError(f"Unknown type: {obj_type}")
        
        klass = self.registry[obj_type]
        
        if not hasattr(klass, 'schema_version'):
            raise ValueError(f"Invalid class: {obj_type}")
        
        if not getattr(klass, 'serializable', False):
            raise ValueError(f"Type not serializable: {obj_type}")
        
        constructor_data = {k: v for k, v in data.items() if k != '_type'}
        
        instance = klass(**constructor_data)
        
        for key, value in data.items():
            if isinstance(value, dict) and '_type' in value:
                nested = self.deserialize(value)
                setattr(instance, key, nested)
        
        return instance
    
    def serialize(self, obj):
        if hasattr(obj, 'to_dict'):
            return obj.to_dict()
        return str(obj)