import pytest

from .price_normalizer import PriceNormalised

def test_price_normalised_with_valid_input():
    # Arrange
    min_price = 10.0
    max_price = 100.0
    normaliser = PriceNormalised(min_price, max_price)

    # Act
    result = normaliser.normalise(55.0)

    # Assert
    assert result == 0.5  # Normalized value between 10 and 100

def test_price_normalised_below_min_price():
    # Arrange
    min_price = 10.0
    max_price = 100.0
    normaliser = PriceNormalised(min_price, max_price)

    # Act
    result = normaliser.normalise(5.0)

    # Assert
    assert result == 0  # Price below min should return 0

def test_price_normalised_above_max_price():
    # Arrange
    min_price = 10.0
    max_price = 100.0
    normaliser = PriceNormalised(min_price, max_price)

    # Act
    result = normaliser.normalise(150.0)

    # Assert
    assert result == 1  # Price above max should return 1

def test_price_normalised_with_invalid_input():
    # Arrange
    min_price = 10.0
    max_price = 100.0
    normaliser = PriceNormalised(min_price, max_price)

    # Act
    result = normaliser.normalise("invalid")

    # Assert
    assert result == 0  # Invalid input should return 0

def test_price_normalised_with_min_price_equal_max_price():
    # Arrange
    min_price = 50.0
    max_price = 50.0

    # Act & Assert
    with pytest.raises(ValueError, match="min_price should be less than max_price"):
        PriceNormalised(min_price, max_price)

def test_price_normalised_edge_cases():
    # Arrange
    min_price = 10.0
    max_price = 100.0
    normaliser = PriceNormalised(min_price, max_price)

    # Act & Assert
    assert normaliser.normalise(10.0) == 0  # Exactly at min price
    assert normaliser.normalise(100.0) == 1  # Exactly at max price
