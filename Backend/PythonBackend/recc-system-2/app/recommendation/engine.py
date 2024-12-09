from sqlalchemy import MetaData, Table, select
import numpy as np

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
