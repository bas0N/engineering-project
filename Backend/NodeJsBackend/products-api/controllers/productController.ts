import { Request, Response } from "express";
import Product from "../models/productModel";

export const getProducts = async (req: Request, res: Response) => {
  try {
    const {
      page = 1,
      limit = 10,
      sort = "title",
      main_category,
      title,
      min_price,
      max_price,
      min_rating,
      max_rating,
      categories,
      store,
    } = req.query;

    // Pagination and sorting options
    const options = {
      page: Number(page),
      limit: Number(limit),
      sort: { [sort as string]: 1 },
    };

    // Constructing the query object
    const query: any = {};

    // Filter by main_category
    if (main_category) {
      query.main_category = main_category;
    }

    // Filter by title (case-insensitive regex)
    if (title) {
      query.title = new RegExp(title as string, "i");
    }

    // Filter by price range
    if (min_price || max_price) {
      query.price = {};
      if (min_price) query.price.$gte = Number(min_price);
      if (max_price) query.price.$lte = Number(max_price);
    }

    // Filter by rating range
    if (min_rating || max_rating) {
      query.average_rating = {};
      if (min_rating) query.average_rating.$gte = Number(min_rating);
      if (max_rating) query.average_rating.$lte = Number(max_rating);
    }

    // Filter by categories (expects an array or comma-separated string)
    if (categories) {
      const categoriesArray = (categories as string)
        .split(",")
        .map((cat) => cat.trim());
      query.categories = { $in: categoriesArray };
    }

    // Filter by store
    if (store) {
      query.store = store;
    }
    console.log("query:");
    console.log(query);
    // Fetching paginated products based on the query and options
    const products = await Product.paginate(query, options);

    res.json(products);
  } catch (err) {
    res.status(500).json({ message: "Server Error", error: err });
  }
};
