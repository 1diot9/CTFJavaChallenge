import os

class Config:
    DATABASE_PATH = os.environ.get('DB_PATH', '/var/tmp/sessionmaze/app.db')
    UPLOAD_FOLDER = '/var/tmp/sessionmaze/uploads'
    ASSETS_FOLDER = '/var/tmp/sessionmaze/assets'
    CACHE_FOLDER = '/var/tmp/sessionmaze/cache'
    TEMPLATE_FOLDER = '/var/tmp/sessionmaze/templates'
    MAX_CONTENT_LENGTH = 16 * 1024 * 1024
    SESSION_COOKIE_HTTPONLY = True
    SESSION_COOKIE_SAMESITE = 'Lax'
    PERMANENT_SESSION_LIFETIME = 3600
    
class DevelopmentConfig(Config):
    DEBUG = True
    
class ProductionConfig(Config):
    DEBUG = False