import numpy as np
from sklearn.preprocessing import QuantileTransformer
import joblib

# Generate some example price data
sample_price_data = np.array([10, 20, 15, 30, 25, 50, 40, 35, 60, 70]).reshape(-1, 1)

# Initialize and fit the QuantileTransformer
quantile_transformer = QuantileTransformer(output_distribution="uniform", random_state=0)
quantile_transformer.fit(sample_price_data)

# Save the fitted transformer to a joblib file
joblib.dump(quantile_transformer, "quantile_transformer.joblib")

print("QuantileTransformer has been saved to 'quantile_transformer.joblib'")


## modify it to pass file first