class Analytics:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, event_type, properties=None, **kwargs):
        self.event_type = event_type
        self.properties = properties or {}
        self.metadata = kwargs
    
    def to_dict(self):
        return {
            'event_type': self.event_type,
            'properties': self.properties
        }

class ReportConfiguration:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, report_type, date_range, metrics=None, dimensions=None, processor=None, **kwargs):
        self.report_type = report_type
        self.date_range = date_range
        self.metrics = metrics or []
        self.dimensions = dimensions or []
        self.processor = processor
        self.output_format = kwargs.get('output_format', 'json')
        self.template = kwargs.get('template')
        
        if isinstance(self.processor, dict) and '_type' in self.processor:
            from services.object_manager import ObjectManager
            manager = ObjectManager()
            self.processor = manager.deserialize(self.processor)
    
    def to_dict(self):
        return {
            'report_type': self.report_type,
            'date_range': self.date_range,
            'metrics': self.metrics,
            'dimensions': self.dimensions
        }

class MetricAggregator:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, metric_name, aggregation_type='sum', **kwargs):
        self.metric_name = metric_name
        self.aggregation_type = aggregation_type
        self.options = kwargs
    
    def to_dict(self):
        return {
            'metric_name': self.metric_name,
            'aggregation_type': self.aggregation_type
        }

class AnalyticsProcessor:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, data_source, aggregation_rules=None, output_config=None, **kwargs):
        self.data_source = data_source
        self.aggregation_rules = aggregation_rules or []
        self.output_config = output_config
        self.options = kwargs
        
        if isinstance(self.output_config, dict) and '_type' in self.output_config:
            from services.object_manager import ObjectManager
            manager = ObjectManager()
            self.output_config = manager.deserialize(self.output_config)
    
    def to_dict(self):
        return {
            'data_source': self.data_source,
            'aggregation_rules': self.aggregation_rules
        }