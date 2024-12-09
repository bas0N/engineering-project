from sqlalchemy import create_engine
from app.config.db_config import Config

def get_mysql_engine():
    print(Config.MYSQL_CONNECTION_STRING)
    engine = create_engine(Config.MYSQL_CONNECTION_STRING)
    return engine
