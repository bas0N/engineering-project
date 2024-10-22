import { ItemType } from "../../components/search/lastItems/LastItems";
import {
    Text
} from "@fluentui/react-components";
import { useTranslation } from '../../../node_modules/react-i18next';
import { Search } from "../../components/search/Search";
import { 
    ProductWrapper, 
    ProductPresentationSection,
    ProductDescriptionSection,
} from "./Product.styled";
import { ImagesCarousel } from "../../components/product/ImagesCarousel/ImagesCarousel";
import { DetailsAndFeatures } from "../../components/product/DetailsAndFeatures/DetailsAndFeatures";
import { ProductPresentation } from "../../components/product/ProductPresentation/ProductPresentation";
import { useState } from "react";
//import { useParams } from "react-router-dom";
//import axios from "axios";

export const Product = () => {

    const {t} = useTranslation();

    const [item] = useState<ItemType|null>({
        id: "66bcc259d9c41c4db330477d",
        boughtTogether: null,
        categories: [
            "Gift Cards",
            "Gift Card Categories",
            "Specialty Cards"
        ],
        description: [],
        details: {
            "Is Discontinued By Manufacturer": "No",
            "Package Dimensions": "5.25 x 4 x 0.05 inches; 0.32 ounces",
            "Date First Available": "April 1, 2019"
        },
        features: [
            "This card is non-reloadable. No expiration of funds. NO cash or ATM access. Cards are shipped ready to use.",
            "Use your Mastercard Gift Card in the U.S. everywhere Mastercard debit cards are accepted, including online. Your Amazon.com Balance cannot be used to purchase Visa gift cards.",
            "A one-time $5.95 purchase fee applies at the time of purchase. No fees after purchase (including dormancy, service or other fees).",
            "This item is not eligible for refund or return. Available for sale within the United States only (not available to Puerto Rico residents). Additional shipping restrictions apply to Hawaii, Kansas, New Mexico, South Dakota, US Virgin Islands, Vermont, and West Virginia.",
            "Do not provide any gift card details (such as the claim code) to someone you do not know or trust. There are a variety of scams in which fraudsters try to trick others into paying with gift cards. We want to make sure our customers are aware of potential scams that may involve asking for payment using gift cards. See \"Be Informed\" link at the top of the page to learn more."
        ],
        images: [
            {
                thumb: "https://m.media-amazon.com/images/I/519AAiepM1L._SX38_SY50_CR,0,0,38,50_.jpg",
                large: "https://m.media-amazon.com/images/I/519AAiepM1L.jpg",
                variant: "MAIN",
                hiRes: null
            },
            {
                thumb: "https://m.media-amazon.com/images/I/618sQ66+qPL._SX38_SY50_CR,0,0,38,50_.jpg",
                large: "https://m.media-amazon.com/images/I/618sQ66+qPL.jpg",
                variant: "PT01",
                hiRes: null
            }
        ],
        mainCategory: null,
        parentAsin: null,
        price: "105.95",
        ratingNumber: null,
        store: "Mastercard",
        title: "$100 Mastercard Gift Card (plus $5.95 Purchase Fee)",
        videos: [
            {
                "title": "The best ways to use",
                "url": "https://www.amazon.com/vdp/0d840ee9be7f4ecb877b09673ba75e62?ref=dp_vse_rvc_0",
                "userId": null
            }
        ],
        averageRating: null
    });
    /*const {productId} = useParams();

    useEffect(() => {
        const getItemData = async() => {
            const result = await axios.get()
        };
        getItemData();
    }, []);
*/
    return (
        <>
            <Search />
            {
                item === null ? <Text>
                    {t('product.loadingFailed')}
                </Text> : 
                <ProductWrapper>
                    <ProductPresentationSection height={80}>
                        <ImagesCarousel title={item.title} images={item.images} /> 
                        <ProductPresentation 
                            title={item.title}
                            categories={item.categories}
                            price={item.price}
                            ratingNumber={item.ratingNumber}
                            averageRating={item.averageRating}
                        />
                    </ProductPresentationSection>
                    <ProductDescriptionSection>
                        {item.description}
                    </ProductDescriptionSection>
                    <ProductPresentationSection height={50}>
                        <DetailsAndFeatures 
                            features={item.features} 
                            details={item.details} 
                        />
                    </ProductPresentationSection>
                </ProductWrapper>
            }
        </>
    )
}