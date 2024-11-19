from enum import Enum

class ProductColumnsToEmbed(Enum):
    ID = "parent_asin"
    TITLE = "title"
    DESCRIPTION = "description"
    MAIN_CATEGORY = "main_category"
    PRICE = "price"
    IMAGES = "images"
    AVERAGE_RATING = "average_rating"
    RATINGS_NUMBER = "rating_number"

class ProductColumnsEmbedded(Enum):
    ID = "parent_asin"
    TITLE = "title"
    DESCRIPTION = "description"
    MAIN_CATEGORY = "main_category"
    PRICE = "price"
    EMBEDDING = "embedding"