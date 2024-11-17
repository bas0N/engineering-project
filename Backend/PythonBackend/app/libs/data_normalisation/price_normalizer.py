from .interfaces import Normalisable

class PriceNormalised(Normalisable):
    def __init__(self, min_price: float, max_price: float):
        if min_price >= max_price:
            raise ValueError("min_price should be less than max_price")
        self.min_price = min_price
        self.max_price = max_price

    def normalise(self, price: float) -> float:
        try:
            # Check if price is a valid number
            if not isinstance(price, (int, float)):
                return 0  # Return 0 if the input is invalid

            # Return 0 if price is below the minimum
            if price < self.min_price:
                return 0
            # Return 1 if price is above the maximum
            elif price > self.max_price:
                return 1

            # Normalize the price
            return (price - self.min_price) / (self.max_price - self.min_price)
        except (TypeError, ValueError):
            return 0  # Return 0 if any exception occurs

