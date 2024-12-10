import os
from dotenv import load_dotenv

load_dotenv()

MONGO_URI=os.getenv('MONGO_URI', 'default_mongo_uri')
print("MONGO_URI: ", MONGO_URI)
MONGO_DB_NAME = os.getenv('MONGO_DB', 'dev')
MONGO_COLLECTION_NAME_REVIEWS = os.getenv('MONGO_COLLECTION_NAME', 'reviews_Health_and_Personal_Care')
MONGO_COLLECTION_NAME_PRODUCTS = os.getenv('MONGO_COLLECTION_NAME', 'meta_Health_and_Personal_Care')
MYSQL_CONNECTION_STRING = os.getenv('MYSQL_CONNECTION_STRING', 'mysql+pymysql://myuser:mypassword@localhost:3306/mydatabase')
NUM_PRODUCTS = int(os.getenv('NUM_PRODUCTS', 1000))