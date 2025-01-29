import pytest
from unittest.mock import Mock
from .interfaces import Cleanable
from .dict_cleaner import DictionaryCleaner

@pytest.fixture
def mock_cleanable():
    return Mock(spec=Cleanable)

@pytest.fixture
def mock_cleanable_none():
    cleaner = Mock(spec=Cleanable)
    cleaner.clean.return_value = None
    return cleaner

def test_clean_dict_with_no_field_cleaners():
    data = {"field1": "value1", "field2": "value2"}
    cleaner = DictionaryCleaner(field_cleaners={})

    result = cleaner.clean_dict(data)

    assert result == data

def test_clean_dict_with_single_cleaner(mock_cleanable):
    mock_cleanable.clean.side_effect = lambda x: x.upper()
    field_cleaners = {"field1": [mock_cleanable]}
    data = {"field1": "value1", "field2": "value2"}

    cleaner = DictionaryCleaner(field_cleaners=field_cleaners)
    result = cleaner.clean_dict(data)

    assert result == {"field1": "VALUE1", "field2": "value2"}
    mock_cleanable.clean.assert_called_once_with("value1")

def test_clean_dict_with_multiple_cleaners(mock_cleanable):
    cleaner1 = Mock(spec=Cleanable)
    cleaner1.clean.side_effect = lambda x: x.upper()
    cleaner2 = Mock(spec=Cleanable)
    cleaner2.clean.side_effect = lambda x: x + "!"

    field_cleaners = {"field1": [cleaner1, cleaner2]}
    data = {"field1": "value1"}

    cleaner = DictionaryCleaner(field_cleaners=field_cleaners)
    result = cleaner.clean_dict(data)

    assert result == {"field1": "VALUE1!"}
    cleaner1.clean.assert_called_once_with("value1")
    cleaner2.clean.assert_called_once_with("VALUE1")

def test_clean_dict_returns_none_if_cleaner_returns_none(mock_cleanable_none):
    field_cleaners = {"field1": [mock_cleanable_none]}
    data = {"field1": "value1"}

    cleaner = DictionaryCleaner(field_cleaners=field_cleaners)
    result = cleaner.clean_dict(data)

    assert result is None
    mock_cleanable_none.clean.assert_called_once_with("value1")

def test_clean_dict_with_partial_cleaning(mock_cleanable):
    mock_cleanable.clean.side_effect = lambda x: x[::-1]
    field_cleaners = {"field1": [mock_cleanable]}
    data = {"field1": "value1", "field2": "value2"}

    cleaner = DictionaryCleaner(field_cleaners=field_cleaners)
    result = cleaner.clean_dict(data)

    assert result == {"field1": "1eulav", "field2": "value2"}
    mock_cleanable.clean.assert_called_once_with("value1")