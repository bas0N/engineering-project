import re
from typing import Optional, Set
from .interfaces import Cleanable
class TextCleaner(Cleanable):
    def __init__(self, stop_words: Optional[Set[str]] = None):
        self.stop_words = stop_words or set()

    def clean(self, value):
        """Clean text by removing special characters, extra spaces, and stopwords."""
        if isinstance(value, str):
            clean_words = [
                re.sub(r'\W+', '', word) for word in value.split() if word.lower() not in self.stop_words
            ]
            return " ".join(clean_words)
        return value  # If it's not a string, leave it as is