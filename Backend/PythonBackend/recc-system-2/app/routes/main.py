from flask import Blueprint, request, jsonify
from app.database.mysql import get_mysql_engine
from app.recommendation.engine import fetch_top_n_recommendations

bp = Blueprint('main', __name__)

@bp.route('/recommend', methods=['GET'])
def recommend():
    user_id = request.args.get('user_id', default=None, type=str)
    n = request.args.get('n', default=10, type=int)

    if not user_id:
        return jsonify({"error": "user_id parameter is required"}), 400

    engine = get_mysql_engine()
    print("user_id",user_id, n)
    recommendations = fetch_top_n_recommendations(engine, user_id, n)
    return jsonify(recommendations), 200
