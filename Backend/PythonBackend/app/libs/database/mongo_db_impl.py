from typing import List, Dict, Any, Optional
from pymongo import MongoClient, errors
from pymongo.collection import Collection
from pymongo.results import UpdateResult
from threading import Lock
from .mongo_db_impl_types import ConfigDocument, ProductDocument
from .interfaces import NoSqlDatabaseIntegrationInterface

class MongoDBIntegration(NoSqlDatabaseIntegrationInterface):
    _client_instance: Optional[MongoClient] = None
    _instance: Optional['MongoDBIntegration'] = None
    _lock = Lock()  # For thread safety

    def __new__(cls, db_uri: Optional[str] = None, db_name: Optional[str] = None):
        if cls._instance is None:
            with cls._lock:
                if cls._instance is None:
                    cls._instance = super(MongoDBIntegration, cls).__new__(cls)
                    if cls._client_instance is None:
                        if db_uri is None:
                            raise ValueError("db_uri must be provided during the first initialization.")
                        try:
                            cls._client_instance = MongoClient(db_uri)
                            cls._client_instance.admin.command('ping')  # Force connection check
                        except errors.ConnectionFailure as e:
                            raise ConnectionError(f"Failed to connect to MongoDB: {e}")
        return cls._instance

    @classmethod
    def get_instance(cls, db_uri: Optional[str] = None, db_name: Optional[str] = None) -> 'MongoDBIntegration':
        """Get or create the singleton instance with custom database details."""
        if cls._instance is None:
            if db_uri is None or db_name is None:
                raise ValueError("Both db_uri and db_name must be provided for the first instantiation.")
            cls._instance = cls(db_uri=db_uri, db_name=db_name)
        return cls._instance

    def __init__(self, db_uri: str, db_name: str):
        if not hasattr(self, 'initialized'):
            self.db = self._client_instance[db_name]
            self.config_collection: Collection = self.db['configs']
            self.product_collection: Collection = self.db['products']
            self.initialized = True

    def get_all_config_docs(self) -> List[ConfigDocument]:
        """Fetch all configuration documents."""
        return list(self.config_collection.find({}))

    def update_config_doc(self, filter_query: Dict[str, Any], update_data: Dict[str, Any]) -> int:
        """Update configuration documents based on a filter and return the number of modified documents."""
        result: UpdateResult = self.config_collection.update_many(filter_query, {"$set": update_data})
        return result.modified_count

    def get_all_products(self) -> List[ProductDocument]:
        """Fetch all product documents."""
        return list(self.product_collection.find({}))

    def collection_exists(self, collection_name: str) -> bool:
        """Check the existence of a given collection."""
        return collection_name in self.db.list_collection_names()
