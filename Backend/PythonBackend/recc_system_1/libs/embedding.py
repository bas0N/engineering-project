import numpy as np

class WeightedConcatenatedEmbeddingGenerator:
    def __init__(self, embedding_model, text_weights, numeric_weights, numeric_transformer):
        self.embedding_model = embedding_model
        self.text_weights = text_weights
        self.numeric_weights = numeric_weights
        self.numeric_transformer = numeric_transformer

    def generate_embedding(self, row):
        """
        Generate a weighted concatenated vector.

        Parameters:
        - row: A row from a pandas DataFrame (pandas Series) containing data values for each Column.

        Returns:
        - Numpy array representing the weighted concatenated vector.
        """
        vector = []

        # Process text columns
        for column, weight in self.text_weights.items():
            if column.value in row and isinstance(row[column.value], str):
                embedding = self.embedding_model(row[column.value])
                weighted_embedding = weight * np.array(embedding)
                vector.append(weighted_embedding)

        # Process numeric columns
        for column, weight in self.numeric_weights.items():
            if column == ProductColumnsToEmbed.PRICE and column.value in row:
                normalized_value = self.numeric_transformer.transform(row[column.value])
                weighted_value = weight * normalized_value
                vector.append([weighted_value])

        # Concatenate all weighted components into a single vector
        concatenated_vector = np.concatenate(vector)
        return concatenated_vector