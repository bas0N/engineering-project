import { Button, Text } from '@fluentui/react-components';
import { styled } from "styled-components";

export const DetailsWrapper = styled.section`
    width: calc(100% - 10px);
    padding: 10px 5px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const DetailsAddingContainer = styled.div`
    display: flex;
    flex-direction: column;
    gap: 8px;
    justify-content: center;
    align-items: center;
    padding: 10px 0px;

    @media screen and (min-width: 768px) {
        flex-direction: row;
        justify-content: center;
        align-items: center;
    }
`;

export const DetailsContainer = styled.div`
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;

    @media screen and (min-width: 425px) {
        width: calc(80% - 20px);
    }

    @media screen and (min-width: 768px) {
        width: calc(50% - 20px);
    }
`;

export const DetailsRow = styled.div`
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-around;
    gap: 8px;
`;

export const DetailsRowText = styled(Text).attrs({align: 'center'})`
    flex: 1;
    font-size: 14px;

    @media screen and (min-width: 768px) {
        font-size: 20px;
    }
`;

export const DetailsDeletionButton = styled(Button).attrs({appearance: 'subtle'})`
    padding: 10px 10px;
    min-width: fit-content;
    width: fit-content !important;
`;

export const DetailsOperations = styled.div`
    display; flex;
    gap: 32px;
    justify-content: center;
`;