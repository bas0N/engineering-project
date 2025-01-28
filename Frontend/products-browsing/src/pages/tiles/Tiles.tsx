import { useParams } from "react-router-dom";
import { Sidebar } from "../../components/tiles/sidebar/Sidebar";
import { TilesWrapper, TilesContainer, TilesFiltersOpeningWrapper } from "./Tiles.styled";
import { Tile } from "../../components/tiles/tile/Tile";
import { useEffect, useState } from "react";
import { Button, Text } from '@fluentui/react-components';
import { useTranslation } from 'react-i18next';
import axios from "axios";
import { ItemType } from "../../components/product/ProductPresentation/ProductPresentation";
import '../../i18n/i18n';

const Tiles = () => {

    const {query} = useParams();
    const {t} = useTranslation();
    const token = localStorage.getItem('authToken');
    const [tiles, setTiles] = useState<ItemType[]>([]);
    const [categories, setCategories] = useState<string[]>([]);
    const [minPrice, selectMinPrice] = useState<number|undefined>(undefined);
    const [maxPrice, selectMaxPrice] = useState<number|undefined>(undefined);
    const [selectedCategories, setSelectedCategories] = useState<string[]>([]);

    const [isSidebarOpened, setIsSidebarOpened] = useState(false);

    const closeSidebar = () => setIsSidebarOpened(false);

    useEffect(() => {
        const getData = async() => {
            try {
                const result = await axios.get(`${import.meta.env.VITE_API_URL}product/search`, {
                    params: {
                        title: query,
                        categories: selectedCategories,
                        minPrice,
                        maxPrice,
                    },
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                
                const newCategories = result.data.content
                .map((item: ItemType) => [item.mainCategory, ...item.categories]);
                const flattenedCategories:string[] = [].concat(...newCategories);
                const distinctCategories = flattenedCategories.filter((value, index, array) => array.indexOf(value) === index);
                
                setCategories(distinctCategories);
                setTiles(result.data.content);
            } catch (error){
                console.log(error);
            }
        }
        getData();
    }, [maxPrice, minPrice, query, selectedCategories, token]);

    if(token === null) return <></>

    return (<>
        <TilesWrapper>
            <Sidebar 
                isOpened={isSidebarOpened} 
                closeSidebar={closeSidebar} 
                categories={categories}
                onCategoriesSelect={setSelectedCategories}
                minPrice={minPrice}
                selectMinPrice={selectMinPrice}
                maxPrice={maxPrice}
                selectMaxPrice={selectMaxPrice}
            />
            <TilesFiltersOpeningWrapper>
                <Button onClick={() => setIsSidebarOpened(true)}>
                    {t('tiles.sidebarOpeningButton')}
                </Button>
            </TilesFiltersOpeningWrapper>
            <TilesContainer>
                {
                    tiles.length > 0 ?
                    tiles.map((tile, ind) => (
                        <Tile 
                            id={tile.parentAsin}
                            title={tile.title} 
                            images={tile.images}
                            averageRating={tile.averageRating}
                            ratingNumber={tile.ratingNumber}
                            price={tile.price}
                            key={`tile-${ind}`}
                        />
                    )) : <Text size={500}>
                        No results
                    </Text>
                }
            </TilesContainer>
        </TilesWrapper>
    </>);
}

export default Tiles;