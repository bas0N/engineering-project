import mongoose, { Document, PaginateModel, Schema } from "mongoose";
import mongoosePaginate from "mongoose-paginate-v2";

export interface IProduct extends Document {
  main_category: string;
  title: string;
  average_rating: number;
  rating_number: number;
  features: string[];
  description: string[];
  price: number | null;
  images: {
    thumb: string;
    large: string;
    variant: string;
    hi_res: string;
  }[];
  videos: string[];
  store: string;
  categories: string[];
  details: {
    [key: string]: string;
  };
  parent_asin: string;
  bought_together: string[] | null;
}

const ProductSchema: Schema = new Schema(
  {
    main_category: { type: String, required: true },
    title: { type: String, required: true },
    average_rating: { type: Number, required: true },
    rating_number: { type: Number, required: true },
    features: { type: [String], required: true },
    description: { type: [String], required: true },
    price: { type: Number, default: null },
    images: [
      {
        thumb: { type: String, required: true },
        large: { type: String, required: true },
        variant: { type: String, required: true },
        hi_res: { type: String, required: true },
      },
    ],
    videos: { type: [String], default: [] },
    store: { type: String, required: true },
    categories: { type: [String], required: true },
    details: { type: Object, required: true },
    parent_asin: { type: String, required: true },
    bought_together: { type: [String], default: null },
  },
  { collection: "my_custom_collection_name" }
);
ProductSchema.plugin(mongoosePaginate);
type ProductModel = PaginateModel<IProduct>;

export default mongoose.model<IProduct>(
  "my_custom_collection_name",
  ProductSchema,
  "my_custom_collection_name"
) as ProductModel;
