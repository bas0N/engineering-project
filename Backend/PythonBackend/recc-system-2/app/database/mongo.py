import certifi
from pymongo import MongoClient
from app.config.db_config import Config

def get_mongo_collection():
    client = MongoClient(Config.MONGO_URI, tlsCAFile=certifi.where())
    db = client[Config.MONGO_DB_NAME]
    return db[Config.MONGO_COLLECTION_NAME]
