import numpy as np
from typing import Dict, Any, Callable
from .interfaces import Embedding
class EmbeddingImpl(Embedding):
    """Implementation of the Embedding interface for generating weighted concatenated embeddings."""

    def __init__(self, embedding_model: Callable[[str], np.ndarray], weights: Dict[str, float]):
        """
        Initialize the embedding generator.

        Args:
            embedding_model (Callable[[str], np.ndarray]): Model function to generate embeddings for text data.
            weights (Dict[str, float]): Dictionary mapping columns to their weights (text or numeric).
        """
        self.embedding_model = embedding_model
        self.weights = weights

    def generate_embedding(self, data: Dict[str, Any]) -> np.ndarray:
        """
        Generate a weighted concatenated vector.

        Args:
            data (Dict[str, Any]): A dictionary representing the data.

        Returns:
            np.ndarray: A numpy array representing the weighted concatenated vector.
        """
        vector = []
        used_fields = []  # Collect fields used in the embedding
        field_shapes = {}  # Collect vector shapes for each field

        for column, weight in self.weights.items():
            if column in data:
                value = data[column]
                used_fields.append(column)  # Record the field being used

                # Process text columns
                if isinstance(value, str):
                    embedding = self.embedding_model(value)
                    weighted_embedding = weight * np.array(embedding)
                    vector.append(weighted_embedding)
                    field_shapes[column] = weighted_embedding.shape  # Record shape

                # Process numeric columns
                elif isinstance(value, (int, float)):
                    weighted_value = weight * value  # Assumes value is already normalized
                    vector.append([weighted_value])
                    field_shapes[column] = (1,)  # Numeric value shape

        # Concatenate all weighted components into a single vector
        concatenated_vector = np.concatenate(vector)

        # Print the fields used in the embedding and their shapes
        print(f"Fields used in embedding: {used_fields}")
        print(f"Vector shapes for each field: {field_shapes}")
        print(f"Final concatenated vector shape: {concatenated_vector.shape}")

        return concatenated_vector

