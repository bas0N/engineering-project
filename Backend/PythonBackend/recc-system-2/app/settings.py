import os

MONGO_URI =os.environ.get('MONGO_URI', 'default_mongo_uri')
MONGO_DB_NAME = os.environ.get('MONGO_DB_NAME', 'dev')
MONGO_COLLECTION_NAME = os.environ.get('MONGO_COLLECTION_NAME', 'reviews_Health_and_Personal_Care')
MYSQL_CONNECTION_STRING = os.environ.get('MYSQL_CONNECTION_STRING', 'mysql+mysqlconnector://user:password@localhost:3306/database')
