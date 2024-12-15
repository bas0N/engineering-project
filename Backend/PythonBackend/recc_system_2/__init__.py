from dotenv import load_dotenv
from flask import Flask
from recc_system_2.app.routes.main import register_routes
from recc_system_2.app.factory import init_recommendation_system

def create_app():
    load_dotenv()
    app = Flask(__name__)

    # Run initialization logic after app is created
    init_recommendation_system()
    register_routes(app)
    print(app.url_map)

    return app
