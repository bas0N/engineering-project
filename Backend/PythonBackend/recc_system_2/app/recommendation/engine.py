from sqlalchemy import MetaData, Table, select
import numpy as np

from pymongo import MongoClient
import certifi
from pymongo import MongoClient
import certifi
from recc_system_2.app.database.mongo import get_mongo_collection
from recc_system_2.app.config.db_config import Config



def fetch_product_details_for_recommendations(recommendations):
    # Extract all item_ids from recommendations
    item_ids = [rec["item_id"] for rec in recommendations]

    collection = get_mongo_collection(Config.MONGO_COLLECTION_NAME_PRODUCTS)

    # Query all products with asin in the item_ids list
    cursor = collection.find({"parent_asin": {"$in": item_ids}})
    docs = list(cursor)

    # Create a mapping from asin to product doc
    doc_map = {doc["parent_asin"]: doc for doc in docs if "parent_asin" in doc}

    # Transform each recommendation with product details
    detailed_recommendations = []
    for rec in recommendations:
        item_id = rec["item_id"]
        doc = doc_map.get(item_id)

        if doc:
            average_rating = doc.get("average_rating", 0.0)
            description_list = doc.get("description", [])
            description = description_list[0] if description_list and isinstance(description_list[0], str) else ""
            images = doc.get("images", [])
            image_url = images[0]["large"] if images and "large" in images[0] else ""
            main_category = doc.get("main_category", "")
            price_str = doc.get("price", 0)
            try:
                price = float(price_str)
            except ValueError:
                price = 0.0
            rating_number = doc.get("rating_number", 0)
            title = doc.get("title", "")
            ids = item_id  # from doc.get("asin", item_id) but already have item_id

            detailed_rec = {
                "fit_ratio": rec.get("fit_ratio", 0),
                "average_rating": average_rating,
                "description": description,
                "ids": ids,
                "image": image_url,
                "main_category": main_category,
                "price": price,
                "rating_number": rating_number,
                "title": title
            }
            detailed_recommendations.append(detailed_rec)
        else:
            # If no doc found, just append the recommendation as-is or handle accordingly
            detailed_recommendations.append(rec)

    return detailed_recommendations


def fetch_top_n_recommendations(engine, user_id, n):
    metadata = MetaData()
    metadata.reflect(engine)

    # Reflect the tables
    users_table = Table('users', metadata, autoload_with=engine)
    items_table = Table('items', metadata, autoload_with=engine)
    user_factors_table = Table('user_factors', metadata, autoload_with=engine)
    item_factors_table = Table('item_factors', metadata, autoload_with=engine)

    with engine.connect() as conn:
        with conn.begin():
            # Fetch user factors
            user_factor_query = select(user_factors_table).where(user_factors_table.c.user_id == user_id)
            user_factors_result = conn.execute(user_factor_query).mappings()  # Ensures row is dict-like
            user_factors = {row['factor_index']: row['value'] for row in user_factors_result}

            if not user_factors:
                return []

            user_vector = np.array([user_factors.get(i, 0) for i in range(max(user_factors.keys()) + 1)])

            # Fetch item factors
            item_factor_query = select(item_factors_table)
            item_factors_result = conn.execute(item_factor_query).mappings()  # Ensures row is dict-like
            item_factors = {}
            for row in item_factors_result:
                item_id = row['item_id']
                factor_index = row['factor_index']
                value = row['value']
                if item_id not in item_factors:
                    item_factors[item_id] = {}
                item_factors[item_id][factor_index] = value

            if not item_factors:
                return []

            max_factor_index = max(max(factors.keys()) for factors in item_factors.values())
            item_vectors = {
                item_id: np.array([factors.get(i, 0) for i in range(max_factor_index + 1)])
                for item_id, factors in item_factors.items()
            }

            # Calculate similarity scores
            similarity_scores = {
                item_id: np.dot(user_vector, item_vector)
                for item_id, item_vector in item_vectors.items()
            }

            max_score = max(similarity_scores.values()) if similarity_scores else 1
            normalized_scores = {
                item_id: (score / max_score) * 100
                for item_id, score in similarity_scores.items()
            }

            # Sort and fetch top N items
            sorted_items = sorted(normalized_scores.items(), key=lambda x: x[1], reverse=True)
            top_n_items = [item_id for item_id, _ in sorted_items[:n]]

            item_details_query = select(items_table).where(items_table.c.item_id.in_(top_n_items))
            item_details_result = conn.execute(item_details_query).mappings()  # Ensures row is dict-like
            item_details = {row['item_id']: dict(row) for row in item_details_result}

            # Prepare recommendations
            recommendations = [
                {
                    "item_id": item_id,
                    "fit_ratio": round(score, 2),
                    **item_details.get(item_id, {})
                }
                for item_id, score in sorted_items[:n]
            ]

            return recommendations
