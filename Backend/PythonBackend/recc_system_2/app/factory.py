from dotenv import load_dotenv
from flask import Flask
from recc_system_2.app.routes.main import bp
from recc_system_2.app.database.mongo import get_mongo_collection
from recc_system_2.app.database.mysql import get_mysql_engine
from recc_system_2.app.data.loader import load_n_products
from recc_system_2.app.svd.trainer import train_svd
from recc_system_2.app.factors.saver import save_latent_factors_to_db
from pymongo import MongoClient
import certifi
from recc_system_2.app.config.db_config import Config
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
        main_collection = get_mongo_collection(Config.MONGO_COLLECTION_NAME_REVIEWS)  # from mongo.py, gets the main reviews collection
        reviews = load_n_products(main_collection, n=Config.NUM_PRODUCTS)

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
