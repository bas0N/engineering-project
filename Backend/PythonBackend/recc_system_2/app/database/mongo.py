import certifi
from pymongo import MongoClient
from recc_system_2.app.config.db_config import Config

def get_mongo_collection(collection_name):
    client = MongoClient(Config.MONGO_URI, tlsCAFile=certifi.where())
    db = client[Config.MONGO_DB_NAME]
    return db[collection_name]
