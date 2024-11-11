from flask import Blueprint, app, jsonify, request, current_app

main_routes = Blueprint('main_routes', __name__)


@main_routes.route('/products', methods=['POST'])
def add_product():
    # Get JSON data from the request
    data = request.get_json()

    # Define expected fields and their types
    expected_fields = {
        "parent_asin": str,
        "title": str,
        "description": str,
        "main_category": str,
        "price": float
    }

    # Validate presence and types of required fields
    missing_fields = [field for field in expected_fields if field not in data]
    type_mismatches = [
        field for field, field_type in expected_fields.items()
        if field in data and not isinstance(data[field], field_type)
    ]

    if missing_fields:
        return jsonify({
            "error": "Invalid request",
            "message": f"Missing required fields: {', '.join(missing_fields)}"
        }), 400

    if type_mismatches:
        return jsonify({
            "error": "Invalid request",
            "message": f"Type mismatch for fields: {', '.join(type_mismatches)}"
        }), 400

    # Process data with service A
    try:
        data_processor = current_app.config.get('data_processor_instance')
        if not data_processor:
            return jsonify({"error": "Data processor instance not found"}), 500
        response_data = product_service.add_single_dict(data)
    except Exception as e:
        # Log the error
        print("Error processing data with service A: %s", str(e))
        # Return an error response to the user
        return jsonify({
            "error": "Processing error",
            "message": "An error occurred while processing your request. Please try again later."
        }), 500  # HTTP 500 Internal Server Error

    # Return response as JSON
    return jsonify(response_data), 201

# @main_routes.route('/products', methods=['GET'])
# def get_products():
#     # Get query parameters
#     product_id = request.args.get('product_id')
#     number_of_products = request.args.get('number_of_products')

#     # Validate presence of required fields
#     if not product_id or not number_of_products:
#         return jsonify({
#             "error": "Invalid request",
#             "message": "Both 'product_id' and 'number_of_products' query parameters are required."
#         }), 400  # HTTP 400 Bad Request

#     try:
#         # Attempt to convert number_of_products to int and call product service
#         number_of_products = int(number_of_products)

#         data = product_service.get_similar_products(product_id, number_of_products)
#     except ValueError:
#         # Log and handle case where number_of_products is not an integer
#         print("Invalid type for 'number_of_products': expected integer")
#         return jsonify({
#             "error": "Invalid request",
#             "message": "'number_of_products' must be an integer."
#         }), 400
#     except Exception as e:
#         # Log any other error that occurs
#         print("Error retrieving similar products: %s", str(e))
#         return jsonify({
#             "error": "Processing error",
#             "message": "An error occurred while processing your request. Please try again later."
#         }), 500  # HTTP 500 Internal Server Error

#     return jsonify(data)
def register_routes(app):
    app.register_blueprint(main_routes)