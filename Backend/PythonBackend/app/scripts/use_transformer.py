import joblib
import numpy as np

# Load the QuantileTransformer from the joblib file
try:
    quantile_transformer = joblib.load("quantile_transformer.joblib")
    print("QuantileTransformer loaded successfully.")
except FileNotFoundError:
    print("Error: 'quantile_transformer.joblib' file not found. Please ensure the transformer is saved.")

# Define a price to normalize
price_to_normalize = 45

# Normalize the price
try:
    normalized_price = quantile_transformer.transform([[price_to_normalize]])
    print(f"Original price: {price_to_normalize}")
    print(f"Normalized price: {normalized_price[0][0]}")
except Exception as e:
    print(f"An error occurred during normalization: {e}")
