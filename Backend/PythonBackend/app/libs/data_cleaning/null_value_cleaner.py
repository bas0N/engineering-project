import pandas as pd
from .interfaces import Cleanable

class NullValueCleaner(Cleanable):
    def clean(self,value):
        """Return None if the value is null, otherwise return the value."""
        print("NullValueCleaner value", value)
        return value if pd.notnull(value) else None

