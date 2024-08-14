import mongoose, { ConnectOptions } from "mongoose";

const connectDB = async () => {
  try {
    await mongoose.connect(process.env.MONGO_URI!, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    } as ConnectOptions);
    console.log("MongoDB Connected...");
  } catch (err) {
    console.log("MongoDB Connection Error: ", err);
    process.exit(1);
  }
};

export default connectDB;
