import { Image } from '@fluentui/react-components';
import styled from 'styled-components';

export const ProductPresentationImagesSection = styled.div`
    flex: 2;
    display: flex;
    gap: 8px;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    height: 80%;

    @media screen and (min-width: 1024px){
        height: 100%;
        width: 60vw !important;
        max-width: 60vw !important;
        overflow-x: hidden;
    }
`;

export const ProductPresentationImage = styled(Image)`
    max-height: 50vh;
    width: auto;
    display: block;
    margin-left: auto;
    margin-right: auto;
    z-index: 7;

    @media screen and (min-width: 1024px){
        max-height: 60vh;
    }
`;