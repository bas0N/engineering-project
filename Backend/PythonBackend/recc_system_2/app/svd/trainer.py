from scipy.sparse import csr_matrix
from sklearn.decomposition import TruncatedSVD
import pandas as pd
import numpy as np

def train_svd(reviews, n_components=30):
    user_codes = reviews['user_id'].astype('category').cat.codes
    item_codes = reviews['asin'].astype('category').cat.codes

    sparse_matrix = csr_matrix((reviews['rating'], (user_codes, item_codes)))

    svd = TruncatedSVD(n_components=n_components, random_state=42)
    user_factors = svd.fit_transform(sparse_matrix)
    item_factors = svd.components_.T

    return user_factors, item_factors
