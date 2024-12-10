from flask import Blueprint, request, jsonify
from recc_system_2.app.database.mysql import get_mysql_engine
from recc_system_2.app.recommendation.engine import fetch_top_n_recommendations,fetch_product_details_for_recommendations

bp = Blueprint('main', __name__)

@bp.route('/recc-system-2', methods=['GET'])
def recommend():
    user_id = request.headers.get('userId')  # Get user_id from headers
    n = request.args.get('number_of_products', default=10, type=int)

    if not user_id:
        return jsonify({"error": "Forbidden: userId header is required"}), 403

    engine = get_mysql_engine()
    print("user_id", user_id, n)
    recommendations = fetch_top_n_recommendations(engine, user_id, n)
    final_output = fetch_product_details_for_recommendations(recommendations)
    return jsonify(final_output), 200
