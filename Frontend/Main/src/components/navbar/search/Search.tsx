import { useEffect, useState } from "react";
import { useTranslation } from "../../../../node_modules/react-i18next";
import { 
    ItemsSearchBox, 
    ItemsSearchButton, 
    NoItemsBanner, 
    SearchContainer,
    SearchResponseWrapper, 
} from "./Search.styled";
import { SearchRegular } from "@fluentui/react-icons";
import { ItemType, LastItems } from "./lastItems/LastItems";
import { Spinner } from "@fluentui/react-components";
import axios from 'axios';
import Fuse from 'fuse.js';
import { useNavigate } from "react-router-dom";

export const Search = () => {

    const [search, setSearch] = useState('');
    const {t} = useTranslation();
    const navigate = useNavigate();
    const token = localStorage.getItem('authToken');

    const [lastItemsOpened, setLastItemsOpened] = useState(true);
    const [items, setItems] = useState<ItemType[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    const [filteredItems, setFilteredItems] = useState<ItemType[]>([]);


    const handleSearch = (query: string) => {
        if (query) {
            const fuse = new Fuse(items.map((elem) => elem.title), { keys: [''], threshold: 0.3 });
            const fuseResults = fuse.search(query);
            setFilteredItems(items.filter((item) => fuseResults.some((result) => result.item === item.title)));
          } else {
            setFilteredItems([]);
          }
    };

    useEffect(() => {
        const getSearchData = async() => {
            const results = await axios.get(`${import.meta.env.VITE_API_URL}product/search`, {
                params: {
                    title: search
                },
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log(results);
            setItems(results.data.content as ItemType[]);
        };
        getSearchData();
    }, [search, token]);

    const handleSearchBox = (query: string) => {
        setIsLoading(true);
        setSearch(query);
        handleSearch(query);
        setIsLoading(false);
    };

    const handleSearchButton = () => {
        navigate(`/products/search/${search}`);
        setLastItemsOpened(false);
    }

    const closeLastItems = () => setLastItemsOpened(false);

    return (
        <>
            <SearchContainer>
                <ItemsSearchBox
                    value={search} 
                    onChange={(_, data) => handleSearchBox(data.value)} 
                    placeholder={t('searchBox.placeholder')}
                />
                <ItemsSearchButton onClick={handleSearchButton}>
                    <SearchRegular />
                </ItemsSearchButton>
            </SearchContainer>
            <SearchResponseWrapper>
                {
                    lastItemsOpened && (filteredItems.length > 0 ? (
                        <LastItems 
                            items={filteredItems} 
                            closeLastItems={closeLastItems}
                        />
                    ) : search.length > 0 ? (!isLoading ? (
                        <NoItemsBanner>{t('searchBox.noItems')}</NoItemsBanner>
                    ) :  <Spinner label={t('searchBox.loading')}/>) : null)
                }
            </SearchResponseWrapper>
        </>
      )
}