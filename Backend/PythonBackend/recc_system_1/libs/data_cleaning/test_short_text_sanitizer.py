import pytest
from typing import List, Union
from .short_text_sanitizer import ShortTextCleaner

@pytest.fixture
def cleaner():
    return ShortTextCleaner(min_words=4)

def test_clean_single_string_valid(cleaner):
    text = "This is a valid text."
    result = cleaner.clean(text)
    assert result == text

def test_clean_single_string_invalid(cleaner):
    text = "Too short"
    result = cleaner.clean(text)
    assert result is None

def test_clean_invalid_type(cleaner):
    invalid_input = 12345  # Not a string or list
    result = cleaner.clean(invalid_input)
    assert result is None

def test_clean_empty_string(cleaner):
    text = ""
    result = cleaner.clean(text)
    assert result is None

def test_clean_empty_list(cleaner):
    texts = []
    result = cleaner.clean(texts)
    assert result == []

def test_custom_min_words():
    cleaner = ShortTextCleaner(min_words=2)
    text = "Short text"
    result = cleaner.clean(text)
    assert result == text
    invalid_text = "One"
    result = cleaner.clean(invalid_text)
    assert result is None
