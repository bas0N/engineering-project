from flask import Flask
from app.routes.main import bp
from app.database.mongo import get_mongo_collection
from app.database.mysql import get_mysql_engine
from app.data.loader import load_n_products
from app.svd.trainer import train_svd
from app.factors.saver import save_latent_factors_to_db
from pymongo import MongoClient
import certifi
from app.config.db_config import Config
import pandas as pd

def init_recommendation_system():
    # Connect to MongoDB
    client = MongoClient(Config.MONGO_URI, tlsCAFile=certifi.where())
    db = client[Config.MONGO_DB_NAME]
    config_collection = db['config']

    # Find the config doc for collection_name: recc_system_2
    config_doc = config_collection.find_one({"collection_name": "recc_system_2"})

    if config_doc and config_doc.get("should_run", False):
        # should_run is True, so we execute the logic

        # 1. Load reviews data
        main_collection = get_mongo_collection()  # from mongo.py, gets the main reviews collection
        reviews = load_n_products(main_collection, n=1000)

        # 2. Train SVD
        user_factors, item_factors = train_svd(reviews, n_components=30)

        # 3. Save factors to MySQL
        engine = get_mysql_engine()
        save_latent_factors_to_db(engine, reviews, user_factors, item_factors)

        # (Optional) Update the config to prevent reruns
        config_collection.update_one(
            {"_id": config_doc["_id"]},
            {"$set": {"should_run": False}}
        )

def create_app():
    app = Flask(__name__)
    app.register_blueprint(bp)

    # Run initialization logic after app is created
    init_recommendation_system()

    return app
