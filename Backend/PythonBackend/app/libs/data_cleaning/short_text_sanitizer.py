from typing import List, Union
from .interfaces import Cleanable

class ShortTextCleaner(Cleanable):
    def __init__(self, min_words=4):
        self.min_words = min_words

    def clean(self, value: Union[str, List[str]]):
        """Remove text if it has fewer than min_words words, otherwise return the text or process an array of text."""
        print("ShortTextCleaner value", value)

        def clean_text(text: str) -> Union[str, None]:
            return text if len(text.split()) >= self.min_words else None

        if isinstance(value, str):
            return clean_text(value)
        elif isinstance(value, list):
            return [clean_text(item) if isinstance(item, str) else item for item in value]
        return None  # Reject if it's not a string or a list