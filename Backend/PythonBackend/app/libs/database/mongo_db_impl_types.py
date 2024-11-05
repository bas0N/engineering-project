from enum import Enum
from typing import TypedDict, Optional
from datetime import datetime

class CollectionEnum(Enum):
    HEALTH_CARE = "health_care",
    CONFIG = "config"

class InitialisationStatusEnum(Enum):
    DONE="done"
    IN_PROGRESS="in_progress"
    TO_DO="to_do"
class ConfigDocument(TypedDict):
    collection_name: CollectionEnum
    page_size: int
    last_page_processed: int
    total: int
    status: InitialisationStatusEnum

class ProductDocument(TypedDict):
    parent_asin: str
    title: str
    description: str
    main_category:str
    price: float
