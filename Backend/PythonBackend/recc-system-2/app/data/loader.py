import pandas as pd

def load_n_products(collection, n=10):
    pipeline = [
        {"$limit": n}
    ]
    data = list(collection.aggregate(pipeline, allowDiskUse=True))
    df = pd.DataFrame(data)
    return df
