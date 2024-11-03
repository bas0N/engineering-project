from .interfaces import Normalisable

class PriceNormalised(Normalisable):
    def __init__(self, min_price: float, max_price: float):
        if min_price >= max_price:
            raise ValueError("min_price should be less than max_price")
        self.min_price = min_price
        self.max_price = max_price

    def normalise(self, price: float) -> float:
        if not (self.min_price <= price <= self.max_price):
            raise ValueError("Price should be within the range defined by min_price and max_price")
        return (price - self.min_price) / (self.max_price - self.min_price)
