from typing import Any, List
from .interfaces import Normalisable

class DictionaryNormalizer:
    def __init__(self, field_normalisers: dict[str, List[Normalisable]]):
        self.field_normalisers = field_normalisers

    def normalise_dict(self, data: dict[str, Any]) -> dict[str, Any]:
        normalised_data = {}

        for field, value in data.items():
            if field in self.field_normalisers:
                # Apply each normaliser in the list sequentially
                for normaliser in self.field_normalisers[field]:
                    value = normaliser.normalise(value)

            normalised_data[field] = value

        return normalised_data