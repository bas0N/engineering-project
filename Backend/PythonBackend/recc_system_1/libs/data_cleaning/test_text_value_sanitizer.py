import pytest
from .text_value_sanitizer import TextContentCleaner

@pytest.fixture
def cleaner_with_stop_words():
    return TextContentCleaner(stop_words={"and", "the", "to"})

@pytest.fixture
def cleaner_without_stop_words():
    return TextContentCleaner()

def test_clean_with_valid_string_and_stop_words(cleaner_with_stop_words):
    input_text = "The quick brown fox jumps to the lazy dog and runs away."
    expected_output = "quick brown fox jumps lazy dog runs away"

    result = cleaner_with_stop_words.clean(input_text)
    assert result == expected_output

def test_clean_with_valid_string_without_stop_words(cleaner_without_stop_words):
    input_text = "The quick brown fox jumps to the lazy dog and runs away."
    expected_output = "The quick brown fox jumps to the lazy dog and runs away"

    result = cleaner_without_stop_words.clean(input_text)
    assert result == expected_output

def test_clean_with_empty_string(cleaner_with_stop_words):
    input_text = ""
    result = cleaner_with_stop_words.clean(input_text)
    assert result is None

def test_clean_with_non_string_input(cleaner_with_stop_words):
    non_string_input = 12345
    result = cleaner_with_stop_words.clean(non_string_input)
    assert result is None

def test_clean_removes_punctuation(cleaner_without_stop_words):
    input_text = "Hello, world! How's it going?"
    expected_output = "Hello world Hows it going"

    result = cleaner_without_stop_words.clean(input_text)
    assert result == expected_output

def test_clean_with_only_stop_words(cleaner_with_stop_words):
    input_text = "and the to and the"
    result = cleaner_with_stop_words.clean(input_text)
    assert result is None

def test_clean_with_mixed_case_stop_words(cleaner_with_stop_words):
    input_text = "And THE tO jump over and over again"
    expected_output = "jump over over again"

    result = cleaner_with_stop_words.clean(input_text)
    assert result == expected_output