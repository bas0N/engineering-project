from enum import Enum

class ProductColumnsToEmbed(Enum):
    ID = "parent_asin"
    TITLE = "title"
    DESCRIPTION = "description"
    MAIN_CATEGORY = "main_category"
    PRICE = "price"

class ProductColumnsEmbedded(Enum):
    ID = "parent_asin"
    TITLE = "title"
    DESCRIPTION = "description"
    MAIN_CATEGORY = "main_category"
    PRICE = "price"
    EMBEDDING = "embedding"