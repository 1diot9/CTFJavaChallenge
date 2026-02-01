class Content:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, title, body, content_type='text', **kwargs):
        self.title = title
        self.body = body
        self.content_type = content_type
        self.metadata = kwargs
    
    def to_dict(self):
        return {
            'title': self.title,
            'content_type': self.content_type
        }

class ContentMetadata:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, tags=None, category=None, author=None, **kwargs):
        self.tags = tags or []
        self.category = category
        self.author = author
        self.extra = kwargs
    
    def to_dict(self):
        return {
            'tags': self.tags,
            'category': self.category,
            'author': self.author
        }

class TemplateSpecification:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, template_name, variables=None, output_path=None, **kwargs):
        self.template_name = template_name
        self.variables = variables or {}
        self.output_path = output_path
        self.options = kwargs
    
    def to_dict(self):
        return {
            'template_name': self.template_name,
            'output_path': self.output_path
        }