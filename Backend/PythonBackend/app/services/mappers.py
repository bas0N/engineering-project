from typing import Any, Dict


class MapperAdapter:
    """Interface for mapping a dictionary row to ChromaDB-compatible format."""

    def map_row_to_chromadb(self, row: Dict[str, Any]) -> Dict[str, Any]:
        """Maps a single dictionary row to ChromaDB metadata and embedding format.

        Args:
            row (Dict[str, Any]): A single dictionary representing a row of data.

        Returns:
            Dict[str, Any]: A dictionary with 'metadata' and 'embedding' keys.
        """
        raise NotImplementedError("MapperAdapter subclasses must implement this method.")


class DefaultMapperAdapter(MapperAdapter):
    """Default implementation of MapperAdapter for specific field mapping."""

    def map_row_to_chromadb(self, row: Dict[str, Any]) -> Dict[str, Any]:
        metadata = {
            "ids": row.get("parent_asin"),
            "title": row.get("title"),
            "description": row.get("description"),
            "main_category": row.get("main_category"),
            "price": row.get("price")
        }
        embedding = row.get("embedding")

        return {
            "ids": row.get("parent_asin"),
            "metadata": metadata,
            "title": row.get("title"),
            "embedding": embedding
        }
