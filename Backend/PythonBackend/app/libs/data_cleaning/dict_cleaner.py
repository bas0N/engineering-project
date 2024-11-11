from .interfaces import Cleanable

class DictionaryCleaner:
    def __init__(self, field_cleaners: dict[str, Cleanable]):
        self.field_cleaners = field_cleaners

    from typing import Optional

    def clean_dict(self, data: dict[str, any]) -> Optional[dict[str, any]]:
        cleaned_data = {}
        for field, value in data.items():
            print("field_searched", field)
            if field in self.field_cleaners:
                cleaner = self.field_cleaners[field]
                cleaned_value = cleaner.clean(value)
                # If any field after cleaning is None, return None immediately
                # print cleaned value and the param name

                print("cleaned_value for field", field, "is", cleaned_value)

                if cleaned_value is None:
                    return None
                cleaned_data[field] = cleaned_value
            else:
                cleaned_data[field] = value  # Leave fields without sanitizers as they are

        return cleaned_data