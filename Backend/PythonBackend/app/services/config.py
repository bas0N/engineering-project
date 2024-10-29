from sentence_transformers import SentenceTransformer
from app.libs.data_sanitizer import NullValueHandler,ShortTextFilter

null_handler = NullValueHandler()
short_text_filter = ShortTextFilter(min_words=4)
text_sanitizer = TextSanitizer(stop_words=set(stopwords.words('english')))
price_normalizer = PriceNormalizer()



data_sanitizer = DataSanitizer(
    null_handler=null_handler,
    short_text_filter=short_text_filter,
    text_sanitizer=text_sanitizer,
    price_normalizer=price_normalizer
)
#vectors
embedding_model = SentenceTransformer('all-MiniLM-L6-v2')
def embedding_model_function(text):
    return embedding_model.encode(text)
text_weights = {
    ProductColumnsToEmbed.TITLE: 0.7,
    ProductColumnsToEmbed.DESCRIPTION: 0.5,
    ProductColumnsToEmbed.MAIN_CATEGORY: 0.3
}

numeric_weights = {
    ProductColumnsToEmbed.PRICE: 1.0
}

price_normalizer = PriceNormalizer()
price_normalizer.fit([10, 20, 30, 40, 50])

weighted_vectorizer = WeightedConcatenatedVector(
    embedding_model=embedding_model_function,
    text_weights=text_weights,
    numeric_weights=numeric_weights,
    numeric_transformer=price_normalizer
)



db_integration = ChromaDBIntegration(collection_name="product_collection", mapper=DefaultMapperAdapter())
