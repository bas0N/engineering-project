import { Text } from "@fluentui/react-components";
import { styled } from "styled-components";

export const ProductsImageManagementWrapper = styled.section`
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const ProductsImageHeader = styled(Text).attrs({size: 600, align: 'center', weight: 'semibold'})`
    width: calc(90% - 20px);
    padding: 10px;
    padding-bottom: 30px;
`;

export const ProductsImageError = styled(Text).attrs({size: 400, align: 'center'})`
    width: calc(80% - 20px);
    padding: 10px;
`;