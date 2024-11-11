from dotenv import load_dotenv
from flask import Flask
import os
from sentence_transformers import SentenceTransformer

from .services import data_processor
from .libs.data_cleaning.dict_cleaner import DictionaryCleaner
from .libs.data_cleaning.null_value_cleaner import NullValueCleaner
from .libs.data_cleaning.short_text_sanitizer import ShortTextCleaner
from .libs.data_cleaning.text_value_sanitizer import TextContentCleaner
from .libs.data_cleaning.array_text_value_cleaner import ArrayTextContentCleaner

from .libs.data_normalisation.price_normalizer import PriceNormalised
from .libs.data_normalisation.dict_normaliser import DictionaryNormalizer



from .libs.database.mongo_db_impl import MongoDBIntegration
from .libs.embeddings.embeddings_impl import EmbeddingImpl

from .libs.vector_database.chroma_db_impl import ChromaDBIntegration

from .services.enums import ProductColumnsToEmbed, ProductColumnsEmbedded

from .services.data_processor import DataProcessor

from .services.mappers import DefaultMapperAdapter

def create_app():
    # setup env
    load_dotenv()

    app = Flask(__name__)

    app.config['MONGO_URI'] = os.getenv('MONGO_URI','mongodb://localhost:27017/dev')


    ## cleaners
    null_cleaner = NullValueCleaner()
    short_text_cleaner = ShortTextCleaner(min_words=4)
    text_content_cleaner = TextContentCleaner()
    array_text_content_cleaner = ArrayTextContentCleaner()
    field_cleaners = {
        #ID
        ProductColumnsToEmbed.ID.value: [null_cleaner],

        #TITLE
        ProductColumnsToEmbed.TITLE.value: [null_cleaner,text_content_cleaner],

        #DESCRIPTION
        #ProductColumnsToEmbed.DESCRIPTION.value: null_cleaner,
        ProductColumnsToEmbed.DESCRIPTION.value: [null_cleaner,array_text_content_cleaner,short_text_cleaner],

        #MAIN_CATEGORY
        ProductColumnsToEmbed.MAIN_CATEGORY.value: [null_cleaner,text_content_cleaner],
    }
    dictionary_cleaner = DictionaryCleaner(field_cleaners=field_cleaners)

    ## normalisers
    price_normaliser = PriceNormalised(0,10000)
    field_normalisers = {
        ProductColumnsToEmbed.PRICE: [price_normaliser],
    }
    dictionary_normalizer = DictionaryNormalizer(field_normalisers=field_normalisers)


    mongo_db_integration = MongoDBIntegration(os.getenv('MONGO_URI'),'dev')

    embedding_model = SentenceTransformer('all-MiniLM-L6-v2')
    def embedding_model_function(text):
        return embedding_model.encode(text)


    my_weights = {
        ProductColumnsToEmbed.TITLE.value: 0.4,
        ProductColumnsToEmbed.DESCRIPTION.value: 0.3,
        ProductColumnsToEmbed.MAIN_CATEGORY.value: 0.2,
        ProductColumnsToEmbed.PRICE.value:0.1
    }
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
    from .routes import register_routes
    register_routes(app)

    return app
