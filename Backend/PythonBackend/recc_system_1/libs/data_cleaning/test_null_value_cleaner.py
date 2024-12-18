import pytest
import pandas as pd
from .null_value_cleaner import NullValueCleaner

def test_clean_non_null_value():
    # Arrange
    cleaner = NullValueCleaner()
    value = "non-null value"

    # Act
    result = cleaner.clean(value)

    # Assert
    assert result == value

def test_clean_null_value():
    # Arrange
    cleaner = NullValueCleaner()
    value = None

    # Act
    result = cleaner.clean(value)

    # Assert
    assert result is None

def test_clean_nan_value():
    # Arrange
    cleaner = NullValueCleaner()
    value = float('nan')

    # Act
    result = cleaner.clean(value)

    # Assert
    assert result is None

def test_clean_pandas_nan():
    # Arrange
    cleaner = NullValueCleaner()
    value = pd.NA

    # Act
    result = cleaner.clean(value)

    # Assert
    assert result is None

def test_clean_empty_string():
    # Arrange
    cleaner = NullValueCleaner()
    value = ""

    # Act
    result = cleaner.clean(value)

    # Assert
    assert result == value