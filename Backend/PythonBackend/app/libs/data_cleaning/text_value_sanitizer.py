import re
from typing import List, Optional, Set, Union
from .interfaces import Cleanable
class TextContentCleaner(Cleanable):
    def __init__(self, stop_words: Optional[Set[str]] = None):
        self.stop_words = stop_words or set()

    def clean(self, value: Union[str, List[str]]):
        print("TextContentCleaner value", value)
        """Clean text or an array of text by removing special characters, extra spaces, and stopwords."""

        def clean_text(text: str) -> str:
            clean_words = [
                re.sub(r'\W+', '', word) for word in text.split() if word.lower() not in self.stop_words
            ]
            return " ".join(clean_words)

        if isinstance(value, str):
            return clean_text(value)
        elif isinstance(value, list):
            return [clean_text(item) if isinstance(item, str) else item for item in value]
        return value  # If it's not a string or a list, leave it as is