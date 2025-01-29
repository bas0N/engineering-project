import { Image, Text } from "@fluentui/react-components";
import styled from "styled-components";

export const BasketItemListContainer = styled.div`
    padding: 16px;
    padding-bottom: 36px;
    display: flex;
    gap: 12px;
    flex-direction: column;
`;

export const BasketItemHeader = styled(Text)`
    font-weight: 600;
    padding-bottom: 8px;
`;

export const BasketItemWrapper = styled.div`
    display: flex:
    gap: 12px;
    align-items: center;
`;

export const BasketItemDetails = styled.div`
    display: flex;
    gap: 4px;
    flex-direction: column;
`;

export const BasketItemImage = styled(Image)`
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px
`;
