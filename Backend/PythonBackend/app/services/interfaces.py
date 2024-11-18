from abc import ABC, abstractmethod
from typing import List, Dict, Any
import numpy as np
import pandas as pd

class DataProcessorInterface(ABC):
    """Interface for a data processor that handles cleaning, normalization, embedding, and database operations."""

    @abstractmethod
    def add_single_dict(self, data: Dict[str, Any]) -> None:
        """
        Cleans, normalizes, embeds, and adds a single dictionary to the vector database.

        Args:
            data (Dict[str, Any]): The dictionary to be processed and added.
        """
        pass

    @abstractmethod
    def add_list_of_dicts(self, data_list: List[Dict[str, Any]]) -> None:
        """
        Cleans, normalizes, embeds, and adds a list of dictionaries to the vector database.

        Args:
            data_list (List[Dict[str, Any]]): The list of dictionaries to be processed and added.
        """
        pass

    @abstractmethod
    def fetch_similar_products(self, embedding: List[float], n: int = 5) -> pd.DataFrame:
        """
        Fetches top `n` similar products given an embedding.

        Args:
            embedding (List[float]): The embedding vector to search for similar products.
            n (int): The number of similar products to retrieve.

        Returns:
            pd.DataFrame: DataFrame containing the similar products with metadata and distances.
        """
        pass

    @abstractmethod
    def initialise_recommendation_system(self) -> List[Dict[str, Any]]:
        """
        Scans the configs and fetches all products accordingly.

        Returns:
            List[Dict[str, Any]]: List of dictionaries representing the products.
        """
        pass