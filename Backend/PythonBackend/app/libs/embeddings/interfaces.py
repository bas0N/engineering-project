from abc import ABC, abstractmethod
import numpy as np
from typing import Dict, Any

class Embedding(ABC):
    """Interface for generating embeddings from a dictionary."""

    @abstractmethod
    def generate_embedding(self, data: Dict[str, Any]) -> np.ndarray:
        """
        Generate an embedding from a dictionary.

        Args:
            data (Dict[str, Any]): A dictionary representing the data.

        Returns:
            np.ndarray: A numpy array representing the embedding.
        """
        pass
