from flask import Blueprint, jsonify, request
from .services import service_a, service_b

main_routes = Blueprint('main_routes', __name__)

@main_routes.route('/service_a', methods=['POST'])
def use_service_a_post():
    # Get JSON data from the request
    data = request.get_json()

    # Process data with service A
    response_data = service_a.process_data_from_service_a(data)

    # Return response as JSON
    return jsonify(response_data), 201

@main_routes.route('/service_b', methods=['GET'])
def use_service_b():
    data = service_b.get_data_from_service_b()
    return jsonify(data)
