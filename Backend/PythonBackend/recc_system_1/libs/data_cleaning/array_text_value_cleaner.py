import re
from typing import List, Optional, Set, Union
from .interfaces import Cleanable

class ArrayTextContentCleaner(Cleanable):
    def __init__(self, stop_words: Optional[Set[str]] = None):
        self.stop_words = stop_words or set()

    def clean(self, value: list) -> Optional[str]:
        if not isinstance(value, list) or not all(isinstance(item, str) for item in value):
            return None

        def clean_text(text: str) -> str:
            clean_words = [
                re.sub(r'\W+', '', word) for word in text.split() if word.lower() not in self.stop_words
            ]
            return " ".join(clean_words)

        concatenated_text = " ".join([clean_text(item) for item in value])
        return concatenated_text if concatenated_text else None