import { useParams } from "react-router-dom";
import { Sidebar } from "../../components/tiles/sidebar/Sidebar";
import { Search } from "../../components/search/Search";
import { TilesWrapper, TilesContainer, TilesFiltersOpeningWrapper } from "./Tiles.styled";
import { Tile } from "../../components/tiles/tile/Tile";
import { useState } from "react";
import { Button } from '@fluentui/react-components';
import { useTranslation } from '../../../node_modules/react-i18next';

export const Tiles = () => {

    const {query} = useParams();
    const {t} = useTranslation();

    const [isSidebarOpened, setIsSidebarOpened] = useState(false);

    console.log(query);

    const closeSidebar = () => setIsSidebarOpened(false);

    return (<>
        <Search />
        <TilesWrapper>
            <Sidebar isOpened={isSidebarOpened} closeSidebar={closeSidebar} />
            <TilesFiltersOpeningWrapper>
                <Button onClick={() => setIsSidebarOpened(true)}>
                    {t('tiles.sidebarOpeningButton')}
                </Button>
            </TilesFiltersOpeningWrapper>
            <TilesContainer>
                <Tile 
                    id='mockId' 
                    title='mock title' 
                    images={[{
                        thumb: "https://m.media-amazon.com/images/I/519AAiepM1L._SX38_SY50_CR,0,0,38,50_.jpg",
                        large: "https://m.media-amazon.com/images/I/519AAiepM1L.jpg",
                        variant: "MAIN",
                        hiRes: null
                    }]}
                    averageRating={4.8}
                    ratingNumber={2000}
                    price={'148.2'}
                />

                <Tile 
                    id='mockId' 
                    title='mock title' 
                    images={[{
                        thumb: "https://m.media-amazon.com/images/I/519AAiepM1L._SX38_SY50_CR,0,0,38,50_.jpg",
                        large: "https://m.media-amazon.com/images/I/519AAiepM1L.jpg",
                        variant: "MAIN",
                        hiRes: null
                    }]}
                    averageRating={4.8}
                    ratingNumber={2000}
                    price={'148.2'}
                />
                <Tile 
                    id='mockId' 
                    title='mock title' 
                    images={[{
                        thumb: "https://m.media-amazon.com/images/I/519AAiepM1L._SX38_SY50_CR,0,0,38,50_.jpg",
                        large: "https://m.media-amazon.com/images/I/519AAiepM1L.jpg",
                        variant: "MAIN",
                        hiRes: null
                    }]}
                    averageRating={4.8}
                    ratingNumber={2000}
                    price={'148.2'}
                />
                <Tile 
                    id='mockId' 
                    title='mock title' 
                    images={[{
                        thumb: "https://m.media-amazon.com/images/I/519AAiepM1L._SX38_SY50_CR,0,0,38,50_.jpg",
                        large: "https://m.media-amazon.com/images/I/519AAiepM1L.jpg",
                        variant: "MAIN",
                        hiRes: null
                    }]}
                    averageRating={4.8}
                    ratingNumber={2000}
                    price={'148.2'}
                />
                <Tile 
                    id='mockId' 
                    title='mock title' 
                    images={[{
                        thumb: "https://m.media-amazon.com/images/I/519AAiepM1L._SX38_SY50_CR,0,0,38,50_.jpg",
                        large: "https://m.media-amazon.com/images/I/519AAiepM1L.jpg",
                        variant: "MAIN",
                        hiRes: null
                    }]}
                    averageRating={4.8}
                    ratingNumber={2000}
                    price={'148.2'}
                />
                <Tile 
                    id='mockId' 
                    title='mock title' 
                    images={[{
                        thumb: "https://m.media-amazon.com/images/I/519AAiepM1L._SX38_SY50_CR,0,0,38,50_.jpg",
                        large: "https://m.media-amazon.com/images/I/519AAiepM1L.jpg",
                        variant: "MAIN",
                        hiRes: null
                    }]}
                    averageRating={4.8}
                    ratingNumber={2000}
                    price={'148.2'}
                />
            </TilesContainer>
        </TilesWrapper>
    </>);
}