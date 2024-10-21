import styled from 'styled-components';
import { Text } from '@fluentui/react-components';

export const ProductDetailsAndFeatures = styled.section<{gapSize?: number}>`
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: ${(props) => props.gapSize ? props.gapSize : 4}px;
    align-items: center;
`;

export const ProductsDetailsAndFeaturesHeader = styled(Text).attrs({size: 700, as: 'h2', align: 'center'})`
    width: 100%;
    padding-bottom: 32px;
`;

export const ProductsDetailsAndFeaturesListItem = styled(Text).attrs({size: 400, align: 'center', as: 'p'})``;

export const ProductsDetailWrapper = styled.div`
    width: 80%;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    gap: 4px;
`;

export const ProductCategoriesWrapper = styled.section`
    display: flex;
    gap: 4px;
    flex-wrap: wrap;
    padding: 10px 5px 20px;
`;