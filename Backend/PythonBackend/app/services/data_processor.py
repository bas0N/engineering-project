from typing import List, Dict, Any
import numpy as np
import pandas as pd
from ..libs.data_cleaning.dict_cleaner import DictionaryCleaner
from ..libs.data_normalisation.dict_normaliser import DictionaryNormalizer
from ..libs.database.interfaces import NoSqlDatabaseIntegrationInterface
from ..libs.embeddings.interfaces import Embedding
from ..libs.vector_database.interfaces import VectorDBIntegration
from ..libs.database.mongo_db_impl_types import ConfigDocument, InitialisationStatusEnum
from ..libs.database.mongo_db_impl_types import InitialisationStatusEnum
from ..services.enums import ProductColumnsToEmbed

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
        self.DEFAULT_BATCH_SIZE=100

    def filter_dict_fields(self,original_dict, fields):
        """
        Filters the given dictionary to only include the specified fields.

        Parameters:
            original_dict (dict): The original dictionary to filter.
            fields (list): A list of keys to keep in the dictionary.

        Returns:
            dict: A new dictionary with only the specified fields.
        """
        return {key: original_dict[key] for key in fields if key in original_dict}
    def add_single_dict(self, data: Dict[str, Any]) -> None:
        """Cleans, normalizes, embeds, and adds a single dictionary to the vector database."""
        # step 0: filter the dictionary to only include the specified fields
        field_to_filter = [ ProductColumnsToEmbed.ID.value, ProductColumnsToEmbed.TITLE.value, ProductColumnsToEmbed.DESCRIPTION.value, ProductColumnsToEmbed.MAIN_CATEGORY.value, ProductColumnsToEmbed.PRICE.value]
        data = self.filter_dict_fields(data, field_to_filter)
        # Step 1: Clean the data
        cleaned_data = self.dictionary_cleaner.clean_dict(data)

        # Step 2: Normalize the data
        normalized_data = self.dictionary_normalizer.normalise_dict(cleaned_data)

        # Step 3: Generate embedding
        embedding = self.embedding_generator.generate_embedding(normalized_data)

        # Step 4: Save to the vector database
        self.vector_db_integration.save_dict({**normalized_data, 'embedding': embedding})

    def add_list_of_dicts(self, data_list: List[Dict[str, Any]]) -> None:
        """Cleans, normalizes, embeds, and adds a list of dictionaries to the vector database."""
        processed_data_list = []
        print("Data list", data_list)
        for data in data_list:
            # step 0: filter the dictionary to only include the specified fields
            field_to_filter = [ ProductColumnsToEmbed.ID.value, ProductColumnsToEmbed.TITLE.value, ProductColumnsToEmbed.DESCRIPTION.value, ProductColumnsToEmbed.MAIN_CATEGORY.value, ProductColumnsToEmbed.PRICE.value]
            data = self.filter_dict_fields(data, field_to_filter)
            # Step 1: Clean the data
            cleaned_data = self.dictionary_cleaner.clean_dict(data)
            print("Cleaned data", cleaned_data)

            # Step 2: Normalize the data
            normalized_data = self.dictionary_normalizer.normalise_dict(cleaned_data)
            print("Normalized data", normalized_data)

            # Step 3: Generate embedding
            embedding = self.embedding_generator.generate_embedding(normalized_data)
            print("Embedding", embedding)

            # Prepare data for saving
            processed_data_list.append({**normalized_data, 'embedding': embedding})
            print("Processed data list", processed_data_list)

        # Step 4: Save the batch to the vector database
        self.vector_db_integration.save_dicts(processed_data_list)

    def fetch_similar_products(self, embedding: List[float], n: int = 5) -> pd.DataFrame:
        # pass id
        # get product from db
        # get embedding
        # get similar products
        """Fetches top `n` similar products given an embedding."""
        return self.vector_db_integration.fetch_similar_products(embedding, n)

    def initialise_recommendation_system(self) -> List[Dict[str, Any]]:
        print("Initialising the recommendation system...")
        """Scans the configs and fetches all products accordingly."""
        # Retrieve all configuration documents
        config_docs:ConfigDocument = self.db_integration.get_all_config_docs()
        for config in config_docs:
            if config['status'] == InitialisationStatusEnum.DONE.value:
                continue
            elif config['status'] == InitialisationStatusEnum.IN_PROGRESS.value:
            # Continue processing from where it was left off
                last_batch_processed = config.get('last_batch_processed', 0)
                batch_size = config.get('batch_size', self.DEFAULT_BATCH_SIZE)
                collection_name = config['collection_name']

            # Fetch data in batches and process
                while True:
                    data_batch = self.db_integration.get_data_batch(collection_name, last_batch_processed, batch_size)
                    if not data_batch:
                        break

                    self.add_list_of_dicts(data_batch)
                    last_batch_processed += len(data_batch)

                    # Update the config document with the new last_batch_processed value
                    self.db_integration.update_config_doc({'_id': config['_id']}, {'last_batch_processed': last_batch_processed})

                # Mark the config as DONE
                self.db_integration.update_config_doc({'_id': config['_id']}, {'status':  InitialisationStatusEnum.DONE.value})
            elif config['status'] == InitialisationStatusEnum.TO_DO.value:
                collection_name = config['collection_name']

                # Check if the collection exists
                if not self.db_integration.collection_exists(collection_name):
                    raise ValueError(f"Collection {collection_name} does not exist.")

                # Update the status to IN_PROGRESS
                self.db_integration.update_config_doc({'_id': config['_id']}, {'status': InitialisationStatusEnum.IN_PROGRESS.value, 'last_batch_processed': 0})

                # Fetch data in batches and process
                last_batch_processed = 0
                batch_size = config.get('batch_size', self.DEFAULT_BATCH_SIZE)

                while True:
                    data_batch = self.db_integration.get_data_batch(collection_name, last_batch_processed, batch_size)
                    if not data_batch:
                        break

                    self.add_list_of_dicts(data_batch)
                    last_batch_processed += 1

                    # Update the config document with the new last_batch_processed value
                    self.db_integration.update_config_doc({'_id': config['_id']}, {'last_batch_processed': last_batch_processed})

                # Mark the config as DONE
                self.db_integration.update_config_doc({'_id': config['_id']}, {'status':  InitialisationStatusEnum.DONE.value })
            else:
                print(f"Unknown status: {config['status']}")

        print("Config documents fetched:", config_docs)

        # Fetch all products based on some logic with the configs
        print("All data loaded. Application can start now.")
        return
