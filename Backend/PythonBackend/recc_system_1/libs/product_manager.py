from data_sanitizer import DataSanitizer
from embedding import WeightedConcatenatedEmbeddingGenerator
from vector_db import ChromaDBIntegration
from typing import Set, Optional, Union, Dict, List
import pandas as pd
import numpy as np

class ProductManager:
    """Manager class for product data, handling data sanitization, embedding, and database operations."""

    def __init__(self,
                 data_sanitizer: DataSanitizer,
                 vector_generator: WeightedConcatenatedEmbeddingGenerator,
                 db_integration: ChromaDBIntegration):
        self.data_sanitizer = data_sanitizer
        self.vector_generator = vector_generator
        self.db_integration = db_integration

    def load_data(self, file_paths: List[str], limit: int = 50000) -> pd.DataFrame:
        """Loads data from gzip files and combines them into a DataFrame."""
        dataframes = []
        for file_path in file_paths:
            if not os.path.exists(file_path):
                raise FileNotFoundError(f"File '{file_path}' not found.")
            try:
                df = load_data_as_dataframe(file_path, limit=limit)
                dataframes.append(df)
            except Exception as e:
                raise IOError(f"Could not read file '{file_path}': {e}")

        return pd.concat(dataframes, ignore_index=True)

    def sanitize_data(self, df: pd.DataFrame) -> pd.DataFrame:
        """Sanitizes the DataFrame using the DataSanitizer."""
        df_selected_columns=select_columns(df,["parent_asin", "title", "description", "main_category", "price"])
        return self.data_sanitizer.clean_dataframe(df_selected_columns)

    def add_embeddings_column(self, df: pd.DataFrame) -> pd.DataFrame:
        """Generates embeddings for each row in the DataFrame and adds them as a new column, with simple progress tracking."""
        total = len(df)
        embeddings = []

        for i, (_, row) in enumerate(df.iterrows(), 1):
            embedding = self.vector_generator.generate_embedding(row)
            embeddings.append(embedding)

            # Print progress percentage
            if i % 10 == 0 or i == total:  # Update every 10 rows or on the last row
                print(f"Progress: {i / total:.2%}")

        df['embedding'] = embeddings
        return df


    def save_to_db_in_batches(self, df: pd.DataFrame, batch_size: int = 100):
        """Saves the DataFrame to ChromaDB in batches."""
        for i in range(0, len(df), batch_size):
            batch = df.iloc[i:i + batch_size]
            self.db_integration.save_dataframe(batch)
        print(f"Total documents added: {len(df)}")

    def fetch_similar_products(self, embedding: List[float], n: int = 5) -> pd.DataFrame:
        """Fetches top `n` similar products given an embedding."""
        return self.db_integration.fetch_similar_products(embedding, n)

    def dict_to_embedding(self,product_dict: dict) -> list:
        """
        Converts a dictionary of product details into a pandas Series and generates an embedding.

        Parameters:
        - product_dict: Dictionary containing product details with keys: 'parent_asin', 'title', 'description',
                        'main_category', and 'price'.
        - vector_generator: Object with a method generate_embedding that takes a Series and returns an embedding.

        Returns:
        - Series containing the embedding.
        """
        # Convert the dictionary to a pandas Series
        product_series = pd.Series(product_dict)

        # Generate the embedding using the provided vector generator
        embedding = self.vector_generator.generate_embedding(product_series)

        return embedding