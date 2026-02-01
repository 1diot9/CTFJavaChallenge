class User:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, username, email, role='user', **kwargs):
        self.username = username
        self.email = email
        self.role = role
        self.metadata = kwargs
    
    def to_dict(self):
        return {
            'username': self.username,
            'email': self.email,
            'role': self.role
        }

class UserPreferences:
    schema_version = "1.0"
    serializable = True
    
    def __init__(self, theme='light', notifications=True, developer_mode=False, **kwargs):
        self.theme = theme
        self.notifications = notifications
        self.developer_mode = developer_mode
        self.custom = kwargs
    
    def to_dict(self):
        return {
            'theme': self.theme,
            'notifications': self.notifications,
            'developer_mode': self.developer_mode
        }   