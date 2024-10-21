import { 
    CarouselAnnouncerFunction, 
    Carousel, 
    CarouselSlider, 
    CarouselCard, 
    CarouselNavContainer, 
    CarouselNav, 
    CarouselNavButton 
} from "@fluentui/react-components";
import { ProductPresentationImage, ProductPresentationImagesSection } from "./ImagesCarousel.styled";

const getAnnouncement: CarouselAnnouncerFunction = (
    index: number,
    totalSlides: number,
  ) => {
    return `Carousel slide ${index + 1} of ${totalSlides}`;
};

export type ProductImage = {
    thumb: string,
    large: string,
    variant: string,
    hiRes: string | null,
};

interface ImagesCarouselProps {
    images: ProductImage[];
    title: string;
}

export const ImagesCarousel = ({
    images,
    title,
} : ImagesCarouselProps) => (
    <ProductPresentationImagesSection>
        <Carousel groupSize={1} circular announcement={getAnnouncement}>
            <CarouselSlider>
                {
                    images.map((elem,ind) => <CarouselCard id={`test-${ind}`}>
                        <ProductPresentationImage 
                            src={elem.hiRes ?? elem.large}
                            alt={`${title}-photo-${ind}`}
                        />
                    </CarouselCard>
                    )
                }
            </CarouselSlider>
            <CarouselNavContainer 
                layout="inline"
                autoplay={undefined}
                next={{ "aria-label": "go to next" }}
                prev={{ "aria-label": "go to prev" }}
            >
                <CarouselNav>
                    {(index) => <CarouselNavButton aria-label={`Carousel Nav Button ${index}`}/>}
                </CarouselNav>
            </CarouselNavContainer>
        </Carousel>
    </ProductPresentationImagesSection>
);