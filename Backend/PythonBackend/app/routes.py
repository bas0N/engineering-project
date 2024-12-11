from flask import Blueprint, app, jsonify, request, current_app
from . import listeners

main_routes = Blueprint('main_routes', __name__)

@main_routes.route('/recc-system-1', methods=['POST'])
def add_product():
    # Get query parameters
    product_id = request.args.get('product_id')

    # Validate presence of required fields
    if not product_id:
        response = jsonify({
            "error": "Invalid request",
            "message": "'product_id' query parameter is required."
        })
        response.status_code = 400  # HTTP 400 Bad Request
        response.headers.add('Access-Control-Allow-Origin', '*')
        return response

    try:
        data_processor = current_app.config.get('data_processor_instance')

        response_data = data_processor.add_product_by_id(product_id)
        if response_data is None:
            response = jsonify({
                "error": "Not found",
                "message": "Product not found or no similar products found."
            })
            response.status_code = 404  # HTTP 404 Not Found
            response.headers.add('Access-Control-Allow-Origin', '*')
            return response

        response = jsonify(response_data)
        response.status_code = 201  # HTTP 201 Created
        response.headers.add('Access-Control-Allow-Origin', '*')
        return response

    except Exception as e:
        # Log any other error that occurs
        print("Error retrieving similar products: %s", str(e))
        response = jsonify({
            "error": "Processing error",
            "message": "An error occurred while processing your request. Please try again later."
        })
        response.status_code = 500  # HTTP 500 Internal Server Error
        response.headers.add('Access-Control-Allow-Origin', '*')
        return response


@main_routes.route('/recc-system-1', methods=['GET'])
def get_products():
    # Get query parameters
    product_id = request.args.get('product_id')
    number_of_products = request.args.get('number_of_products')

    # Validate presence of required fields
    if not product_id or not number_of_products:
        response = jsonify({
            "error": "Invalid request",
            "message": "Both 'product_id' and 'number_of_products' query parameters are required."
        })
        response.status_code = 400  # HTTP 400 Bad Request
        response.headers.add('Access-Control-Allow-Origin', '*')
        return response

    try:
        data_processor = current_app.config.get('data_processor_instance')

        # Attempt to convert number_of_products to int and call product service
        number_of_products = int(number_of_products)

        response_data = data_processor.fetch_similar_products(product_id, number_of_products)
        if response_data is None:
            response = jsonify({
                "error": "Not found",
                "message": "Product not found or no similar products found."
            })
            response.status_code = 404  # HTTP 404 Not Found
            response.headers.add('Access-Control-Allow-Origin', '*')
            return response

        response = jsonify(response_data)
        response.status_code = 200  # HTTP 200 OK
        response.headers.add('Access-Control-Allow-Origin', '*')
        return response

    except ValueError as e:
        # Log and handle case where number_of_products is not an integer
        print("Invalid type for 'number_of_products': expected integer")
        response = jsonify({
            "error": "Invalid request: " + str(e),
            "message": "'number_of_products' must be an integer."
        })
        response.status_code = 400  # HTTP 400 Bad Request
        response.headers.add('Access-Control-Allow-Origin', '*')
        return response

    except Exception as e:
        # Log any other error that occurs
        print("Error retrieving similar products: %s", str(e))
        response = jsonify({
            "message": "An error occurred while processing your request. Please try again later.",
            "error": str(e)
        })
        response.status_code = 500  # HTTP 500 Internal Server Error
        response.headers.add('Access-Control-Allow-Origin', '*')
        return response


def register_routes(app):
    app.register_blueprint(main_routes)