from .interfaces import Sanitizable

class ShortTextSanitizer(Sanitizable):
    def __init__(self, min_words=4):
        self.min_words = min_words

    def sanitize(self, value):
        """Remove text if it has fewer than min_words words, otherwise return the text."""
        if isinstance(value, str) and len(value.split()) >= self.min_words:
            return value
        return None  # Reject if it doesn't meet the word requirement