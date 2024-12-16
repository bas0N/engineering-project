import chromadb
import pandas as pd
from typing import Dict, Any, List

class MapperAdapter:
    """Interface for mapping DataFrame rows to ChromaDB-compatible format."""

    def map_row_to_chromadb(self, row: pd.Series) -> Dict[str, Any]:
        """Maps a DataFrame row to ChromaDB metadata and embedding format.

        Args:
            row (pd.Series): A row from the DataFrame.

        Returns:
            Dict[str, Any]: A dictionary with 'metadata' and 'embedding' keys.
        """
        raise NotImplementedError("MapperAdapter subclasses must implement this method.")


class DefaultMapperAdapter(MapperAdapter):
    """Default implementation of MapperAdapter for specific field mapping."""

    def map_row_to_chromadb(self, row: pd.Series) -> Dict[str, Any]:
        metadata = {
            "ids": row["parent_asin"],
            "title": row["title"],
            "description": row["description"],
            "main_category": row["main_category"],
            "price": row["price"]
        }
        embedding = row["embedding"]

        return {"ids":row["parent_asin"],"metadata": metadata,"title":row["title"], "embedding": embedding}


class ChromaDBIntegration:
    """Class for integrating with ChromaDB, with support for mapping DataFrame rows."""

    def __init__(self, collection_name: str, mapper: MapperAdapter):
        self.client = chromadb.Client()
        self.collection = self.client.get_or_create_collection(collection_name)
        self.mapper = mapper

    def save_dataframe(self, df: pd.DataFrame):
        """Saves rows from a DataFrame to ChromaDB in batches.

        Args:
            df (pd.DataFrame): The DataFrame containing data to save.
        """
        for i in range(0, len(df)):
            # Prepare documents, metadata, and embeddings using the mapper
            ids = []
            documents = []
            metadatas = []
            embeddings = []

            for _, row in df.iterrows():
                mapped_data = self.mapper.map_row_to_chromadb(row)
                ids.append(mapped_data["ids"])
                documents.append(mapped_data["title"])
                metadatas.append(mapped_data["metadata"])
                embeddings.append(mapped_data["embedding"])

            # Add all entries to the collection at once
            self.collection.add(ids=ids,documents=documents, metadatas=metadatas, embeddings=embeddings)
        print("Data saved to ChromaDB.",self.collection.get())
        print(f"Number of documents added: {len(documents)}")

    def fetch_similar_products(self, embedding: List[float], n: int = 5) -> pd.DataFrame:
        """Fetches top `n` similar products given an embedding.

        Args:
            embedding (List[float]): The embedding vector to search for similar products.
            n (int): The number of similar products to retrieve.

        Returns:
            pd.DataFrame: DataFrame containing the similar products with metadata and distances.
        """
        results = self.collection.query(
            query_embeddings=[embedding],
            n_results=n,
            include=["documents", "metadatas", "distances"]
        )
        return results