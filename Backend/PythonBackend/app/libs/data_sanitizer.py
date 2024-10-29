from typing import Set, Optional, Union, Dict, List
from dataclasses import dataclass, field
from enums import ProductColumnsEmbedded, ProductColumnsToEmbed
import pandas as pd
import numpy as np
from sklearn.preprocessing import QuantileTransformer
import os

class NullValueHandler:
    @staticmethod
    def drop_null(value):
        """Return None if the value is null, otherwise return the value."""
        return value if pd.notnull(value) else None

# Short text filter class
class ShortTextFilter:
    def __init__(self, min_words=4):
        self.min_words = min_words

    def filter_short_text(self, value):
        """Remove text if it has fewer than min_words words, otherwise return the text."""
        if isinstance(value, str) and len(value.split()) >= self.min_words:
            return value
        return None  # Reject if it doesn't meet the word requirement

# Text sanitizer class with injected stop words
class TextSanitizer:
    def __init__(self, stop_words: Optional[Set[str]] = None):
        self.stop_words = stop_words or set()

    def sanitize_text(self, value):
        """Sanitize text by removing special characters, extra spaces, and stopwords."""
        if isinstance(value, str):
            clean_words = [
                re.sub(r'\W+', '', word) for word in value.split() if word.lower() not in self.stop_words
            ]
            return " ".join(clean_words)
        return value  # If it's not a string, leave it as is
class PriceNormalizer:
    def __init__(self, file_path="quantile_transformer.joblib"):
        self.quantile_transformer = None
        self.file_path = file_path
        self._load_transformer()

        # Verify the existence and loading of the transformer
        if not self.verify_transformer():
            raise FileNotFoundError(
                f"Transformer file '{self.file_path}' not found or transformer is not loaded."
            )

    def _save_transformer(self):
        """Save the fitted transformer to a file."""
        if self.quantile_transformer:
            joblib.dump(self.quantile_transformer, self.file_path)

    def _load_transformer(self):
        """Load the transformer from the file if it exists."""
        if os.path.exists(self.file_path):
            self.quantile_transformer = joblib.load(self.file_path)

    def verify_transformer(self):
        """
        Verify the existence of the transformer file and the presence of the transformer.

        Returns:
        - bool: True if both file exists and transformer is loaded, False otherwise.
        """
        file_exists = os.path.exists(self.file_path)
        transformer_loaded = self.quantile_transformer is not None
        return file_exists and transformer_loaded

    def fit(self, price_data):
        """
        Fit the QuantileTransformer on provided price data and save the transformer.

        Parameters:
        - price_data: List or numpy array of prices to fit the transformer on.
        """
        price_data = np.array(price_data).reshape(-1, 1)
        self.quantile_transformer = QuantileTransformer(output_distribution="uniform", random_state=0)
        self.quantile_transformer.fit(price_data)
        self._save_transformer()  # Save the transformer after fitting

    def transform(self, price):
        """
        Transform a single price using the fitted quantile transformer.

        Parameters:
        - price: Single price value to normalize.

        Returns:
        - Normalized price or raises an error if the transformer is not fitted.
        """
        if self.quantile_transformer:
            transformed_price = self.quantile_transformer.transform([[price]])
            return transformed_price[0][0]  # Return the normalized price as a single value
        else:
            raise ValueError("The quantile transformer is not fitted. Please call `fit` first.")

    def update_and_refit(self, new_price_data):
        """
        Update the transformer with new price data by re-fitting and save the updated transformer.

        Parameters:
        - new_price_data: List or numpy array of new prices to add for fitting.
        """
        if self.quantile_transformer:
            # Get the inverse-transformed data to approximate the original distribution
            current_data = self.quantile_transformer.inverse_transform(np.linspace(0, 1, 100).reshape(-1, 1))
            # Combine current and new data, then re-fit
            updated_data = np.concatenate((current_data, np.array(new_price_data).reshape(-1, 1)), axis=0)
            self.fit(updated_data)
        else:
            # Fit directly if no previous transformer exists
            self.fit(new_price_data)

class DataSanitizer:
    null_handler: NullValueHandler
    short_text_filter: ShortTextFilter
    text_sanitizer: TextSanitizer
    price_normalizer: PriceNormalizer
    column_handlers: Dict[ProductColumnsEmbedded, callable] = field(init=False)

    def __post_init__(self):
        """Initialize column-specific handlers."""
        self.column_handlers = {
            ProductColumnsToEmbed.ID: self.null_handler.drop_null,
            ProductColumnsToEmbed.TITLE: lambda v: self.short_text_filter.filter_short_text(self.text_sanitizer.sanitize_text(v)),
            ProductColumnsToEmbed.DESCRIPTION: lambda v: self.short_text_filter.filter_short_text(
                self.text_sanitizer.sanitize_text(" ".join(v) if isinstance(v, list) else v)
            ),            ProductColumnsToEmbed.MAIN_CATEGORY: self.null_handler.drop_null,
            ProductColumnsToEmbed.PRICE: self.price_normalizer.transform,
        }

    def clean_value(self, value: Union[str, float, int], column: ProductColumnsToEmbed) -> Optional[Union[str, float, int]]:
        """
        Clean a single value based on the specified column name, returning None if the value is NaN.

        Args:
            value (Union[str, float, int]): The value to clean.
            column (Column): The name of the column, which determines the cleaning process.

        Returns:
            Optional[Union[str, float, int]]: The cleaned value, or None if it does not meet the criteria.
        """
        # Check if value is NaN, handling both scalars and arrays
        if pd.isna(value).any() if isinstance(value, (list, pd.Series, pd.DataFrame)) else pd.isna(value):
            return None

        # Apply the specific handler for the column
        handler = self.column_handlers.get(column)
        return handler(value) if handler else value

    def clean_dataframe(self, df: pd.DataFrame) -> pd.DataFrame:
        """
        Clean the DataFrame by applying all cleaning functions to each column.

        Args:
            df (pd.DataFrame): The input DataFrame with columns 'id', 'title', 'main_category', 'price', 'description'.

        Returns:
            pd.DataFrame: The cleaned DataFrame.
        """
        # Apply cleaning steps on DataFrame columns
        for col in ProductColumnsToEmbed:
            if col.value in df.columns:  # Check if column exists in DataFrame
                df[col.value] = df[col.value].apply(lambda x: self.clean_value(x, col))
            else:
                raise KeyError(f"Column '{col.value}' not found in DataFrame")

        # Drop any rows that now have None values after cleaning
        df = df.dropna()

        return df