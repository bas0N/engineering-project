from sqlalchemy import MetaData, Table, Column, Integer, String, Float
from sqlalchemy.exc import SQLAlchemyError

def save_latent_factors_to_db(engine, reviews, user_factors, item_factors):
    metadata = MetaData()
    users_table = Table('users', metadata,
                        Column('id', Integer, primary_key=True, autoincrement=True),
                        Column('user_id', String(255), unique=True))
    items_table = Table('items', metadata,
                        Column('id', Integer, primary_key=True, autoincrement=True),
                        Column('item_id', String(255), unique=True))
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

    metadata.create_all(engine)

    user_mapping = {uid: i for i, uid in enumerate(reviews['user_id'].astype('category').cat.categories)}
    item_mapping = {iid: i for i, iid in enumerate(reviews['asin'].astype('category').cat.categories)}

    try:
        with engine.connect() as conn:
            with conn.begin():
                conn.execute(users_table.insert(), [{'user_id': uid} for uid in user_mapping.keys()])
                conn.execute(items_table.insert(), [{'item_id': iid} for iid in item_mapping.keys()])

                user_factor_records = [
                    {'user_id': uid, 'factor_index': idx, 'value': float(value)}
                    for uid, factors in zip(user_mapping.keys(), user_factors)
                    for idx, value in enumerate(factors)
                ]
                conn.execute(user_factors_table.insert(), user_factor_records)

                item_factor_records = [
                    {'item_id': iid, 'factor_index': idx, 'value': float(value)}
                    for iid, factors in zip(item_mapping.keys(), item_factors)
                    for idx, value in enumerate(factors)
                ]
                conn.execute(item_factors_table.insert(), item_factor_records)

    except SQLAlchemyError as e:
        print("Error during database operations:", e)
        raise

    print("Latent factors saved to database.")
