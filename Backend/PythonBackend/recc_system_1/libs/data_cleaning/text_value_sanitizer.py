import re
from typing import List, Optional, Set, Union
from .interfaces import Cleanable
class TextContentCleaner(Cleanable):
    def __init__(self, stop_words: Optional[Set[str]] = None):
        self.stop_words = stop_words or set()

    def clean(self, value: str) -> Optional[str]:
        if not isinstance(value, str):
            return None

        def clean_text(text: str) -> str:
            clean_words = [
                re.sub(r'\W+', '', word) for word in text.split() if word.lower() not in self.stop_words
            ]
            return " ".join(clean_words)

        cleaned_value = clean_text(value)
        return cleaned_value if cleaned_value else None