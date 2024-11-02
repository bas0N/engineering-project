from .interfaces import Sanitizable

class NullValueSanitizer(Sanitizable):
    def sanitize(self,value):
        """Return None if the value is null, otherwise return the value."""
        return value if pd.notnull(value) else None

