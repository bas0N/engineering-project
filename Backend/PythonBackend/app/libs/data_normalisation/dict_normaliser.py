from .interfaces import Normalisable

class DictionaryNormalizer:
    def __init__(self, field_normalisers: dict[str, Normalisable]):
        self.field_normalisers = field_normalisers

    def normalise_dict(self, data: dict[str, any]) -> dict[str, any]:
        normalised_data = {}
        for field, value in data.items():
            if field in self.field_normalisers:
                normaliser = self.field_normalisers[field]
                normalised_data[field] = normaliser.normalise(value)
            else:
                normalised_data[field] = value  # Leave fields without normalisers unchanged
        return normalised_data