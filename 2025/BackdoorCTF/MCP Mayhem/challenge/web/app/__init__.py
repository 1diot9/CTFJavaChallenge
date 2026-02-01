from flask import Flask
from app.auth.routes import auth_bp
from app.client.routes import client_bp

def create_app():
    app = Flask(__name__, subdomain_matching=True)
    app.config.from_object('config.Config')

    app.register_blueprint(auth_bp)
    app.register_blueprint(client_bp)

    return app