from .interfaces import Cleanable
from typing import Optional,List,Any

class DictionaryCleaner:
    def __init__(self, field_cleaners: dict[str, List[Cleanable]]):
        self.field_cleaners = field_cleaners

    def clean_dict(self, data: dict[str, Any]) -> Optional[dict[str, Any]]:
        cleaned_data = {}

        for field, value in data.items():
            if field in self.field_cleaners:
                # Apply each cleaner in the list sequentially
                for cleaner in self.field_cleaners[field]:
                    print("value to clean", value)
                    value = cleaner.clean(value)
                    print("cleaned value", value)
                    if value is None:
                        return None  # Return None if any cleaner produces None

            cleaned_data[field] = value

        return cleaned_data



