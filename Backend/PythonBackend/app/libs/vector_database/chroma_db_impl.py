from typing import List, Dict, Any, Optional
#import numpy as np
import chromadb
from threading import Lock
from .interfaces import VectorDBIntegration
import os
from chromadb.config import Settings

class ChromaDBIntegration(VectorDBIntegration):
    """Implementation of VectorDBIntegration for ChromaDB."""

    _instance: Optional['ChromaDBIntegration'] = None
    _lock = Lock()

    def __new__(cls, collection_name: str, mapper: Any):
        if cls._instance is None:
            with cls._lock:
                if cls._instance is None:
                    cls._instance = super(ChromaDBIntegration, cls).__new__(cls)
        return cls._instance

    def __init__(self, collection_name: str, mapper: Any):
        if not hasattr(self, 'initialized'):
            #self.client = chromadb.HttpClient(host=os.getenv('CHROMA_HOST','chroma'), port = 8000, settings=Settings(allow_reset=True, anonymized_telemetry=False))
            self.client = chromadb.PersistentClient(path="volume/chromadb-data")
            self.collection = self.client.get_or_create_collection(collection_name)
            self.mapper = mapper
            self.initialized = True

    @classmethod
    def get_instance(cls, collection_name: str, mapper: Any) -> 'ChromaDBIntegration':
        """Get or create the singleton instance with collection details."""
        if cls._instance is None:
            cls._instance = cls(collection_name=collection_name, mapper=mapper)
        return cls._instance

    def save_dict(self, data: Dict[str, Any]) -> None:
        """Saves a single dictionary to ChromaDB.

        Args:
            data (Dict[str, Any]): The dictionary containing data to save.
        """
        mapped_data = self.mapper.map_row_to_chromadb(data)
        self.collection.add(
            ids=[mapped_data["ids"]],
            documents=[mapped_data["title"]],
            metadatas=[mapped_data["metadata"]],
            embeddings=[mapped_data["embedding"]]
        )
        print("One document added.")

    def save_dicts(self, data_list: List[Dict[str, Any]]) -> None:
        """Saves data from a list of dictionaries to ChromaDB in batches.

        Args:
            data_list (List[Dict[str, Any]]): The list of dictionaries containing data to save.
        """
        ids = []
        documents = []
        metadatas = []
        embeddings = []

        for entry in data_list:
            mapped_data = self.mapper.map_row_to_chromadb(entry)
            ids.append(mapped_data["ids"])
            documents.append(mapped_data["title"])
            metadatas.append(mapped_data["metadata"])
            embeddings.append(mapped_data["embedding"])

        # Add all entries to the collection at once
        self.collection.add(ids=ids, documents=documents, metadatas=metadatas, embeddings=embeddings)
        print(f"Number of documents added: {len(documents)}")

    def fetch_similar_products(self, embedding: List[float], n: int = 5):
        """Fetches top `n` similar products given an embedding.

        Args:
            embedding (List[float]): The embedding vector to search for similar products.
            n (int): The number of similar products to retrieve.

        Returns:
            pd.DataFrame: DataFrame containing the similar products with metadata and distances.
        """
        try:
            results = self.collection.query(
                query_embeddings=[embedding],
                n_results=n,
                include=["documents", "metadatas", "distances","embeddings"]
            )
            print("results: ",  results)
            metadatas = results['metadatas'][0]
            distances = results['distances'][0]
            for metadata, distance in zip(metadatas, distances):
                metadata['distance'] = distance

            return metadatas
        except Exception as e:
            print(f"Error fetching similar products: {str(e)}")
            return None
