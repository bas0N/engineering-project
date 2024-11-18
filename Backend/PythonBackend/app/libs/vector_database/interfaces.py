from abc import ABC, abstractmethod
from typing import List, Dict, Any
import pandas as pd

class VectorDBIntegration(ABC):
    """Interface for integrating with a vector database."""

    @abstractmethod
    def save_dict(self, data: Dict[str, Any]) -> None:
        """Saves a single dictionary to the vector database.

        Args:
            data (Dict[str, Any]): The dictionary containing data to save.
        """
        pass

    @abstractmethod
    def save_dicts(self, data_list: List[Dict[str, Any]]) -> None:
        """Saves data from a list of dictionaries to the vector database in batches.

        Args:
            data_list (List[Dict[str, Any]]): The list of dictionaries containing data to save.
        """
        pass

    @abstractmethod
    def fetch_similar_products(self, embedding: List[float], n: int = 5):
        """Fetches top `n` similar products given an embedding.

        Args:
            embedding (List[float]): The embedding vector to search for similar products.
            n (int): The number of similar products to retrieve.

        Returns:
            List containing the similar products with metadata and distances.
        """
        pass
