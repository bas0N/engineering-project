from app.services.config import data_sanitizer,weighted_vectorizer,db_integration
from app.services.libs import ProductManager

product_manager = ProductManager(data_sanitizer,weighted_vectorizer,db_integration)

def get_similar_products(product_id,number_of_products):
    return {}
    # get product by id
    # product_embedding = product_manager.dict_to_embedding
    # return product_manager.fetch_similar_products(product_embedding,number_of_products)

def add_product_embedding(product):
    # turn dict to df
    # product_manager.add_embeddings_column()
    return product