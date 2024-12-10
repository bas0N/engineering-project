from dotenv import load_dotenv
from flask import Flask
from recc_system_2.app.factory import init_recommendation_system
from recc_system_2.app.routes.main import bp

def create_app():
    load_dotenv()
    app = Flask(__name__)
    app.register_blueprint(bp)

    # Run initialization logic after app is created
    init_recommendation_system()

    return app
