import { Text } from '@fluentui/react-components';
import { styled } from 'styled-components';

export const ProductsWrapper = styled.section`
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const ProductsWrapperHeader = styled(Text).attrs({size: 600, align: 'center'})`
    padding: 10px;
`;