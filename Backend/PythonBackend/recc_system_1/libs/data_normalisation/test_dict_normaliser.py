import pytest
from unittest.mock import MagicMock
from .dict_normaliser import DictionaryNormalizer
from .interfaces import Normalisable

def test_normalise_dict_with_no_normalisers():
    # Arrange
    field_normalisers = {}
    normalizer = DictionaryNormalizer(field_normalisers)
    data = {"field1": "value1", "field2": "value2"}

    # Act
    result = normalizer.normalise_dict(data)

    # Assert
    assert result == data

def test_normalise_dict_with_single_normaliser():
    # Arrange
    mock_normaliser = MagicMock(spec=Normalisable)
    mock_normaliser.normalise.return_value = "normalised_value"

    field_normalisers = {
        "field1": [mock_normaliser]
    }
    normalizer = DictionaryNormalizer(field_normalisers)
    data = {"field1": "value1", "field2": "value2"}

    # Act
    result = normalizer.normalise_dict(data)

    # Assert
    assert result == {"field1": "normalised_value", "field2": "value2"}
    mock_normaliser.normalise.assert_called_once_with("value1")

def test_normalise_dict_with_multiple_normalisers():
    # Arrange
    mock_normaliser1 = MagicMock(spec=Normalisable)
    mock_normaliser2 = MagicMock(spec=Normalisable)

    mock_normaliser1.normalise.return_value = "intermediate_value"
    mock_normaliser2.normalise.return_value = "final_value"

    field_normalisers = {
        "field1": [mock_normaliser1, mock_normaliser2]
    }
    normalizer = DictionaryNormalizer(field_normalisers)
    data = {"field1": "value1", "field2": "value2"}

    # Act
    result = normalizer.normalise_dict(data)

    # Assert
    assert result == {"field1": "final_value", "field2": "value2"}
    mock_normaliser1.normalise.assert_called_once_with("value1")
    mock_normaliser2.normalise.assert_called_once_with("intermediate_value")

def test_normalise_dict_with_field_not_in_normalisers():
    # Arrange
    mock_normaliser = MagicMock(spec=Normalisable)
    mock_normaliser.normalise.return_value = "normalised_value"

    field_normalisers = {
        "field1": [mock_normaliser]
    }
    normalizer = DictionaryNormalizer(field_normalisers)
    data = {"field2": "value2"}

    # Act
    result = normalizer.normalise_dict(data)

    # Assert
    assert result == {"field2": "value2"}
    mock_normaliser.normalise.assert_not_called()

def test_normalise_dict_with_empty_data():
    # Arrange
    field_normalisers = {
        "field1": [MagicMock(spec=Normalisable)]
    }
    normalizer = DictionaryNormalizer(field_normalisers)
    data = {}

    # Act
    result = normalizer.normalise_dict(data)

    # Assert
    assert result == {}