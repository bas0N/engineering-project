from .interfaces import Cleanable

class DictionaryCleaner:
    def __init__(self, field_cleaners: dict[str, Cleanable]):
        self.field_cleaners = field_cleaners

    from typing import Optional

    def sanitize_dict(self, data: dict[str, any]) -> Optional[dict[str, any]]:
        cleaned_data = {}
        for field, value in data.items():
            if field in self.field_cleaners:
                cleaner = self.field_cleaners[field]
                cleaned_value = cleaner.clean(value)
                # If any field after cleaning is None, return None immediately
                if cleaned_value is None:
                    return None
                cleaned_data[field] = cleaned_value
            else:
                cleaned_data[field] = value  # Leave fields without sanitizers as they are
                # Check if the value itself is None
                if value is None:
                    return None

        return cleaned_data