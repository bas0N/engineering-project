from typing import List, Dict, Any
import numpy as np
import pandas as pd
from ..libs.data_cleaning.dict_cleaner import DictionaryCleaner
from ..libs.data_normalisation.dict_normaliser import DictionaryNormalizer
from ..libs.database.interfaces import NoSqlDatabaseIntegrationInterface
from ..libs.embeddings.interfaces import Embedding
from ..libs.vector_database.interfaces import VectorDBIntegration
from ..libs.database.mongo_db_impl_types import ConfigDocument, InitialisationStatusEnum

class DataProcessor:
    """Class that integrates data cleaning, normalization, embedding generation, and database operations."""

    def __init__(self,
                 dictionary_cleaner: DictionaryCleaner,
                 dictionary_normalizer: DictionaryNormalizer,
                 db_integration: NoSqlDatabaseIntegrationInterface,
                 embedding_generator: Embedding,
                 vector_db_integration: VectorDBIntegration):
        """
        Initialize the data processor.

        Args:
            dictionary_cleaner (DictionaryCleaner): Instance for cleaning data.
            dictionary_normalizer (DictionaryNormalizer): Instance for normalizing data.
            db_integration (NoSqlDatabaseIntegrationInterface): Database integration instance.
            embedding_generator (Embedding): Instance for generating embeddings.
            vector_db_integration (VectorDBIntegration): Vector database integration instance.
        """
        self.dictionary_cleaner = dictionary_cleaner
        self.dictionary_normalizer = dictionary_normalizer
        self.db_integration = db_integration
        self.embedding_generator = embedding_generator
        self.vector_db_integration = vector_db_integration

    def add_single_dict(self, data: Dict[str, Any]) -> None:
        """Cleans, normalizes, embeds, and adds a single dictionary to the vector database."""
        # Step 1: Clean the data
        cleaned_data = self.dictionary_cleaner.sanitize_dict(data)

        # Step 2: Normalize the data
        normalized_data = self.dictionary_normalizer.normalise_dict(cleaned_data)

        # Step 3: Generate embedding
        embedding = self.embedding_generator.generate_embedding(normalized_data)

        # Step 4: Save to the vector database
        self.vector_db_integration.save_dict({**normalized_data, 'embedding': embedding})

    def add_list_of_dicts(self, data_list: List[Dict[str, Any]]) -> None:
        """Cleans, normalizes, embeds, and adds a list of dictionaries to the vector database."""
        processed_data_list = []

        for data in data_list:
            # Step 1: Clean the data
            cleaned_data = self.dictionary_cleaner.sanitize_dict(data)

            # Step 2: Normalize the data
            normalized_data = self.dictionary_normalizer.normalise_dict(cleaned_data)

            # Step 3: Generate embedding
            embedding = self.embedding_generator.generate_embedding(normalized_data)

            # Prepare data for saving
            processed_data_list.append({**normalized_data, 'embedding': embedding})

        # Step 4: Save the batch to the vector database
        self.vector_db_integration.save_dicts(processed_data_list)

    def fetch_similar_products(self, embedding: List[float], n: int = 5) -> pd.DataFrame:
        """Fetches top `n` similar products given an embedding."""
        return self.vector_db_integration.fetch_similar_products(embedding, n)

    def initialise_recommendation_system(self) -> List[Dict[str, Any]]:
        """. get config documents from db, they contain:
                1. name of the collection
                2. status:
                    1. DONE - do nothing
                    2. IN_PROGRESS
                        1. continute the loop from where last saved
                        2. change the status to done if done
                        3.
                    3. TODO:
                        1. check if the collection exists - if not, throw error
                        2. change the status to inprogress
                        3. save normalisation data, batch size, last processed etc
                        4. get data for normalisation purposes
                        5. get the total number, split into batches
                        6. process batches in a loop
                        7. finish when done"""

        """Scans the configs and fetches all products accordingly."""
        # Retrieve all configuration documents
        config_docs:ConfigDocument = self.db_integration.get_all_config_docs()
        for config in config_docs:
            if config['status'] == InitialisationStatusEnum.DONE:
                continue
            elif config['status'] == InitialisationStatusEnum.IN_PROGRESS:
            # Continue processing from where it was left off
                last_processed = config.get('last_processed', 0)
                batch_size = config.get('batch_size', 100)
                collection_name = config['collection_name']

            # Fetch data in batches and process
                while True:
                    data_batch = self.db_integration.get_data_batch(collection_name, last_processed, batch_size)
                    if not data_batch:
                        break

                    self.add_list_of_dicts(data_batch)
                    last_processed += len(data_batch)

                    # Update the config document with the new last_processed value
                    self.db_integration.update_config_doc(config['_id'], {'last_processed': last_processed})

                # Mark the config as DONE
                self.db_integration.update_config_doc(config['_id'], {'status': 'DONE'})
            elif config['status'] == InitialisationStatusEnum.TO_DO:
                collection_name = config['collection_name']

                # Check if the collection exists
                if not self.db_integration.collection_exists(collection_name):
                    raise ValueError(f"Collection {collection_name} does not exist.")

                # Update the status to IN_PROGRESS
                self.db_integration.update_config_doc(config['_id'], {'status': 'IN_PROGRESS', 'last_processed': 0})

                # Fetch data in batches and process
                last_processed = 0
                batch_size = config.get('batch_size', 100)

                while True:
                    data_batch = self.db_integration.get_data_batch(collection_name, last_processed, batch_size)
                    if not data_batch:
                        break

                    self.add_list_of_dicts(data_batch)
                    last_processed += len(data_batch)

                    # Update the config document with the new last_processed value
                    self.db_integration.update_config_doc(config['_id'], {'last_processed': last_processed})

                # Mark the config as DONE
                self.db_integration.update_config_doc(config['_id'], {'status': 'DONE'})

        print("Config documents fetched:", config_docs)

        # Fetch all products based on some logic with the configs
        products = self.db_integration.get_all_products()
        print("Products fetched:", products)

        return products
