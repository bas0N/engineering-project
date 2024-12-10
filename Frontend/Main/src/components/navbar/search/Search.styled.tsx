import { Button, SearchBox, Text } from "@fluentui/react-components";
import { styled } from "styled-components";

export const SearchContainer = styled.div`
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    justify-content: center;
    gap: 4px;

    @media screen and (min-width: 375px){
        width: calc(100% - 24px);
    }
`;

export const SearchResponseWrapper = styled.section`
    position: fixed;
    top: 10vh;
    z-index: 9;
    align-items: center;
    width: 100%;
`;

export const ItemsSearchBox = styled(SearchBox)`
    min-width: fit-content !important;
    @media screen and (min-width: 375px){
        flex: 1;
        max-width: auto;
    }
`;

export const ItemsSearchButton = styled(Button)`
    display: none;
    @media screen and (min-width: 375px){
        display: block;
    }
`;

export const NoItemsBanner = styled(Text).attrs({size: 400, align: 'center'})`
    display: block;
    margin-left: auto;
    margin-right: auto;
`;