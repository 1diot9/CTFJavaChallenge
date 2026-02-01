class Campaign:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, name, target_audience, budget=0, **kwargs):
        self.name = name
        self.target_audience = target_audience
        self.budget = budget
        self.status = 'draft'
        self.metadata = kwargs
    
    def to_dict(self):
        return {
            'name': self.name,
            'target_audience': self.target_audience,
            'budget': self.budget,
            'status': self.status
        }

class CampaignSchedule:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, start_date, end_date, frequency='daily', **kwargs):
        self.start_date = start_date
        self.end_date = end_date
        self.frequency = frequency
        self.options = kwargs
    
    def to_dict(self):
        return {
            'start_date': self.start_date,
            'end_date': self.end_date,
            'frequency': self.frequency
        }

class TargetAudience:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, demographics, interests, locations=None, **kwargs):
        self.demographics = demographics
        self.interests = interests
        self.locations = locations or []
        self.filters = kwargs
    
    def to_dict(self):
        return {
            'demographics': self.demographics,
            'interests': self.interests,
            'locations': self.locations
        }