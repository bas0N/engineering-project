import pytest
from .array_text_value_cleaner import ArrayTextContentCleaner

@pytest.fixture
def cleaner():
    """Fixture to initialize the cleaner with default settings."""
    return ArrayTextContentCleaner()

@pytest.fixture
def cleaner_with_stop_words():
    """Fixture to initialize the cleaner with a set of stop words."""
    stop_words = {"and", "the", "is"}
    return ArrayTextContentCleaner(stop_words=stop_words)

def test_clean_with_stop_words(cleaner_with_stop_words):
    """Test cleaning input list with stop words."""
    input_data = ["The quick brown fox", "jumps over and over"]
    expected_output = "quick brown fox jumps over over"
    assert cleaner_with_stop_words.clean(input_data) == expected_output

def test_clean_empty_list(cleaner):
    """Test cleaning an empty list."""
    input_data = []
    assert cleaner.clean(input_data) is None

def test_clean_non_string_items(cleaner):
    """Test cleaning a list with non-string items."""
    input_data = ["Hello", 123, None]
    assert cleaner.clean(input_data) is None

def test_clean_invalid_input(cleaner):
    """Test cleaning invalid input (not a list)."""
    input_data = "Not a list"
    assert cleaner.clean(input_data) is None

