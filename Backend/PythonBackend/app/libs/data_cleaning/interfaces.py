from abc import ABC, abstractmethod

class Sanitizable(ABC):
    @abstractmethod
    def sanitize(self, value):
        """Sanitize the input value based on specific criteria."""
        pass