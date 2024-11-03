import { Button, Input, Text } from '@fluentui/react-components';
import { styled } from 'styled-components';

export const BasketItemsWrapper = styled.section`
    height: fit-content;
    overflow-y: scroll;
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;

    @media screen and (min-width: 1024px) {
        flex: 1;
        height: calc(80vh - 20px);
        justify-content: flex-start;
    }
`;

export const BasketItem = styled.div`
    width: calc(100% - 22px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    border: 1px solid #555;
    border-radius: 10px;

    @media screen and (min-width: 768px){
        width: calc(100% - 26px);
        flex-direction: row;
    }
`;

export const BasketItemDescription = styled.div`
    flex: 4;
    display: flex;
    gap: 8px;
    flex-direction: column;
    align-items: center;

    @media screen and (min-width: 425px){
        flex-direction: row;
    }

    @media screen and (min-width: 1024px) {
        flex: 3;
    }
`;

export const BasketItemManagement = styled.div`
    flex: 1;
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 8px;
`;

export const BasketItemTitle = styled(Text).attrs({weight: 'semibold', truncate: true, wrap: false})`
    padding-left: 10px;
    font-size: 14px; 
    overflow-x: hidden;
    max-width: calc(80vw - 10px);

    @media screen and (min-width: 425px) {
        max-width: calc(50vw - 10px);
    }

    @media screen and (min-width: 768px) {
        max-width: calc(30vw - 10px);
    }

    @media screen and (min-width: 1024px) {
        padding-left: 20px;
        font-size: 18px; 
    }
`;

export const BasketItemPrice = styled(Text).attrs({size: 500})`
    padding: 0px 10px;
    font-size: 14px; 

    @media screen and (min-width: 1024px) {
        font-size: 18px; 
    }
`;

export const BasketItemQuantity = styled(Input).attrs({appearance: 'underline'})`
    flex: 1;
    max-width: 140px !important;
    width: fit-content;
    min-width: auto !important;
    text-align: center;
`;

export const BasketItemDeleteButton = styled(Button).attrs({appearance: 'subtle'})`
    flex: 1;
    padding: 10px 0px;
`;
