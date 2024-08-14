# Product API Documentation

## Overview

This API allows you to fetch a list of products with various filtering, sorting, and pagination options.

## Endpoint

`GET /api/products`

## Query Parameters

- **`page`**: _Optional_ - The page number for pagination. Default is `1`.
- **`limit`**: _Optional_ - The number of products per page. Default is `10`.
- **`sort`**: _Optional_ - Field by which to sort the results. Default is `title`.
- **`main_category`**: _Optional_ - Filter products by main category.
- **`title`**: _Optional_ - Filter products by title. Supports case-insensitive search.
- **`min_price`**: _Optional_ - Filter products with a price greater than or equal to this value.
- **`max_price`**: _Optional_ - Filter products with a price less than or equal to this value.
- **`min_rating`**: _Optional_ - Filter products with an average rating greater than or equal to this value.
- **`max_rating`**: _Optional_ - Filter products with an average rating less than or equal to this value.
- **`categories`**: _Optional_ - Filter products by categories. Accepts a comma-separated string of category names.
- **`store`**: _Optional_ - Filter products by store.

## Response

The API returns a paginated list of products based on the query parameters.

### Example Response

```json
{
  "docs": [
    {
      "main_category": "Electronics",
      "title": "Smartphone X",
      "average_rating": 4.5,
      "rating_number": 120,
      "features": ["4G LTE", "64GB Storage", "12MP Camera"],
      "description": ["Latest model with high performance"],
      "price": 499.99,
      "images": [
        {
          "thumb": "thumb_url",
          "large": "large_url",
          "variant": "variant_url",
          "hi_res": "hi_res_url"
        }
      ],
      "videos": ["video_url"],
      "store": "StoreName",
      "categories": ["Electronics", "Smartphones"],
      "details": {
        "brand": "BrandName",
        "model": "Model123"
      },
      "parent_asin": "B08XXXXXXX",
      "bought_together": ["B08YYYYYYY"]
    }
  ],
  "totalDocs": 100,
  "limit": 10,
  "page": 1,
  "totalPages": 10,
  "hasPrevPage": false,
  "hasNextPage": true,
  "prevPage": null,
  "nextPage": 2
}
```
