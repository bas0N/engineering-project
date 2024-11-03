from typing import TypedDict, Optional
from datetime import datetime

class ConfigDocument(TypedDict):
    config_name: str
    value: str
    last_updated: Optional[datetime]

class ProductDocument(TypedDict):
    product_id: str
    name: str
    price: float
    description: Optional[str]
    in_stock: bool
