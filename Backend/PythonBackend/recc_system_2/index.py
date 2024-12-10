#!/usr/bin/env python
# coding: utf-8

# In[1]:


# Data processing
import pandas as pd
import numpy as np
import scipy.stats

# Visualization
import seaborn as sns

# Similarity
from sklearn.metrics.pairwise import cosine_similarity
from scipy.sparse.linalg import svds
import numpy as np
import pandas as pd
import pymongo
from pymongo import MongoClient, errors
import certifi
import pymysql
pymysql.install_as_MySQLdb()


# In[2]:


client = pymongo.MongoClient("mongodb+srv://admin:48J4OcNj2GPVnuqN@engineering-proj-dev.a7fitxf.mongodb.net/dev?retryWrites=true&w=majority&appName=engineering-proj-dev",tlsCAFile=certifi.where())
db = client['dev']
collection = db['reviews_Health_and_Personal_Care']


# In[3]:


def load_n_products(n=10):  # Default to fetching reviews for the top 10 products
    pipeline = [
        {"$limit": n}
    ]

    # Execute the aggregation pipeline with allowDiskUse enabled
    data = list(collection.aggregate(pipeline, allowDiskUse=True))
    df = pd.DataFrame(data)
    return df


# In[4]:


reviews = load_n_products(1000)


# In[5]:


len(reviews)


# In[7]:


from scipy.sparse import csr_matrix
from sklearn.decomposition import TruncatedSVD
user_codes = reviews['user_id'].astype('category').cat.codes
item_codes = reviews['asin'].astype('category').cat.codes

# Create a sparse matrix
sparse_matrix = csr_matrix((reviews['rating'], (user_codes, item_codes)))
# Apply SVD
svd = TruncatedSVD(n_components=30, random_state=42)  # Adjust n_components as needed
user_factors = svd.fit_transform(sparse_matrix)       # User latent factors
item_factors = svd.components_.T


# In[ ]:


import pandas as pd
import numpy as np
from sqlalchemy import create_engine, MetaData, Table, Column, Integer, String, Float

from sqlalchemy.exc import SQLAlchemyError

def save_latent_factors_to_db(connection_string, reviews, user_factors, item_factors):
    # Establish a database connection
    engine = create_engine(connection_string)
    metadata = MetaData()

    # Define tables
    users_table = Table('users', metadata,
                        Column('id', Integer, primary_key=True, autoincrement=True),
                        Column('user_id', String(255), unique=True))  # Specify length

    items_table = Table('items', metadata,
                        Column('id', Integer, primary_key=True, autoincrement=True),
                        Column('item_id', String(255), unique=True))  # Specify length

    user_factors_table = Table('user_factors', metadata,
                               Column("id", Integer, primary_key=True, autoincrement=True),
                               Column('user_id', String(255)),
                               Column('factor_index', Integer),
                               Column('value', Float))

    item_factors_table = Table('item_factors', metadata,
                               Column("id", Integer, primary_key=True, autoincrement=True),
                               Column('item_id', String(255)),
                               Column('factor_index', Integer),
                               Column('value', Float))

    # Create tables in the database
    metadata.create_all(engine)

    # Map user and item IDs to database IDs
    user_mapping = {uid: i for i, uid in enumerate(reviews['user_id'].astype('category').cat.categories)}
    item_mapping = {iid: i for i, iid in enumerate(reviews['asin'].astype('category').cat.categories)}

    # Insert data
    try:
        with engine.connect() as conn:
            with conn.begin():
                # Insert users
                conn.execute(users_table.insert(), [{'user_id': uid} for uid in user_mapping.keys()])

                # Insert items
                conn.execute(items_table.insert(), [{'item_id': iid} for iid in item_mapping.keys()])

                # Insert user factors
                user_factor_records = [
                    {'user_id': uid, 'factor_index': idx, 'value': float(value)}
                    for uid, factors in zip(user_mapping.keys(), user_factors)
                    for idx, value in enumerate(factors)
                ]
                conn.execute(user_factors_table.insert(), user_factor_records)

                # Insert item factors
                item_factor_records = [
                    {'item_id': iid, 'factor_index': idx, 'value': float(value)}
                    for iid, factors in zip(item_mapping.keys(), item_factors)
                    for idx, value in enumerate(factors)
                ]
                print("Item Factor Records:", item_factor_records)
                conn.execute(item_factors_table.insert(), item_factor_records)

    except SQLAlchemyError as e:
        print("Error during database operations:", e)
        raise

    print("Latent factors saved to database.")


# In[17]:


save_latent_factors_to_db(
    connection_string="mysql://myuser:mypassword@localhost:3306/mydatabase",
    reviews=reviews,
    user_factors=user_factors,
    item_factors=item_factors
)


# In[64]:


from sqlalchemy import create_engine, Table, MetaData, select
import numpy as np

def fetch_top_n_recommendations(connection_string, user_id, n):
    # Establish a database connection
    engine = create_engine(connection_string)
    metadata = MetaData()

    # Reflect the tables
    users_table = Table('users', metadata, autoload_with=engine)
    items_table = Table('items', metadata, autoload_with=engine)
    user_factors_table = Table('user_factors', metadata, autoload_with=engine)
    item_factors_table = Table('item_factors', metadata, autoload_with=engine)

    with engine.connect() as conn:
        with conn.begin():
            user_factor_query = select(user_factors_table).where(user_factors_table.c.user_id == user_id)
            user_factors = {row[2]: row[3] for row in conn.execute(user_factor_query)}  # Adjusted indices
            user_vector = np.array([user_factors.get(i, 0) for i in range(max(user_factors.keys()) + 1)])

            # Fetch all items and their latent factors
            item_factor_query = select(item_factors_table)
            item_factors = {}
            for row in conn.execute(item_factor_query):
                item_id = row[1]  # Adjusted index
                factor_index = row[2]  # Adjusted index
                value = row[3]  # Adjusted index
                if item_id not in item_factors:
                    item_factors[item_id] = {}
                item_factors[item_id][factor_index] = value

            # Convert item factors to vectors
            item_vectors = {
                item_id: np.array([factors.get(i, 0) for i in range(max(factors.keys()) + 1)])
                for item_id, factors in item_factors.items()
            }

            # Compute similarity scores
            similarity_scores = {
                item_id: np.dot(user_vector, item_vector)
                for item_id, item_vector in item_vectors.items()
            }

            # Normalize scores to a percentage
            max_score = max(similarity_scores.values()) if similarity_scores else 1
            normalized_scores = {
                item_id: (score / max_score) * 100
                for item_id, score in similarity_scores.items()
            }

            # Sort items by similarity score
            sorted_items = sorted(normalized_scores.items(), key=lambda x: x[1], reverse=True)

            # Fetch top N items
            top_n_items = [item_id for item_id, _ in sorted_items[:n]]

            # Fetch item details
            item_details_query = select(items_table).where(items_table.c.item_id.in_(top_n_items))
            item_details = {row[1]: dict(row._mapping) for row in conn.execute(item_details_query)}  # Map item details by ID

            # Combine item details with scores
            recommendations = [
                {
                    "item_id": item_id,
                    "fit_ratio": round(score, 2),
                    **item_details.get(item_id, {})
                }
                for item_id, score in sorted_items[:n]
            ]

            return recommendations


# In[66]:


fetch_top_n_recommendations(
    connection_string="mysql+mysqlconnector://myuser:mypassword@localhost:3306/mydatabase",
    user_id="AE2DHR54CMZQTA3ST6AXQR7DQJTA",
    n=10
)


# In[ ]:




