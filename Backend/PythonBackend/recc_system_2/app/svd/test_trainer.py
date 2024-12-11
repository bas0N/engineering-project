import pytest
import pandas as pd
import numpy as np
from trainer import train_svd  # Replace `your_module` with the name of your module file.

def test_train_svd():
    # Mock input data
    reviews = pd.DataFrame({
        'user_id': ['user1', 'user2', 'user1', 'user3'],
        'asin': ['item1', 'item2', 'item3', 'item1'],
        'rating': [5, 4, 3, 2]
    })

    # Call the function
    user_factors, item_factors = train_svd(reviews, n_components=2)

    # Assertions
    assert isinstance(user_factors, np.ndarray)  # Check that user_factors is a numpy array
    assert isinstance(item_factors, np.ndarray)  # Check that item_factors is a numpy array

    assert user_factors.shape[1] == 2  # Ensure the number of components is correct
    assert item_factors.shape[1] == 2  # Ensure the number of components is correct
    assert user_factors.shape[0] == len(reviews['user_id'].astype('category').cat.categories)  # Check user factor rows
    assert item_factors.shape[0] == len(reviews['asin'].astype('category').cat.categories)  # Check item factor rows

    # Verify SVD components produce meaningful reductions
    assert user_factors.shape[1] == item_factors.shape[1]  # Dimensions should match the number of components
