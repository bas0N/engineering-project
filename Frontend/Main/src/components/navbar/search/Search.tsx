import { useEffect, useState } from "react";
import { useTranslation } from "../../../../node_modules/react-i18next";
import { ItemsSearchBox, ItemsSearchButton, NoItemsBanner, SearchContainer } from "./Search.styled";
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

    const [lastItemsOpened] = useState(true);
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
            // TODO: this URL is just for the test purposes, change it after we have the backend ready
            const results = await axios.get('http://localhost:3000/items');
            console.log(results.data);
            setItems(results.data as ItemType[]);
        };
        getSearchData();
    }, []);

    const handleSearchBox = (query: string) => {
        setIsLoading(true);
        setSearch(query);
        handleSearch(query);
        setIsLoading(false);
    };

    const handleSearchButton = () => {
        navigate(`/products/search/${search}`);
    }

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
            {
                lastItemsOpened && (filteredItems.length > 0 ? (
                    <LastItems items={filteredItems} />
                ) : search.length > 0 ? (!isLoading ? (
                    <NoItemsBanner>{t('searchBox.noItems')}</NoItemsBanner>
                ) :  <Spinner label={t('searchBox.loading')}/>) : null)
            }
        </>
      )
}