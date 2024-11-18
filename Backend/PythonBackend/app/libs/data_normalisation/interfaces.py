from abc import ABC, abstractmethod

class Normalisable(ABC):
    @abstractmethod
    def normalise(self, value):
        """Normalise the input value based on specific criteria."""
        pass