from abc import ABC, abstractmethod
from typing import List, Dict, Any

class NoSqlDatabaseIntegrationInterface(ABC):
    @abstractmethod
    def get_all_config_docs(self) -> List[Dict[str, Any]]:
        """Retrieve all configuration documents."""
        pass

    @abstractmethod
    def update_config_doc(self, filter_query: Dict[str, Any], update_data: Dict[str, Any]) -> int:
        """Update configuration documents based on a filter and return the number of modified documents."""
        pass

    @abstractmethod
    def get_all_products(self) -> List[Dict[str, Any]]:
        """Retrieve all product documents."""
        pass

    @abstractmethod
    def collection_exists(self, collection_name: str) -> bool:
        """Check the existence of a given collection."""
        pass

    @classmethod
    @abstractmethod
    def get_instance(cls, db_name: str) -> 'DatabaseIntegrationInterface':
        """Get the singleton instance without passing the URI."""
        pass
