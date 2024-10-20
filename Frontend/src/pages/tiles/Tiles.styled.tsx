import { tokens } from '@fluentui/react-components';
import styled from 'styled-components';

export const TilesWrapper = styled.section`
    width: calc(100vw - 10px);
    overflow-x: hidden;
    padding: 30px 5px 0px;

    @media screen and (min-width: 1024px) {
        display: flex;
        flex-direction: row;
        gap: 12px;
    }
`;

export const TilesContainer = styled.div`
    flex: 1;
    display: grid;
    grid-template-columns: repeat(1, 1fr);
    grid-row-gap: 4px;
    grid-column-gap: 4px;
    overflow-y: scroll;
    max-height: 80vh;
    scrollbar-color: ${tokens.colorPalettePurpleBackground2};
    scrollbar-width: 8px;

    @media screen and (min-width: 425px) {
        grid-template-columns: repeat(2, 1fr);
    }

    @media screen and (min-width: 768px){
        grid-template-columns: repeat(3, 1fr);
    }

    @media screen and (min-width: 1440px) {
        grid-template-columns: repeat(4, 1fr);   
    }
`;

export const TilesFiltersOpeningWrapper = styled.section`
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: row;
    gap: 4px;
    justify-content: space-around;

    @media screen and (min-width: 1024px){
        display: none;
    }
`;