import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { ItemsSearchBox, ItemsSearchButton, NoItemsBanner, SearchContainer } from "./Search.styled";
import { SearchRegular } from "@fluentui/react-icons";
import { ItemType, LastItems } from "./lastItems/LastItems";
import { Spinner } from "@fluentui/react-components";
import axios from 'axios';
import Fuse from 'fuse.js';

export const Search = () => {

    const [search, setSearch] = useState('');
    const {t} = useTranslation();

    const [lastItemsOpened] = useState(true);
    const [items, setItems] = useState<ItemType[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    const [filteredItems, setFilteredItems] = useState<ItemType[]>([]);


    const handleSearch = (query: string) => {
        if (query) {
            const fuse = new Fuse(items.map((elem) => elem.name), { keys: [''], threshold: 0.3 });
            const fuseResults = fuse.search(query);
            setFilteredItems(items.filter((item) => fuseResults.some((result) => result.item === item.name)));
          } else {
            setFilteredItems([]);
          }
    };

    useEffect(() => {
        const getSearchData = async() => {
            // TODO: this URL is just for the test purposes, change it after we have the backend ready
            const results = await axios.get('http://localhost:3000/items');
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

    return (
        <>
            <SearchContainer>
                <ItemsSearchBox
                    value={search} 
                    onChange={(_, data) => handleSearchBox(data.value)} 
                    placeholder={t('searchBox.placeholder')}
                />
                <ItemsSearchButton>
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