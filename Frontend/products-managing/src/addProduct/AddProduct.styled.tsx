import { Input, Text, Textarea } from "@fluentui/react-components";
import styled from "styled-components";

export const AddProductWrapper = styled.section`
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const AddProductHeader = styled(Text).attrs({align: 'center'})`
    padding: 10px 5px 30px;
`;

export const AddProductInput = styled(Input).attrs({appearance: 'underline'})`
    width: calc(80% - 20px);
    padding: 10px;

    @media screen and (min-width: 768px){
        width: calc(60% - 20px);
    }
`;

export const AddProductTextarea = styled(Textarea).attrs({resize: 'vertical'})`
    min-width: calc(80% - 20px);
    padding: 10px;

    @media screen and (min-width: 768px){
        min-width: calc(60% - 20px);
    }
`;

export const AddProductInputWrapper = styled.div`
    display: flex;
    flex-direction: row;
    align-items: center;
    width: calc(60% - 20px);
    padding: 10px;
    gap: 8px;

    @media screen and (min-width: 768px){
        width: calc(40% - 20px);
    }
`;

export const AddProductInputText = styled(Text).attrs({size: 400, align: 'start'})`
    flex: 1;
`;

export const AddProductButtonsWrapper = styled.div`
    width: calc(50% - 20px);
    padding: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 36px;
`;

export const AddProductAfterCreationActivites = styled.section`
    display: flex;
    flex-direction: column;
    gap: 8px;
    width: calc(80% - 20px);
    padding: 10px;
    justify-content: center;

    @media screen and (min-width: 425px){
        flex-direction: row;
    }

    @media screen and (min-width: 768px) {
        width: calc(50% - 20px);
    }
`;