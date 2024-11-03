from .interfaces import Cleanable

class DictionaryCleaner:
    def __init__(self, field_cleaners: dict[str, Cleanable]):
        self.field_cleaners = field_cleaners

    def sanitize_dict(self, data: dict[str, any]) -> dict[str, any]:
        cleaned_data = {}
        for field, value in data.items():
            if field in self.field_cleaners:
                cleaner = self.field_cleaners[field]
                cleaned_data[field] = cleaner.clean(value)
            else:
                cleaned_data[field] = value  # Leave fields without sanitizers as they are
        return cleaned_data