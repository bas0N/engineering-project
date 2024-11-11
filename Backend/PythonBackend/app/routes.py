from flask import Blueprint, app, jsonify, request, current_app

main_routes = Blueprint('main_routes', __name__)

@main_routes.route('/products', methods=['POST'])
def add_product():
    # Get query parameters
    product_id = request.args.get('product_id')

    # Validate presence of required fields
    if not product_id:
        return jsonify({
            "error": "Invalid request",
            "message": " 'product_id' query parameter is required."
        }), 400  # HTTP 400 Bad Request

    try:
        data_processor = current_app.config.get('data_processor_instance')

        response_data = data_processor.add_product_by_id(product_id)
        if response_data is None:
            return jsonify({
                "error": "Not found",
                "message": "Product not found or no similar products found."
            }), 404
        return jsonify(response_data), 201
    except Exception as e:
        # Log any other error that occurs
        print("Error retrieving similar products: %s", str(e))
        return jsonify({
            "error": "Processing error",
            "message": "An error occurred while processing your request. Please try again later."
        }), 500  # HTTP 500 Internal Server Error

@main_routes.route('/products', methods=['GET'])
def get_products():
    # Get query parameters
    product_id = request.args.get('product_id')
    number_of_products = request.args.get('number_of_products')

    # Validate presence of required fields
    if not product_id or not number_of_products:

        return jsonify({
            "error": "Invalid request",
            "message": "Both 'product_id' and 'number_of_products' query parameters are required."
        }), 400  # HTTP 400 Bad Request

    try:
        data_processor = current_app.config.get('data_processor_instance')

        # Attempt to convert number_of_products to int and call product service
        number_of_products = int(number_of_products)

        response_data = data_processor.fetch_similar_products(product_id, number_of_products)
        if response_data is None:
            return jsonify({
                "error": "Not found",
                "message": "Product not found or no similar products found."
            }), 404
        return jsonify(response_data), 200
    except ValueError:
        # Log and handle case where number_of_products is not an integer
        print("Invalid type for 'number_of_products': expected integer")
        return jsonify({
            "error": "Invalid request",
            "message": "'number_of_products' must be an integer."
        }), 400
    except Exception as e:
        # Log any other error that occurs
        print("Error retrieving similar products: %s", str(e))
        return jsonify({
            "error": "Processing error",
            "message": "An error occurred while processing your request. Please try again later."
        }), 500  # HTTP 500 Internal Server Error

def register_routes(app):
    app.register_blueprint(main_routes)