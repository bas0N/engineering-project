from abc import ABC, abstractmethod

class Cleanable(ABC):
    @abstractmethod
    def clean(self, value):
        """Cleans the input value based on specific criteria."""
        pass