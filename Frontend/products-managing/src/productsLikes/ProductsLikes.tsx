import { useEffect, useState } from 'react';
import axios from 'axios';
import {
    ProductsWrapper,
    ProductCard,
    ProductImage,
    ProductDetails,
    ProductTitle,
    ProductPrice,
    ProductRating,
    ProductHeader,
    ProductInfo
} from './ProductsLikes.styled.tsx';

interface ImageResponse {
    thumb: string;
    large: string;
    variant: string;
    hiRes: string;
}

interface ProductResponse {
    title: string;
    price: string;
    ratingNumber: number;
    averageRating: number;
    images: ImageResponse[];
    isActive: boolean;
}

export const ProductsLikes = () => {
    const [products, setProducts] = useState<ProductResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const token = localStorage.getItem('authToken');

    const fetchLikedProducts = async () => {
        try {
            const response = await axios.get(`${import.meta.env.VITE_API_URL}like/my`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setProducts(response.data);
        } catch (error) {
            console.error('Error fetching liked products:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchLikedProducts();
    }, []);

    if (loading) {
        return <p>Loading...</p>;
    }

    return (
        <>
            <ProductHeader>Products You Have Liked</ProductHeader>
            <ProductsWrapper>
                {products.map((product, index) => (
                    <ProductCard key={index} isActive={product.isActive}>
                        <ProductImage
                            src={product.images[0]?.thumb}
                            alt={product.title}
                        />
                        <ProductDetails>
                            <ProductTitle>{product.title}</ProductTitle>
                            <ProductInfo>
                                <strong>Price:</strong> <ProductPrice>${product.price}</ProductPrice>
                            </ProductInfo>
                            <ProductInfo>
                                <strong>Rating:</strong> <ProductRating>{product.averageRating} ★ ({product.ratingNumber} reviews)</ProductRating>
                            </ProductInfo>
                        </ProductDetails>
                    </ProductCard>
                ))}
            </ProductsWrapper>
        </>
    );
};

export default ProductsLikes;
