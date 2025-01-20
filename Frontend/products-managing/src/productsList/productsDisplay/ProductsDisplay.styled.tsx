import { DataGrid } from '@fluentui/react-components';
import { styled } from 'styled-components';

export const ProductsDisplayWrapper = styled(DataGrid)`
    width: calc(80% - 20px);
    padding: 10px;
`;

export const ProductActionsWrapper = styled.div`
    display: flex;
    gap: 8px;
    align-items: center;
    justify-content: center;
`;