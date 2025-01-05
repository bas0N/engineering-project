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
    flex-direction: row;
    gap: 8px;
    justify-content: center;
    align-items: center;
    padding: 10px 0px;
`;

export const DetailsContainer = styled.div`
    width: calc(50% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;
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

export const DetailsRowText = styled(Text).attrs({align: 'center', size: 600})`
    flex: 1;
`;

export const DetailsDeletionButton = styled(Button).attrs({appearance: 'subtle'})`
    padding: 10px 5px;
    width: fit-content;
`;

export const DetailsOperations = styled.div`
    display; flex;
    gap: 8px;
    justify-content: center;
`;