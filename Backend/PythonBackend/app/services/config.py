from sentence_transformers import SentenceTransformer
from app.libs.data_sanitizer import NullValueHandler,ShortTextFilter

# sanitization config
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
#embedding config
embedding_model = SentenceTransformer('all-MiniLM-L6-v2')
def embedding_model_function(text):
    return embedding_model.encode(text)
text_weights = {
    ProductColumnsToEmbed.TITLE: 0.4,
    ProductColumnsToEmbed.DESCRIPTION: 0.3,
    ProductColumnsToEmbed.MAIN_CATEGORY: 0.2
}
numeric_weights = {
    ProductColumnsToEmbed.PRICE: 0.1
}

price_normalizer = PriceNormalizer()

weighted_vectorizer = WeightedConcatenatedVector(
    embedding_model=embedding_model_function,
    text_weights=text_weights,
    numeric_weights=numeric_weights,
    numeric_transformer=price_normalizer
)

# vector db
db_integration = ChromaDBIntegration(collection_name="product_collection", mapper=DefaultMapperAdapter())
