from sentence_transformers import SentenceTransformer
from recc_system_1.libs.data_sanitizer import NullValueHandler,ShortTextFilter

# # sanitization config
# null_handler = NullValueHandler()
# short_text_filter = ShortTextFilter(min_words=4)
# text_sanitizer = TextSanitizer(stop_words=set(stopwords.words('english')))
# price_normalizer = PriceNormalizer()

# data_sanitizer = DataSanitizer(
#     null_handler=null_handler,
#     short_text_filter=short_text_filter,
#     text_sanitizer=text_sanitizer,
#     price_normalizer=price_normalizer
# )
# #embedding config
# embedding_model = SentenceTransformer('all-MiniLM-L6-v2')
# def embedding_model_function(text):
#     return embedding_model.encode(text)
# text_weights = {
#     ProductColumnsToEmbed.TITLE: 0.4,
#     ProductColumnsToEmbed.DESCRIPTION: 0.3,
#     ProductColumnsToEmbed.MAIN_CATEGORY: 0.2
# }
# numeric_weights = {
#     ProductColumnsToEmbed.PRICE: 0.1
# }

# price_normalizer = PriceNormalizer()

# weighted_vectorizer = WeightedConcatenatedVector(
#     embedding_model=embedding_model_function,
#     text_weights=text_weights,
#     numeric_weights=numeric_weights,
#     numeric_transformer=price_normalizer
# )

# # vector db
# db_integration = ChromaDBIntegration(collection_name="product_collection", mapper=DefaultMapperAdapter())

# dictionary_cleaner = DictionaryCleaner(field_cleaners={})
# dictionary_normalizer = DictionaryNormalizer(field_normalisers={})
# db_integration = NoSqlDatabaseIntegration()  # Replace with an actual implementation instance
# embedding_generator = EmbeddingImpl(embedding_model=my_embedding_model, weights=my_weights)
# vector_db_integration = VectorDBIntegrationImpl()  # Replace with an actual implementation instance

# # Create an instance of DataProcessor
# data_processor_instance = DataProcessor(
#     dictionary_cleaner=dictionary_cleaner,
#     dictionary_normalizer=dictionary_normalizer,
#     db_integration=db_integration,
#     embedding_generator=embedding_generator,
#     vector_db_integration=vector_db_integration
# )