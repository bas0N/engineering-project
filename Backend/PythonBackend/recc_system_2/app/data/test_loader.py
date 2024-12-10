import pytest
import pandas as pd
from unittest.mock import MagicMock
from loader import load_n_products

def test_load_n_products():
    # Mock the collection object
    mock_collection = MagicMock()

    # Mock return value for the aggregate function
    mock_collection.aggregate.return_value = [
        {"product_id": 1, "name": "Product A"},
        {"product_id": 2, "name": "Product B"}
    ]

    # Call the function
    df = load_n_products(mock_collection, n=2)

    # Assertions
    assert isinstance(df, pd.DataFrame)  # Check if the result is a DataFrame
    assert len(df) == 2  # Check the number of rows
    assert list(df.columns) == ["product_id", "name"]  # Check the column names

    # Verify that the aggregate method was called correctly
    mock_collection.aggregate.assert_called_once_with(
        [{"$limit": 2}], allowDiskUse=True
    )
