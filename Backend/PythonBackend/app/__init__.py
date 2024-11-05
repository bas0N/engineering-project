from flask import Flask
import os
from sentence_transformers import SentenceTransformer

from .services import data_processor
from .libs.data_cleaning.dict_cleaner import DictionaryCleaner
from .libs.data_cleaning.null_value_cleaner import NullValueCleaner
from .libs.data_cleaning.short_text_sanitizer import ShortTextCleaner
from .libs.data_cleaning.text_value_sanitizer import TextCleaner

from .libs.data_normalisation.price_normalizer import PriceNormalised
from .libs.data_normalisation.dict_normaliser import DictionaryNormalizer



from .libs.database.mongo_db_impl import MongoDBIntegration
from .libs.embeddings.embeddings_impl import EmbeddingImpl

from .libs.vector_database.chroma_db_impl import ChromaDBIntegration

from .services.enums import ProductColumnsToEmbed, ProductColumnsEmbedded

from .services.data_processor import DataProcessor

from .services.mappers import DefaultMapperAdapter

def create_app():
    app = Flask(__name__)

    # setup dependencies

    ## cleaners
    null_cleaner = NullValueCleaner()
    short_text_cleaner = ShortTextCleaner(min_words=4)
    text_cleaner = TextCleaner()
    field_cleaners = {
        ProductColumnsToEmbed.ID: null_cleaner,
        'field2': short_text_cleaner,
        # Add more fields and their respective cleaners as needed
    }
    dictionary_cleaner = DictionaryCleaner(field_cleaners=field_cleaners)

    ## normalisers
    price_normaliser = PriceNormalised(0,10000)
    field_normalisers = {
        ProductColumnsToEmbed.PRICE: price_normaliser,
    }
    dictionary_normalizer = DictionaryNormalizer(field_normalisers=field_normalisers)


    mongo_db_integration = MongoDBIntegration(os.getenv('MONGO_URI'),os.getenv('dev'))

    embedding_model = SentenceTransformer('all-MiniLM-L6-v2')
    def embedding_model_function(text):
        return embedding_model.encode(text)
    embedding_generator = EmbeddingImpl(embedding_model=embedding_model_function, weights=my_weights)

    mapper = DefaultMapperAdapter()
    vector_db_integration = ChromaDBIntegration("products", mapper)

    # Create an instance of DataProcessor
    data_processor_instance = DataProcessor(
        dictionary_cleaner,
        dictionary_normalizer,
        mongo_db_integration,
        embedding_generator,
        vector_db_integration
    )


    app.config['data_processor_instance'] = data_processor_instance

    data_processor_instance.initialise_recommendation_system()

    # Register services or blueprints
    from .routes import main_routes
    app.register_blueprint(main_routes)

    return app
