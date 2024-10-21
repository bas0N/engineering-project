import { Button, Input, RatingDisplay, Text } from '@fluentui/react-components';
import { styled } from "styled-components";

export const ProductWrapper = styled.section`
    width: calc(100% - 20px);
    padding: 10px;
    padding-top: 48px;
    display: flex;
    flex-direction: column;
    gap: 4px;
    align-items: center;
`;

export const ProductPresentationSection = styled.div<{height: number}>`
    width: calc(100% - 20px);
    height: fit-content;
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 4px;

    @media screen and (min-width: 1024px) {
        flex-direction: row;
        height: calc(${(props) => props.height}vh - 20px);
    }
`;

export const ProductPresentationOrderingSection = styled.div`
    flex: 3;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const ProductPresentationHeader = styled(Text).attrs({weight: 'semibold', align: 'center'})`
    padding-bottom: 10px;
    font-size: 28px;
    line-height: 28px;

    @media screen and (min-width: 1024px){
        font-size: 32px;
        line-height: 32px;
    }
`;

export const ProductBuyingSection = styled.div`
    flex: 1;
    display: grid;
    grid-template-columns: 40% auto;
    grid-template-rows: auto auto;
    gap: 4px;
    max-height: 15vh;
    width: 90%;
    padding: 32px 5% 0px;

    @media screen and (min-width: 425px){
        width: 80%;
        padding: 32px 10% 0px;
        grid-template-columns: 30% auto;
    }

    @media screen and (min-width: 1024px) {
        grid-template-columns: 1fr 6fr;
        width: 100%;
        max-height: 15vh;
        padding: 32px 0 0px;
    }
`;

export const ProductPrice = styled(Text).attrs({align: 'center', size: 500})`
    grid-row: 1;
    grid-columns: 1;
    padding-bottom: 0;
`;

export const ProductRatingDisplay = styled(RatingDisplay)`
    padding-bottom: 32px;
`;

export const ProductAmountInput = styled(Input).attrs({type: 'number', min: '0'})`
    grid-row: 2;
    grid-columns: 1;
    text-align: center;
`;

export const ProductAddToTheBaskedButton = styled(Button)`
    grid-row: 2;
    grid-columns: 2;
`;

export const ProductDescriptionSection = styled(Text).attrs({size: 400, align: 'start'})`
    flex: 1;
    padding: 10px;
`;